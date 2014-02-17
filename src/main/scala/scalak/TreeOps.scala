package scalak

import scalaz.Tree
import scalaz.std.stream._

object TreeOps {

  /**
   * Catamorphism (generalizeld fold) and paramorphism injected into
   * Tree class (similar to C# extension methods).
   *
   * @tparam A generic item type of this collection
   */
  implicit class TreeCata[A](self: Tree[A]) {
    def cata[B](g: A => Stream[B] => B): B =
      g(self.rootLabel)(self.subForest map { _ cata g })
    def para[B](p: A => Stream[Tree[A]] => Stream[B] => B): B =
      p(self.rootLabel)(self.subForest)(self.subForest map { _ para p })
  }
}
