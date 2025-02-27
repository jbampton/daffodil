/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.daffodil.unparsing

import org.junit.Assert._
import org.junit.Test
import java.io.File
import org.apache.daffodil.CLI.Util
import net.sf.expectit.matcher.Matchers.contains
import net.sf.expectit.matcher.Matchers.eof
import org.apache.daffodil.Main.ExitCode

class TestCLIunparsing {

  @Test def test_3525_CLI_Unparsing_SimpleUnparse_inFile(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input12.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3526_CLI_Unparsing_SimpleUnparse_inFile2(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input13.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -s %s --root e3 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("[1,2]"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3527_CLI_Unparsing_SimpleUnparse_stdin(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input14.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
	  val cmd = String.format(Util.cat(testInputFile) + "| %s unparse -s %s --root e3", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("[1,2]"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3528_CLI_Unparsing_SimpleUnparse_stdin2(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {
      val cmd = String.format(Util.echoN("\"<tns:e1 xmlns:tns='http://example.com'>Hello</tns:e1>\"") + "| %s unparse -s %s --root e1", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3529_CLI_Unparsing_SimpleUnparse_stdin3(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {
      val cmd = String.format(Util.echoN("\"<tns:e1 xmlns:tns='http://example.com'>Hello</tns:e1>\"") + "| %s unparse -s %s --root e1 -", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3584_CLI_Unparsing_SimpleUnparse_stdin4(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section06/entities/charClassEntities.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {
      val input = "\"<tns:file xmlns:tns='http://www.example.org/example1/'><tns:header><tns:title>1</tns:title><tns:title>2</tns:title><tns:title>3</tns:title></tns:header><tns:record><tns:item>4</tns:item><tns:item>5</tns:item><tns:item>6</tns:item></tns:record></tns:file>\""
      val cmd = String.format(Util.echoN(input) + "| %s unparse -s %s --root file", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("1,2,3"))
      shell.expect(contains("4,5,6"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3574_CLI_Unparsing_SimpleUnparse_extVars(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section07/external_variables/external_variables.dfdl.xsd")
    val configFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section07/external_variables/daffodil_config_cli_test.xml")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input15.txt")
    val (testSchemaFile, testConfigFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(configFile), Util.cmdConvert(inputFile)) else (schemaFile, configFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -s %s -r row -D\"{http://example.com}var1=99\" -c %s %s", Util.binPath, testSchemaFile, testConfigFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("0"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3575_CLI_Unparsing_SimpleUnparse_extVars2(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section07/external_variables/external_variables.dfdl.xsd")
    val configFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section07/external_variables/daffodil_config_cli_test.xml")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input16.txt")
    val (testSchemaFile, testConfigFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(configFile), Util.cmdConvert(inputFile)) else (schemaFile, configFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -s %s -r row -c %s %s", Util.binPath, testSchemaFile, testConfigFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("0"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3582_CLI_Unparsing_SimpleUnparse_outFile(): Unit = {
    val tmp_filename: String = (System.currentTimeMillis / 1000).toString()
    val file = new File(tmp_filename)
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input13.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)
    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -s %s -r e3 -o %s %s", Util.binPath, testSchemaFile, tmp_filename, testInputFile)
      shell.sendLine(cmd)

      val catCmd = if (Util.isWindows) "type" else "cat"
      val openCmd = String.format("%s %s", catCmd, tmp_filename)

      shell.sendLine(openCmd)
      shell.expect(contains("[1,2]"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
      assertTrue("Failed to remove temporary file: %s".format(file), file.delete)
    }
  }

  @Test def test_3581_CLI_Unparsing_SimpleUnparse_stOutDash(): Unit = {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input13.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)
    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -s %s -r e3 -o - %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)

      shell.expect(contains("[1,2]"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  @Test def test_3580_CLI_Unparsing_SimpleUnparse_verboseMode(): Unit = {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {

      shell.sendLine(String.format(Util.echoN("\"<tns:e1 xmlns:tns='http://example.com'>Hello</tns:e1>\"") + "| %s -v unparse -s %s --root e1", Util.binPath, testSchemaFile))
      shell.expectIn(1, contains("[info]"))

      shell.sendLine(String.format(Util.echoN("\"<tns:e1 xmlns:tns='http://example.com'>Hello</tns:e1>\"") + "| %s -vv unparse -s %s --root e1", Util.binPath, testSchemaFile))
      shell.expectIn(1, contains("[debug]"))

      shell.sendLine(String.format(Util.echoN("\"<tns:e1 xmlns:tns='http://example.com'>Hello</tns:e1>\"") + "| %s -vvv unparse -s %s --root e1", Util.binPath, testSchemaFile))
      shell.expectIn(1, contains("[trace]"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  @Test def test_3579_CLI_Unparsing_negativeTest(): Unit = {
    val shell = Util.start("")

    try {
      val cmd = String.format(Util.echoN("\"<tns:e1 xmlns:tns='http://example.com'>Hello</tns:e1>\"") + "| %s unparse", Util.binPath)
      shell.sendLine(cmd)
      shell.expectIn(1, contains("There should be exactly one of the following options: schema, parser"))

      Util.expectExitCode(ExitCode.Usage, shell)
      shell.send("exit\n")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  @Test def test_3578_CLI_Unparsing_SimpleUnparse_defaultRoot(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val (testSchemaFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile)) else (schemaFile)

    val shell = Util.start("")

    try {
      val cmd = String.format(Util.echoN("\"<tns:e1 xmlns:tns='http://example.com'>Hello</tns:e1>\"") + "| %s unparse -s %s", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_3583_CLI_Unparsing_SimpleUnparse_rootPath(): Unit = {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section06/entities/charClassEntities.dfdl.xsd")
    val testSchemaFile = if (Util.isWindows) Util.cmdConvert(schemaFile) else schemaFile

    val shell = Util.start("")

    try {
      val cmd = String.format(Util.echoN("\"<tns:hcp2 xmlns:tns='http://www.example.org/example1/'>12</tns:hcp2>\"") + "| %s unparse -s %s -r hcp2 -p /", Util.binPath, testSchemaFile)

      shell.sendLine(cmd)
      shell.expect(contains("12"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  /*
  // See DFDL-1346
  @Test def test_3576_CLI_Unparsing_validate() {

    val schemaFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/cli_schema.dfdl.xsd")
    val testSchemaFile = if (Util.isWindows) Util.cmdConvert(schemaFile) else schemaFile

    val shell = Util.start("")

    try {
      val cmd = String.format("""echo '<ex:validation_check xmlns:ex="http://example.com">test</ex:validation_check>' | %s unparse -s %s -r validation_check --validate on """, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("[warn] Validation Error: validation_check: cvc-pattern-valid"))
      shell.expect(contains("[warn] Validation Error: validation_check failed"))

      cmd = String.format("""echo '<ex:validation_check xmlns:ex="http://example.com">test</ex:validation_check>' | %s unparse -s %s -r validation_check --validate """, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("[warn] Validation Error: validation_check: cvc-pattern-valid"))
      shell.expect(contains("[warn] Validation Error: validation_check failed"))

      cmd = String.format("""echo '<ex:validation_check xmlns:ex="http://example.com">test</ex:validation_check>' | %s unparse -s %s -r validation_check --validate limited """, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("[warn] Validation Error: validation_check failed"))

      cmd = String.format("""echo '<ex:validation_check xmlns:ex="http://example.com">test</ex:validation_check>' | %s unparse -s %s -r validation_check --validate off """, Util.binPath, testSchemaFile)
      shell.sendLine(cmd)

      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }
*/

/*
  @Test def test_3577_CLI_Unparsing_traceMode() {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section06/namespaces/multi_base_15.dfdl.xsd")
    val testSchemaFile = if (Util.isWindows) Util.cmdConvert(schemaFile) else schemaFile
    val shell = Util.start("")

    try {
      val cmd = String.format("echo '<rabbitHole><nestSequence><nest>test</nest></nestSequence></rabbitHole>'| %s -t unparse -s %s", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expect(contains("parser: <Element name='nest'>"))
      shell.expect(contains("parser: <Element name='rabbitHole'>"))
      shell.expect(contains("test"))
      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }
*/

  @Test def test_3662_CLI_Unparsing_badSchemaPath(): Unit = {
    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section06/entities/doesnotexist.dfdl.xsd")
    val testSchemaFile = if (Util.isWindows) Util.cmdConvert(schemaFile) else schemaFile

    val shell = Util.start("")

    try {
      val cmd = String.format(Util.echoN("\"<tns:e1>Hello</tns:e1>\"") + "| %s unparse -s %s -r root", Util.binPath, testSchemaFile)
      shell.sendLine(cmd)
      shell.expectIn(1, contains("Bad arguments for option 'schema'"))
      shell.expectIn(1, contains("Could not find file or resource"))

      Util.expectExitCode(ExitCode.Usage, shell)
      shell.sendLine("exit")
      shell.expect(eof)
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_w3cdom(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -I w3cdom -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_jdom(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -I jdom -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_scala_xml(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -I scala-xml -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_json(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.json")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -I json -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_w3cdom_stream(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse --stream -I w3cdom -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_jdom_stream(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse --stream -I jdom -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_scala_xml_stream(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse --stream -I scala-xml -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_json_stream(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.json")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse --stream -I json -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_sax(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -I sax -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_xxxx_CLI_Unparsing_SimpleUnparse_null(): Unit = {

    val schemaFile = Util.daffodilPath("daffodil-test/src/test/resources/org/apache/daffodil/section00/general/generalSchema.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input18.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse -I null -s %s --root e1 %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("Hello"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
      shell.expect(eof)
      shell.close()
    } finally {
      shell.close()
    }
  }

  @Test def test_XXX_CLI_Unparsing_Stream_01(): Unit = {
    val schemaFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/cli_schema_02.dfdl.xsd")
    val inputFile = Util.daffodilPath("daffodil-cli/src/it/resources/org/apache/daffodil/CLI/input/input19.txt")
    val (testSchemaFile, testInputFile) = if (Util.isWindows) (Util.cmdConvert(schemaFile), Util.cmdConvert(inputFile)) else (schemaFile, inputFile)

    val shell = Util.start("")

    try {
      val cmd = String.format("%s unparse --stream -s %s %s", Util.binPath, testSchemaFile, testInputFile)
      shell.sendLine(cmd)
      shell.expect(contains("123"))

      Util.expectExitCode(ExitCode.Success, shell)
      shell.send("exit\n")
    } finally {
      shell.close()
    }
  }


}
