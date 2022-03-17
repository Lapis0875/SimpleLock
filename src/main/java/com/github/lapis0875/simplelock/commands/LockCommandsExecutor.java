package com.github.lapis0875.simplelock.commands;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.models.NBTLock;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LockCommandsExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (sender instanceof Player player) {
            switch (command.getName()) {
                case Constants.CMD_LOCK:
                    // lock
                    return this.onLockCommand(player, command, label, args);
                case Constants.CMD_UNLOCK:
                    // unlock
                    return this.onUnLockCommand(player, command, label, args);
            }
        }
        return true;
    }

    private boolean onLockCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ItemStack onHand = player.getInventory().getItemInMainHand();
        Optional<Block> blockFacing = Optional.ofNullable(player.getTargetBlock(100));
        switch (args.length) {
            case 0:
                // lock object in your hand.
                return this.lockObject(player, onHand, blockFacing);
            case 1:
                switch (args[0]) {
                    case Constants.CMD_LOCK_VIEW, "show", "info" ->
                            // Allow user to lock.
                            this.viewLock(player, onHand, blockFacing);
                }
            case 2:
                switch (args[0]) {
                    case Constants.CMD_ALLOW, "add", "append" ->
                            // Allow user to lock.
                            this.allowUser(player, onHand, blockFacing, args[1]);
                    case Constants.CMD_DENY, "remove", "delete" ->
                            // Allow user to lock.
                            this.denyUser(player, onHand, blockFacing, args[1]);
                    case Constants.NOTIFICATION, "notify" ->
                            // Change notification config
                            this.changeNotificationConfig(player);
                }

        }
        return true;
    }

    private void changeNotificationConfig(Player player) {
        Optional<Byte> ofConfig = Optional.ofNullable(player.getPersistentDataContainer().get(Constants.NOTIFICATION_KEY(), PersistentDataType.BYTE));
        ofConfig.ifPresentOrElse(
            (value) -> {
                boolean config = (value == 1);
                player.getPersistentDataContainer().set(Constants.NOTIFICATION_KEY(), PersistentDataType.BYTE,  config ? (byte) 0 : (byte) 1);
                // player.sendMessage(Component.text("잠근 블럭의 알림 상태 : %b -> %b".formatted(config, !config), Constants.INFO_COLOR));
            },
            () -> {
                player.getPersistentDataContainer().set(Constants.NOTIFICATION_KEY(), PersistentDataType.BYTE, (byte) 0);
                // player.sendMessage(Component.text("잠근 블럭의 알림 상태 : true -> false", Constants.INFO_COLOR));
            }
        );
    }

    private boolean lockObject(Player player, ItemStack onHand, Optional<Block> blockFacing) {
        if (NBTLock.isLockable(onHand.getType()))
        {
            Optional<NBTLock> optLock = NBTLock.getLock(onHand.getItemMeta().getPersistentDataContainer());
            if (optLock.isPresent()) {
                NBTLock lock = optLock.get();
                player.sendMessage(Component.text(
                        String.format(
                                "%s 아이템은 이미 %s님의 소유입니다!",
                                onHand.displayName(), lock.ownerName
                        ),
                        Constants.DENY_COLOR
                ));
                return true;
            }
            // Lock Item
            NBTLock lock = new NBTLock(player);
            lock.applyLock(onHand);
            player.sendMessage(Component.text(
                    String.format(
                            "%s 아이템을 잠갔습니다!",
                            onHand.getType().getKey().getKey()
                    ),
                    Constants.ALLOW_COLOR
            ));
        }
        else if (blockFacing.isPresent() && NBTLock.isLockable(blockFacing.get().getType())) {
            Block block = blockFacing.get();
            TileState state = (TileState) block.getState();
            Optional<NBTLock> optLock = NBTLock.getLock(state.getPersistentDataContainer());
            if (optLock.isPresent()) {
                NBTLock lock = optLock.get();
                player.sendMessage(Component.text(
                        String.format(
                                "%s 블럭은 이미 %s님의 소유입니다!",
                                block.getType().toString().toLowerCase().replace('_', ' '), lock.ownerName
                        ),
                        Constants.DENY_COLOR
                ));
                return true;
            }
            NBTLock lock = new NBTLock(player);
            Block b = blockFacing.get();
            lock.applyLock(b);
            player.sendMessage(Component.text(
                String.format(
                    "%s 블럭을 잠갔습니다!",
                    b.getType().getKey().getKey()
                ),
                Constants.ALLOW_COLOR
            ));
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠글 수 없습니다.", Constants.DENY_COLOR));
        }
        return true;
    }

    private void allowUser(@NotNull Player player, @NotNull ItemStack onHand, Optional<Block> blockFacing, @NotNull String targetPlayerName) {
        Optional<NBTLock> lock;
        if (NBTLock.isLockable(onHand.getType()))
        {
            // Item Lock
            lock = NBTLock.getLock(onHand.getItemMeta().getPersistentDataContainer());
        }
        else if (blockFacing.isPresent() && NBTLock.isLockable(blockFacing.get().getType())) {
            // Block Lock
            Block block = blockFacing.get();
            TileState state = (TileState) block.getState();
            lock = NBTLock.getLock(state.getPersistentDataContainer());
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠글 수 없습니다.", Constants.DENY_COLOR));
            return;
        }

        if (lock.isPresent()) {
            Optional<Player> targetPlayer = Optional.ofNullable(player.getServer().getPlayer(targetPlayerName));
            if (targetPlayer.isPresent()) {
                lock.get().allowPlayer(targetPlayer.get());
                player.sendMessage(Component.text("이제 플레이어 " + targetPlayerName + "님이 이 잠금에 접근할 수 있습니다.", Constants.ALLOW_COLOR));
            }
            else {
                player.sendMessage(Component.text(targetPlayerName + " 은/는 존재하지 않는 플레이어입니다.", Constants.DENY_COLOR));
            }
        }
        else {
            player.sendMessage(Component.text("잠궈지지 않은 아이템에 사용자를 추가할 수 없습니다.", Constants.DENY_COLOR));
        }
    }

    private void denyUser(@NotNull Player player, @NotNull ItemStack onHand, Optional<Block> blockFacing, @NotNull String targetPlayerName) {
        Optional<NBTLock> lock;
        if (NBTLock.isLockable(onHand.getType()))
        {
            // Item Lock
            lock = NBTLock.getLock(onHand.getItemMeta().getPersistentDataContainer());
        }
        else if (blockFacing.isPresent() && NBTLock.isLockable(blockFacing.get().getType())) {
            // Block Lock
            Block block = blockFacing.get();
            TileState state = (TileState) block.getState();
            lock = NBTLock.getLock(state.getPersistentDataContainer());
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠글 수 없습니다.", Constants.DENY_COLOR));
            return;
        }

        if (lock.isPresent()) {
            Optional<Player> targetPlayer = Optional.ofNullable(player.getServer().getPlayer(targetPlayerName));
            if (targetPlayer.isPresent()) {
                lock.get().denyPlayer(targetPlayer.get());
                player.sendMessage(Component.text("이제 플레이어 " + targetPlayerName + "님이 이 잠금에 접근할 수 없습니다.", Constants.ALLOW_COLOR));
            }
            else {
                player.sendMessage(Component.text(targetPlayerName + " 은/는 존재하지 않는 플레이어입니다.", Constants.DENY_COLOR));
            }
        }
        else {
            player.sendMessage(Component.text("잠궈지지 않은 아이템에서 사용자를 제거할 수 없습니다.", Constants.DENY_COLOR));
        }
    }

    private void viewLock(@NotNull Player player, @NotNull ItemStack onHand, Optional<Block> blockFacing) {
        Optional<NBTLock> lock;
        if (NBTLock.isLockable(onHand.getType()))
        {
            // Item Lock
            lock = NBTLock.getLock(onHand.getItemMeta().getPersistentDataContainer());
        }
        else if (blockFacing.isPresent() && NBTLock.isLockable(blockFacing.get().getType())) {
            // Block Lock
            Block block = blockFacing.get();
            TileState state = (TileState) block.getState();
            lock = NBTLock.getLock(state.getPersistentDataContainer());
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠글 수 없습니다.", Constants.DENY_COLOR));
            return;
        }

        if (lock.isPresent()) {
            NBTLock l = lock.get();
            Bukkit.getLogger().info(l.ownerName);
            Bukkit.getLogger().info(l.ownerUUID.toString());
            player.sendMessage(Component.text(
                String.format(
                    "소유자 : %s (%s)",
                    l.ownerName, l.ownerUUID
                ),
                Constants.ALLOW_COLOR
            ));
            StringBuilder sb = new StringBuilder("허가된 플레이어 목록 :\n");
            l.allowedPlayers.forEach(
                (uuid) -> {
                    Optional<Player> optPlayer = Optional.ofNullable(player.getServer().getPlayer(uuid));
                    sb.append("- ");
                    optPlayer.ifPresentOrElse(
                            (p) -> sb.append(p.getName()),
                            () -> sb.append(uuid)
                    );
                    sb.append('\n');
                }
            );
            sb.trimToSize();
            player.sendMessage(Component.text(sb.toString(), Constants.INFO_COLOR));
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠궈지지 않았습니다.", Constants.DENY_COLOR));
        }

    }

    private boolean onUnLockCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Only unlock
        ItemStack item = player.getInventory().getItemInMainHand();
        Optional<Block> targetBlock = Optional.ofNullable(player.getTargetBlock(100));
        if (NBTLock.isLockable(item.getType())) {
            Optional<NBTLock> objectLock = NBTLock.getLock(item.getItemMeta().getPersistentDataContainer());
            objectLock.ifPresent(
                (lock) -> {
                    if (lock.ownerUUID.equals(player.getUniqueId())) {
                        lock.removeLock(item);
                        player.sendMessage(Component.text(
                            String.format(
                                "%s 아이템을 잠금 해제했습니다!",
                                item.getType().getKey().getKey()
                            ),
                            Constants.ALLOW_COLOR
                        ));
                    }
                    else {
                        player.sendMessage(Component.text(
                            String.format(
                                "이 아이템은 %s님의 소유입니다. 잠금을 풀 수 없습니다.",
                                lock.ownerName
                            ),
                            Constants.DENY_COLOR
                        ));
                    }
                }
            );
        }
        else if (targetBlock.isPresent() && NBTLock.isLockable(targetBlock.get().getType())) {
            Block block = targetBlock.get();
            TileState state = (TileState) block.getState();
            Optional<NBTLock> objectLock = NBTLock.getLock(state.getPersistentDataContainer());
            objectLock.ifPresent(
                (lock) -> {
                    if (lock.ownerUUID.equals(player.getUniqueId())) {
                        lock.removeLock(targetBlock.get());
                        player.sendMessage(Component.text(
                            String.format(
                                "%s 블럭을 잠금 해제했습니다!",
                                targetBlock.get().getType().getKey().getKey()
                            ),
                            Constants.ALLOW_COLOR
                        ));
                    }
                    else {
                        player.sendMessage(Component.text(
                            String.format(
                                "이 블럭은 %s님의 소유입니다. 잠금을 풀 수 없습니다.",
                                lock.ownerName
                            ),
                            Constants.DENY_COLOR
                        ));
                    }
                })
            ;
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠그거나 잠금 해제할 수 없습니다.", Constants.DENY_COLOR));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabCompletion = new ArrayList<>();
        if (command.getName().equals(Constants.CMD_LOCK)) {
            if (sender instanceof Player player) {
                switch (args.length) {
                    case 1:
                        tabCompletion.add(Constants.CMD_ALLOW);
                        tabCompletion.add(Constants.CMD_DENY);
                        tabCompletion.add(Constants.CMD_LOCK_VIEW);
                        tabCompletion.add(Constants.NOTIFICATION);
                        break;
                    case 2:
                        switch (args[0]) {
                            case Constants.CMD_ALLOW:
                            case Constants.CMD_DENY:
                                for (Player p: player.getServer().getOnlinePlayers()) {
                                    if (p.getUniqueId() != player.getUniqueId()) {
                                        tabCompletion.add(p.getName());
                                    }
                                }
                                break;
                        }
                }
            }
        }
        return tabCompletion;
    }
}
