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

//===========================================================================

trait SproutEq[OT](using ot: cats.Eq[OT]) { this: Burry[OT] =>

  given eqNewType: cats.Eq[this.Type] = new cats.Eq[this.Type] {

    override def eqv(x: SproutEq.this.Type, y: SproutEq.this.Type): Boolean =
      ot.eqv(SproutEq.this.oldType(x), SproutEq.this.oldType(y))
  }

  given scalaEqNewType: scala.math.Equiv[this.Type] = new scala.math.Equiv[this.Type] {
    override def equiv(x: SproutEq.this.Type, y: SproutEq.this.Type): Boolean = eqNewType.eqv(x, y)
  }

  given scalaCanEqualType: scala.CanEqual[this.Type, this.Type] = scala.CanEqual.derived
}

//===========================================================================

trait SproutOrder[OT](using co: cats.Order[OT]) { this: Burry[OT] =>

  given orderNewType: cats.Order[this.Type] = new cats.Order[this.Type] {

    override def compare(x: SproutOrder.this.Type, y: SproutOrder.this.Type): Int =
      co.compare(SproutOrder.this.oldType(x), SproutOrder.this.oldType(y))
  }

  given scalaOrderingNewType: scala.math.Ordering[this.Type] = new scala.math.Ordering[this.Type] {
    override def compare(x: SproutOrder.this.Type, y: SproutOrder.this.Type): Int = orderNewType.compare(x, y)
  }

}

//===========================================================================

trait SproutShow[OT](using ot: cats.Show[OT]) { this: Burry[OT] =>

  given showNewType: cats.Show[this.Type] = new cats.Show[this.Type] {
    override def show(t: SproutShow.this.Type): String = ot.show(SproutShow.this.oldType(t))
  }
}
