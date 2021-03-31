# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# unreleased

# 0.0.2

New scala versions:
- `3.0.0-RC2`

Dependency upgrades:
- [cats-core](https://github.com/typelevel/cats) `2.5.0`

# 0.0.1

Dependencies:
- [cats-core](https://github.com/typelevel/cats) `2.4.2`
- [shapeless](https://github.com/milessabin/shapeless) `2.3.3` — only for Scala 2

Initial release, cross-compiled for Scala:

- 2.12, 2.13 — encoding provided via [shapeless](https://github.com/milessabin/shapeless)
- 3.0.0-RC1 — opaque type encoding, no shapeless dependency
