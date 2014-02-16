import scalaz.{ Cofree, Equal, Functor }
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.syntax.equal._   // for assert_===
import scalaz.syntax.functor._ // for map

import scalak._                // algebra types and injected cata method

/*
 * In this example, we demonstrate that we can unify the natf and natoption
 * examples by defining an abstraction to describe the initial algebra
 * of a given endofunctor.
 *
 * As a result, we can avoid the duplication of essentially equivalent code
 * between natf and natoption!
 */

/**
 * A common abstraction for the initial algebra of any endofunctor
 * isomorphic to `Option`.
 *
 * @tparam F the endofunctor
 */
trait FInitial[F[+_]] {
  /** The zero constructor. */
  val z: F[Nothing]
  /** The successor constructor. */
  def s[A]: A => F[A]
  /** The extractor. */
  def out[A]: F[A] => Option[A]
  /** The `Functor` typeclass instance. */
  implicit val FFunctor = new Functor[F] {
    def map[A, B](fa: F[A])(f: A => B): F[B] = out(fa) match {
      case None    => z: F[B]
      case Some(n) => s(f(n))
    }
  }
}

/**
 * A common abstraction over the code in the `natf` and `natoption`
 * example worksheets made possible by the `FInitial` abstraction.
 *
 * @tparam F the endofunctor we are going to examine
 * @param fi the initial algebra of the endofunctor
 */
def test[F[+_]](fi: FInitial[F]) = {
  import fi._

  val zero: µ[F]          = In(z)
  def succ(n: µ[F]): µ[F] = In(s(n))

  val one   = succ(zero)
  val two   = succ(one)
  val three = succ(two)

  val toInt: Algebra[F, Int] = out(_) match {
    case None    => 0
    case Some(n) => n + 1
  }

  zero  cata toInt assert_=== 0
  three cata toInt assert_=== 3

  def fromInt: Coalgebra[F, Int] = (n: Int) => {
    require { n >= 0 }
    if   (n == 0) z
    else          s(n - 1)
  }

  µ.unfold(0)(fromInt) cata toInt assert_=== 0
  µ.unfold(7)(fromInt) cata toInt assert_=== 7

  def plus(m: µ[F]): Algebra[F, µ[F]] = out(_) match {
    case None    => m
    case Some(n) => succ(n)
  }
  zero  cata plus(zero)  cata toInt assert_=== 0
  zero  cata plus(three) cata toInt assert_=== 3
  three cata plus(zero)  cata toInt assert_=== 3
  two   cata plus(three) cata toInt assert_=== 5
}

/**
 * Initial algebra for the `Option` endofunctor.
 * This is conceptually the identity descriptor.
 */
object OptionC extends FInitial[Option] {
  val z = None
  def s[A]: A => Option[A] = Some(_)
  def out[A]: Option[A] => Option[A] = identity _
}

/**
 * Initial algebra for the `NatF` endofunctor.
 * This benefits from an outer wrapper for bundling everything together.
 * It also exposes the isomorphism between `NatF` and `Option`.
 */
object NatCWrapper {
  sealed trait NatF[+A]
  case object Zero extends NatF[Nothing]
  case class Succ[A](n: A) extends NatF[A]
  object C extends FInitial[NatF] {
    val z: NatF[Nothing]   = Zero
    def s[A]: A => NatF[A] = Succ(_)
    def out[A]: NatF[A] => Option[A] = {
      case Zero    => None
      case Succ(n) => Some(n)
    }
  }
}

// now testing both functor choices using the same code :D
test(NatCWrapper.C)
test(OptionC)

println("■")

