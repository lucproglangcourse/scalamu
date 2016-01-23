package scalamu

import scalaz.Tree
import scalaz.syntax.tree._
import org.scalatest._

/**
 * Small sample test, to be expanded later to a proper test suite.
 */
class TreeCataTests extends FunSuite {

  // TODO expand

  val t = 1.node(
    2.node(
      4.leaf,
      5.leaf
    ),
    3.node(
      6.node(
        8.leaf,
        9.leaf
      ),
      7.leaf
    )
  )

  def depth[A]: A => Stream[Int] => Int = _ => {
    case Stream.Empty => 1
    case s            => 1 + s.max
  }

  test("Simple catamorphisms on trees should work") {
    assert { (t cata depth) == 4 }
  }
}
