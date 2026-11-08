# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

Bearings is a Fabric mod for Minecraft 1.21.1 that hides F3 coordinates and time unless the player carries (or wears on a Trinkets belt slot) a compass or clock. `Bearings.md` in this directory is the design spec, including config options, item tags, open questions and the manual test checklist. Read it before implementing features.

## Commands

```sh
./gradlew build       # compile and produce jars in build/libs/
./gradlew runClient   # launch a dev client
./gradlew runServer   # launch a dev server
./gradlew genSources  # decompile Minecraft for reading vanilla code
./gradlew runClient -PnoCompat  # dev client without Trinkets and BetterF3
```

There are no automated tests and no lint setup. Verification is manual, using the Testing section of `Bearings.md`.

The code targets Java 21 (`options.release = 21`); CI builds with JDK 25 using `./gradlew build`.

## Architecture

- **Split source sets** (`splitEnvironmentSourceSets()`): `src/main` is common code and `src/client` is client-only. `main` cannot reference `client` classes or client-only Minecraft classes, so the F3 gating belongs in `src/client`.
- **Mojang mappings** (`loom.officialMojangMappings()`): use Mojang names such as `DebugScreenOverlay`, not Yarn names.
- **Mixins**: all mixins are client-side, listed in `src/client/resources/bearings.client.mixins.json`. `defaultRequire: 1` makes an injection that fails to match a hard error.
- **Versions** live in `gradle.properties`. `fabric.mod.json` receives `${version}` through `processResources`.
- **Gating**: `Instrument` decides whether an item is carried, and `DebugGating` holds the rules and edits vanilla's F3 line lists by string prefix. Those prefixes must match `DebugScreenOverlay`'s text. It always accepts the vanilla items as well as the `bearings:shows_*` tags, because a server without the mod sends no tags.

## Optional integrations

Trinkets and BetterF3 are `modCompileOnly`, and the mod must load without either.

- **Trinkets**: belt slots are data only (`data/trinkets/...`). Code touching its API stays in `TrinketsCompat`, which is only loaded after an `isModLoaded` check.
- **BetterF3** replaces vanilla's left and right columns, so `BetterF3Gating` hides its lines by id. `BearingsMixinPlugin` skips the BetterF3 mixin when the mod is absent. Lines added to `getGameInformation` (Time, Last Death) reach BetterF3 through its Misc Left module.
