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

/** @see [[sprout.NewType]]
  * This typeclass is usefull for defining the contravariant
  * functors. e.g. io.circe.Encoder for your new types.
  */
trait OldType[O, N] {
  @inline def oldType(n: N): O

 /**
   * Ought to be used only for pretty printing and debug messages,
   * not intented to represent extremly precise and consistent
   * values that can be relied on in mission critical code
   * 
   * @return
   * e.g.
   * ``
   *  object TestValue extends Sprout[Int] //which extends OldType
   *  type TestValue = TestValue.Type
   * ``
   * returns TestValue
   */
  def symbolicName: String
}

object OldType {
  @inline def apply[O, N](implicit i: OldType[O, N]): OldType[O, N] = i
}
