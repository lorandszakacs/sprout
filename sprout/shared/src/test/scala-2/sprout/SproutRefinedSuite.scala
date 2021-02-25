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
import cats.MonadError
import cats.effect._
import cats.effect.std._
import munit.CatsEffectSuite

final class SproutRefinedSuite extends CatsEffectSuite {

  private type TestSprout = TestSprout.Type

  private object TestSprout
    extends SproutRefinedThrow[String]
    with SproutEq[String]
    with SproutShow[String]
    with SproutOrder[String] {

    override def refine[F[_]: MonadThrow](o: String): F[String] =
      if (o.contains("sprout")) o.pure[F] else new RuntimeException("Invalid sprout string").raiseError[F, String]
  }

  private val str1 = "11111-sprout"
  private val str2 = "22222-sprout"
  private val str3 = "33333"
  private val ts1IO: IO[TestSprout] = TestSprout[IO](str1)
  private val ts2IO: IO[TestSprout] = TestSprout[IO](str2)
  private val ts3IO: IO[TestSprout] = TestSprout[IO](str3)

  test("instance of") {
    for {
      ts1 <- ts1IO
    } yield assert(ts1.isInstanceOf[String])
  }

  test("RefinedType.symbolicName") {
    val nt: RefinedTypeThrow[String, TestSprout] = RefinedTypeThrow[String, TestSprout]
    for {
      _ <- IO(assert(clue(nt.symbolicName) == clue(TestSprout.symbolicName)))
      _ <- IO(assert(clue(nt.symbolicName) == clue("TestSprout")))
    } yield ()
  }

  test("SproutEq -- cats.Eq") {
    val catsEQ = cats.Eq[TestSprout]
    for {
      ts1 <- ts1IO
    } yield assert(catsEQ.eqv(ts1, ts1))
  }

  test("SproutOrder -- cats.Order") {
    val catsOrdStr = cats.Order[String]
    val catsOrd    = cats.Order[TestSprout]
    for {
      ts1 <- ts1IO
      ts2 <- ts2IO
      _   <- IO(assert(catsOrd.compare(ts1, ts1) == catsOrdStr.compare(str1, str1)))
      _   <- IO(assert(catsOrd.compare(ts1, ts2) == catsOrdStr.compare(str1, str2)))
      _   <- IO(assert(catsOrd.compare(ts2, ts1) == catsOrdStr.compare(str2, str1)))
    } yield ()
  }

  test("SproutOrder -- scala.math.Ordering") {
    val scalaOrdStr = scala.math.Ordering[String]
    val scalaOrd    = scala.math.Ordering[TestSprout]

    for {
      ts1 <- ts1IO
      ts2 <- ts2IO
      _   <- IO(assert(scalaOrd.compare(ts1, ts1) == scalaOrdStr.compare(str1, str1)))
      _   <- IO(assert(scalaOrd.compare(ts1, ts2) == scalaOrdStr.compare(str1, str2)))
      _   <- IO(assert(scalaOrd.compare(ts2, ts1) == scalaOrdStr.compare(str2, str1)))
    } yield ()
  }

  test("SproutEq -- scala.math.Equiv") {
    val scalaMEquiv = scala.math.Equiv[TestSprout]
    for {
      ts1 <- ts1IO
    } yield assert(scalaMEquiv.equiv(ts1, ts1))
  }

  test("SproutEq -- scala strictEquality -- keep in mind this project is compiled w/ flag -language:strictEquality") {
    for {
      ts1 <- ts1IO
    } yield assert(ts1 == ts1)
  }

  test("SproutShow") {
    val str  = "testing-show-sprout"
    val show = cats.Show[TestSprout]
    for {
      ts <- TestSprout[IO](str)
    } yield assert(str == show.show(ts))
  }

  test("SproutRefined -- failure -- TestSprout.apply") {
    for {
      attempt <- ts3IO.attempt
    } yield assert(attempt.isLeft)
  }

  test("SproutRefined -- failure -- RefinedTypeThrow.newType") {
    val rf: RefinedTypeThrow[String, TestSprout] = RefinedTypeThrow[String, TestSprout]
    for {
      attempt <- rf.newType[IO](str3).attempt
    } yield assert(attempt.isLeft)
  }

  test("RefinedTypeThrow -- symbolicName") {
    val rf: RefinedTypeThrow[String, TestSprout] = RefinedTypeThrow[String, TestSprout]
    val sn1 = rf.symbolicName
    val sn2 = TestSprout.symbolicName
    IO(assert(sn1 == sn2))
  }
}
