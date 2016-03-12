package dynamic.mixin.test

import dynamic.mixin._
import org.scalatest._

object AnBGroup {
  class A extends DTrait {
    def funA(): String = "function A"
    def funCommon(): String = "function Common from A"
  }
  object A extends DTraitExtractor[A]

  class B extends DTrait {
    def funB(): String = "function B"
    def funCommon(): String = "function Common from B"
  }
  object B extends DTraitExtractor[B]
}
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

class DTraitTest extends FlatSpec with Matchers {

  "DTrait" should "support dynamic type mixin" in {
    import AnBGroup._
    val a: A = new A
    val b: B = new B
    val ab: DTrait = a.wrap[B]

    a.funA shouldBe "function A"
    a.funCommon shouldBe "function Common from A"
    b.funB shouldBe "function B"
    b.funCommon shouldBe "function Common from B"


    ab.as[A].funA shouldBe "function A"
    ab.as[B].funB shouldBe "function B"
    ab.as[B].funCommon shouldBe "function Common from B"
    ab.as[A].funCommon shouldBe "function Common from A"

    def typeMatch1(a: DTrait): String = {
      a match {
        case A(a) => a.funCommon
        case B(b) => b.funCommon
        case _ => "unknown"
      }
    }
    def typeMatch2(a: DTrait): String = {
      a match {
        case B(b) => b.funCommon
        case A(a) => a.funCommon
        case _ => "unknown"
      }
    }

    typeMatch1(a) shouldBe "function Common from A"
    typeMatch1(b) shouldBe "function Common from B"
    typeMatch1(ab) shouldBe "function Common from A"
    typeMatch2(ab) shouldBe "function Common from B"
  }

  it should "works even better with implicit and without duplicate function name" in {
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
  }
}
