import scalaz.Cofree
import scalaz.Functor
import scalaz.Equal
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.syntax.equal._   // for assert_===
import scalaz.syntax.functor._ // for map

import edu.luc.cs.scalaz._           // algebra types
import edu.luc.cs.scalaz.CofreeOps._ // injected cata method

/*
 * data Node[A] = P(value: A) | OU(value: A, children: List[Node[A]])
 */

/**
 * Endofunctor for (generic) F-algebra in the category Scala types.
 *
 * @tparam A carrier object of the F-algebra
 */
sealed trait NodeF[+A]
case class P[+A](value: A) extends NodeF[A]
case class OU[+A](value: A, children: NodeF[A]*) extends NodeF[A]

/**
 * Implicit value for declaring NodeF as a Functor in scalaz.
 */
implicit val NodeFunctor: Functor[NodeF] = new Functor[NodeF] {
  def map[A, B](fa: NodeF[A])(f: A => B): NodeF[B] = fa match {
    case P(v) => P(f(v))
    case OU(v, cs @ _*) => OU(f(v), cs map(_ map f): _*)
  }

  /**
   * Fixed point of ExprF as carrier object for initial algebra.
   */
  type Expr = Cofree[ExprF, Unit]




val p = P("George")
assert { p.value == "George" }

val cs =   OU("CS",   P("Sekharan"), P("Rom"), P("Thiruvathukal"))
val math = OU("Math", P("Jensen"), P("Doty"), P("Giaquinto"))
val cas =  OU("CAS",  P("Andress"), P("Andrade"), cs, math )
val luc =  OU("luc",  cas)

def size:  = o match {
  case P(_) => 1
  case OU(_, children @ _*) => children.map(size).sum
}

assert { size(p) == 1 }
assert { size(cs) == 3 }
assert { size(luc) == 8 }

def depth(o: Node): Int = o match {
  case P(_) => 1
  case OU(_, children) => ??? // TODO
}

assert { depth(p) == 1 }
assert { depth(cs) == 2 }
assert { depth(luc) == 4 }

// TODO convert these functions into methods
