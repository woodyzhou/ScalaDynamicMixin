package dynamic.mixin

trait DTrait {

  protected var next: Option[DTrait] = None
  protected var prev: Option[DTrait] = None

  private def isSelfType[T: Manifest]: Boolean = {
    manifest[T].runtimeClass isInstance this 
  }

  private def _findRoot: Option[DTrait] = {
    this.prev match { 
      case None => Option(this)
      case Some(p) => p._findRoot
    }
  }

  def wrap[T<:DTrait: Manifest]: T = {
    val constructor = manifest[T].runtimeClass.getConstructor()
    val r: T = constructor.newInstance().asInstanceOf[T]
    this.prev = Option(r)
    r.next = Option(this)
    r
  }
 
  def unwrap: DTrait = {
    val rn =this.next
    if(rn.isDefined) {
      rn.get.prev = this.prev
      if(this.prev.isDefined) {
        this.prev.get.next = rn
      }
      this.next = None
      this.prev = None
      rn.get
    } else {
      throw new RuntimeException("current NEvent is already the leaf one")
    }
  }

  def is[T: Manifest]: Boolean = {
    var c = _findRoot
    while(c.isDefined && !c.get.isSelfType[T]) {
      c = c.get.next
    }
    c.isDefined
  }

  def as[T: Manifest]: T = {
    var c = _findRoot
    while(c.isDefined && !c.get.isSelfType[T]) {
      c = c.get.next
    }
    if (!c.isDefined) {
      throw new RuntimeException(s"Type ${manifest[T]} is not in the NEvent stack")
    } else {
      c.get.asInstanceOf[T]
    }
  }

  def types: List[String] = {
    next match {
      case Some(c) => this.getClass.getSimpleName :: c.types
      case None => this.getClass.getSimpleName :: Nil
    }
  }

}

/**
 * Sample Usecase:
 *
 * object A extends DTraitExtractor[A]
 * object B extends DTraitExtractor[B]
 *
 * dtrait_instance match {
 *     case A(a) => ???
 *     case B(b) => ???
 * }
 **/

abstract class DTraitExtractor[A: Manifest] {
  def unapply(o: Any): Option[A] = o match {
    case t: DTrait if (t.is[A]) => Some(t.as[A])
    case _ => None
  }
}
