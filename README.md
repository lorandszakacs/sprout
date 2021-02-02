# sprout

## getting started

This library is published for Scala 3, and ScalaJS. Currently, only the SNAPSHOT version.

```scala
val sproutVersion = "0.1.0-SNAPSHOT"

libraryDependencies ++= "com.lorandszakacs" %% "sprout" % sproutVersion
```

And for the module which also depends on `cats-effect` and allows refining types into `F[_]: Sync` constraints, use:

```scala
libraryDependencies ++= "com.lorandszakacs" %% "sprout-effect" % sproutVersion
```

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

## what's new?

Why use sprout, and not [refined](https://github.com/fthomas/refined), [newtypes](https://github.com/gvolpe/newtypes)
, [scala-newtype](https://github.com/estatico/scala-newtype), or just plain old `opaque type`? Well, nothing
revolurionary really. Mostly because it is written and maintained _with the intent_ to provide easy integration with 3rd
party libraries via the `NewType[OldType, NewType]` typeclass. And it allows you to trivially write such integrations in
your own projects. Thus giving you "newtypes" that work seemlessly in your own gynormous code base!

## example

WIP: section WIP as library evolves.

Define a new type as simply as this:

```scala
  import sprout._

object TestSprout extends SproutNewType[String]

type TestSprout = TestSprout.T
```

If you wish to add `cats.Show` and `cats.Eq` typeclasses, then you can extend the appropriate types:

```scala
  import sprout._

type TestSprout = TestSprout.T

object TestSprout extends SproutNewType[String]
  with SproutNewType.SproutShow[String]
  with SproutNewType.SproutEq[String]
```

## scala 2 support

There exists a scala 2 implementation of roughly the same functionality using shapeless. There is no binary
compatability between Scala 2 and Scala 3 versions, and source compatability is a goal, but not a guarantee. The Scala 2
version exists mostly to use in projects where migration to Scala 3 is on the horizon, but not quite there yet.