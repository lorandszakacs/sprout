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

import cats.effect.Sync

trait RefinedSyncType[OldType, NewType] {
  @inline final def apply[F[_]: Sync](o: OldType): F[NewType] = newType[F](o)
  @inline final def sprout[F[_]: Sync](o: OldType): F[NewType] = newType[F](o)
  @inline final def subside(n: NewType): OldType = oldType(n)
  
  @inline def refine[F[_]: Sync](o: OldType): F[OldType]
  @inline def newType[F[_]: Sync](o: OldType): F[NewType]
  @inline def oldType(n: NewType): OldType
}

trait SproutRefinedSyncType[OldType] {
  opaque type T = OldType
  type Underlying = OldType

  import cats.implicits.*
  
  def refine[F[_]: Sync](o: OldType): F[OldType]

  @inline final def newType[F[_]: Sync](o: OldType): F[T] = refine(o).map((ot: OldType) => ot: T)
  @inline final def apply[F[_]: Sync](o: OldType): F[T] = newType[F](o)
  @inline final def sprout[F[_]: Sync](o: OldType): F[T] = newType[F](o)
  @inline final def subside(n: T): OldType = oldType(n)
  @inline final def oldType(n: T): OldType = n
  
  given refinedSyncTypeInstance: RefinedSyncType[OldType, T] = _defaultRefinedSyncType

  private lazy val _defaultRefinedSyncType: RefinedSyncType[OldType, T] = new RefinedSyncType[OldType, T] {
    @inline override def refine[F[_]: Sync](o: OldType): F[OldType] = SproutRefinedSyncType.this.newType[F](o)
    @inline override def newType[F[_]: Sync](o: OldType): F[T] = SproutRefinedSyncType.this.newType[F](o)
    @inline override def oldType(n: T): OldType = n
  }
}

object SproutRefinedSyncType {
}

