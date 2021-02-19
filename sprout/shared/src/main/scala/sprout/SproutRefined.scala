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

import cats.MonadError

trait SproutRefined[O, E] {
  opaque type Type = O
  type OldType = O

  def refine[F[_]](o: O)(using m: MonadError[F, E]): F[O] 
  
  final def apply[F[_]](o: O)(using m: MonadError[F, E]): F[Type] = refine[F](o)
  @inline final def newType[F[_]](o: O)(using m: MonadError[F, E]): F[Type] = newTypeInstance.newType(o)
  @inline final def oldType(n: Type): O = newTypeInstance.oldType(n)

  given newTypeInstance: RefinedType[O, Type, E] = _defaultRefinedType

  /**
   * Used for better error messages, and certain integrations.
   */
  protected def typeName: String = this.getClass.getSimpleName.stripSuffix("$")

  private lazy val _defaultRefinedType: RefinedType[O, Type, E] = new RefinedType[O, Type, E] {
    @inline override def newType[F[_]](o: O)(using m: MonadError[F, E]): F[Type] = apply[F](o)
    @inline override def oldType(n: Type): O = n
    override val symbolicName: String = typeName.stripSuffix("$")
  }
}
