package edu.luc.cs

package object scalaz {

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
   * @tparam A generic item type of the F-algebra
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
}