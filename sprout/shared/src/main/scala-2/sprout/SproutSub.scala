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

trait SproutSub[O] extends Burry[O] with TagSubType[O] {

  def apply(o: O): Type = tagOld(o)

  @inline final def newType(o: O): Type = newTypeInstance.newType(o)

  implicit def newTypeInstance: NewType[O, Type] = _defaultNewType

  private lazy val _defaultNewType: NewType[O, Type] = new NewType[O, Type] {
    @inline override def newType(o: O):    Type = apply(o)
    @inline override def oldType(n: Type): O    = SproutSub.this.oldType(n)
    override val symbolicName: String = SproutSub.this.symbolicName
  }
}
