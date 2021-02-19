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

trait Sprout[O] {
  opaque type Type = O
  type OldType = O
  
  def apply(o: O): Type = o
  
  @inline final def newType(o: O): Type = newTypeInstance.newType(o)
  @inline final def oldType(n: Type): O = newTypeInstance.oldType(n)

  given newTypeInstance: NewType[O, Type] = _defaultNewType

  /**
   * Used for better error messages, and certain integrations.
   */
  protected def typeName: String = this.getClass.getSimpleName.stripSuffix("$")
  
  private lazy val _defaultNewType: NewType[O, Type] = new NewType[O, Type] {
    @inline override def newType(o: O): Type = apply(o)
    @inline override def oldType(n: Type): O = n
    override val symbolicName: String = typeName.stripSuffix("$")
  }
}

object Sprout {
  
  //===========================================================================
  
  trait Eq[OldType](using ot: cats.Eq[OldType]) { this: Sprout[OldType] =>
    
    given eqNewType: cats.Eq[this.Type] = new cats.Eq[this.Type] {
      override def eqv(x: Eq.this.Type, y: Eq.this.Type): Boolean = ot.eqv(Eq.this.oldType(x), Eq.this.oldType(y))
    }
    
    given scalaEqNewType: scala.math.Equiv[this.Type] = new scala.math.Equiv[this.Type] {
      override def equiv(x: Eq.this.Type, y: Eq.this.Type): Boolean = eqNewType.eqv(x, y)
    }
    
    given scalaCanEqualType: scala.CanEqual[this.Type, this.Type] = scala.CanEqual.derived
  }

  //===========================================================================
  
  trait Order[OldType](using co: cats.Order[OldType]){this: Sprout[OldType] =>
    given orderNewType: cats.Order[this.Type] = new cats.Order[this.Type] {
      override def compare(x: Order.this.Type, y: Order.this.Type): Int = co.compare(Order.this.oldType(x), Order.this.oldType(y))
    }

    given scalaOrderingNewType: scala.math.Ordering[this.Type] = new scala.math.Ordering[this.Type] {
      override def compare(x: Order.this.Type, y: Order.this.Type): Int = orderNewType.compare(x, y)
    }
    
  }

  //===========================================================================
  
  trait Show[OldType](using ot: cats.Show[OldType]) { this: Sprout[OldType] =>
    given showNewType: cats.Show[this.Type] = new cats.Show[this.Type] {
      override def show(t: Show.this.Type): String = ot.show(Show.this.oldType(t))
    }
  }
  
}
