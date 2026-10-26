# Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `jtemplate.other` | Target other players with commands that support selectors (needs the matching command permission too). | OP |
| `jtemplate.reload` | Use `/jtemplate reload`. | OP |
| `jtemplate.help` | Use `/jtemplate help` (entries are permission-filtered). | Everyone |
| `jtemplate.version` | Use `/jtemplate version`. | Everyone |

Permissions are checked on the command nodes with Brigadier `requires()`
predicates (see `CommandContext.requiresPermission`); the node constants
live in `TemplatePermissions`. Commands that target other players check
`jtemplate.other` through `TargetSelector`. See
[commands.md](commands.md).
