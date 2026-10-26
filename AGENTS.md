# AGENTS.md: guide for AI agents working on JPaperTemplate

JPaperTemplate is a Paper plugin template for **Paper 26.2+**, written in
**Java 25** and built with Gradle (`com.github.jruk8.plugin-conventions`,
which supplies the Paper API, JUnit 5, Mockito, and checkstyle). The
example plugin is `JTemplate`: Brigadier commands with selectors, a
MiniMessage message system, sounds, PAPI placeholders, SQLite storage,
and a GitHub update checker. PlaceholderAPI is a soft
dependency: `compileOnly` and **never bundled**, gated on availability with a
graceful warning. Read this file first, then `docs/`.

## 1. Orientation (5 minutes)

| Read | Why |
|---|---|
| `CONTRIBUTING.md` | Environment, style, tests, commits; the human-facing rules agents also follow |
| `README.md` | What the template ships, in one page |
| `docs/index.md` + `docs/getting-started.md` | Feature tour and first run |
| `docs/commands.md` | The command surface new plugins extend |
| `docs/configuration.md` | Every config file and key |
| `docs/permissions.md` | Permission nodes new commands must register |
| `docs/api.md` | Where new code plugs in |
| `mkdocs.yml` › `nav` | Where a new docs page plugs in |
| `build.gradle` | Dependencies and the `paper-plugin.yml` expansion |

## 2. Repo map

```text
src/main/java/com/jruk8/jtemplate/core/
  JTemplatePlugin           composition root only: setupCoreModules() wires the registrars
  Bootstrap/Reloadable      registrar contracts; Reloader re-runs every Reloadable
  commands/                 Paper Brigadier nodes (Commands.literal/argument) + registrars
    user/                   /jtemplate help + version (alias /jt)
    admin/reload/           /jtemplate reload
    selectors/              TargetSelector, SelectorSuggestions, ValueSuggestions
  configs/                  Okaeri schema (PluginConfig) + ConfigRegistrar loader
  messages/                 MiniMessage parsing (MessageParser) + dispatch (Messenger)
  sounds/                   config-driven playback (SoundPlayer, Adventure API only)
  placeholders/             PlaceholderAPI expansion counting command executions
  storage/                  raw JDBC SQLite key/value counters (jtemplate.db)
  update/                   async GitHub update checker (service + HTTP port + notifier)
src/main/resources/
  paper-plugin.yml          commands, permissions, softdepends (tokens expanded by Gradle)
src/test/java/...           JUnit 5 + Mockito, mirroring the main packages
docs/ + mkdocs.yml          Zensical site; CI deploys it from main
```

**Seams are thin, services hold logic.** Commands only parse args and
delegate; config parsing belongs in the schema/loaders, never in commands.
Reuse an existing service before introducing a new one, match the sibling
pattern when the area has one, and avoid new static state: instances are
created once and injected.

## 3. Golden rules

1. **Config schema is the source of truth.** Defaults live in the Okaeri
   schema classes; data-folder files generate from schema on first run.
   Never hardcode paths.
2. **Always consider reload.** New configuration must behave sanely under
   `/jtemplate reload`: reread, revalidate, and rewire without a restart,
   or say clearly in docs why a restart is needed. Commands cannot be
   re-registered (Paper offers no unregister API), so root aliases apply
   on restart and `reload` says so when it detects a change.
3. **Everything user-facing is documented.** New commands, settings,
   permissions, and behavior changes land on the matching docs page in
   the same change; add pages for new features and register them in
   `mkdocs.yml` nav. Keep `README.md` short: it links out, it does not
   enumerate.
4. **Commands serve console and players.** Support both senders unless
   impossible, add a permission check plus a `permissions.md` row, and
   keep tab completion working.
5. **Messages go through the services.** All player and console text runs
   through `Messenger` and the `MessagesConfig` keys; all sounds run
   through `SoundPlayer` and `SoundsConfig`. Messages are strictly
   MiniMessage: plugin placeholders use `{braces}`, angle brackets are
   raw MiniMessage. Highlight numbers and player names with `{hl}`.
   New message keys ship with every sibling key covered,
   in the same change.
6. **Respect the main thread.** Bukkit API calls run on the main thread;
   recurring or blocking work (the update check) stays async.
7. **Tests are the gate.** Every behavior change comes with unit tests in
   the mirrored package: goldens plus malformed-input cases that report
   errors instead of throwing. Never add tests that need a server,
   database, or network. Iterate with focused runs; finish with the full
   gate (§4).
8. **Minimal, consistent changes.** No new dependencies without maintainer
   approval, no duplicated logic, no drive-by refactors. A fix lands at
   the root cause across every case it implies, not at the symptom.
9. **Punctuation.** Never use the em dash character. Replace double hyphens
   or hyphens used as dash separators with suitable punctuation. For list
   labels followed by explanations, use `»`, for example:
   `- selectors first » the executor's own name comes first.` Keep ordinary
   hyphens in terms, ranges, and Markdown list markers.

## 4. Before you finish a task

```sh
./gradlew test --tests "com.jruk8.jtemplate.core.commands.selectors.TargetSelectorTest"  # focused suite while iterating
./gradlew test                                                                        # all tests before widening
./gradlew build                                                                       # full gate: tests + checkstyle
```

A commit is never merge-ready until `./gradlew build` is green; a
`@SuppressWarnings` needs a comment naming the blocker and the revisit
condition. Then: docs updated for user-facing behavior (§3.3), no stray
files (`git status` shows only your change), and commits use Conventional
Commits. Commit messages describe the change only: no phases, agent,
session, or other process remarks. Keep the subject single-line; when it
would be ambiguous on its own, add a one- or two-sentence body. Never push
or tag unless explicitly requested. There is no in-repo live server, so say
what you verified live versus in unit tests, and never present an unrunnable
check as green.

## 5. Where things are tracked

- `CHANGELOG.md`: curated at release time; don't append per change.
- `docs/` + `mkdocs.yml`: the published site; CI deploys it from `main`.
- GitHub: issues and PRs. CI builds every push and PR (`build.yml`);
  releases cut from `v*` tags (`publish.yml`) to GitHub releases and
  Modrinth (see the secret names documented at the top of
  `.github/workflows/publish.yml`).

## Starting a new plugin

1. Find & replace `jtemplate` → *yourname* and `JTemplate` → *YourName*
   everywhere (packages, command labels, placeholder identifier, database
   name, docs, update-checker repo/slug/links).
2. Update `gradle.properties` (`pluginName`, `pluginMain`, `pluginAuthor`,
   `pluginDescription`, `pluginWebsite`) and `rootProject.name` in
   `settings.gradle`.
3. Point `GitHubReleaseHttp` at the new repo, `UpdateCheckService` at the
   new Modrinth slug, and `versionSuccess` at the new project links.
4. Set the bStats plugin ID in `MetricsBootstrap`.
5. Delete the template modules you do not need; see `docs/api.md` for
   what plugs in where.
