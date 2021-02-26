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

import cats.implicits._
import cats.effect._
import munit.CatsEffectSuite

final class SproutSuite extends CatsEffectSuite {

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

  test("compilation — cannot assign TestSprout to String") {
    val errs = compileErrors(
      """
         val s: TestSprout = ???
         val x: String = s
        """
    )
    IO.println(errs) >>
      IO(
        assert(
          clue(errs.contains("type mismatch"))
            && clue(errs.contains("found   : SproutSuite.this.TestSprout"))
            && clue(errs.contains("required: String"))
        )
      )
  }

  test("compilation — cannot assign String to TestSprout") {
    val errs = compileErrors(
      """
         val s: TestSprout = "342"
        """
    )
    IO.println(errs) >>
      IO(
        assert(
          clue(errs.contains("type mismatch"))
            && clue(errs.contains("found   : String"))
            && clue(errs.contains("required: SproutSuite.this.TestSprout"))
        )
      )
  }

  test("instance of") {
    IO(assert(ts1.isInstanceOf[String]))
  }

  test("NewType.symbolicName") {
    val nt: NewType[String, TestSprout] = NewType[String, TestSprout]
    for {
      _ <- IO(assert(clue(nt.symbolicName) == clue(TestSprout.symbolicName)))
      _ <- IO(assert(clue(nt.symbolicName) == clue("TestSprout")))
    } yield ()
  }

  test("SproutEq — cats.Eq") {
    val catsEQ = cats.Eq[TestSprout]

    IO(assert(catsEQ.eqv(ts1, ts1)))
  }

  test("SproutOrder — cats.Order") {
    val catsOrdStr = cats.Order[String]
    val catsOrd    = cats.Order[TestSprout]

    for {
      _ <- IO(assert(catsOrd.compare(ts1, ts1) == catsOrdStr.compare(str1, str1)))
      _ <- IO(assert(catsOrd.compare(ts1, ts2) == catsOrdStr.compare(str1, str2)))
      _ <- IO(assert(catsOrd.compare(ts2, ts1) == catsOrdStr.compare(str2, str1)))
    } yield ()
  }

  test("SproutOrder — scala.math.Ordering") {
    val scalaOrdStr = scala.math.Ordering[String]
    val scalaOrd    = scala.math.Ordering[TestSprout]

    for {
      _ <- IO(assert(scalaOrd.compare(ts1, ts1) == scalaOrdStr.compare(str1, str1)))
      _ <- IO(assert(scalaOrd.compare(ts1, ts2) == scalaOrdStr.compare(str1, str2)))
      _ <- IO(assert(scalaOrd.compare(ts2, ts1) == scalaOrdStr.compare(str2, str1)))
    } yield ()
  }

  test("SproutEq — scala.math.Equiv") {
    val scalaMEquiv = scala.math.Equiv[TestSprout]
    IO(assert(scalaMEquiv.equiv(ts1, ts1)))
  }

  test("SproutEq — scala strictEquality — keep in mind this project is compiled w/ flag -language:strictEquality") {
    IO(assert(ts1 == ts1))
  }

  test("SproutShow") {
    val str  = "testing eq"
    val show = cats.Show[TestSprout]
    IO(assert(str == show.show(TestSprout(str))))
  }

}
