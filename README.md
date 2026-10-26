<div align="center">

![JGameMode banner](https://raw.githubusercontent.com/jruk8/JGameMode/main/docs/assets/banner-1280x640.png)

<h3>Modern 26.x plugin for /gmc and /gm 0, with a F3+N-style switcher.</h3>

### [Modrinth](https://modrinth.com/project/jgamemode) · [Wiki](https://jruk8.github.io/JGameMode/) · [GitHub](https://github.com/jruk8/JGameMode)

</div>

# Quick Start 🏃

1. Drop the jar into `plugins/` and restart the server.
2. Run **`/gm s`**.
3. Toggle with **`/gmt`** (spectator and back, like F3+N).

**That's it!** Try aliases (`/gm creat`, `/gm 1`) or
vanilla selectors (`Steve`, `@a`). 

**Next:** see config or run `/jgm help`.

# Features

- **Direct aliases** » `/gms`, `/gmc`, `/gma`, `/gmsp`, plus
  `/gm s|c|a|sp` and `/s`, `/c`, `/a`, `/sp`.
- **Typo resolution** » Feeling lazy? Just type partially, like: `/gm spec`.
- **F3+N-style switcher** » `/gm`, `/gmt` and `/t` switch between your last mode
  and spectator.
- **Vanilla expansion** » expands vanilla `/gamemode`.
- **Full configurability** » toggle any aliases off or manage permissions.
- **Also for serious deployment** » MiniMessage, sounds, PAPI placeholders
  (`%jgm_uses%`, `%jgm_toggle_uses%`, per-mode and global variants),
  and an SQLite store for history.

### Requirements

- Server Software » [Paper 26.2+](https://papermc.io/downloads/paper) (**Folia🍃** supported)
- Java Version » **Java 25**
- Dependencies » **None** (PlaceholderAPI is optional, only for the
  `%jgm_...%` placeholders)

### Commands

| Command | Description |
| --- | --- |
| `/gm [<mode>\|toggle] [targets]` | Set gamemode, or toggle without a mode. |
| `/gms\|/gmc\|/gma\|/gmsp [targets]` | Switch directly to a gamemode. |
| `/gmt [targets]` | Toggle spectator and back (alias `/t`). |
| `/s\|/c\|/a\|/sp [targets]` | Single-letter shortcuts (unless other plugins override). |
| `/gamemode [<mode>\|toggle] [targets]` | Vanilla gamemode through this plugin. |
| `/jgamemode <help\|version\|reload>` | Help, version, and config reload (alias `/jgm`). |

See the full [Commands Page](https://jruk8.github.io/JGameMode/commands/) for more info.

### Permissions

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
| `jgamemode.help` | Use `/jgamemode help`. | Everyone |
| `jgamemode.version` | Use `/jgamemode version`. | Everyone |

### Config

Find `config.yml`, `messages.yml` and `sounds.yml` in `plugins/JGameMode/`:

- `updateChecker` » checks for updates.
- `aliases` » aliases for `/jgm` root command (needs **R**estart).
- `overrideVanillaCommand` » extend Vanilla's `/gamemode`.
- `storage` » SQLite DB (stats plus toggle history).
- `commands` » /gmc, /gms, ... aliases (**R**).
- `singles` » boolean for `/s`, `/c`, `/a`, `/sp` (**R**).
- `colors` » color palette for messages.
- `metrics` » anonymous bStats stats (**R**).
- `messages.yml` / `sounds.yml` » messages and sounds.

See the full [Configuration Page](https://jruk8.github.io/JGameMode/configuration/) for more info.

# Links

- [Documentation](https://jruk8.github.io/JFlySpeed/)
- [Discord](https://discord.com/invite/hkWmCVmWDC)
- [GitHub](https://github.com/jruk8/JFlySpeed)

### Donate ☕

If you like my plugins, you can support my work through a [**small donation**](https://ko-fi.com/jruk). **Thanks!** 👐
