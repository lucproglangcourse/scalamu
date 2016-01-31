[![Build Status](https://travis-ci.org/LoyolaChicagoCode/scalamu.svg)](https://travis-ci.org/LoyolaChicagoCode/scalamu)
[![Coverage Status](https://coveralls.io/repos/LoyolaChicagoCode/scalamu/badge.svg?branch=master)](https://coveralls.io/r/LoyolaChicagoCode/scalamu?branch=master)
[![Codacy Badge](https://www.codacy.com/project/badge/8996f07e06ad46019b85c351db66df77)](https://www.codacy.com/public/laufer/scalamu)

[![Issue Stats](http://issuestats.com/github/LoyolaChicagoCode/scalamu/badge/pr)](http://issuestats.com/github/LoyolaChicagoCode/scalamu)
[![Issue Stats](http://issuestats.com/github/LoyolaChicagoCode/scalamu/badge/issue)](http://issuestats.com/github/LoyolaChicagoCode/scalamu)


# Scalaµ

Small extensions to [Scalaz](http://github.com/scalaz/scalaz) and
examples for exploring and [teaching](lucproglangcourse.github.io)
the software engineering benefits of defining algebraic data types
as initial F-algebras.

# Getting Scalaµ

To use Scalaµ in your project, add these lines to your `build.sbt`:

    resolvers += "laufer@bintray" at "http://dl.bintray.com/laufer/maven"

    libraryDependencies += "edu.luc.etl" %% "scalamu" % "0.4.1"

You can also just clone this project and play around with the example
worksheets.

Scalaµ works with Scala 2.10 and 2.11 and uses Scalaz 7.2.0.

# Quick Start

Natural numbers as the initial algebra for the `Option` endofunctor.

    import scalaz._
    import Scalaz._
    import scalamu._

    type Nat = µ[Option]

    val zero = In[Option](None)
    val succ = (n: Nat) => In[Option](Some(n))

    val two   = succ(succ(zero))
    val three = succ(three)

Conversion to `Int` as a catamorphism.

    val toInt: Algebra[Option, Int] = {
      case None    => 0
      case Some(n) => n + 1
    }

    three cata toInt assert_=== 3

Conversion from `Int` as an anamorphism.

    val fromInt: Coalgebra[Option, Int] = n => {
      require { n >= 0 }
      if   (n == 0) None
      else          Some(n - 1)
    }

    µ.unfold(7)(fromInt) cata toInt assert_=== 7

Addition as another catamorphism.

    val plus: Nat => Algebra[Option, Nat] = m => {
      case None    => m
      case Some(n) => succ(n)
    }

    two cata plus(three) cata toInt assert_=== 5

# API Documentation

Available [here](http://loyolachicagocode.github.io/scalamu/doc/#scalamu.package).

# Guide to Examples

We recommend starting with
[this standalone example](https://github.com/LoyolaChicagoCode/expressions-algebraic-scala)
of arithmetic expressions.

We then recommend looking at the example worksheets in this order:

1. `natf`
1. `natoption`
1. `natHigherKinded`
1. `mylist`
1. `orgchart`

To run an example worksheet:

    $ sbt test:console
    :load examples/natf.sc

[Here is a more advanced example](https://github.com/LoyolaChicagoCode/simpleimperative-algebraic-scala)
of a simple imperative language interpreter implemented using Scalaµ.

# Glossary

TODO

# References

- [Understanding F-Algebras](https://www.fpcomplete.com/user/bartosz/understanding-algebras)
- [F-Algebras (Wikipedia)](http://en.wikipedia.org/wiki/F-algebra)
- [Haskell recursion patterns library reference](http://hackage.haskell.org/package/pointless-haskell-0.0.8/docs/Generics-Pointless-RecursionPatterns.html)
