---
icon: material/tune
---

# Configuration

The plugin writes `config.yml`, `messages.yml` and `sounds.yml` into its data
folder (`plugins/JGameMode/`) on first start. Edit a file and run
`/jgamemode reload` to apply the changes without restarting the server
(command registration is the exception; see `commands` below). Missing
keys are re-added with defaults; unknown keys are removed.

## config.yml

```yaml
updateChecker:
  enabled: true
aliases:
  - jgm
  - jg
overrideVanillaCommand: true
storage:
  dbFile: jgamemode.db
commands:
  gm:
    enabled: true
    aliases: []
  survival:
    enabled: true
    aliases:
      - gm0
  creative:
    enabled: true
    aliases:
      - gm1
  adventure:
    enabled: true
    aliases:
      - gm2
  spectator:
    enabled: true
    aliases:
      - gm3
  toggle:
    enabled: true
    aliases: []
singles:
  enabled: true
colors:
  plainColor: '<#e0d8b8>'
  brandColor: '<#edca3b>'
  highlightColor: '<#f2f0e9>'
  errorColor: '<#d9ba41>'
metrics:
  enabled: true
```

### `updateChecker`

- `enabled` » checks GitHub releases for new versions on enable and on
  reload. The check runs asynchronously; findings go to the console and
  in-game to players holding `jgamemode.reload` (on join and via
  `/jgamemode version`). Update notices link both the GitHub release and
  the Modrinth project page.

### `aliases`

Extra labels for the `/jgamemode` hub command (`/jgm` and `/jg` work out
of the box). Running the hub with no arguments shows the help listing.
Blanks, duplicates and the `jgamemode` label itself are ignored. Applies
on restart; `/jgamemode reload` reports when a restart is needed.

### `overrideVanillaCommand`

- `true` » vanilla `/gamemode` is routed through this plugin: same
  targets plus dynamic mode resolution, `/gamemode toggle` and toggle
  history. `false` » vanilla `/gamemode` is left untouched.

### `storage`

- `dbFile` » name of the SQLite database file created in the plugin's data
  folder. The database holds usage counters (tables `player_stats` and
  `global_stats`) and each player's toggle history (key
  `jgm_toggle_history`).

### `commands`

Each channel has an `enabled` toggle registering (or hiding) its
standalone root command (`gm`, `gms`, `gmc`, `gma`, `gmsp`, `gmt`, plus
the native `/t` toggle root), plus `aliases` » extra labels for that root
(`gm0`-`gm3` answer to the matching mode root by default). The
`/jgamemode` subcommands always exist. Aliases colliding with a hub label,
a subcommand or another alias are ignored. Changes take effect after a
restart; `/jgamemode reload` reports when a restart is needed.

### `singles`

- `enabled` » registers the `/s`, `/c`, `/a` and `/sp` single-letter
  roots. Each label is only registered when free; taken labels are skipped
  with a console warning so one contested alias can never break the plugin.

### `colors`

The message color palette. Every `messages.yml` entry uses these via
`{plain}`, `{brand}`, `{hl}` and `{error}`, resolved when the message is
rendered; editing a color and reloading is enough, the messages
themselves never hardcode colors.

### `metrics`

- `enabled` » sends anonymous usage statistics via bStats (open-source,
  no personal data or IP addresses). Read once at startup: disabling it
  needs a server restart to take effect.

## messages.yml

The plugin messages, always in MiniMessage format. Every message starts
with `{prefix}` (which includes its trailing space), and colors come
from the `config.yml` palette. Placeholders use `{braces}`; angle
brackets are raw MiniMessage, and `\n` breaks the line:

- `prefix` » prefix prepended via the `{prefix}` placeholder.
- `core.reloadSuccess` / `core.reloadFailure` » reload outcome
  (use `{time}`).
- `core.reloadRestartRequired` » shown when command toggles changed and
  need a restart to apply.
- `core.helpHeader` / `core.helpEntryFormat` » help command layout
  (use `{version}` for the header; `{syntax}` and `{description}` per entry).
- `core.versionSuccess` » version command output (use `{version}` and
  `{authors}`; linked author names arrive pre-formatted).
- `core.updateAvailable` » update notice (use `{latest}`, `{current}`,
  `{url}` and `{modrinth-url}`).
- `core.noPermission` » the sender lacks a permission (use `{permission}`).
- `core.noTargets` » the target selector matched no players.
- `core.consoleNeedsTarget` » console ran a command without a target.
- `mode.setSelf` » feedback for self-targeting switches (use `{mode}`).
- `mode.setOther` » summary for the sender when one other player is
  switched (use `{mode}` and `{targets}`).
- `mode.setMultiple` » summary for the sender when several players are
  switched (use `{mode}`, `{targets}` and `{count}`). Mixed toggle
  targets are grouped by resulting mode, one summary per group.
- `mode.setTarget` » notification sent to each affected player
  (use `{mode}`).
- `mode.toggleNoPermission` » a toggle cannot swap (use `{permission}`
  for the tip naming the most relevant mode node).
- `mode.invalid` » the mode token matched no gamemode (use `{input}`).
- `mode.noModePermission` » the sender lacks the permission for the
  target gamemode (use `{permission}` and `{mode}`).

Messages can embed PlaceholderAPI placeholders (`%player_name%`) when
PlaceholderAPI is installed.

## sounds.yml

Sound effects for command feedback. Each entry supports `enabled`, a
namespaced `sound` key, `pitch` and `volume`
([sound explorer](https://jruk8.github.io/JSoundExplorer/)):

- `neutral` » neutral feedback for gamemode switches, heard by the executor
  and every affected player.
- `reload` / `version` / `help` » celebration feedback for the hub
  commands, heard by the executor.
- `error` » played on failed commands (invalid input, missing permission,
  failed reload).

## Storage (SQLite)

The database file (default `jgamemode.db`) lives in the plugin's data folder
and is opened on enable, reloaded on `/jgamemode reload`, and closed on plugin
disable. The JDBC driver comes from the Minecraft server itself, so the plugin
jar stays small; if your server distribution does not bundle SQLite, the
plugin logs a warning and runs without state persistence.
