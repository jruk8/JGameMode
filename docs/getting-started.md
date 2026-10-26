![Banner](assets/banner-1280x640.png)
# Getting Started

## Requirements

- Paper 26.2 or newer
- Java 25

## Installation

1. Build the plugin (`.\gradlew.bat build`) or download a release jar.
2. Place the jar in your server's `plugins/` folder.
3. Restart the server. The plugin generates `config.yml`, `messages.yml` and
   `sounds.yml` in `plugins/JTemplate/` and creates the SQLite database
   `jtemplate.db`.
4. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
   to use the placeholders.

## Your First Commands

```text
/jtemplate help
/jtemplate version
/jtemplate reload
```

`/jt` is a built-in alias for `/jtemplate`. The template placeholders count
how often `/jtemplate version` is executed. Run it a
couple of times, install PlaceholderAPI, and try
`%jtemplate_uses%` in a scoreboard or chat.

## Starting a New Plugin from the Template

1. Find & replace project-wide: `jtemplate` to *yourname* and `JTemplate` to
   *YourName* (packages, commands, placeholder identifiers, database name,
   docs). Then update `gradle.properties` and `settings.gradle`.
2. Delete or adapt the template modules you do not need; see
   [Extending the template](api.md) for what plugs in where.

## Next Steps

- [Commands](commands.md)
- [Configuration](configuration.md)
- [Placeholders](placeholders.md)
