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

import scala.util.Try
import cats.MonadThrow

final class SproutRefinedSuite extends munit.FunSuite {

  private val errMessage = "Invalid sprout string"
  private type TestSprout = TestSprout.Type

  private object TestSprout
    extends SproutRefinedThrow[String]
    with SproutEq[String]
    with SproutShow[String]
    with SproutOrder[String] {

    override def refine[F[_]: MonadThrow](o: String): F[String] =
      if (o.contains("sprout")) MonadThrow[F].pure(o)
      else MonadThrow[F].raiseError[String](new RuntimeException(errMessage))
  }

  private val str1 = "11111-sprout"
  private val str2 = "22222-sprout"
  private val str3 = "33333-failed"
  private val ts1F: Try[TestSprout] = TestSprout[Try](str1)
  private val ts2F: Try[TestSprout] = TestSprout[Try](str2)
  private val ts3F: Try[TestSprout] = TestSprout[Try](str3)

  test("instance of") {
    ts1F.map(ts1 => assert(ts1.isInstanceOf[String], "sprout was not its underlying type")).get
  }

  test("RefinedType.symbolicName") {
    val nt: RefinedTypeThrow[String, TestSprout] = RefinedTypeThrow[String, TestSprout]
    assertEquals(clue(nt.symbolicName), clue(TestSprout.symbolicName))
    assertEquals(clue(nt.symbolicName), clue("TestSprout"))
  }

  test("SproutEq — cats.Eq") {
    val catsEQ = cats.Eq[TestSprout]
    ts1F.map(ts1 => assert(catsEQ.eqv(ts1, ts1), s"cats.Eq.eqv($ts1, $ts1)")).get
  }

  test("SproutOrder — cats.Order") {
    val catsOrdStr = cats.Order[String]
    val catsOrd    = cats.Order[TestSprout]
    val t          = for {
      ts1 <- ts1F
      ts2 <- ts2F
      _ = assertEquals(catsOrd.compare(ts1, ts1), catsOrdStr.compare(str1, str1))
      _ = assertEquals(catsOrd.compare(ts1, ts2), catsOrdStr.compare(str1, str2))
      _ = assertEquals(catsOrd.compare(ts2, ts1), catsOrdStr.compare(str2, str1))
    } yield ()
    t.get
  }

  test("SproutOrder — scala.math.Ordering") {
    val scalaOrdStr = scala.math.Ordering[String]
    val scalaOrd    = scala.math.Ordering[TestSprout]

    for {
      ts1 <- ts1F
      ts2 <- ts2F
      _ = assertEquals(scalaOrd.compare(ts1, ts1), scalaOrdStr.compare(str1, str1))
      _ = assertEquals(scalaOrd.compare(ts1, ts2), scalaOrdStr.compare(str1, str2))
      _ = assertEquals(scalaOrd.compare(ts2, ts1), scalaOrdStr.compare(str2, str1))
    } yield ()
  }

  test("SproutEq — scala.math.Equiv") {
    val scalaMEquiv = scala.math.Equiv[TestSprout]
    ts1F.map(ts1 => assert(scalaMEquiv.equiv(ts1, ts1))).get
  }

  test("SproutEq — scala strictEquality — keep in mind this project is compiled w/ flag -language:strictEquality") {
    ts1F.map(ts1 => assert(ts1 == ts1)).get
  }

  test("SproutShow") {
    val str  = "testing-show-sprout"
    val show = cats.Show[TestSprout]
    val t    = for {
      ts <- TestSprout[Try](str)
    } yield assert(str == show.show(ts))
    t.get
  }

  test("SproutRefined — failure — TestSprout.apply") {
    interceptMessage[RuntimeException](errMessage)(ts3F.get)
  }

  test("SproutRefined — failure — RefinedTypeThrow.newType") {
    val rf: RefinedTypeThrow[String, TestSprout] = RefinedTypeThrow[String, TestSprout]
    interceptMessage[RuntimeException](errMessage)(rf.newType[Try](str3).get)
  }

  test("RefinedTypeThrow — symbolicName") {
    val rf: RefinedTypeThrow[String, TestSprout] = RefinedTypeThrow[String, TestSprout]
    val sn1 = rf.symbolicName
    val sn2 = TestSprout.symbolicName
    assertEquals(sn1, sn2)
  }
}
