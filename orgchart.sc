import scalaz.Cofree
import scalaz.Functor
import scalaz.Equal
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.std.string._     // for assert_=== to work on strings
import scalaz.syntax.equal._   // for assert_===
import scalaz.syntax.functor._ // for map
import edu.luc.cs.scalaz._           // algebra types
import edu.luc.cs.scalaz.CofreeOps._ // injected cata method

/**
 * Endofunctor for (generic) F-algebra in the category Scala types.
 *
 * data NodeF[+A] = P | OU(A*)
 *
 * (This is largely equivalent to rose trees.)
 *
 * @tparam A carrier object of the F-algebra
 */
sealed trait NodeF[+A]
case object P extends NodeF[Nothing]
case class OU[A](children: A*) extends NodeF[A]

/**
 * Implicit value for declaring NodeF as a Functor in scalaz.
 */
implicit def NodeFunctor[T]: Functor[NodeF] = new Functor[NodeF] {
  def map[A, B](fa: NodeF[A])(f: A => B): NodeF[B] = fa match {
    case P => P
    case OU(cs @ _*) => OU(cs map f: _*)
  }
}

/**
 * Fixed point of ExprF as carrier object for initial algebra.
 */
type Node[+T] = Cofree[NodeF, T]

/**
 * Factory methods for convenience.
 */
def p[T](value: T): Node[T]                      = Cofree(value, P)
def ou[T](value: T, children: Node[T]*): Node[T] = Cofree(value, OU(children: _*))

val g = p("George")
g.head assert_=== "George"

val cs =   ou("CS",   p("Sekharan"), p("Rom"), p("Thiruvathukal"))
val math = ou("Math", p("Jensen"), p("Doty"), p("Giaquinto"))
val cas =  ou("CAS",  p("Andress"), p("Andrade"), cs, math )
val luc =  ou("luc",  cas)

def size[A]: Algebra[A, NodeF, Int] = _ => {
  case P => 1
  case OU(cs @ _*) => cs.sum
}

luc.cata(size) assert_=== 8

def depth[A]: Algebra[A, NodeF, Int] = _ => {
  case P => 1
  case OU(cs @ _*) => 1 + cs.max
}

luc.cata(depth) assert_=== 4



println("yahoo")