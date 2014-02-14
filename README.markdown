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

    val three = succ(succ(succ(zero)))

    def toInt: Algebra[Option, Int] = {
      case None    => 0
      case Some(n) => n + 1
    }

    three cata toInt assert_=== 3
