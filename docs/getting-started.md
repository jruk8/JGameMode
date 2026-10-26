# Getting Started

## Requirements

- Paper 26.2 or newer (Folia supported)
- Java 25

## Installation

1. Download the latest JGameMode release jar.
2. Place the jar in your server's `plugins/` folder.
3. Restart the server. The plugin generates `config.yml`, `messages.yml` and
   `sounds.yml` in `plugins/JGameMode/` and creates the SQLite database
   `jgamemode.db`.
4. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
   to use the placeholders.

## Your First Commands

```text
/gm s
/gmc
/gm toggle
/jgamemode version
```

`/gm s` sets your survival mode, `/gmc` is the creative shortcut, and
`/gm toggle` swaps you to spectator and back. Resolve modes dynamically
(`/gm creat`), target others (`/gmc Steve`, `/gm sp @a`), and run
`/jgamemode help` for the rest.

`/jgm` is the main alias for `/jgamemode`. The placeholders count your
gamemode switches: run a few commands, install
PlaceholderAPI, and try `%jgm_uses%` in a scoreboard or chat.

## Next Steps

- [Commands](commands.md)
- [Configuration](configuration.md)
- [Placeholders](placeholders.md)
