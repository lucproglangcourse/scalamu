package scalamu

import scalaz.Tree
import scalaz.std.stream._
import scalaz.syntax.Ops

/**
 * Wraps a value `self` and provides catamorphism and related recursion
 * patterns for injection into `Tree`.
 *
 * @tparam A generic item type of this tree
 */
private[scalamu] final class TreeCataOps[A](val self: Tree[A]) extends Ops[Tree[A]] {

  /**
   * The catamorphism (generalized tree fold) for a generic F-algebra.
   *
   * @param f generic F-algebra to apply to this tree
   * @tparam B carrier object of `f` and result type of the catamorphism
   * @return the result of applying the catamorphism to this tree
   */
  def cata[B](f: A => Stream[B] => B): B =
    para((a => _ => fb => f(a)(fb)): A => Stream[Tree[A]] => Stream[B] => B)
  // equivalent to f(self.rootLabel)(self.subForest map { _ cata f })
  // per definition of para below (f doesn't need the extra self.tail arg)

  /**
   * The paramorphism (generalized catamorphism) for a generic morphism.
   *
   * @param p morphism to apply to the partial results
   *          available for the children of this instance
   * @tparam B carrier object of `p` and result type of the paramorphism
   * @return the result of applying the paramorphism to this tree
   */
  def para[B](p: A => Stream[Tree[A]] => Stream[B] => B): B =
    p(self.rootLabel)(self.subForest)(self.subForest map { _ para p })
}

trait ToTreeCataOps {
  implicit def ToTreeCataOps[A](t: Tree[A]): TreeCataOps[A] = new TreeCataOps[A](t)
}
