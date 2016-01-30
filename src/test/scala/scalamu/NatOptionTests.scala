package scalamu.test

import org.scalatest.FunSuite
import scalaz.std.option._   // for Option as Functor and Equal instance
import scalaz.std.anyVal._   // for Unit as Equal instance
import scalaz.syntax.equal._ // for ≟ and ≠
import scalaz.syntax.show._ // for .show syntax
import scalamu._

/** Small sample test, to be expanded later to a proper test suite. */
class NatOptionTests extends FunSuite {

  // using ≟ and ≠ to avoid ambiguity of ===

  type Nat = µ[Option]

  val zero = In[Option](None)
  val succ = (n: Nat) => In[Option](Some(n))

  val one = succ(zero)
  val two = succ(one)
  val three = succ(two)

  val toInt: Algebra[Option, Int] = {
    case None    => 0
    case Some(n) => n + 1
  }
  
  test("Equality on naturals should work") {
    assert { zero ≟ zero }
    assert { one ≠ zero }
    assert { one ≟ succ(zero) }
    assert { two ≟ succ(succ(zero)) }
    assert { three ≟ succ(succ(succ(zero))) }
  }

  test("Show on naturals should work") {
    assert { zero.show ≟ "Cofree((),None)" }
    assert { two.show ≟ "Cofree((),Some(Cofree((),Some(Cofree((),None)))))" }
  }

  test("Simple catamorphisms on naturals should work") {
    assert { (zero cata toInt) ≟ 0 }
    assert { (three cata toInt) ≟ 3 }
  }
}
