import scalaz.Cofree
import scalaz.Functor
import scalaz.syntax.functor._

package object scalak {

  /**
   * A morphism for a (nongeneric) F-algebra.
   *
   * @tparam F endofunctor (type constructor of arity 1) of the category of Scala types
   * @tparam B carrier object of the algebra
   */
  type Algebra[F[_], B] = F[B] => B

  /**
   * A morphism for a generic F-algebra.
   *
   * @tparam A generic item type of the F-algebra
   *           (to be contained by an instance of Cofree)
   * @tparam F endofunctor (type constructor of arity 1) of the category of Scala types
   * @tparam B carrier object of the algebra
   */
  type GenericAlgebra[A, F[_], B] = A => F[B] => B

  /**
   * A morphism for an F-coalgebra.
   *
   * @tparam F endofunctor (type constructor of arity 1) of the category of Scala types
   * @tparam B carrier object of the coalgebra
   */
  type Coalgebra[F[_], B] = B => F[B]

  /**
   * The least fixpoint operator on endofunctors in the category of Scala types.
   * This is used to form the initial algebra (recursive type) for a given endofunctor.
   * Implemented as a partial application of Cofree to Unit.
   *
   * @tparam F endofunctor whose least fixpoint we are forming
   */
  type µ[F[+_]] = Cofree[F, Unit]

  type Mu[F[+_]] = µ[F]

  /**
   * Constructor and deconstructor for instances of initial F-algebras.
   */
  object In {

    /**
     * Instance constructor.
     *
     * @tparam F endofunctor (type constructor of arity 1) of the category of Scala types
     * @param value an instance of F applied to the initial algebra
     * @return the resulting wrapped instance of the initial algebra for F
     */
    def apply[F[+_]](value: F[µ[F]])(implicit F: Functor[F]): µ[F] = Cofree((), value)

    /**
     * Extractor for pattern matching.
     *
     * @tparam F endofunctor (type constructor of arity 1) of the category of Scala types
     * @param node an instance of the initial algebra for F
     * @return the resulting unwrapped instance of F applied to the initial algebra
     */
    def unapply[F[+_]](node: µ[F]): F[µ[F]] = node.tail
  }

  /**
   * Wrapper to inject useful methods into Cofree
   * (similar to C# extension methods).
   *
   * @tparam F branching endofunctor of this collection
   * @tparam A generic item type of this collection
   */
  implicit class CofreeOps[F[+_], +A](self: Cofree[F, A]) {

    /**
     * Catamorphism (generalizeld fold) for Cofree.
     * Note that this is the only place with explicit recursion in this example.
     *
     * @param g function to apply to each node's value and partial results
     *          already computed for its children
     * @tparam B result type of the catamorphism
     * @return the result of applying the g-based catamorphism to the root
     */
    def cata[B](g: A => F[B] => B)(implicit F: Functor[F]): B =
      g(self.head)(self.tail map { _ cata g })
    // TODO paramorphism
  }

  /**
   * Wrapper to inject useful methods into µ.
   *
   * @tparam F endofunctor for this initial algebra
   */
  implicit class MuOps[F[+_]](self: µ[F]) {

    /**
     * Catamorphism (generalizeld fold) for µ.
     *
     * @param g function to apply to each node's value and partial results
     *          already computed for its children
     * @tparam B result type of the catamorphism
     * @return the result of applying the g-based catamorphism to the root
     */
    def cata[B](g: F[B] => B)(implicit F: Functor[F]): B =
      new CofreeOps(self) cata { _ => g }
    // TODO paramorphism
  }

  object µ {

    /**
     * Cofree corecursion with values at nodes converted to unit.
     */
    def unfold[F[+_], A](a: A)(f: A => F[A])(implicit F: Functor[F]): µ[F] =
      Cofree.unfoldC(a)(f) map Function.const()
  }

  val Mu = µ
}