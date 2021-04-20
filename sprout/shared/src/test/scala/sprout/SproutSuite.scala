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

final class SproutSuite extends munit.FunSuite {

  private type TestSprout = TestSprout.Type

  private object TestSprout
    extends Sprout[String]
    with SproutEq[String]
    with SproutShow[String]
    with SproutOrder[String]

  private val str1 = "11111"
  private val str2 = "22222"
  private val ts1: TestSprout = TestSprout(str1)
  private val ts2: TestSprout = TestSprout(str2)

  test("instance of") {
    assert(ts1.isInstanceOf[String], "sprout was not its underlying type")
  }

  test("NewType.symbolicName") {
    val nt: NewType[String, TestSprout] = NewType[String, TestSprout]
    assertEquals(clue(nt.symbolicName), clue(TestSprout.symbolicName))
    assertEquals(clue(nt.symbolicName), clue("TestSprout"))
  }

  test("SproutEq — cats.Eq") {
    val catsEQ = cats.Eq[TestSprout]
    assert(catsEQ.eqv(ts1, ts1), s"cats.Eq.eqv($ts1, $ts1)")
  }

  test("SproutOrder — cats.Order") {
    val catsOrdStr = cats.Order[String]
    val catsOrd    = cats.Order[TestSprout]

    assertEquals(catsOrd.compare(ts1, ts1), catsOrdStr.compare(str1, str1))
    assertEquals(catsOrd.compare(ts1, ts2), catsOrdStr.compare(str1, str2))
    assertEquals(catsOrd.compare(ts2, ts1), catsOrdStr.compare(str2, str1))
  }

  test("SproutOrder — scala.math.Ordering") {
    val scalaOrdStr = scala.math.Ordering[String]
    val scalaOrd    = scala.math.Ordering[TestSprout]

    assertEquals(scalaOrd.compare(ts1, ts1), scalaOrdStr.compare(str1, str1))
    assertEquals(scalaOrd.compare(ts1, ts2), scalaOrdStr.compare(str1, str2))
    assertEquals(scalaOrd.compare(ts2, ts1), scalaOrdStr.compare(str2, str1))
  }

  test("SproutEq — scala.math.Equiv") {
    val scalaMEquiv = scala.math.Equiv[TestSprout]
    assert(scalaMEquiv.equiv(ts1, ts1))
  }

  test("SproutEq — scala strictEquality — keep in mind this project is compiled w/ flag -language:strictEquality") {
    assert(ts1 == ts1)
  }

  test("SproutShow") {
    val str  = "testing eq"
    val show = cats.Show[TestSprout]
    assertEquals(str, show.show(TestSprout(str)))
  }

}
