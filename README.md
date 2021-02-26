# sprout

## getting started

This library is published for Scala 3, Scala 2 (with some caveats), and ScalaJS.

### sprout

```scala
libraryDependencies ++= "com.lorandszakacs" %% "sprout" % "0.0.1"
```

Depends on:

- [cats-core](https://github.com/typelevel/cats) `2.4.2`
- [shapeless](https://github.com/milessabin/shapeless/) `2.3.3` — only for Scala 2

### snapshots

To fetch snapshots of the library add the following to your build:

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

## motivation

An opinionated "newtype" encoding for Scala 3 using opaque types. It exists because in real world projects we keep
seeing how it's extremely useful to never have any primitive type show up anywhere in your domain model, we're talking
about literally hundreds of "new types", for every little thing. Oftimes, they don't need much more extra validation
than their underlying type, so we need an encoding that allows us to easily continue using semi-auto derivation of
various other codecs for encodings (circe JSON codecs, doobie Put/Get, http4s query param codecs, config readers, etc.).

## what's different?

Why use sprout, and not [refined](https://github.com/fthomas/refined), [newtypes](https://github.com/gvolpe/newtypes), [scala-newtype](https://github.com/estatico/scala-newtype), or just plain old `opaque type`? Well, nothing revolurionary really. Mostly because it is written and maintained _with the intent_ to provide easy integration with 3rd party libraries via the `NewType[OldType, NewType]` typeclass. And it allows you to trivially write such integrations in your own projects. Thus giving you "newtypes" that work seemlessly in your own gynormous code base!

TL;DR, if your project needs seemless refinement of types whose values are coming somewhere from the outside (e.g. user input) and compile time
awesomeness like that found in `refined` doesn't help anymore then `sprout` might just be the lightweigh alternative for you!

## example

WIP: section WIP as library evolves. Check tests for full set of examples and features of the library.

Define a new type as simply as this:

```scala
import sprout.*
type TestSprout = TestSprout.Type
object TestSprout extends Sprout[String]

val s: TestSprout = TestSprout("a plain string")
```

If you wish to add `cats.Show` and `cats.Eq` typeclasses, then you can extend the appropriate types:

```scala
import sprout.*

type TestSprout = TestSprout.Type
object TestSprout extends Sprout[String]
  with SproutEq[String]
  with SproutShow[String]
  with SproutOrder[String]
```

### subtypes

There are also subtyping alternatives where you create a "subtype" of your base type by extending `SproutSub[T]` e.g:

```scala
import sprout.*

type TestSprout = TestSprout.Type
object TestSprout extends SproutSub[String]
  with SproutEq[String]
  with SproutShow[String]
  with SproutOrder[String]
val s: TestSprout = TestSprout("a plain string")
val s2: String = s //this compiles now, as opposed to when extending Sprout[String]
```

### refined types

There is also a trait to add more checks:

```scala
private type TestSprout = TestSprout.Type

private object TestSprout
  extends SproutRefinedThrow[String]
  with SproutEq[String]
  with SproutShow[String]
  with SproutOrder[String] {

  override def refine[F[_]: MonadThrow](o: String): F[String] =
    if (o.contains("sprout")) o.pure[F] else new RuntimeException("Invalid sprout string").raiseError[F, String]
}

val errored: IO[TestSprout] = TestSprout[IO]("1")
val succces: IO[TestSprout] = TestSprout[IO]("1-sprout")
```

## scala 2 support

There exists a scala 2 implementation of roughly the same functionality using shapeless.

:warning: Source compatability is a goal, but not a guarantee. :warning:

The Scala 2 version exists mostly to use in projects where migration to Scala 3 is on the horizon, but not quite there yet.
