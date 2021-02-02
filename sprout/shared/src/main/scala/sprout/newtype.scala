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

trait NewType[OldType, NewType] {
  @inline final def apply(o: OldType): NewType = sprout(o)
  @inline final def sprout(o: OldType): NewType = newType(o)
  @inline final def subside(n: NewType): OldType = oldType(n)
  
  @inline def newType(o: OldType): NewType
  @inline def oldType(n: NewType): OldType
}

trait SproutNewType[OldType] {
  opaque type T = OldType
  type Underlying = OldType

  @inline def apply(o: OldType): T = newTypeInstance.apply(o)
  @inline def sprout(o: OldType): T = newTypeInstance.sprout(o)
  @inline def subside(n: T): OldType = newTypeInstance.subside(n)
  @inline def newType(o: OldType): T = newTypeInstance.newType(o)
  @inline def oldType(n: T): OldType = newTypeInstance.oldType(n)

  given newTypeInstance: NewType[OldType, T] = _defaultNewType
  
  private lazy val _defaultNewType: NewType[OldType, T] = new NewType[OldType, T] {
    @inline override def newType(o: OldType): T = o
    @inline override def oldType(n: T): OldType = n
  }
}

object SproutNewType {
  import cats.Eq

  trait SproutEq[OldType](using ot: Eq[OldType]) { this: SproutNewType[OldType] =>
    given eqNewType: Eq[this.T] = new Eq[this.T] {
      override def eqv(x: SproutEq.this.T, y: SproutEq.this.T): Boolean = ot.eqv(SproutEq.this.subside(x), SproutEq.this.subside(y))
    }
  }

  import cats.Show

  trait SproutShow[OldType](using ot: Show[OldType]) { this: SproutNewType[OldType] =>
    given showNewType: Show[this.T] = new Show[this.T] {
      override def show(a: SproutShow.this.T): String = ot.show(SproutShow.this.newTypeInstance.subside(a))
    }
  }


}
