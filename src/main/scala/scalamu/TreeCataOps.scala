package scalamu

import scalaz.Tree
import scalaz.std.stream._
import scalaz.syntax.Ops

/**
 * Wraps a value `self` and provides additional methods related to `Tree`.
 *
 * @tparam A generic item type of this tree
 */
trait TreeCataOps[A] extends Ops[Tree[A]] {

  new ToTreeCataOps {}

  /**
   * The catamorphism (generalized tree fold) for a generic F-algebra.
   *
   * @param g generic F-algebra to apply to this tree
   * @tparam B carrier object of `f` and result type of the catamorphism
   * @return the result of applying the catamorphism to this tree
   */
  def cata[B](g: A => Stream[B] => B): B =
    g(self.rootLabel)(self.subForest map { _ cata g })

  /**
   * The paramorphism (generalized catamorphism) for a generic morphism.
   *
   * @param p morphism to apply to the partial results
   *          available for the children of this instance
   * @tparam B carrier object of `f` and result type of the paramorphism
   * @return the result of applying the paramorphism to this tree
   */
  def para[B](p: A => Stream[Tree[A]] => Stream[B] => B): B =
    p(self.rootLabel)(self.subForest)(self.subForest map { _ para p })
}

trait ToTreeCataOps {
  import scala.language.implicitConversions
  implicit def ToTreeCataOps[A](t: Tree[A]): TreeCataOps[A] = new TreeCataOps[A] {
    def self = t
  }
}
