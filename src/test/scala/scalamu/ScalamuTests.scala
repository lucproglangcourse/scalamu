package scalamu

import scalaz.std.option._ // for Option as Functor instance
import org.scalatest._

/**
 * Small sample test, to be expanded later to a proper test suite.
 */
class ScalamuTests extends FunSuite {

  // TODO expand

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

  test("Simple catamorphisms should work") {
    assert { (zero cata toInt) == 0 }
    assert { (three cata toInt) == 3 }
  }
}