# Changelog
## [1.4.0] - 2026-10-27

### Features

- Static link buttons, new update notice and banner flag


### Miscellaneous

- Update for v1.3.0 [skip ci]

## [1.3.0] - 2026-10-27

### Features

- Print a startup banner on enable

> Brand-tinted title plus Modrinth, Discord and GitHub bullets.


### Miscellaneous

- Update for v1.2.0 [skip ci]

## [1.2.0] - 2026-10-27

### Bug Fixes

- Keep a return ticket when switching directly to spectator

> Seeds an empty toggle cache instead of leaving it blank, so the next
> toggle returns to the previous mode rather than the permission fallback.
> Existing tickets are still never overridden.


### Documentation

- Cover /t, /jg, /gm0-/gm3 and spectator seeding


### Features

- Add native /t toggle root

> /t is now a standalone root and hub subcommand sharing the toggle
> channel; a stale t alias can no longer collide with it.

- Add /jg hub alias, /gm0-/gm3 defaults, banner and gradient

> JFlyspeed-style ASCII banner plus tagline, /jg alongside /jgm, numeric
> mode aliases out of the box, and the new gold prefix gradient.


### Miscellaneous

- Update for v1.1.0 [skip ci]

## [1.1.0] - 2026-10-26

### Documentation

- Add JGameMode banner

> Replaces the template banner with the real 1280x640 JGameMode banner and references it from the README and docs landing pages.

- Hook favicon and light/dark icons to the site

> Adds the JGameMode favicon and header marks and wires theme logo, favicon and extra.logo_light, mirroring the sibling docs setup.

- Update README.md

- Update README.md

- Update README.md

- Update README.md

- Discord announce, header buttons and page icons

> Replaces the template announce with a Discord invite line, ports the stacked GitHub plus Join Discord header buttons with the search-after-sources layout, adds discord_url, shortens the nav to Plugin API, and adds Material page icons to every docs page.


### Features

- Chiseled bookshelf error cue

> Error feedback uses block.chiseled_bookshelf.insert at 1.1 pitch and full volume.


### Refactor

- Rename namespace to com.jruk8.jgamemode

> No traces of jtemplate or JTemplate remain: main and test packages, plugin group and main class coordinates, docs and agent references, and the shaded third-party prefix (now com.jruk8.jgamemode.libs) all use the new namespace.

## [1.1.0] - 2026-08-17

### Features

- Placeholders, storage (db)


### Miscellaneous

- Update for v1.0.5 [skip ci]


## [Unreleased]

### Features

- Add template PlaceholderAPI expansion with `authors_command_uses` and
  `authors_command_uses_global` placeholders and a configurable identifier
- Add reusable SQLite storage module (`jgamemode.db`) with generic
  per-player and global counters
- Add storage and placeholder config sections to `config.yml`

### Documentation

- Rewrite README and all docs as template documentation (replacing copied
  third-party content)
- Add AI/developer guidance to AGENTS.md

## [1.0.5] - 2026-08-17

### Bug Fixes

- Checkstyle .* import

- Test import


### Miscellaneous

- Bump plugin-conventions to 1.4.0 and remove useless parts

## [1.0.4] - 2026-08-16

### Bug Fixes

- Ci


### Build & CI

- Fix (please)

## [1.0.3] - 2026-08-16

### Bug Fixes

- Ci

