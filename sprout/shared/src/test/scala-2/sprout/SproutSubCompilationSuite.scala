/*
 * Copyright 2021 Loránd Szakács
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sprout

class SproutSubCompilationSuite extends munit.FunSuite {

  test("compilation — cannot assign String to TestSprout") {
    val errs = compileErrors(
      """
         type TestSprout = TestSprout.Type
         object TestSprout extends Sprout[String]

         val s: TestSprout = "342"
        """
    )

    assert(
      clue(errs.contains("type mismatch"))
        && clue(errs.contains("found   : String"))
        && clue(errs.contains("required: TestSprout")),
      clue = s"""|Actual compiler errors were (sans the -----):
                 |-----
                 |$errs
                 |-----
                 |""".stripMargin
    )
  }
}
