import scalaz.Cofree
import scalaz.Functor
import scalaz.Equal
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.std.option._     // for Option as Functor instance
import scalaz.syntax.equal._   // for assert_===
import scalaz.syntax.functor._ // for map

/*
 * data Node[A] = P(value: A) | OU(value: A, children: List[Node[A]])
 */

/**
 * NodeF functor for
 * @tparam A
 * @tparam B
 */
sealed trait NodeF[+A, +B]
case class P[+A](value: A) extends NodeF[A, Nothing]
case class OU[+A, +B](value: A, children: NodeF[A, B]*) extends NodeF[A, B]

val p = P("George")
assert { p.value == "George" }

val cs =   OU("CS",   P("Sekharan"), P("Rom"), P("Thiruvathukal"))
val math = OU("Math", P("Jensen"), P("Doty"), P("Giaquinto"))
val cas =  OU("CAS",  P("Andress"), P("Andrade"), cs, math )
val luc =  OU("luc",  cas)

def size(o: Node): Int = o match {
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
