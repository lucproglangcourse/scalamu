package edu.luc.cs.scalaz

import scalaz.{ Functor, Cofree }
import scalaz.syntax.functor._

object CofreeOps {

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
