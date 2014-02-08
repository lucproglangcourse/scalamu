import scalaz.Cofree
import scalaz.Functor
import scalaz.Equal
import scalaz.std.anyVal._     // for assert_=== to work on basic values
import scalaz.std.option._     // for Option as Functor instance
import scalaz.syntax.equal._   // for assert_===
import scalaz.syntax.functor._ // for map

import edu.luc.cs.scalaz._           // algebra types
import edu.luc.cs.scalaz.CofreeOps._ // injected cata method

/*
 * Generic MyList F-algebra: using Option as the required
 * endofunctor in the category Scala types.
 */

/**
 * Fixed point of Option as carrier object for initial algebra.
 *
 * @tparam A generic item type of the resulting initial algebra
 */
type MyList[+A] = Cofree[Option, A]

/**
 * Factory methods for convenience.
 */
def nil[A](dummy: A): MyList[A]                  = Cofree(dummy, None) // need dummy because Cofree expects non-null
def cons[A](head: A, tail: MyList[A]): MyList[A] = Cofree(head, Some(tail))

// some instances

val list0: MyList[String] = nil("dummy")
val list1: MyList[String] = cons("hello", list0)
val list2: MyList[String] = cons("world", list1)


val list3: MyList[String] = cons("good morning", list2)



/**
 * Algebra for carrier object Int in category Scala types.
 * Note that this is nonrecursive.
 *
 * @tparam A generic item type of the F-algebra
 */
def length[A]: Algebra[A, Option, Int] = _ => {
  case None    => 0     // end of list:  0
  case Some(n) => 1 + n // regular node: add 1 to sum accumulated so far
}

// now we can fold the length algebra into instances.

list0.cata(length) assert_=== 0
list3.cata(length) assert_=== 3

/**
 * Another algebra for carrier object Int but specific item type, also Int.
 */
def sum: Algebra[Int, Option, Int] = v => {
  case None    => 0     // end of list:  0
  case Some(n) => v + n // regular node: add value to sum accumulated so far
}

val list4: MyList[Int] = Cofree(4, Some(Cofree(3, Some(Cofree(2, Some(Cofree(1, Some(Cofree(-1, None)))))))))


list4.cata(sum) assert_=== 10

/**
 * Coalgebra for carrier object Int in category Scala types.
 */
def downFrom: Coalgebra[Option, Int] = (n: Int) => {
  require { n >= 0 }
  if (n == 0)
    None
  else
    Some(n / 2)
}

/**
 * Now we can create instances by unfolding the coalgebra from a starting value.
 */
Cofree.unfoldC(0)(downFrom).cata(length) //assert_=== 0 // Nil
Cofree.unfoldC(8)(downFrom).cata(length) //assert_=== 4 // Seq(8, 4, 2, 1)

println("yahoo")