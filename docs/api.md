---
icon: material/puzzle
---

# Plugin API

This page explains where new code plugs in. Every module follows the same
shape: a package under `com.jruk8.jgamemode.core`, its own config class (or
section), and a registrar implementing `Bootstrap` that is invoked from
`JGameModePlugin.setupCoreModules()`.

Dependencies flow one way, constructor-injected: configs -> services ->
commands/placeholders. There are no singletons or static state.

## Add a Command

Commands are Paper-native Brigadier nodes registered through
`LifecycleEvents.COMMANDS` (see `CommandsRegistrar`).

1. Create a package `core.commands.user.<name>` (or `admin`, or `mode`)
   with a command class exposing a `node()` method:

   ```java
   public class PingCommand {
       private final CommandContext ctx;

       public PingCommand(CommandContext ctx) { this.ctx = ctx; }

       public LiteralArgumentBuilder<CommandSourceStack> node() {
           return Commands.literal("ping")
                   .requires(CommandContext.requiresPermission(GameModePermissions.PING))
                   .executes(context -> {
                       // Own text lives in MessagesConfig; {braces} resolve from the map.
                       ctx.sendMessage(context.getSource(),
                               messages -> messages.getCore().getHelpHeader(), Map.of());
                       return Command.SINGLE_SUCCESS;
                   });
       }
   }
   ```

   Commands are Paper-native Brigadier nodes registered through
   `LifecycleEvents.COMMANDS` (see `CommandsRegistrar`). The hub root
   (`jgamemode`, plus its configured aliases) carries a bare-root help
   fallback, so a new command only defines its own subcommand node.
   Messages are strictly MiniMessage: plugin placeholders use `{braces}`,
   angle brackets are raw MiniMessage.

2. Register it in the matching registrar
   (`UserCommandsRegistrar.register()`):
   `root.then(new PingCommand(ctx).node());`

3. Declare the permission in `paper-plugin.yml` (with a constant in
   `GameModePermissions`) and document the command in `docs/commands.md` and
   `docs/permissions.md`. Add a `HelpEntry` in `HelpCommand` so
   `/jgamemode help` lists it.

## Add a Config Option

The config classes are okaeri-configs. For a small option, add a field to an
existing config class; for a coherent new section, add a nested static class
and give it a field in the parent (see `PluginConfig.Colors` for the
pattern). The section will be created in the yaml file automatically and
reloaded by `ConfigRegistrar` on `/jgamemode reload`.

## Add a Stat to the Database

`SqliteStorage` exposes counter and value operations for generic key/value
state:

- `increment(key, player)` and `incrementGlobal(key)` per-player / global
- `playerTotal(key, player)` and `globalTotal(key)` reads
- `setPlayerValue(key, player, value)` and `playerValue(key, player)` writes
  and nullable reads (for toggle history)

Give your feature its own stat key and a small service class around
`SqliteStorage` (like `GameUsageTracker` or `ToggleHistory`), then inject
that service where state is produced (command) and consumed (placeholder).

## Add a Placeholder

Add a suffix to `GameUsageExpansion` (counter key plus `uses`,
`uses_<player>` and `uses_global` variants come from `suffixes()`):

```java
if ("my_stat_uses".equals(params)) {
    return selfUses(player, tracker::playerMyStat);
}
```

Provider the value through an injected service and store it in SQLite if it
should survive restarts. Registered placeholders are announced to
PlaceholderAPI via `getPlaceholders()`.

## Remove the Placeholders Feature

If you do not need PAPI in your plugin:

1. Delete the `core.placeholders` package.
2. Remove the usage tracker wiring in `JGameModePlugin`
   (`setupStorage()` and the `CommandsRegistrar` argument).
3. Remove `PlaceholderAPI` from `paper-plugin.yml` (`dependencies`), or keep
   it if your messages use `%otherplugin_...%` placeholders.

## Modules Reference

| Package | Key classes |
| --- | --- |
| `core.commands` | `CommandsRegistrar`, `UserCommandsRegistrar`, `AdminCommandsRegistrar`, `CommandContext`, `GameModePermissions` |
| `core.commands.mode` | `GmCommand`, `DirectModeCommand`, `ToggleCommand`, `ModeCommandsRegistrar`, `GameModeExecutor`, `GameModeOption`, `ModeResolver`, `ModeSuggestions`, `ToggleHistory`, `GameUsageTracker` |
| `core.commands.selectors` | `TargetSelector`, `SelectorSuggestions`, `ValueSuggestions` |
| `core.commands.user` | `HelpCommand`, `HelpEntry`, `VersionCommand`, `AuthorFormatter` |
| `core.configs` | `ConfigRegistrar`, `PluginConfig` |
| `core.messages` | `MessageBootstrap`, `Messenger`, `MessageParser` |
| `core.sounds` | `SoundPlayer`, `SoundsConfig` |
| `core.placeholders` | `PlaceholdersBootstrap`, `GameUsageTracker`, `GameUsageExpansion` |
| `core.storage` | `StorageBootstrap`, `SqliteStorage` |
| `core.update` | `UpdateCheckerBootstrap`, `UpdateCheckService`, `GitHubReleaseHttp`, `UpdateCheckNotifier`, `UpdateCheckJoinListener` |
