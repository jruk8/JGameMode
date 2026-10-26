# Placeholders

PlaceholderAPI support is handled by the `core.placeholders` module. When
PlaceholderAPI is installed, the plugin registers an expansion with the
identifier `jtemplate`. The expansion is re-registered on
`/jtemplate reload`, and unregistered when the plugin disables.

## Template Placeholders

| Placeholder | Type | Description |
| --- | --- | --- |
| `%jtemplate_uses%` | INTEGER | Number of times you have executed a tracked command. |
| `%jtemplate_uses_<player>%` | INTEGER | Another player's tracked command executions. |

A use is recorded whenever a player successfully runs a tracked command
(currently `/jtemplate version`). Executions by console are not attributed
to any player and are not counted. Counts are stored in the SQLite
database, so they survive restarts and are separate per player. Unknown
player names read as `0`.

## Adding Your Own Placeholders

Track new state with `CommandUsageTracker` (one stat key per counter,
recorded from the command) and expose it in `UsageExpansion.onRequest()`:

```java
if ("my_stat".equals(params)) {
    return selfUses(player, tracker::playerMyStat);
}
```

Provider the value through an injected service and store it in SQLite if it
should survive restarts. Registered placeholders are announced to
PlaceholderAPI via `getPlaceholders()`. A full walkthrough (including new
stat keys) lives in [api.md](api.md).
