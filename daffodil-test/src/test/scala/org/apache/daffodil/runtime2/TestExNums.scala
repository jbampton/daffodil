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

package org.apache.daffodil.runtime2

import org.junit.Test
import org.apache.daffodil.tdml.Runner
import org.junit.AfterClass

object TestExNums {
  val testDir = "/org/apache/daffodil/runtime2/"
  val runner = Runner(testDir, "ex_nums.tdml")

  @AfterClass def shutDown(): Unit = { runner.reset }
}

class TestExNums {
  import TestExNums._

  @Test def test_ex_nums_runtime1(): Unit = { runner.runOneTest("ex_nums_runtime1") }
  @Test def test_ex_nums_runtime1_error(): Unit = { runner.runOneTest("ex_nums_runtime1_error") }
  @Test def test_ex_nums_runtime2(): Unit = { runner.runOneTest("ex_nums_runtime2") }
  @Test def test_ex_nums_runtime2_error(): Unit = { runner.runOneTest("ex_nums_runtime2_error") }
}
