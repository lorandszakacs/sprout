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

/** The purpose of this type class is to give further application code a way to talk about this "new type" encoding. So
  * that converting to and from the underlying type can be done with as little boilerplate as possible in inter-op with
  * 3rd party libraries, and other classes.
  *
  * @see
  *   [[sprout.SproutShow]] as an example. We can easily provide a Show instance for N if the underlying representation
  *   has one. And it's opt in.
  *
  * This principle is then extended to a host of 3rd party libraries to eliminate boilerplate. See the sprout-interop
  * github repo for many other examples.
  *
  * @tparam O
  *   O for old type. i.e. the underlying runtime type
  * @tparam N
  *   N for new type. i.e. the type that ought to live only in the compiler
  */
trait NewType[O, N] extends OldType[O, N] {
  @inline def newType(o: O): N
}

object NewType {
  @inline def apply[O, N](implicit i: NewType[O, N]): NewType[O, N] = i
}
