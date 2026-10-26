<div align="center">

![JTemplate banner](https://raw.githubusercontent.com/jruk8/JPaperTemplate/main/docs/assets/banner-1280x640.png)

<h3>A clean starting point for modern Paper plugins.</h3>

### [Wiki](https://jruk8.github.io/JPaperTemplate/) · [GitHub](https://github.com/jruk8/JPaperTemplate)

</div>

# Quick Start 🏃

1. Find & replace `jtemplate` → *yourname* and `JTemplate` → *YourName*
   across the repo (packages, commands, placeholders, database, docs).
2. Update `gradle.properties` (`pluginName`, `pluginMain`, `pluginAuthor`,
   `pluginDescription`, `pluginWebsite`) and `rootProject.name` in
   `settings.gradle`.
3. Build the jar and drop it into `plugins/`.

**That's it!** You have a working plugin with Brigadier commands,
MiniMessage messages, sounds, PAPI placeholders, SQLite storage and a
GitHub update checker. Delete the modules you do not need, then add your
business logic on top.

# Features

- **Brigadier commands** » Paper-native nodes with permission gates,
  config-driven root aliases, and a bare-root help listing.
- **Selectors ready** » `TargetSelector` plus selector/value tab
  completion out of the box.
- **MiniMessage everywhere** » `{braces}` for plugin placeholders,
  angle brackets reserved for raw MiniMessage, shared color palette.
- **Sounds** » config-driven Adventure sounds per feedback event.
- **Placeholders** » a PAPI expansion with self/other variants
  (`%jtemplate_uses%`, `%jtemplate_uses_<player>%`).
- **Update checker** » async GitHub releases check (console log, join
  notice, `/version` line).
- **Reloadable** » `/jtemplate reload` re-reads every config and
  re-opens storage and placeholders without a restart.

### Requirements

- Server Software » [Paper 26.2+](https://papermc.io/downloads/paper) (Folia supported)
- Java Version » **Java 25**
- Dependencies » **None** (PlaceholderAPI is optional, only for the
  `%jtemplate_...%` placeholders)

# Commands

| Command | Description |
| --- | --- |
| `/jtemplate <help\|version\|reload>` | Help, version, and config reload (alias `/jt`). |

Running the root with no arguments shows the help listing. Full table and
examples live in
[commands.md](https://github.com/jruk8/JPaperTemplate/blob/main/docs/commands.md).

# Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `jtemplate.other` | Target other players (needs the matching command permission too). | OP |
| `jtemplate.reload` | Use `/jtemplate reload`. | OP |
| `jtemplate.help` | Use `/jtemplate help` (entries are permission-filtered). | Everyone |
| `jtemplate.version` | Use `/jtemplate version`. | Everyone |

# Configuration

First start generates `config.yml`, `messages.yml` and `sounds.yml` in
`plugins/JTemplate/`:

- `updateChecker` » the async GitHub update check (console log,
  join notice, `/version` line).
- `aliases` » extra labels for the `/jtemplate` root (restart to apply).
- `storage` » the SQLite database file.
- `colors` » the shared message color palette.
- `metrics` » anonymous bStats statistics (restart to apply).
- `messages.yml` / `sounds.yml` » every message and the sound played
  for each feedback event.

Every value is documented in [configuration.md](https://github.com/jruk8/JPaperTemplate/blob/main/docs/configuration.md).

# Build

```shell
./gradlew build            # macOS/Linux
.\gradlew.bat build        # Windows
```

`build` runs the tests and both checkstyle tasks; the jar lands in
`build/libs/`. Built with Java 25 and the
[jruk8 plugin-conventions](https://github.com/jruk8/plugin-conventions).

### Links

- [Documentation](https://jruk8.github.io/JPaperTemplate/)
- [GitHub](https://github.com/jruk8/JPaperTemplate)

## Contributing

See [CONTRIBUTING.md](https://github.com/jruk8/JPaperTemplate/blob/main/CONTRIBUTING.md).

&copy; 2026 jruk8. Licensed under [GPL-3.0](https://github.com/jruk8/JPaperTemplate/blob/main/LICENSE).
