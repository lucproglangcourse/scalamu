package edu.luc.cs.scalaz

import scalaz.Tree
import scalaz.std.stream._

object TreeOps {

  /**
   * Catamorphism (generalizeld fold) injected into Tree class
   * (similar to C# extension methods).
   *
   * @tparam A generic item type of this collection
   */
  implicit class TreeCata[A](self: Tree[A]) {
    def cata[B](g: A => Stream[B] => B): B =
      g(self.rootLabel)(self.subForest map { _ cata g })
    // TODO paramorphism
  }
}
