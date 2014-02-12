package edu.luc.cs

import scalaz.Cofree
import scalaz.Functor
import scalaz.syntax.functor._

package object scalak {

  /**
   * An algebra for a (nongeneric) F-algebra.
   *
   * @tparam S branching constructor of the initial algebra
   * @tparam B carrier object of the algebra
   */
  type Algebra[S[_], B] = Unit => S[B] => B

  /**
   * An algebra for a generic F-algebra.
   *
   * @tparam A generic item type of the F-algebra (held by Cofree)
   * @tparam S branching constructor of the initial algebra
   * @tparam B carrier object of the algebra
   */
  type GenericAlgebra[A, S[_], B] = A => S[B] => B

  /**
   * A coalgebra for an F-algebra.
   *
   * @tparam S branching constructor of the initial algebra
   * @tparam B carrier object of the coalgebra
   */
  type Coalgebra[S[_], B] = B => S[B]

  /**
   * The fixpoint operator on functors. 
   * Implemented as an instance of Cofree.
   * 
   * @tparam S functor whose fixpoint we are forming
   */
  type µ[S[+_]] = Cofree[S, Unit]

  /**
   * Constructor and deconstructor for instances of fixpoint types of branching functors.
   */
  object In {

    /**
     * Instance constructor.
     * @param value an instance of the branching functor
     * @tparam S the branching functor
     * @return the value wrapped as an instance of the fixpoint
     */
    def apply[S[+_]](value: S[µ[S]])(implicit S: Functor[S]): µ[S] = Cofree((), value)

    /**
     * Extractor for pattern matching.
     * @param node an instance of the fixpoint wrapped around an instance of the branching functor
     * @tparam S the branching functor
     * @return the instance of the branching functor
     */
    def unapply[S[+_]](node: µ[S]): S[µ[S]] = node.tail
  }

  /**
   * Catamorphism (generalizeld fold) for Cofree injected into Cofree class
   * (similar to C# extension methods).
   *
   * Note that this is the only place with explicit recursion in this example.
   *
   * @tparam S branching functor of this collection
   * @tparam A generic item type of this collection
   *
   */
  implicit class CofreeCata[S[+_], +A](self: Cofree[S, A]) {
    def cata[B](g: A => S[B] => B)(implicit S: Functor[S]): B =
      g(self.head)(self.tail map { _ cata g })
    // TODO paramorphism
  }
}