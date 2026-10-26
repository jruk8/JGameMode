---
icon: material/variable
---

# Placeholders

PlaceholderAPI support is handled by the `core.placeholders` module. When
PlaceholderAPI is installed, the plugin registers an expansion with the
identifier `jgm`. The expansion is re-registered on
`/jgamemode reload`, and unregistered when the plugin disables.

## Usage Placeholders

| Placeholder | Type | Description |
| --- | --- | --- |
| `%jgm_uses%` | INTEGER | Your total gamemode switches. |
| `%jgm_uses_<player>%` | INTEGER | Another player's total gamemode switches. |
| `%jgm_uses_global%` | INTEGER | Server-wide total gamemode switches. |
| `%jgm_toggle_uses%` | INTEGER | Your toggle uses. |
| `%jgm_toggle_uses_<player>%` | INTEGER | Another player's toggle uses. |
| `%jgm_toggle_uses_global%` | INTEGER | Server-wide toggle uses. |
| `%jgm_survival_uses%` | INTEGER | Your switches to survival. |
| `%jgm_survival_uses_<player>%` | INTEGER | Another player's switches to survival. |
| `%jgm_survival_uses_global%` | INTEGER | Server-wide switches to survival. |
| `%jgm_creative_uses%` | INTEGER | Your switches to creative. |
| `%jgm_creative_uses_<player>%` | INTEGER | Another player's switches to creative. |
| `%jgm_creative_uses_global%` | INTEGER | Server-wide switches to creative. |
| `%jgm_adventure_uses%` | INTEGER | Your switches to adventure. |
| `%jgm_adventure_uses_<player>%` | INTEGER | Another player's switches to adventure. |
| `%jgm_adventure_uses_global%` | INTEGER | Server-wide switches to adventure. |
| `%jgm_spectator_uses%` | INTEGER | Your switches to spectator. |
| `%jgm_spectator_uses_<player>%` | INTEGER | Another player's switches to spectator. |
| `%jgm_spectator_uses_global%` | INTEGER | Server-wide switches to spectator. |

A use is recorded whenever a player successfully runs a switching command
(`/gm`, `/gms`, `/gmc`, `/gma`, `/gmsp`, `/gmt`, `/gamemode`, the
single-letter roots). Toggles count once overall, once on the toggle
counter, and once on each distinct resulting channel. Executions by console
are not attributed to any player and are not counted. Counts live in the
SQLite database, so they survive restarts and are separate per player.
Unknown player names read as `0`.

## Adding Your Own Placeholders

Track new state with `GameUsageTracker` (one stat key per counter,
recorded from the command) and expose it in `GameUsageExpansion.onRequest()`:

```java
if ("my_stat".equals(params)) {
    return selfUses(player, tracker::playerMyStat);
}
```

Provider the value through an injected service and store it in SQLite if it
should survive restarts. Registered placeholders are announced to
PlaceholderAPI via `getPlaceholders()`. A full walkthrough (including new
stat keys) lives in [api.md](api.md).
