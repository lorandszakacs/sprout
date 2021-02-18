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

final class SproutRefinedSyncTypeSuite extends CatsEffectSuite {

  private type TestRefinedSync = TestRefinedSync.T

  private object TestRefinedSync extends SproutRefinedSyncType[String] {
    override def refine[F[_]: Sync](o: String): F[String] = Sync[F].pure(o)
  }

  test("refine string in IO") {
    val str = "testIO"
    for {
      tagged <- TestRefinedSync[IO]("testIO")
      _      <- IO(assert(TestRefinedSync.subside(tagged) == str))
    } yield ()
  }

}
