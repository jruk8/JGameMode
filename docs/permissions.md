# Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `jgamemode.use` | Switch gamemodes (master switch; also needs the scope and mode permissions). | OP |
| `jgamemode.use.survival` | Switch to survival. | Everyone |
| `jgamemode.use.creative` | Switch to creative. | Everyone |
| `jgamemode.use.adventure` | Switch to adventure. | Everyone |
| `jgamemode.use.spectator` | Switch to spectator. | Everyone |
| `jgamemode.self` | Switch your own gamemode (needs `jgamemode.use`). | Everyone |
| `jgamemode.other` | Switch other players' gamemode (needs `jgamemode.use`). | OP |
| `jgamemode.reload` | Use `/jgamemode reload`. | OP |
| `jgamemode.help` | Use `/jgamemode help` (entries are permission-filtered). | Everyone |
| `jgamemode.version` | Use `/jgamemode version`. | Everyone |

`jgamemode.use` alone switches nothing: every switch also checks the scope
(`self` for yourself, `other` for anyone else) and the target mode node.
Deny a mode opt-out style by negating its node, e.g.
`jgamemode.use.spectator: false` prevents switching to spectator.

Permissions are checked on the command nodes with Brigadier `requires()`
predicates (see `CommandContext.requiresPermission`); the node constants
live in `GameModePermissions`. Commands that target other players check
`jgamemode.other` through `TargetSelector`. See
[commands.md](commands.md).
