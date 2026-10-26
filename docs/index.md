![Banner](assets/banner-1280x640.png)
# JGameMode

Modern 26.x plugin for `/gm s` and `/gmc`: polished gamemode aliases with
vanilla selectors, permission-gated tab completion, a spectator toggle
mirroring F3+N, usage statistics and an async update checker.

## Key Features

- **Direct aliases** - `/gms`, `/gmc`, `/gma`, `/gmsp`, plus `/gm s|c|a|sp`
  and the single-letter `/s`, `/c`, `/a`, `/sp` roots.
- **Dynamic resolution** - full names, numbers and any substring
  (`/gm creat`), first match wins; toggle via `/gm`, `/gmt`, `/t` or
  `/gm tog`.
- **Spectator toggle** - swaps between spectator and the last
  non-spectator mode like vanilla F3+N, with permission checks vanilla
  lacks.
- **Permission gating** - `jgamemode.use` master switch, opt-out
  per-mode nodes, `self`/`other` scopes; completion only shows what you
  may use.
- **Placeholders** - a PlaceholderAPI expansion (`%jgm_uses%`,
  `%jgm_toggle_uses%`, per-mode and per-player/global variants) counting
  command executions.
- **Vanilla override** - optional 1:1 `/gamemode` replacement with
  resolution, toggle and history on top.
- **Reloadable** - `/jgamemode reload` re-reads every config and re-opens
  the storage and placeholders.

## Documentation

- [Getting Started](getting-started.md)
- [Commands](commands.md)
- [Permissions](permissions.md)
- [Configuration](configuration.md)
- [Placeholders](placeholders.md)
- [Plugin API for developers](api.md)
