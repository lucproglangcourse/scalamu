import scalaz.{ Show, Equal }

/**
 * Small extensions to scalaz for working with F-algebras, where `F` is an
 * endofunctor of the category Scala types (type constructor of arity 1
 * with a `map` function that obeys certain laws).
 */
package object scalamu
    extends ToMuOps with ToCofreeCataOps with ToTreeCataOps {

  /**
   * A (nongeneric) F-algebra with carrier object `B`.
   *
   * @tparam F an endofunctor of the category Scala types
   * @tparam B a carrier object (Scala type)
   */
  type Algebra[F[_], B] = F[B] => B

  /**
   * A generic F-algebra with carrier object `B` and type parameter `A`.
   *
   * @tparam A the generic type parameter of the F-algebra
   *           (instances of `A` held by instances of `Cofree`)
   * @tparam F an endofunctor of the category Scala types
   * @tparam B a carrier object (Scala type)
   */
  type GenericAlgebra[A, F[_], B] = A => F[B] => B

  /**
   * A (nongeneric) F-coalgebra with carrier object `B`.
   *
   * @tparam F an endofunctor of the category Scala types
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
  type µ[F[+_]] = scalaz.Cofree[F, Unit]

  /**
   * Alias for `µ`.
   */
  type Mu[F[+_]] = µ[F]

  /**
   * Alias for `In`.
   */
  val µ = In

  /**
   * Implicit value for declaring `µ` as an instance of
   * typeclass `Equal` in scalaz using `Equal`'s structural equality.
   * This enables `===` and `assert_===` on `µ` instances.
   */
  implicit def MuEqual[F[+_]]: Equal[µ[F]] = Equal.equalA

  /**
   * Implicit value for declaring `µ` as an instance of
   * typeclass `Show` in scalaz using `Show`'s default
   * implementation based on `toString`.
   * This enables `===` and `assert_===` on `µ` instances.
   */
  implicit def MuShow[F[+_]]: Show[µ[F]] = Show.showFromToString
}
