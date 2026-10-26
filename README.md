<div align="center">

<h3>Modern 26.x plugin for /gm s and /gmc.</h3>

### [Modrinth](https://modrinth.com/project/jgamemode) · [Wiki](https://jruk8.github.io/JGameMode/) · [GitHub](https://github.com/jruk8/JGameMode)

</div>

> Banner pending: drop a `JGameMode.png` banner at
> `docs/assets/banner-1280x640.png` and reference it here and in the docs.

# Quick Start 🏃

1. Drop the jar into `plugins/` and restart the server.
2. Run **`/gm s`**.
3. Toggle with **`/gmt`** (spectator and back, like F3+N).

**That's it!** Modes resolve dynamically (`/gm creat`, `/gm 1`),
targets use vanilla selectors (`/gmc Steve`, `/gm sp @a`), and
`/jgm help` lists the rest.

# Features

- **Direct aliases** » `/gms`, `/gmc`, `/gma`, `/gmsp`, plus
  `/gm s|c|a|sp` and the single-letter `/s`, `/c`, `/a`, `/sp` roots.
- **Dynamic resolution** » full names, numbers and substrings
  (`/gm tog` toggles); gibberish errors out.
- **Spectator toggle** » `/gm`, `/gmt` and `/t` swap between spectator
  and the last non-spectator mode, mirroring vanilla F3+N with
  permission checks.
- **Permission gating** » `use` master switch, opt-out per-mode nodes,
  `self`/`other` scopes; tab completion only shows permitted modes and
  selectors.
- **Vanilla override** » optional 1:1 `/gamemode` replacement (toggle
  and history included).
- **Also for serious deployment** » MiniMessage, sounds, PAPI placeholders
  (`%jgm_uses%`, `%jgm_toggle_uses%`, per-mode and global variants),
  and an SQLite store for stats and toggle history.

### Requirements

- Server Software » [Paper 26.2+](https://papermc.io/downloads/paper) (Folia supported)
- Java Version » **Java 25**
- Dependencies » **None** (PlaceholderAPI is optional, only for the
  `%jgm_...%` placeholders)

# Commands

| Command | Description |
| --- | --- |
| `/gm [<mode>\|toggle] [targets]` | Set gamemode, or toggle without a mode. |
| `/gms\|/gmc\|/gma\|/gmsp [targets]` | Switch directly to a gamemode. |
| `/gmt [targets]` | Toggle spectator and back (alias `/t`). |
| `/s\|/c\|/a\|/sp [targets]` | Single-letter shortcuts (when free). |
| `/gamemode [<mode>\|toggle] [targets]` | Vanilla gamemode through this plugin. |
| `/jgamemode <help\|version\|reload>` | Help, version, and config reload (alias `/jgm`). |

An omitted target means yourself; console must always name one. Modes
accept names, shorthands, numbers and substrings; the toggle swaps with
spectator. Full table and examples live in
[commands.md](https://github.com/jruk8/JGameMode/blob/main/docs/commands.md).

# Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `jgamemode.use` | Switch gamemodes (master switch; also needs scope and mode). | OP |
| `jgamemode.use.survival` | Switch to survival. | Everyone |
| `jgamemode.use.creative` | Switch to creative. | Everyone |
| `jgamemode.use.adventure` | Switch to adventure. | Everyone |
| `jgamemode.use.spectator` | Switch to spectator. | Everyone |
| `jgamemode.self` | Switch your own gamemode. | Everyone |
| `jgamemode.other` | Switch other players' gamemode. | OP |
| `jgamemode.reload` | Use `/jgamemode reload`. | OP |
| `jgamemode.help` | Use `/jgamemode help` (entries are permission-filtered). | Everyone |
| `jgamemode.version` | Use `/jgamemode version`. | Everyone |

# Configuration

First start generates `config.yml`, `messages.yml` and `sounds.yml` in
`plugins/JGameMode/`:

- `updateChecker` » the async GitHub update check (console log,
  join notice, `/version` line).
- `aliases` » extra labels for the `/jgamemode` hub (restart to apply).
- `overrideVanillaCommand` » route vanilla `/gamemode` through the plugin.
- `storage` » the SQLite database file (stats plus toggle history).
- `commands` » the standalone root toggles and aliases
  (restart to apply).
- `singles` » the `/s`, `/c`, `/a`, `/sp` single-letter roots.
- `colors` » the shared message color palette.
- `metrics` » anonymous bStats statistics (restart to apply).
- `messages.yml` / `sounds.yml` » every message and the sound played
  for each feedback event.

Every value is documented in [configuration.md](https://github.com/jruk8/JGameMode/blob/main/docs/configuration.md).

# Build

```shell
./gradlew build            # macOS/Linux
.\gradlew.bat build        # Windows
```

`build` runs the tests and both checkstyle tasks; the jar lands in
`build/libs/`. Built with Java 25 and the
[jruk8 plugin-conventions](https://github.com/jruk8/plugin-conventions).

### Links

- [Documentation](https://jruk8.github.io/JGameMode/)
- [GitHub](https://github.com/jruk8/JGameMode)

## Contributing

See [CONTRIBUTING.md](https://github.com/jruk8/JGameMode/blob/main/CONTRIBUTING.md).

&copy; 2026 jruk8. Licensed under [AGPL-3.0](https://github.com/jruk8/JGameMode/blob/main/LICENSE).
