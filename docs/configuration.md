# Configuration

The plugin writes `config.yml`, `messages.yml` and `sounds.yml` into its data
folder (`plugins/JTemplate/`) on first start. Edit a file and run
`/jtemplate reload` to apply the changes without restarting the server.
Missing keys are re-added with defaults; unknown keys are removed.

## config.yml

```yaml
updateChecker:
  enabled: true
aliases:
  - jt
storage:
  dbFile: jtemplate.db
colors:
  plainColor: '<#b8d8e0>'
  brandColor: '<#84b8db>'
  highlightColor: '<#ebf1f2>'
  errorColor: '<#415ad9>'
metrics:
  enabled: true
```

### `updateChecker`

- `enabled` » checks GitHub releases for new versions on enable and on
  reload. The check runs asynchronously; findings go to the console and
  in-game to players holding `jtemplate.reload` (on join and via
  `/jtemplate version`). Update notices link both the GitHub release and
  the Modrinth project page.

### `aliases`

Extra labels for the `/jtemplate` root command (`/jt` works out of the
box). Running the root with no arguments shows the help listing. Blanks,
duplicates and the `jtemplate` label itself are ignored. Applies on
restart; `/jtemplate reload` reports when a restart is needed.

### `storage`

- `dbFile` » name of the SQLite database file created in the plugin's data
  folder. The database holds generic per-player and server-wide counters
  (tables `player_stats` and `global_stats`).

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
- `core.reloadRestartRequired` » shown when root labels changed and need
  a restart to apply.
- `core.helpHeader` / `core.helpEntryFormat` » help command layout
  (use `{version}` for the header; `{syntax}` and `{description}` per entry).
- `core.versionSuccess` » version command output (use `{version}` and
  `{authors}`; linked author names arrive pre-formatted).
- `core.updateAvailable` » update notice (use `{latest}`, `{current}`,
  `{url}` and `{modrinth-url}`).
- `core.noPermission` » the sender lacks a permission (use `{permission}`).
- `core.noTargets` » a target selector matched no players.
- `core.consoleNeedsTarget` » console ran a command without a target.

Messages can embed PlaceholderAPI placeholders (`%player_name%`) when
PlaceholderAPI is installed.

## sounds.yml

Sound effects for command feedback. Each entry supports `enabled`, a
namespaced `sound` key, `pitch` and `volume`
([sound explorer](https://jruk8.github.io/JSoundExplorer/)):

- `neutral` » neutral feedback for ordinary commands, heard by the executor
  and every affected player.
- `reset` » feedback for state resets, heard by the executor and every
  affected player.
- `reload` » played when the configuration is reloaded.
- `help` » played when the help menu is shown.
- `error` » played on failed commands (invalid input, missing permission,
  failed reload).

## Storage (SQLite)

The database file (default `jtemplate.db`) lives in the plugin's data folder
and is opened on enable, reloaded on `/jtemplate reload`, and closed on plugin
disable. The JDBC driver comes from the Minecraft server itself, so the plugin
jar stays small; if your server distribution does not bundle SQLite, the
plugin logs a warning and runs without state persistence.
