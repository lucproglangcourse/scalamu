package scalamu

import scalaz.Cofree
import scalaz.Functor

/**
 * Object to hold the initial F-algebra.
 * This also serves as a companion to type constructor µ.
 */
object In {

  /**
   * The initial F-algebra: an F-algebra with `µ[F]`,
   * the least fixpoint of `F`, as the carrier object.
   *
   * @tparam F endofunctor (type constructor of arity 1) of the category Scala types
   * @param value an unwrapped instance of `F` applied to `µ[F]`,
   *              that is, `F[µ[F]]`
   * @return the resulting wrapped instance of `µ[F]`
   */
  def apply[F[+_]: Functor](value: F[µ[F]]): µ[F] = Cofree((), value)

  /**
   * Extractor from initial F-algebra.
   *
   * @tparam F endofunctor (type constructor of arity 1)
   *           of the category Scala types
   * @param wrapped a wrapped instance of `µ[F]`
   * @return the resulting unwrapped instance of `F[µ[F]]`
   */
  def unapply[F[+_]](wrapped: µ[F]): Option[F[µ[F]]] = Some(wrapped.out)
  // _.out is the same as _.tail used in cata below
  // but better conveys its role as inverse of In

  /**
   * The Anamorphism (generalized unfold) for the F-coalgebra `g` with
   * carrier object `B`, also denoted `[( g )]`.
   * `[( g )]` corecursively builds up an instance of `µ[F]` by using `g` to
   * generate successive values of type `F[B]`.
   * Anamorphisms are the categorical dual of catamorphisms.
   *
   * @tparam F endofunctor (type constructor of arity 1)
   *           of the category Scala types
   * @param g F-coalgebra for generating successive values of type `F[B]`
   * @tparam B carrier object of `g`
   * @param s seed value (starting point) for generating successive values
   */
  def unfold[F[+_]: Functor, B](s: B)(g: B => F[B]): µ[F] =
    Cofree.unfoldC(s)(g) map Function.const()
}
