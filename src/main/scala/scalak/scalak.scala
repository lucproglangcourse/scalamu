import scalaz.Cofree
import scalaz.Functor
import scalaz.syntax.functor._

package object scalak {

  /**
   * A (nongeneric) F-algebra with carrier object `B`.
   *
   * @tparam F an endofunctor (type constructor of arity 1)
   *           of the category Scala types
   * @tparam B a carrier object (Scala type)
   */
  type Algebra[F[_], B] = F[B] => B

  /**
   * A generic F-algebra with carrier object `B` and type parameter `A`.
   *
   * @tparam A the generic type parameter of the F-algebra
   *           (instances of `A` held by instances of `Cofree`)
   * @tparam F an endofunctor (type constructor of arity 1)
   *           of the category Scala types
   * @tparam B a carrier object (Scala type)
   */
  type GenericAlgebra[A, F[_], B] = A => F[B] => B

  /**
   * A (nongeneric) F-coalgebra with carrier object `B`.
   *
   * @tparam F an endofunctor (type constructor of arity 1)
   *           of the category Scala types
   * @tparam B a carrier object (Scala type)
   */
  type Coalgebra[F[_], B] = B => F[B]

  /**
   * The least fixpoint operator on endofunctors in the category Scala types.
   * This is used to form the carrier object (recursive type) for the initial
   * F-algebra.
   * Implemented as a partial application of `Cofree` to `Unit`.
   *
   * @tparam F endofunctor whose least fixpoint we are forming
   */
  type µ[F[+_]] = Cofree[F, Unit]

  type Mu[F[+_]] = µ[F]

  /**
   * Object to hold the initial F-algebra.
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
    def apply[F[+_]](value: F[µ[F]])(implicit F: Functor[F]): µ[F] = Cofree((), value)

    /**
     * Extractor for pattern matching.
     *
     * @tparam F endofunctor (type constructor of arity 1)
     *           of the category Scala types
     * @return the resulting unwrapped instance of `F[µ[F]]`
     */
    def unapply[F[+_]]: µ[F] => F[µ[F]] = _.out
    // _.out is the same as _.tail used in cata below
    // but better conveys its role as inverse of In
  }

  /**
   * Wrapper class for injecting useful methods into `Cofree`
   * (similar to C# extension methods).
   *
   * @tparam F branching endofunctor of this stream
   * @tparam A generic item type of this stream
   */
  implicit class CofreeOps[F[+_], +A](self: Cofree[F, A]) {

    /**
     * The catamorphism (generalizeld fold) for the generic F-algebra `f`
     * with carrier object `B`.
     * This recursively computes a result of type `B` from this
     * instance of `Cofree[F, A]`.
     * Note that this is the only place with explicit recursion in this
     * example.
     *
     * @param f generic F-algebra to apply to the partial results
     *          available for the children of this instance
     * @tparam B carrier object of `f` and result type of the catamorphism
     * @return the result of applying the catamorphism to this instance of
     *         `Cofree[F, A]`
     */
    def cata[B](f: A => F[B] => B)(implicit F: Functor[F]): B =
      f(self.head)(self.tail map { _ cata f })
    // TODO paramorphism
  }

  /**
   * Wrapper class for injecting useful methods into `µ`.
   *
   * @tparam F endofunctor (type constructor of arity 1)
   *           of the category Scala types
   */
  implicit class MuOps[F[+_]](self: µ[F]) {

    /**
     * The catamorphism (generalizeld fold) for the F-algebra `ϕ`
     * with carrier object `B`, also denoted `(| ϕ |)`.
     * `(| ϕ |)` recursively computes a result of type `B` from this
     * instance of `µ[F]`.
     *
     * @param ϕ F-algebra to apply to the partial results
     *          available for the children of this instance
     * @tparam B carrier object of `ϕ` and result type of the catamorphism
     * @return the result of applying `(| ϕ |)` to this instance of `µ[F]`
     */
    def cata[B](ϕ: F[B] => B)(implicit F: Functor[F]): B =
      new CofreeOps(self) cata { _ => ϕ }
    // TODO paramorphism
  }

  /**
   * Companion object for type `µ`.
   */
  object µ {

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
    def unfold[F[+_], B](s: B)(g: B => F[B])(implicit F: Functor[F]): µ[F] =
      Cofree.unfoldC(s)(g) map Function.const()
  }

  val Mu = µ
}