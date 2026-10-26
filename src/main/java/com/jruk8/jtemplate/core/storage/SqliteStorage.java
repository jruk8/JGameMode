package com.jruk8.jtemplate.core.storage;

import org.sqlite.JDBC;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Minimal SQLite-backed key/value store for plugin statistics.
 * <p>
 * Two generic tables are maintained so the database can host stats for future
 * features, not just placeholders:
 * <ul>
 *   <li>{@code player_stats} - per-player counters keyed by a stat name</li>
 *   <li>{@code global_stats} - single counters keyed by a stat name</li>
 * </ul>
 * <p>
 * The JDBC driver is loaded by name at runtime so the plugin relies on the
 * SQLite driver bundled with the server instead of shipping another copy.
 * Failed states degrade gracefully: reads return {@code 0} and writes are
 * no-ops while the connection is unavailable.
 */
public final class SqliteStorage implements AutoCloseable {

    private static final String TABLE_PLAYER_STATS = "player_stats";
    private static final String TABLE_GLOBAL_STATS = "global_stats";

    private static final String CREATE_PLAYER_STATS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYER_STATS + " ("
                    + "key TEXT NOT NULL,"
                    + "player TEXT NOT NULL,"
                    + "value INTEGER NOT NULL DEFAULT 0,"
                    + "PRIMARY KEY (key, player))";

    private static final String CREATE_GLOBAL_STATS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_GLOBAL_STATS + " ("
                    + "key TEXT NOT NULL PRIMARY KEY,"
                    + "value INTEGER NOT NULL DEFAULT 0)";

    private static final String INSERT_OR_IGNORE_PLAYER =
            "INSERT OR IGNORE INTO " + TABLE_PLAYER_STATS + " (key, player, value) VALUES (?, ?, 0)";
    private static final String INCREMENT_PLAYER =
            "UPDATE " + TABLE_PLAYER_STATS + " SET value = value + 1 WHERE key = ? AND player = ?";
    private static final String SELECT_PLAYER =
            "SELECT value FROM " + TABLE_PLAYER_STATS + " WHERE key = ? AND player = ?";

    private static final String INSERT_OR_IGNORE_GLOBAL =
            "INSERT OR IGNORE INTO " + TABLE_GLOBAL_STATS + " (key, value) VALUES (?, 0)";
    private static final String INCREMENT_GLOBAL =
            "UPDATE " + TABLE_GLOBAL_STATS + " SET value = value + 1 WHERE key = ?";
    private static final String SELECT_GLOBAL =
            "SELECT value FROM " + TABLE_GLOBAL_STATS + " WHERE key = ?";

    private final String jdbcUrl;
    private final Logger logger;
    private Connection connection;

    /**
     * Creates a storage bound to a SQLite file on disk.
     *
     * @param dbFile the database file; created lazily by {@link #init()}
     * @param logger logger used to report connectivity failures
     */
    public SqliteStorage(File dbFile, Logger logger) {
        this(toJdbcUrl(dbFile), logger);
    }

    /**
     * Creates a storage bound to an arbitrary JDBC URL.
     * <p>
     * Mainly useful for tests that target an in-memory database
     * ({@code jdbc:sqlite::memory:}).
     */
    public SqliteStorage(String jdbcUrl, Logger logger) {
        this.jdbcUrl = jdbcUrl;
        this.logger = logger;
    }

    /**
     * Opens the connection and ensures the schema exists.
     *
     * @return {@code true} when the storage is usable afterwards
     */
    public boolean init() {
        try {
            Class.forName(JDBC.class.getName());
            this.connection = DriverManager.getConnection(this.jdbcUrl);
            try (Statement statement = this.connection.createStatement()) {
                statement.execute(CREATE_PLAYER_STATS);
                statement.execute(CREATE_GLOBAL_STATS);
            }
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            this.logger.log(Level.SEVERE, "Failed to initialize SQLite storage", e);
            close();
            return false;
        }
    }

    /**
     * Increments a per-player counter by one, creating it as {@code 1} if absent.
     *
     * @param key    the stat name
     * @param player the player owning the counter
     */
    public synchronized void increment(String key, UUID player) {
        if (!isReady()) {
            return;
        }
        try {
            executeUpdate(INSERT_OR_IGNORE_PLAYER, key, player.toString());
            executeUpdate(INCREMENT_PLAYER, key, player.toString());
        } catch (SQLException e) {
            this.logger.log(Level.WARNING, "Failed to increment player stat '" + key + "'", e);
        }
    }

    /**
     * Increments a server-wide counter by one, creating it as {@code 1} if absent.
     *
     * @param key the stat name
     */
    public synchronized void incrementGlobal(String key) {
        if (!isReady()) {
            return;
        }
        try {
            executeUpdate(INSERT_OR_IGNORE_GLOBAL, key);
            executeUpdate(INCREMENT_GLOBAL, key);
        } catch (SQLException e) {
            this.logger.log(Level.WARNING, "Failed to increment global stat '" + key + "'", e);
        }
    }

    /**
     * Reads a per-player counter value.
     *
     * @param key    the stat name
     * @param player the player owning the counter
     * @return the current value, or {@code 0} when absent
     */
    public synchronized int playerTotal(String key, UUID player) {
        if (!isReady()) {
            return 0;
        }
        try (PreparedStatement statement = this.connection.prepareStatement(SELECT_PLAYER)) {
            statement.setString(1, key);
            statement.setString(2, player.toString());
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? results.getInt(1) : 0;
            }
        } catch (SQLException e) {
            this.logger.log(Level.WARNING, "Failed to read player stat '" + key + "'", e);
            return 0;
        }
    }

    /**
     * Reads a server-wide counter value.
     *
     * @param key the stat name
     * @return the current value, or {@code 0} if absent
     */
    public synchronized int globalTotal(String key) {
        if (!isReady()) {
            return 0;
        }
        try (PreparedStatement statement = this.connection.prepareStatement(SELECT_GLOBAL)) {
            statement.setString(1, key);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? results.getInt(1) : 0;
            }
        } catch (SQLException e) {
            this.logger.log(Level.WARNING, "Failed to read global stat '" + key + "'", e);
            return 0;
        }
    }

    @Override
    public synchronized void close() {
        if (this.connection == null) {
            return;
        }
        try {
            this.connection.close();
        } catch (SQLException e) {
            this.logger.log(Level.WARNING, "Failed to close SQLite connection", e);
        } finally {
            this.connection = null;
        }
    }

    private boolean isReady() {
        return this.connection != null;
    }

    private void executeUpdate(String sql, String... parameters) throws SQLException {
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setString(i + 1, parameters[i]);
            }
            statement.executeUpdate();
        }
    }

    private static String toJdbcUrl(File dbFile) {
        return "jdbc:sqlite:" + dbFile.getAbsolutePath().replace('\\', '/');
    }
}