import scalaz.Cofree
import scalaz.Functor
import scalaz.Equal
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.std.option._     // for Option as Functor instance
import scalaz.syntax.equal._   // for assert_===
import scalaz.syntax.functor._ // for map

import edu.luc.cs.scalaz._           // algebra types
import edu.luc.cs.scalaz.CofreeOps._ // injected cata method


// TODO parsing/unfold

/*
 * In this example, we represent arithmetic expressions as trees
 * (initial algebra for the endofunctor defined next).
 */

/**
 * Endofunctor for (non-generic) F-algebra in the category Scala types:
 *
 * @tparam A carrier object of the F-algebra
 */
sealed trait ExprF[+A]
case class Constant(value: Int)        extends ExprF[Nothing]
case class UMinus[A](expr: A)          extends ExprF[A]
case class Plus [A](left: A, right: A) extends ExprF[A]
case class Minus[A](left: A, right: A) extends ExprF[A]
case class Times[A](left: A, right: A) extends ExprF[A]
case class Div  [A](left: A, right: A) extends ExprF[A]
case class Mod  [A](left: A, right: A) extends ExprF[A]

/**
 * Implicit value for declaring ExprF as a Functor in scalaz.
 */
implicit val ExprFunctor: Functor[ExprF] = new Functor[ExprF] {
  def map[A, B](fa: ExprF[A])(f: A => B): ExprF[B] = fa match {
    case Constant(c) => Constant(c)
    case UMinus(r)   => UMinus(f(r))
    case Plus (l, r) => Plus (f(l), f(r))
    case Minus(l, r) => Minus(f(l), f(r))
    case Times(l, r) => Times(f(l), f(r))
    case Div  (l, r) => Div  (f(l), f(r))
    case Mod  (l, r) => Mod  (f(l), f(r))
  }
}

/**
 * Fixed point of ExprF as carrier object for initial algebra.
 */
type Expr = Cofree[ExprF, Unit]

object ExprFactory {
  def constant(c: Int): Expr        = Cofree((), Constant(c))
  def uminus(r: Expr): Expr         = Cofree((), UMinus(r))
  def plus (l: Expr, r: Expr): Expr = Cofree((), Plus (l, r))
  def minus(l: Expr, r: Expr): Expr = Cofree((), Minus(l, r))
  def times(l: Expr, r: Expr): Expr = Cofree((), Times(l, r))
  def div  (l: Expr, r: Expr): Expr = Cofree((), Div  (l, r))
  def mod  (l: Expr, r: Expr): Expr = Cofree((), Mod  (l, r))
}
import ExprFactory._

// some instances

object TestFixtures {
  val complex1 =
    div(
      minus(
        plus(
          constant(1),
          constant(2)
        ),
        times(
          constant(3),
          constant(4)
        )
      ),
      constant(5)
    )

  val complex1string = "((1 + 2) - (3 * 4)) / 5"

  val complex2 =
    mod(
      minus(
        plus(
          constant(1),
          constant(2)
        ),
        times(
          uminus(
            constant(3)
          ),
          constant(4)
        )
      ),
      constant(5)
    )
}

// specific algebras: note nonrecursive nature

def evaluate: Algebra[ExprF, Int] = _ => {
  case Constant(c) => c
  case UMinus(r)   => -r
  case Plus (l, r) => l + r
  case Minus(l, r) => l - r
  case Times(l, r) => l * r
  case Div  (l, r) => l / r
  case Mod  (l, r) => l % r
}

TestFixtures.complex1.cata(evaluate) assert_=== -1
TestFixtures.complex2.cata(evaluate) assert_=== 0

def size: Algebra[ExprF, Int] = _ => {
  case Constant(c) => 1
  case UMinus(r)   => 1 + r
  case Plus (l, r) => 1 + l + r
  case Minus(l, r) => 1 + l + r
  case Times(l, r) => 1 + l + r
  case Div  (l, r) => 1 + l + r
  case Mod  (l, r) => 1 + l + r
}

TestFixtures.complex1.cata(size) assert_=== 9
TestFixtures.complex2.cata(size) assert_=== 10

def depth: Algebra[ExprF, Int]= _ => {
  case Constant(c) => 1
  case UMinus(r)   => 1 + r
  case Plus (l, r) => 1 + math.max(l, r)
  case Minus(l, r) => 1 + math.max(l, r)
  case Times(l, r) => 1 + math.max(l, r)
  case Div  (l, r) => 1 + math.max(l, r)
  case Mod  (l, r) => 1 + math.max(l, r)
}
TestFixtures.complex1.cata(depth) assert_=== 4
TestFixtures.complex2.cata(depth) assert_=== 5

println("yahoo")

