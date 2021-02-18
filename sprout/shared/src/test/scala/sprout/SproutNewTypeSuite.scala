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

import cats.effect.*
import cats.effect.std.*
import munit.CatsEffectSuite

final class SproutNewTypeSuite extends CatsEffectSuite {

  private type TestSprout = TestSprout.T
  private object TestSprout
    extends SproutNewType[String]
    with SproutNewType.SproutShow[String]
    with SproutNewType.SproutEq[String]

  test("sprout Eq") {
    val str = "testing eq"
    val eq  = cats.Eq[TestSprout]
    IO(
      assert(eq.eqv(TestSprout(str), TestSprout(str)))
    )
  }

  test("sprout Show") {
    val str  = "testing eq"
    val show = cats.Show[TestSprout]
    IO(
      assert(str == show.show(TestSprout(str)))
    )
  }

}
