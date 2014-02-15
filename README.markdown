# Scalak

Small extensions to [scalaz](http://github.com/scalaz/scalaz) and
examples for teaching the software engineering benefits of defining
algebraic data types as F-algebras.

# Getting Scalak

We are working on the proper packaging.
For now, please just clone this project and play around with it.

# Quick Start

Natural numbers as the initial F-algebra of the `Option` endofunctor.

    import scalaz._
    import Scalaz._
    import scalak._

    type Nat = µ[Option]

    val zero:         Nat = In(None)
    def succ(n: Nat): Nat = In(Some(n))

    val two   = succ(succ(zero))
    val three = succ(three)

Conversion to `Int` as a catamorphism.

    def toInt: Algebra[Option, Int] = {
      case None    => 0
      case Some(n) => n + 1
    }

    three cata toInt assert_=== 3

Conversion from `Int` as an anamorphism.

    def fromInt: Coalgebra[Option, Int] = (n: Int) => {
      require { n >= 0 }
      if (n == 0) None
      else        Some(n - 1)
    }

    µ.unfold(7)(fromInt) cata toInt assert_=== 7

Addition as another catamorphism.

    def plus(m: Nat): Algebra[Option, Nat] = {
      case None    => m
      case Some(n) => succ(n)
    }

    two cata plus(three) cata toInt assert_=== 5

# References

[Understanding F-Algebras](https://www.fpcomplete.com/user/bartosz/understanding-algebras)