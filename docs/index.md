![Banner](assets/banner-1280x640.png)
# JTemplate

A clean, buildable starting point for Paper plugins: commands (Paper
native Brigadier), okaeri-configs, MiniMessage messaging, sounds,
PlaceholderAPI placeholders and SQLite storage -- all wired without a DI
framework.

## Key Features

- **Small modules** -- one package per concern, each self-contained and easy
  to delete or extend.
- **Manual wiring** - a registrar per module (`Bootstrap`) composes services;
  `JTemplatePlugin` is the single composition root.
- **Placeholders** - a template PlaceholderAPI expansion counting command
  executions (`%jtemplate_uses%`, plus a per-player variant).
- **SQLite storage** -- generic per-player / global counters in
  `jtemplate.db`, driven by the server's bundled JDBC driver.
- **Update checker** -- async GitHub releases check with console and
  in-game notices.
- **Reloadable** -- `/jtemplate reload` re-reads every config and re-opens
  the storage and placeholders.

## Documentation

- [Getting Started](getting-started.md)
- [Commands](commands.md)
- [Permissions](permissions.md)
- [Configuration](configuration.md)
- [Placeholders](placeholders.md)
- [Extending the template](api.md)

## Build

Java 25 is required.

```text
./gradlew build
```

The plugin jar is written to `build/libs/`. See the root
[README](../README.md) for the "start a new plugin" workflow.
