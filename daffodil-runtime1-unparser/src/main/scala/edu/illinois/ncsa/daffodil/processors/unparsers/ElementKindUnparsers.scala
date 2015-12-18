/* Copyright (c) 2012-2014 Tresys Technology, LLC. All rights reserved.
 *
 * Developed by: Tresys Technology, LLC
 *               http://www.tresys.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal with
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimers.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimers in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  3. Neither the names of Tresys Technology, nor the names of its contributors
 *     may be used to endorse or promote products derived from this Software
 *     without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 * SOFTWARE.
 */

package edu.illinois.ncsa.daffodil.processors.unparsers
import edu.illinois.ncsa.daffodil.processors._
import edu.illinois.ncsa.daffodil.processors.RuntimeData
import edu.illinois.ncsa.daffodil.util.Maybe._
import edu.illinois.ncsa.daffodil.dsom.EscapeSchemeObject
import edu.illinois.ncsa.daffodil.compiler.DaffodilTunableParameters
import edu.illinois.ncsa.daffodil.api.ValidationMode
import edu.illinois.ncsa.daffodil.dsom.CompiledExpression
import edu.illinois.ncsa.daffodil.schema.annotation.props.gen.EscapeKind
import edu.illinois.ncsa.daffodil.processors.dfa.CreateDelimiterDFA
import edu.illinois.ncsa.daffodil.dsom.EscapeSchemeObject
import edu.illinois.ncsa.daffodil.xml.QNameBase
import edu.illinois.ncsa.daffodil.xml.QName
import edu.illinois.ncsa.daffodil.xml.NamedQName
import edu.illinois.ncsa.daffodil.exceptions.Assert
import edu.illinois.ncsa.daffodil.util.Maybe
import edu.illinois.ncsa.daffodil.util.Maybe._
import edu.illinois.ncsa.daffodil.exceptions.Assert
import edu.illinois.ncsa.daffodil.equality._
import scala.collection.mutable.ArraySeq

class ComplexTypeUnparser(rd: RuntimeData, bodyUnparser: Unparser)
  extends Unparser(rd) {
  override def nom = "ComplexType"

  override lazy val childProcessors = Seq(bodyUnparser)

  def unparse(start: UState): Unit = {
    start.childIndexStack.push(1L) // one-based indexing
    bodyUnparser.unparse1(start, rd)
    start.childIndexStack.pop()
  }
}

class SequenceCombinatorUnparser(rdArg: ModelGroupRuntimeData, childUnparsers: Vector[Unparser])
  extends TermUnparser(rdArg)
  with ToBriefXMLImpl {
  override def nom = "Sequence"

  // Sequences of nothing (no initiator, no terminator, nothing at all) should
  // have been optimized away
  Assert.invariant(childUnparsers.length > 0)

  // Since some of the grammar terms might have folded away to EmptyGram,
  // the number of unparsers here may be different from the number of
  // children of the sequence group.
  Assert.invariant(rdArg.groupMembers.length >= childUnparsers.length)

  override lazy val childProcessors: Seq[Processor] = childUnparsers

  def unparse(start: UState): Unit = {

    start.groupIndexStack.push(1L) // one-based indexing

    var index = 0
    var doUnparser = false
    val limit = childUnparsers.length

    while (index < limit) {
      doUnparser = false
      val childUnparser = childUnparsers(index)
      val childRD = childUnparser.context

      childRD match {
        case erd: ElementRuntimeData if !erd.isRequired => {
          // it's not a required element, so we check to see if we have a matching
          // incoming infoset event
          if (start.inspect) {
            val ev = start.inspectAccessor
            if (ev.isStart) {
              val eventNQN = ev.node.namedQName
              if (eventNQN =:= erd.namedQName) {
                doUnparser = true
              }
            } else if (ev.isEnd && ev.isComplex) {
              val c = ev.asComplex
              //ok. We've peeked ahead and found the end of the complex element
              //that this sequence is the model group of.
              val optParentRD = termRuntimeData.immediateEnclosingRuntimeData
              optParentRD match {
                case Some(e: ElementRuntimeData) =>
                  Assert.invariant(c.runtimeData.namedQName =:= e.namedQName)
                case _ =>
                  Assert.invariantFailed("Not end element for this sequence's containing element. Event %s, optParentRD %s.".format(
                    ev, optParentRD))
              }
            } else {
              Assert.invariantFailed("Not a start event: " + ev)
            }
          }
        }
        case _ => {
          // since only elements can be optional, anything else is non-optional
          doUnparser = true
        }
      }
      if (doUnparser) {
        childUnparser.unparse1(start, childRD)
      }
      index += 1
      //
      // Note: the invariant is that unparsers move over 1 within their group themselves
      // we do not do the moving over here as we are the caller of the unparser.
      //
    }
    start.groupIndexStack.pop()
    //
    // this is establishing the invariant that unparsers (in this case the sequence unparser)
    // moves over within its containing group. The caller of an unparser does not do this move.
    //
    start.moveOverOneGroupIndexOnly()
  }
}

class ChoiceCombinatorUnparser(mgrd: ModelGroupRuntimeData, eventUnparserMap: Map[ChoiceBranchEvent, Unparser])
  extends TermUnparser(mgrd)
  with ToBriefXMLImpl {
  override def nom = "Choice"

  override lazy val childProcessors: Seq[Processor] = eventUnparserMap.map { case (k, v) => v }.toSeq

  def unparse(state: UState): Unit = {

    val event: InfosetAccessor = state.inspectOrError
    val key: ChoiceBranchEvent = event match {
      //
      // The ChoiceBranchStartEvent(...) is not a case class constructor. It is a
      // hash-table lookup for a cached value. This avoids constructing these
      // objects over and over again.
      //
      case e if e.isStart && e.isElement => ChoiceBranchStartEvent(e.asElement.runtimeData.namedQName)
      case e if e.isEnd && e.isElement => ChoiceBranchEndEvent(e.asElement.runtimeData.namedQName)
      case e if e.isStart && e.isArray => ChoiceBranchStartEvent(e.asArray.erd.namedQName)
      case e if e.isEnd && e.isArray => ChoiceBranchEndEvent(e.asArray.erd.namedQName)
    }

    val childUnparser = eventUnparserMap.get(key).getOrElse {
      UnparseError(One(mgrd.schemaFileLocation), One(state.currentLocation), "Encountered event %s. Expected one of %s.",
        key, eventUnparserMap.keys.mkString(", "))
    }
    childUnparser.unparse1(state, mgrd)
  }
}

class DelimiterStackUnparser(outputNewLine: CompiledExpression,
  initiatorOpt: Option[CompiledExpression],
  separatorOpt: Option[CompiledExpression],
  terminatorOpt: Option[CompiledExpression],
  initiatorLoc: (String, String),
  separatorLocOpt: Option[(String, String)],
  terminatorLoc: (String, String),
  isLengthKindDelimited: Boolean,
  rd: RuntimeData,
  bodyUnparser: Unparser)
  extends Unparser(rd) with EvaluatesStaticDynamicTextUnparser {
  override def nom = "DelimiterStack"

  override def toBriefXML(depthLimit: Int = -1): String = {
    if (depthLimit == 0) "..." else
      "<DelimiterStack initiator='" + initiatorOpt +
        "' separator='" + separatorOpt +
        "' terminator='" + terminatorOpt + "'>" +
        bodyUnparser.toBriefXML(depthLimit - 1) +
        "</DelimiterStack>"
  }

  val (staticInits, dynamicInits) = getStaticAndDynamicText(initiatorOpt, outputNewLine, context)
  val (staticSeps, dynamicSeps) = getStaticAndDynamicText(separatorOpt, outputNewLine, context, isLengthKindDelimited)
  val (staticTerms, dynamicTerms) = getStaticAndDynamicText(terminatorOpt, outputNewLine, context, isLengthKindDelimited)

  override lazy val childProcessors: Seq[Processor] = Seq(bodyUnparser)

  def unparse(state: UState): Unit = {
    // Evaluate Delimiters
    val init = if (staticInits.isDefined) staticInits else evaluateDynamicText(dynamicInits, outputNewLine, state, context, false)
    val sep = if (staticSeps.isDefined) staticSeps else evaluateDynamicText(dynamicSeps, outputNewLine, state, context, isLengthKindDelimited)
    val term = if (staticTerms.isDefined) staticTerms else evaluateDynamicText(dynamicTerms, outputNewLine, state, context, isLengthKindDelimited)

    val node = DelimiterStackUnparseNode(init,
      sep,
      term,
      { if (!init.isDefined) Nope else One(initiatorLoc) },
      separatorLocOpt,
      { if (!term.isDefined) Nope else One(terminatorLoc) })

    state.pushDelimiters(node)

    bodyUnparser.unparse1(state, rd)

    state.popDelimiters
  }
}

class EscapeSchemeStackUnparser(escapeScheme: EscapeSchemeObject, rd: RuntimeData, bodyUnparser: Unparser)
  extends Unparser(rd) {
  override def nom = "EscapeSchemeStack"

  override lazy val childProcessors: Seq[Processor] = Seq(bodyUnparser)

  val scheme =
    {
      val isConstant = escapeScheme.escapeKind match {
        case EscapeKind.EscapeBlock => {
          (escapeScheme.optionEscapeEscapeCharacter.isEmpty ||
            escapeScheme.optionEscapeEscapeCharacter.get.isConstant)
        }
        case EscapeKind.EscapeCharacter => {
          (escapeScheme.optionEscapeCharacter.isEmpty ||
            escapeScheme.optionEscapeCharacter.get.isConstant) &&
            (escapeScheme.optionEscapeEscapeCharacter.isEmpty ||
              escapeScheme.optionEscapeEscapeCharacter.get.isConstant)
        }
      }
      val theScheme: EscapeSchemeFactoryBase = {
        if (isConstant) EscapeSchemeFactoryStatic(escapeScheme, rd)
        else EscapeSchemeFactoryDynamic(escapeScheme, rd)
      }
      theScheme
    }

  def unparse(state: UState): Unit = {
    // Evaluate
    val escScheme = scheme.getEscapeSchemeUnparser(state)

    // Set Escape Scheme
    state.currentEscapeScheme = One(escScheme)

    // Unparse
    bodyUnparser.unparse1(state, rd)

    // Clear EscapeScheme
    state.currentEscapeScheme = Nope
  }
}

class EscapeSchemeNoneStackUnparser(
  rd: RuntimeData, bodyUnparser: Unparser)
  extends Unparser(rd) {

  override def nom = "EscapeSchemeStack"

  override lazy val childProcessors = Seq(bodyUnparser)

  def unparse(state: UState): Unit = {

    // Clear Escape Scheme
    state.currentEscapeScheme = Nope

    // Unparse
    bodyUnparser.unparse1(state, rd)

    // Clear EscapeScheme
    state.currentEscapeScheme = Nope

  }
}
class ArrayCombinatorUnparser(erd: ElementRuntimeData, bodyUnparser: Unparser)
  extends TermUnparser(erd) {
  override def nom = "Array"
  override lazy val childProcessors = Seq(bodyUnparser)

  def unparse(state: UState) {
    state.arrayIndexStack.push(1L) // one-based indexing
    state.occursBoundsStack.push(DaffodilTunableParameters.maxOccursBounds)

    var event = state.advanceOrError
    Assert.invariant(event.isStart && event.node.isInstanceOf[DIArray])

    bodyUnparser.unparse1(state, erd)

    event = state.advanceOrError
    if (!(event.isEnd && event.node.isInstanceOf[DIArray])) {
      UnparseError(One(erd.schemaFileLocation), One(state.currentLocation), "Needed end of array, but found %s.", event)
    }

    val shouldValidate = state.dataProc.getValidationMode != ValidationMode.Off

    val actualOccurs = state.arrayIndexStack.pop()
    state.occursBoundsStack.pop()

    if (shouldValidate) {
      (erd.minOccurs, erd.maxOccurs) match {
        case (Some(minOccurs), Some(maxOccurs)) => {
          val isUnbounded = maxOccurs == -1
          val occurrence = actualOccurs - 1
          if (isUnbounded && occurrence < minOccurs)
            state.validationError("%s occurred '%s' times when it was expected to be a " +
              "minimum of '%s' and a maximum of 'UNBOUNDED' times.", erd.prettyName,
              occurrence, minOccurs)
          else if (!isUnbounded && (occurrence < minOccurs || occurrence > maxOccurs))
            state.validationError("%s occurred '%s' times when it was expected to be a " +
              "minimum of '%s' and a maximum of '%s' times.", erd.prettyName,
              occurrence, minOccurs, maxOccurs)
        }
        case _ => // ok
      }
    }
  }
}

class OptionalCombinatorUnparser(erd: ElementRuntimeData, bodyUnparser: Unparser) extends Unparser(erd) {
  override def nom = "Optional"
  override lazy val childProcessors = Seq(bodyUnparser)

  def unparse(state: UState) {

    state.arrayIndexStack.push(1L) // one-based indexing
    state.occursBoundsStack.push(1L)

    val event = state.inspectOrError
    Assert.invariant(event.isStart && !event.node.isInstanceOf[DIArray])

    bodyUnparser.unparse1(state, erd)

    state.arrayIndexStack.pop()
    state.occursBoundsStack.pop()
  }
}
