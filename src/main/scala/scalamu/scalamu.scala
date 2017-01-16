import scalaz.{ Cofree, Functor, Show }
import shapeless.contrib.scalaz.CofreeInstances
/**
 * Small extensions to scalaz for working with F-algebras, where `F` is an
 * endofunctor of the category Scala types (type constructor of arity 1
 * with a `map` function that obeys certain laws).
 *
 * `CofreeInstances` declares `Cofree` and thereby `µ` as an instance of
 * scalaz typeclasses `Equal` (using structural equality)
 * and `Show` (using case-class-like conversion to a string).
 * This enables `===`, `assert_===`, and `.show` on `µ` instances.
 */
package object scalamu
    extends ToMuOps with ToCofreeCataOps with ToTreeCataOps with CofreeInstances {

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
   * A generic F-coalgebra with carrier object `B`.
   *
   * @tparam A the generic type parameter of the F-algebra
   *           (instances of `A` held by instances of `Cofree`)
   * @tparam F an endofunctor of the category Scala types
   * @tparam B a carrier object (Scala type)
   */
  type GenericCoalgebra[A, F[_], B] = B => (A, F[B])

  /**
   * The least fixpoint operator on endofunctors in the category Scala types.
   * This is used to form the carrier object (recursive type) for the initial
   * F-algebra.
   * Implemented as a partial application of `Cofree` to `Unit`.
   *
   * @tparam F endofunctor whose least fixpoint we are forming
   */
  type µ[F[_]] = Cofree[F, Unit]

  /** Alias for `µ`. */
  type Mu[F[_]] = µ[F]

  /** Alias for `In`. */
  val µ = In

  /**
   * Declaration of any `Functor` as an instance of `Show` using `toString`
   * on the case classes underlying the functor. Requires the item type
   * of the functor to be an instance of `Show`. Maps the corresponding
   * `show` method over the functor and then applies `toString`.
   */
  implicit def functorToStringShow[F[_], A](implicit A: Show[A], F: Functor[F]): Show[F[A]] = Show.shows { n =>
    F.map(n)(A.shows _).toString
  }
}
