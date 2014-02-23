package scalamu

import scalaz.Functor
import scalaz.syntax.Ops

/**
 * Wraps a value `self` and provides catamorphism and related recursion
 * patterns for injection into initial F-algebras based on `µ`.
 *
 * @tparam F endofunctor (type constructor of arity 1)
 *           of the category Scala types for this structure
 */
trait MuOps[F[+_]] extends Ops[µ[F]] {

  implicit def FunctorF: Functor[F]

  object cofreeCata extends ToCofreeCataOps

  /**
   * The catamorphism (generalized fold) for the F-algebra `ϕ`
   * with carrier object `B`, also denoted `(| ϕ |)`.
   * `(| ϕ |)` recursively computes a result of type `B` from this
   * instance of `µ[F]`.
   *
   * @param ϕ F-algebra to apply to the partial results
   *          available for the children of this instance
   * @tparam B carrier object of `ϕ` and result type of the catamorphism
   * @return the result of applying `(| ϕ |)` to this instance of `µ[F]`
   */
  def cata[B](ϕ: F[B] => B): B =
    cofreeCata.ToCofreeCataOps(self) cata Function.const(ϕ)

  /**
   * The paramorphism (generalized catamorphism) for the morphism `ψ`
   * with carrier object `B`, also denoted `<[ ψ ]>`.
   * This recursively computes a result of type `B` from this
   * instance of `Cofree[F, A]` by applying `ψ` to the head and tail
   * of this instance and the partial results available for the children.
   * Note that this is the only place with explicit recursion in this
   * example.
   *
   * @param ψ morphism to apply to the partial results
   *          available for the children of this instance
   * @tparam B carrier object of `ψ` and result type of the paramorphism
   * @return the result of applying the paramorphism to this instance of
   *         `Cofree[F, A]`
   */
  def para[B](ψ: F[µ[F]] => F[B] => B): B =
    cofreeCata.ToCofreeCataOps(self) para Function.const(ψ)
}

trait ToMuOps {
  import scala.language.implicitConversions
  implicit def ToMuOps[F[+_]: Functor](c: µ[F]): MuOps[F] =
    new MuOps[F] {
      def self = c
      override val FunctorF = implicitly[Functor[F]]
    }
}