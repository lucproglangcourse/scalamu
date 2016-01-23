package scalamu.test

import org.scalatest.FunSuite
import scalaz.std.option._   // for Option as Functor and Equal instance
import scalaz.std.anyVal._   // for Unit as Equal instance
import scalaz.syntax.equal._ // for === syntax
import scalaz.syntax.show._ // for .show syntax
import scalamu._

/** Small sample test, to be expanded later to a proper test suite. */
class ScalamuTests extends FunSuite {

  // reliably choose Scalaz's === instead of FunSuite's
  import scala.language.implicitConversions
  object teo extends scalaz.syntax.ToEqualOps
  implicit def ToEqualOps[F](v: F)(implicit F0: scalaz.Equal[F]) = teo.ToEqualOps(v)(F0)

  type Nat = µ[Option]

  val zero: Nat = In(None)
  def succ(n: Nat): Nat = In(Some(n))

  val one = succ(zero)
  val two = succ(one)
  val three = succ(two)

  def toInt: Algebra[Option, Int] = {
    case None    => 0
    case Some(n) => n + 1
  }
  
  test("Equality on naturals should work") {
    assert { succ(zero) === succ(zero) }
  }

  // TODO test show
  test("Show on naturals should work") {
    assert { succ(zero).show === "Cofree((),Some(Cofree((),None)))" }
  }

  test("Simple catamorphisms on naturals should work") {
    assert { (zero cata toInt) === 0 }
    assert { (three cata toInt) === 3 }
  }
}
