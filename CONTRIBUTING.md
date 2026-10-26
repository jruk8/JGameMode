# Contributing to JGameMode

Thank you for your interest in contributing to JGameMode! This document
covers the environment, coding conventions, testing expectations, commit
style, and documentation expectations.

## Environment

- **JDK 25** is required (Temurin recommended). Check with
  `java -version` before anything else.
- **Gradle** runs through the wrapper; never use a system install:
  - `./gradlew build` (Windows: `.\gradlew.bat build`) is the full
    gate: unit tests plus both checkstyle tasks. Run it before
    finishing any change.
  - `./gradlew test` runs only the tests while iterating; narrow to
    one suite with `./gradlew test --tests
    "com.jruk8.jgamemode.core.MyTest"`.
  - The plugin jar lands in `build/libs/`.
- **Test server:** there is no committed test server. Spin up a local
  [Paper 26.2+](https://papermc.io/downloads/paper) server, drop the
  built jar into `plugins/`, and restart. PlaceholderAPI is optional
  and only needed to exercise the `%jgm_...%` placeholders.
  Say what you verified live versus in unit tests, and never present
  an unrunnable check as green.

## Coding Conventions

- **Java 25** is the target. Use modern language features (records,
  switch expressions, pattern matching) where appropriate.
- **Checkstyle** is enforced by `build`. Key rules:
  - 4-space indentation, no tabs.
  - Lines stay within 120 characters, methods within 45 lines,
    files within 750 lines: keep classes small and focused.
  - Braces are always required, even for one-line blocks.
  - No star imports (including `javax.*`), no unused imports.
- **Manual wiring:** each class receives only what it needs via its
  constructor, and a module-level registrar (implementing
  `Bootstrap`, optionally `Reloadable`) composes the module.
  `JGameModePlugin.setupCoreModules()` is the single composition
  root. Avoid static state and singletons.
- **Null safety:** use `@NotNull` and `@Nullable` from
  `org.jetbrains.annotations` where nullability is not obvious.
- **Thread safety:** Bukkit operations run on the main thread. The
  update checker already does its HTTP work async; keep it that way.
- **Error handling:** catch exceptions at the boundary (command
  executors, listeners) and log them with context. Never let an
  exception propagate to the server.

## Testing Expectations

- **Logic-based tests** are required for new features. Use **JUnit 5**.
- Tests must not require a running Minecraft server. Mock or stub
  Bukkit dependencies where needed.
- **Do not add runtime or integration tests.** Tests that need a live
  server, a database, or network access will be rejected.
- Place tests in `src/test/java/` mirroring the main package
  structure.

## Commit Message Style

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>[optional scope]: <description>

[optional body]
```

Types:

- `feat`: a new feature
- `fix`: a bug fix
- `docs`: documentation-only changes
- `refactor`: code changes that neither fix a bug nor add a feature
- `test`: adding or fixing tests
- `chore`: maintenance tasks (dependencies, build config, etc.)
- `ci`: CI and workflow changes

Example:

```
feat(commands): accept vanilla selectors as targets
```

Describe the change only: no phases, agent, session, or other
process remarks in commit messages. Keep many small commits rather
than one big one, and never push or tag unless asked.

## Documentation Expectations

- Update `docs/` when adding or changing user-facing features.
- Update `docs/commands.md` for new commands.
- Update `docs/configuration.md` for new configuration options.
- Update `docs/permissions.md` for new permissions.
- Register new pages in the `mkdocs.yml` nav so they appear on the
  site.
- Keep documentation accurate and concise, and keep `README.md`
  short: it links to the docs, it does not duplicate them.

## Pull Request Guidelines

1. **Branch** from `main` with a descriptive name (e.g.
   `feat/my-feature`). Never commit directly on `main`.
2. **Commit** using the style above. Squash fixup commits before
   submitting.
3. **Test**: ensure `./gradlew test` passes.
4. **Build**: ensure `./gradlew build` passes with zero warnings.
5. **Document**: update the relevant docs.
6. **Describe**: write a clear PR description explaining what and why.
7. **Review**: respond to feedback promptly. CI must pass before merge.

## Code of Conduct

Be respectful and constructive in all interactions. Harassment or
discriminatory behavior of any kind will not be tolerated.
