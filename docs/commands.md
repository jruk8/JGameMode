---
icon: material/console
---

# Commands

All mode commands switch gamemodes with vanilla selectors. An omitted
target means yourself; console must always name one. Every switch needs
`jgamemode.use`, plus the scope (`jgamemode.self` or `jgamemode.other`)
and the mode (`jgamemode.use.<mode>`) permissions.

| Command | Description | Permission |
| --- | --- | --- |
| `/gm [<mode>\|toggle] [targets]` | Set gamemode, or toggle without a mode. | `jgamemode.use` |
| `/gms [targets]` | Switch to survival. | `jgamemode.use` |
| `/gmc [targets]` | Switch to creative. | `jgamemode.use` |
| `/gma [targets]` | Switch to adventure. | `jgamemode.use` |
| `/gmsp [targets]` | Switch to spectator. | `jgamemode.use` |
| `/gmt [targets]` | Toggle spectator and back. | `jgamemode.use` |
| `/t [targets]` | Toggle spectator and back (native root). | `jgamemode.use` |
| `/gm0\|/gm1\|/gm2\|/gm3 [targets]` | Switch directly by numeric id. | `jgamemode.use` |
| `/gamemode [<mode>\|toggle] [targets]` | Vanilla gamemode through this plugin. | `jgamemode.use` |
| `/jgamemode <help\|version\|reload>` | Help, version, and config reload (aliases `/jgm`, `/jg`). | — |

The single-letter roots `/s`, `/c`, `/a` and `/sp` switch directly when
their label is free (see [configuration.md](configuration.md)).

- **Modes** are matched dynamically: full names (`survival`), shorthands
  (`s`, `c`, `a`, `sp`), numbers (`0`-`3`) and any substring (`creat`
  works out to creative, first match wins). Gibberish errors out.
- **Toggle** (`/gm`, `/gmt`, `/t`, `/gm toggle`) swaps between spectator
  and the last non-spectator mode, mirroring vanilla F3+N: leaving for
  spectator caches where you came from, coming back never overwrites the
  ticket. Spectators without history fall back to creative, then survival,
  then adventure, by permission.
- **History** is stored per player in the database (only this plugin's
  switches count) and survives restarts. Switching directly to spectator
  never overrides a stored ticket, but seeds an empty cache so the next
  toggle has somewhere to go.

Direct examples: `/gmc`, `/gm sp Steve`, `/gm tog @a`, `/gamemode 1`.

## Adding a Command

1. Create a command class in a new subpackage of `core.commands` (e.g.
   `core.commands.user.fun`) that builds a Brigadier node with
   `Commands.literal(...)`, gates it with `requires()`, and runs its logic
   in `executes(...)`.
2. Attach the node to the root in the matching registrar
   (`UserCommandsRegistrar` or `AdminCommandsRegistrar`) with
   `root.then(new MyCommand(...).node())`, injecting only the services it
   needs (commonly `CommandContext`).
3. Declare the permission in `paper-plugin.yml` (with a constant in
   `GameModePermissions`), document it in [permissions.md](permissions.md)
   and add a `HelpEntry` so the command shows up in `/jgamemode help`.

See [api.md](api.md) for the full walkthrough.
