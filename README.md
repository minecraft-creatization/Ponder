# Ponder — Minecraft 26.3

Ponder, the in-game scene system used to explain and showcase mod behaviour, ported to
**Minecraft 26.3** on Fabric.

This repository is a standalone module. It was split out of the Create port, where Ponder previously
lived as the package `com.zurrtum.create.client.ponder` and was compiled into Create's single jar.
Package names were deliberately left unchanged so Create and the addon trees need no import churn.

## Requirements

- Java 25
- No Gradle install needed — the wrapper (`gradlew` / `gradlew.bat`) is included
- **Flywheel**, cloned as a sibling directory (see below)

## Build

`ponder` depends on `flywheel`, which is resolved from a built sibling checkout:

```bash
git clone https://github.com/minecraft-creatization/Flywheel flywheel-26.3
cd flywheel-26.3 && ./gradlew build && cd ..
./gradlew build            # jar lands in build/libs/
```

The dependency is resolved by glob and the build **fails loudly** if it does not match exactly one jar,
rather than silently building against a stale one:

```
Expected exactly one flywheel jar in ../flywheel-26.3/build/libs, found 0: [].
Build flywheel-26.3 first (cd ../flywheel-26.3 && ./gradlew build).
```

On Windows, `compile.ps1` (compile only, reports the *unique* javac error count) and `build.ps1` are
provided as thin wrappers.

## What is here, and what is deliberately not

This repository is **MIT**, so it contains only MIT code: the Ponder framework, the `catnip` library,
and Ponder's own lang and debug assets.

**Create's own ponder scenes are not here on purpose.** `assets/create/ponder/` (178 `.nbt` scenes) and
Create's `client/infrastructure/ponder/` are inside Create's All-Rights-Reserved asset scope, and must
not be redistributed here. The handful of files that glue Ponder to Create live in Create's repository.

## Notes

- `catnip` ships here rather than separately because `catnip` and `ponder` are mutually recursive — a
  cycle that predates this port and is present in the 26.2 baseline. Upstream's own Ponder repository
  resolves it the same way, shipping `catnip` and `ponder` together.
- Upstream's Ponder may name Create's internals freely because it ships inside Create. To keep this a
  real module, its core-Create references were inverted behind a single seam,
  `client/ponder/api/platform/PonderPlatform`, with safe vanilla defaults — so the framework still works
  when loaded without Create.
- Sodium integration is compile-only and detected at runtime; the one Sodium-dependent mixin is returned
  from `PonderMixinPlugin` rather than listed in `ponder.mixins.json` (applied unconditionally).

## Status

Ported and compiling at **0 javac errors**. Ponder was previously observed registering its scenes
(`Registering Ponder Scenes took 234.0 ms`) with scenes confirmed opening on screen while it was still
part of Create's jar. **It has not been run since the split**, and compilation is not evidence of correct
rendering on 26.3 — 26.3 fails silently. Treat the split as structurally verified, not visually verified.

## Licence

MIT — see [LICENSE](LICENSE). Ponder is by The Create Team.