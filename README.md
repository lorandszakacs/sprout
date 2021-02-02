# sprout

## motivation

An opinionated "newtype" encoding for Scala 3 using opaque types. It exists because in real world projects we keep seeing how it's extremely useful to never have any primitive type show up anywhere in your domain model, we're talking about literally hundreds of "new types", for every little thing. Oftimes, they don't need much more extra validation than their underlying type, so we need an encoding that allows us to easily continue using semi-auto derivation of various other codecs for encodings (circe JSON codecs, doobie Put/Get, http4s query param codecs, config readers, etc.).

## what's new?

Why use sprout, and not [refined](https://github.com/fthomas/refined), [newtypes](https://github.com/gvolpe/newtypes), [scala-newtype](https://github.com/estatico/scala-newtype), or just plain old `opaque type`? Well, nothing revolurionary really. Mostly because it is written and maintained _with the intent_ to provide easy integration with 3rd party libraries via the `NewType[OldType, NewType]` typeclass. And it allows you to trivially write such integrations in your own projects. Thus giving you "newtypes" that work seemlessly in your own gynormous code base!

## example

## scala 2 support
There exists a scala 2 implementation of roughly the same functionality using shapeless. There is no binary compatability between Scala 2 and Scala 3 versions, and source compatability is a goal, but not a guarantee. The Scala 2 version exists mostly to use in projects where migration to Scala 3 is on the horizon, but not quite there yet.