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

/** Intended to be mixed into an object, or in certain cases a class,
  * for them to act as the containers and constructors for your
  * new type.
  *
  * e.g.
  *
  * {{{
  *   object FirstName extends Sprout[String]
  *   type FirstName = FirstName.Type
  * }}}
  * @tparam O
  */
trait Sprout[O] extends TagNewType[O] with Burry[O] {

  def apply(o:                 O): Type = tagOld(o)
  @inline final def newType(o: O): Type = newTypeInstance.newType(o)

  implicit def newTypeInstance: NewType[O, Type] = _defaultNewType

  private lazy val _defaultNewType: NewType[O, Type] = new NewType[O, Type] {
    @inline override def newType(o: O):    Type = apply(o)
    @inline override def oldType(n: Type): O    = Sprout.this.oldType(n)
    override val symbolicName: String = Sprout.this.symbolicName
  }
}
