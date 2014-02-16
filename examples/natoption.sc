import scalaz.{ Cofree, Equal, Functor }
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.std.option._     // for Option as Functor instance
import scalaz.syntax.equal._   // for assert_===
import scalaz.syntax.functor._ // for map
import scalak._                // algebra types and injected cata method

/*
 * In this example, we represent natural numbers
 * essentially as lists without item values:
 *
 * 0 = zero
 * 3 = succ(succ(succ(zero)))
 *
 * We can then define operations such as addition on these.
 */

/*
 * A (nongeneric) F-algebra in the category Scala types:
 * we use the predefined Option[_] endofunctor
 * (type constructor of arity 1). This is already defined
 * as an instance of typeclass Functor in scalaz.
 */

/**
 * Least fixpoint of `Option` (recursive type based on `Option`)
 * as carrier object for initial algebra.
 */
type Nat = µ[Option]

/**
 * Factory methods for convenience.
 */
val zero: Nat         = In(None)
def succ(n: Nat): Nat = In(Some(n))

// some instances
val one   = succ(zero)
val two   = succ(one)
val three = succ(two)

/**
 * Conversion to `Int` as an `Option`-algebra
 * for carrier object `Int` in the category Scala types.
 */
def toInt: Algebra[Option, Int] = {
  case None    => 0
  case Some(n) => n + 1
}

// now we can fold the toInt algebra into instances

zero  cata toInt assert_=== 0
three cata toInt assert_=== 3

/**
 * Conversion from `Int` as an `Option`-coalgebra
 * for carrier object `Int` in category Scala types
 * (generator for corecursion).
 */
def fromInt: Coalgebra[Option, Int] = (n: Int) => {
  require { n >= 0 }
  if   (n == 0) None
  else          Some(n - 1)
}

/*
 * Unfold is an anamorphism for unfolding a Nat from a coalgebra
 * such as fromInt. This is an example of corecursion.
 */
µ.unfold(0)(fromInt) cata toInt assert_=== 0
µ.unfold(7)(fromInt) cata toInt assert_=== 7

/**
 * Addition as an `Option`-algebra
 * for carrier object `Nat` in the category Scala types.
 *
 * @param m the seed value (starting point for generating successive values)
 */
def plus(m: Nat): Algebra[Option, Nat] = {
  case None    => m
  case Some(n) => succ(n)
}

zero  cata plus(zero)  cata toInt assert_=== 0
zero  cata plus(three) cata toInt assert_=== 3
three cata plus(zero)  cata toInt assert_=== 3
two   cata plus(three) cata toInt assert_=== 5

println("■")

// TODO multiplication
// TODO paramorphism/factorial
