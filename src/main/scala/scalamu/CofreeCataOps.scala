package scalamu

import scalaz.Cofree
import scalaz.Functor
import scalaz.syntax.functor._
import scalaz.syntax.Ops

/**
 * Wraps a value `self` and provides catamorphism and related recursion
 * patterns for injection into `Cofree`.
 *
 * @tparam F branching endofunctor of this structure
 * @tparam A generic item type of this structure
 */
final class CofreeCataOps[F[_]: Functor, A](val self: Cofree[F, A]) extends Ops[Cofree[F, A]] {

  /**
   * The catamorphism (generalized fold) for the generic F-algebra `f`
   * with carrier object `B`.
   * This recursively computes a result of type `B` from this
   * instance of `Cofree[F, A]`.
   *
   * @param f generic F-algebra to apply to the head of this instance and
   *          the partial results available for the children of this instance
   * @tparam B carrier object of `f` and result type of the catamorphism
   * @return the result of applying the catamorphism to this instance of
   *         `Cofree[F, A]`
   */
  def cata[B](f: A => F[B] => B): B =
    para((a => _ => fb => f(a)(fb)): A => F[Cofree[F, A]] => F[B] => B)
  // equivalent to f(self.head)(self.tail map { _ cata f })
  // per definition of para below (f doesn't need the extra self.tail arg)

  /**
   * The paramorphism (generalized catamorphism) for the morphism `p`.
   * This recursively computes a result of type `B` from this
   * instance of `Cofree[F, A]` by applying `p` to the head and tail
   * of this instance and the partial results available for the children.
   * Note that this is the only place with explicit recursion in this
   * example.
   *
   * @param p morphism to apply to the partial results
   *          available for the children of this instance
   * @tparam B carrier object of `p` and result type of the paramorphism
   * @return the result of applying the paramorphism to this instance of
   *         `Cofree[F, A]`
   */
  def para[B](p: A => F[Cofree[F, A]] => F[B] => B): B =
    p(self.head)(self.tail)(self.tail map { _ para p })
}

/** Trait for combining multiple implicit wrapper mathods into package object. */
trait ToCofreeCataOps {
  implicit def ToCofreeCataOps[F[_]: Functor, A](c: Cofree[F, A]): CofreeCataOps[F, A] =
    new CofreeCataOps[F, A](c)
}
