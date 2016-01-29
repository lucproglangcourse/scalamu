package scalamu.test

import org.scalatest.FunSuite
import scalaz.{ Equal, Functor, Show }
import scalaz.std.anyVal._   // for Unit as Equal instance
import scalaz.syntax.equal._ // for ≟ and ≠
import scalaz.syntax.show._ // for .show syntax
import scalaz.syntax.functor._ // for .map syntax
import scalamu._

/** Small sample test, to be expanded later to a proper test suite. */
class NatFTests extends FunSuite {

  // using ≟ and ≠ to avoid ambiguity of ===

  sealed trait NatF[+A]
  case object Zero extends NatF[Nothing]
  case class Succ[+A](n: A) extends NatF[A]

  implicit val NatFFunctor = new Functor[NatF] {
    def map[A, B](fa: NatF[A])(f: A => B): NatF[B] = fa match {
      case Zero    => Zero
      case Succ(n) => Succ(f(n))
    }
  }
  
  // TODO generalize to some type of zip
  implicit def NatFEqual[A](implicit A: Equal[A]): Equal[NatF[A]] = Equal.equal {
    case (Succ(n), Succ(m)) => A.equal(n, m)
    case (Zero,    Zero)    => true
    case _                  => false
  }
  
  // TODO generalize for any functor F and put into library
  implicit def NatFShow[A](implicit A: Show[A]): Show[NatF[A]] = Show.shows { n =>
    (n map A.shows _).toString
  }

  type Nat = µ[NatF]
  
  val zero         = In[NatF](Zero)
  def succ(n: Nat) = In[NatF](Succ(n))

  val one   = succ(zero)
  val two   = succ(one)
  val three = succ(two)

  val natFtoInt: Algebra[NatF, Int] = {
    case Zero    => 0
    case Succ(n) => n + 1
  }
  
  test("Equality on NatF should work") {
    assert { (Zero: NatF[Unit]) ≟ Zero }
    assert { (Succ(Succ(Zero)): NatF[NatF[NatF[Unit]]]) ≠ Zero }
    assert { (Succ(Succ(Zero)): NatF[NatF[NatF[Unit]]]) ≟ Succ(Succ(Zero)) }
  }

  test("Equality on naturals should work") {
    assert { zero ≟ zero }
    assert { one ≠ zero }
    assert { one ≟ succ(zero) }
    assert { two ≟ succ(succ(zero)) }
    assert { three ≟ succ(succ(succ(zero))) }
  }

  test("Show on NatF should work") {
    assert { (Zero: NatF[Unit]).show ≟ "Zero" }
    assert { (Succ(Succ(Zero)): NatF[NatF[NatF[Unit]]]).show ≟ "Succ(Succ(Zero))" }
  }

  test("Show on naturals should work") {
    assert { zero.show ≟ "Cofree((),Zero)" }
    assert { two.show ≟ "Cofree((),Succ(Cofree((),Succ(Cofree((),Zero)))))" }
  }

  test("Simple catamorphisms on naturals should work") {
    assert { (zero cata natFtoInt) ≟ 0 }
    assert { (three cata natFtoInt) ≟ 3 }
  }
}
