# ScalaDynamicMixin

This [DTrait](src/main/scala/dynamic/mixin/DTrait.scala) enables dynamically mixin new class with its functions into existing instance extends DTrait during running time.

## Sample Code

Start from [test case](src/test/scala/dynamic/mixin/DTraitTest.scala) is always good idea.

```scala

object CGroup {
  class C extends DTrait {
    def funC(): String = "function C"
  }
  object C extends DTraitExtractor[C]

  import scala.language.implicitConversions
  implicit def DTrait2C(d: DTrait): C = d.as[C]
}

object DGroup {
  class D extends DTrait {
    def funD(): String = "function D"
  }
  object D extends DTraitExtractor[D]

  import scala.language.implicitConversions
  implicit def DTrait2D(d: DTrait): D = d.as[D]
}

import CGroup._
import DGroup._

val cd: DTrait = (new C).wrap[D]
val dc: DTrait = (new D).wrap[C]

cd.funC shouldBe "function C"
dc.funC shouldBe "function C"

cd.funD shouldBe "function D"
dc.funD shouldBe "function D"

val inputs = List(new C, new D, new D, new C)
val outputs = inputs map {
  case C(c) => c.funC
  case D(d) => d.funD
}

outputs(0) shouldBe "function C"
outputs(1) shouldBe "function D"
outputs(2) shouldBe "function D"
outputs(3) shouldBe "function C"
```
