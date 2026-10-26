package com.jruk8.jtemplate.core.commands.mode;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.GameModePermissions;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Applies gamemode switches to resolved targets and sends the matching feedback:
 * the sender always gets a summary, and affected players other than the
 * sender get a short notification.
 * <p>
 * Every successful switch records one overall use plus one use on each
 * resulting channel for the executor; toggles additionally record one
 * toggle use. Switches that change nothing still confirm but leave the
 * toggle history alone. Console executions are not attributed and skip
 * recording.
 */
public class GameModeExecutor {

    private final CommandContext ctx;
    private final ToggleHistory history;
    private final GameUsageTracker tracker;

    public GameModeExecutor(CommandContext ctx, ToggleHistory history, GameUsageTracker tracker) {
        this.ctx = ctx;
        this.history = history;
        this.tracker = tracker;
    }

    /**
     * Switches every target to the given mode.
     *
     * @param source the command source
     * @param mode the target gamemode
     * @param targets the resolved target players
     * @return {@link Command#SINGLE_SUCCESS}, or 0 when a check fails
     */
    public int set(CommandSourceStack source, GameModeOption mode, List<Player> targets) {
        if (!checkAccess(source, targets, mode)) {
            return 0;
        }
        Map<Player, GameModeOption> applied = new LinkedHashMap<>();
        for (Player target : targets) {
            GameMode previous = target.getGameMode();
            target.setGameMode(mode.gameMode());
            history.recordSwitch(target.getUniqueId(), previous, mode.gameMode());
            applied.put(target, mode);
        }
        recordSwitchUse(source, mode);
        sendFeedback(source, targets, applied);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Swaps every target between spectator and their last non-spectator
     * mode, mirroring vanilla F3+N with permission checks: non-spectator
     * targets go to spectator (caching where they came from), spectators
     * return to their cached mode, and spectators without history fall back
     * to creative, then survival, then adventure, by permission. Targets
     * that cannot swap abort the whole toggle with the toggle error naming
     * the most relevant permission.
     *
     * @param source the command source
     * @param targets the resolved target players
     * @return {@link Command#SINGLE_SUCCESS}, or 0 when a check fails
     */
    public int toggle(CommandSourceStack source, List<Player> targets) {
        if (!checkScope(source, targets)) {
            return 0;
        }
        CommandSender viewer = source.getSender();
        Map<Player, GameModeOption> swaps = new LinkedHashMap<>();
        for (Player target : targets) {
            Optional<GameModeOption> swap = toggleTarget(target, viewer);
            if (swap.isEmpty()) {
                toggleError(source, GameModePermissions.modeNode(GameModeOption.CREATIVE));
                return 0;
            }
            swaps.put(target, swap.get());
        }
        for (GameModeOption mode : swaps.values().stream().distinct().toList()) {
            if (!viewer.hasPermission(GameModePermissions.modeNode(mode))) {
                toggleError(source, GameModePermissions.modeNode(mode));
                return 0;
            }
        }
        for (Map.Entry<Player, GameModeOption> swap : swaps.entrySet()) {
            Player target = swap.getKey();
            GameMode previous = target.getGameMode();
            target.setGameMode(swap.getValue().gameMode());
            history.recordSwap(target.getUniqueId(), previous);
        }
        recordToggleUse(source, swaps);
        sendFeedback(source, targets, swaps);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Resolves the toggle target for one player without switching.
     *
     * @param target the player
     * @param viewer the permission source, usually the command sender
     * @return the swap mode, or empty in spectator without history or fallback
     */
    public Optional<GameModeOption> toggleTarget(Player target, CommandSender viewer) {
        if (target.getGameMode() != GameMode.SPECTATOR) {
            return Optional.of(GameModeOption.SPECTATOR);
        }
        Optional<GameMode> cached = history.lastMode(target.getUniqueId());
        if (cached.isPresent()) {
            return Optional.of(GameModeOption.fromGameMode(cached.get()));
        }
        return firstFallback(viewer);
    }

    private Optional<GameModeOption> firstFallback(CommandSender viewer) {
        for (GameModeOption option : List.of(
                GameModeOption.CREATIVE, GameModeOption.SURVIVAL, GameModeOption.ADVENTURE)) {
            if (viewer.hasPermission(GameModePermissions.modeNode(option))) {
                return Optional.of(option);
            }
        }
        return Optional.empty();
    }

    private void toggleError(CommandSourceStack source, String node) {
        ctx.error(source, m -> m.getMode().getToggleNoPermission(), Map.of("permission", node));
    }

    private boolean checkAccess(CommandSourceStack source, List<Player> targets, GameModeOption mode) {
        return checkScope(source, targets) && checkMode(source, mode);
    }

    private boolean checkScope(CommandSourceStack source, List<Player> targets) {
        if (!source.getSender().hasPermission(GameModePermissions.USE)) {
            var tags = Map.of("permission", GameModePermissions.USE);
            ctx.error(source, m -> m.getCore().getNoPermission(), tags);
            return false;
        }
        if (targetsOthers(source, targets)) {
            if (!source.getSender().hasPermission(GameModePermissions.OTHER)) {
                var tags = Map.of("permission", GameModePermissions.OTHER);
                ctx.error(source, m -> m.getCore().getNoPermission(), tags);
                return false;
            }
            return true;
        }
        if (!source.getSender().hasPermission(GameModePermissions.SELF)) {
            var tags = Map.of("permission", GameModePermissions.SELF);
            ctx.error(source, m -> m.getCore().getNoPermission(), tags);
            return false;
        }
        return true;
    }

    private boolean checkMode(CommandSourceStack source, GameModeOption mode) {
        String node = GameModePermissions.modeNode(mode);
        if (!source.getSender().hasPermission(node)) {
            var tags = Map.of("permission", node, "mode", mode.display());
            ctx.error(source, m -> m.getMode().getNoModePermission(), tags);
            return false;
        }
        return true;
    }

    private boolean targetsOthers(CommandSourceStack source, List<Player> targets) {
        if (!(source.getSender() instanceof Player self)) {
            return true;
        }
        return targets.stream().anyMatch(target -> !target.getUniqueId().equals(self.getUniqueId()));
    }

    private void recordSwitchUse(CommandSourceStack source, GameModeOption mode) {
        if (source.getSender() instanceof Player self) {
            tracker.recordSwitch(self.getUniqueId(), mode);
        }
    }

    private void recordToggleUse(CommandSourceStack source, Map<Player, GameModeOption> swaps) {
        if (source.getSender() instanceof Player self) {
            UUID executor = self.getUniqueId();
            tracker.recordToggle(executor, swaps.values().stream().distinct().toList());
        }
    }

    private void sendFeedback(
            CommandSourceStack source,
            List<Player> targets,
            Map<Player, GameModeOption> modes
    ) {
        if (isSelfOnly(source, targets)) {
            GameModeOption mode = modes.get(targets.getFirst());
            ctx.send(source, m -> m.getMode().getSetSelf(), Map.of("mode", mode.display()));
            return;
        }
        Map<GameModeOption, List<Player>> groups = modes.entrySet().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        LinkedHashMap::new,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
        for (Map.Entry<GameModeOption, List<Player>> group : groups.entrySet()) {
            sendGroupSummary(source, group.getKey(), group.getValue());
        }
        for (Map.Entry<Player, GameModeOption> entry : modes.entrySet()) {
            Player target = entry.getKey();
            if (!isSender(source, target)) {
                ctx.notify(target, m -> m.getMode().getSetTarget(), Map.of("mode", entry.getValue().display()));
            }
        }
    }

    private void sendGroupSummary(CommandSourceStack source, GameModeOption mode, List<Player> group) {
        if (group.size() == 1) {
            var tags = Map.of("mode", mode.display(), "targets", group.getFirst().getName());
            ctx.send(source, m -> m.getMode().getSetOther(), tags);
            return;
        }
        var tags = new HashMap<String, String>();
        tags.put("mode", mode.display());
        tags.put("count", String.valueOf(group.size()));
        tags.put("targets", targetNames(group));
        ctx.send(source, m -> m.getMode().getSetMultiple(), tags);
    }

    private boolean isSelfOnly(CommandSourceStack source, List<Player> targets) {
        return targets.size() == 1 && isSender(source, targets.getFirst());
    }

    private boolean isSender(CommandSourceStack source, Player target) {
        return source.getSender() instanceof Player self
                && self.getUniqueId().equals(target.getUniqueId());
    }

    private String targetNames(List<Player> targets) {
        List<String> names = new ArrayList<>();
        for (Player target : targets) {
            names.add(target.getName());
        }
        return String.join(", ", names);
    }
}
