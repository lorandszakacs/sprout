# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# unreleased

# 0.0.5

### dependency upgrades

- [shapeless](https://github.com/milessabin/shapeless) `2.3.7` — only used for Scala 2

### internals:

- bump sbt to `1.5.3`
- bump Scala to `2.13.6` and `2.12.14` respectively
- bump `sbt-spiewak` to `0.21.0`

# 0.0.4

### new scala versions:

- `3.0.0` :party: — JVM, and JS

### dropped scala versions:

- `3.0.0-RC2`
- `3.0.0-RC3`

### dependency upgrades

- [cats-core](https://github.com/typelevel/cats) `2.6.1`
- [shapeless](https://github.com/milessabin/shapeless) `2.3.6` — only used for Scala 2
- [munit](https://github.com/scalameta/munit/releases) `0.7.26`

# 0.0.3

Maintenance release. No breaking changes, source, or binary.

### new scala versions:

- `3.0.0-RC3` — JVM, and JS

### dropped scala versions:

- `3.0.0-RC1`

### dependency upgrades:

- [cats-core](https://github.com/typelevel/cats) `2.6.0`
- [shapeless](https://github.com/milessabin/shapeless) `2.3.4` — only used for Scala 2

### internals:

- replace `munit-cats-effect-3` w/ simple `munit`
- share the same tests between Scala 2 + 3 - huge win for reliability

# 0.0.2

### new scala versions:

- `3.0.0-RC2` — JVM, and JS

### dependency upgrades:

- [cats-core](https://github.com/typelevel/cats) `2.5.0`

# 0.0.1

### dependencies:

- [cats-core](https://github.com/typelevel/cats) `2.4.2`
- [shapeless](https://github.com/milessabin/shapeless) `2.3.3` — only for Scala 2

Initial release, cross-compiled for Scala:

- 2.12, 2.13 — encoding provided via [shapeless](https://github.com/milessabin/shapeless)
- 3.0.0-RC1 — opaque type encoding, no shapeless dependency
