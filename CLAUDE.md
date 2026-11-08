# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

Bearings is a Fabric mod for Minecraft 1.21.1 that hides F3 coordinates and time unless the player carries (or wears on a Trinkets belt slot) a compass or clock. `Bearings.md` in this directory is the design spec, including config options, item tags, open questions and the manual test checklist. Read it before implementing features.

The code is still the unmodified Fabric template: none of the spec is implemented, and `ExampleMixin`, `ExampleClientMixin` and the `fabric.mod.json` description/authors/contact are placeholders.

## Commands

```sh
./gradlew build       # compile and produce jars in build/libs/
./gradlew runClient   # launch a dev client
./gradlew runServer   # launch a dev server
./gradlew genSources  # decompile Minecraft for reading vanilla code
```

There are no automated tests and no lint setup. Verification is manual, using the Testing section of `Bearings.md`.

The code targets Java 21 (`options.release = 21`); CI builds with JDK 25 using `./gradlew build`.

## Architecture

- **Split source sets** (`splitEnvironmentSourceSets()`): `src/main` is common code and `src/client` is client-only. `main` cannot reference `client` classes or client-only Minecraft classes, so the F3 gating belongs in `src/client`.
- **Mojang mappings** (`loom.officialMojangMappings()`): use Mojang names such as `DebugScreenOverlay`, not Yarn names.
- **Mixins**: each source set has its own config, `src/main/resources/bearings.mixins.json` and `src/client/resources/bearings.client.mixins.json`. A new mixin class must be listed in the matching config. `defaultRequire: 1` makes an injection that fails to match a hard error.
- **Versions** live in `gradle.properties`. `fabric.mod.json` receives `${version}` through `processResources`.

## Dependencies from the spec not yet wired up

- **Trinkets** is a soft dependency: the mod must load and work from inventory alone without it. Belt support is planned as data only (`data/trinkets/...`).
- **BetterF3** builds its lines from modules, so it needs gating separately from vanilla's `DebugScreenOverlay`.

Neither is in `build.gradle` yet, and its `repositories` block is empty, so adding them requires their Maven repositories too.
