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
 * data NodeF[+A] = P | OU(A*)
 *
 * @tparam A carrier object of the F-algebra
 */
sealed trait NodeF[+A]
case object P extends NodeF[Nothing]
case class OU[A](children: A*) extends NodeF[T, A]

/**
 * Curried version of NodeF so it takes its arguments one at a time.
 *
 * @tparam A generic item type
 */
//type NodeF1 = ({type λ[γ] = NodeF[_,γ]})#λ

/**
 * Implicit value for declaring NodeF as a Functor in scalaz.
 */
implicit def NodeFunctor[T]: Functor[({type λ[γ] = NodeF[T,γ]})#λ] = new Functor[({type λ[γ] = NodeF[T,γ]})#λ] {
  def map[A, B](fa: NodeF[T, A])(f: A => B): NodeF[T, B] = fa match {
    case P(v) => P(v)
    case OU(v, cs @ _*) => OU(v, cs map (_ map f): _*)
  }
}

/**
 * Fixed point of ExprF as carrier object for initial algebra.
 */
type Node[+T] = Cofree[({type λ[γ] = NodeF[T,γ]})#λ, T]

/**
 * Factory methods for convenience.
 */
def p[T](value: T): Node[T]                      = Cofree(value, P(value))
def ou[T](value: T, children: Node[T]*): Node[T] = Cofree(value, OU)


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
