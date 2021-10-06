package com.github.lapis0875.simplelock.commands;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.models.ObjectLock;
import com.sun.org.apache.bcel.internal.Const;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LockCommandsExecutor implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
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
                    case Constants.CMD_LOCK_VIEW:
                    case "show":
                    case "info":
                        // Allow user to lock.
                        this.viewLock(player, onHand, blockFacing);
                        break;
                }
            case 2:
                switch (args[0]) {
                    case Constants.CMD_ALLOW:
                    case "add":
                    case "append":
                        // Allow user to lock.
                        this.allowUser(player, onHand, blockFacing, args[1]);
                        break;
                    case Constants.CMD_DENY:
                    case "remove":
                    case "delete":
                        // Allow user to lock.
                        this.denyUser(player, onHand, blockFacing, args[1]);
                        break;
                }

        }
        return true;
    }

    private boolean lockObject(Player player, ItemStack onHand, Optional<Block> blockFacing) {
        if (ObjectLock.isLockable(onHand.getType()))
        {
            // Lock Item
            ObjectLock lock = new ObjectLock(player);
            lock.applyLock(onHand);
            player.sendMessage(Component.text(
                    String.format(
                            "%s 아이템을 잠갔습니다!",
                            onHand.getType().getKey().getKey()
                    ),
                    Constants.INFO
            ));
        }
        else if (blockFacing.isPresent() && ObjectLock.isLockable(blockFacing.get().getType())) {
            ObjectLock lock = new ObjectLock(player);
            Block b = blockFacing.get();
            lock.applyLock(b);
            player.sendMessage(Component.text(
                String.format(
                    "%s 블럭을 잠갔습니다!",
                    b.getType().getKey().getKey()
                ),
                Constants.INFO
            ));
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠글 수 없습니다.", Constants.ERROR));
        }
        return true;
    }

    private void allowUser(@NotNull Player player, @NotNull ItemStack onHand, Optional<Block> blockFacing, @NotNull String targetPlayerName) {
        Optional<ObjectLock> lock;
        if (ObjectLock.isLockable(onHand.getType()))
        {
            // Item Lock
            lock = ObjectLock.getObjectLock(onHand.getItemMeta().getPersistentDataContainer());
        }
        else if (blockFacing.isPresent() && ObjectLock.isLockable(blockFacing.get().getType())) {
            // Block Lock
            Block block = blockFacing.get();
            TileState state = (TileState) block.getState();
            lock = ObjectLock.getObjectLock(state.getPersistentDataContainer());
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠글 수 없습니다.", Constants.ERROR));
            return;
        }

        if (lock.isPresent()) {
            Optional<Player> targetPlayer = Optional.ofNullable(player.getServer().getPlayer(targetPlayerName));
            if (targetPlayer.isPresent()) {
                lock.get().allowPlayer(targetPlayer.get());
                player.sendMessage(Component.text("이제 플레이어 " + targetPlayerName + "님이 이 잠금에 접근할 수 있습니다.", Constants.INFO));
            }
            else {
                player.sendMessage(Component.text(targetPlayerName + " 은/는 존재하지 않는 플레이어입니다.", Constants.ERROR));
            }
        }
        else {
            player.sendMessage(Component.text("잠궈지지 않은 아이템에 사용자를 추가할 수 없습니다.", Constants.ERROR));
        }
    }

    private void denyUser(@NotNull Player player, @NotNull ItemStack onHand, Optional<Block> blockFacing, @NotNull String targetPlayerName) {
        Optional<ObjectLock> lock;
        if (ObjectLock.isLockable(onHand.getType()))
        {
            // Item Lock
            lock = ObjectLock.getObjectLock(onHand.getItemMeta().getPersistentDataContainer());
        }
        else if (blockFacing.isPresent() && ObjectLock.isLockable(blockFacing.get().getType())) {
            // Block Lock
            Block block = blockFacing.get();
            TileState state = (TileState) block.getState();
            lock = ObjectLock.getObjectLock(state.getPersistentDataContainer());
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠글 수 없습니다.", Constants.ERROR));
            return;
        }

        if (lock.isPresent()) {
            Optional<Player> targetPlayer = Optional.ofNullable(player.getServer().getPlayer(targetPlayerName));
            if (targetPlayer.isPresent()) {
                lock.get().denyPlayer(targetPlayer.get());
                player.sendMessage(Component.text("이제 플레이어 " + targetPlayerName + "님이 이 잠금에 접근할 수 없습니다.", Constants.INFO));
            }
            else {
                player.sendMessage(Component.text(targetPlayerName + " 은/는 존재하지 않는 플레이어입니다.", Constants.ERROR));
            }
        }
        else {
            player.sendMessage(Component.text("잠궈지지 않은 아이템에서 사용자를 제거할 수 없습니다.", Constants.ERROR));
        }
    }

    private void viewLock(@NotNull Player player, @NotNull ItemStack onHand, Optional<Block> blockFacing) {
        Optional<ObjectLock> lock;
        if (ObjectLock.isLockable(onHand.getType()))
        {
            // Item Lock
            lock = ObjectLock.getObjectLock(onHand.getItemMeta().getPersistentDataContainer());
        }
        else if (blockFacing.isPresent() && ObjectLock.isLockable(blockFacing.get().getType())) {
            // Block Lock
            Block block = blockFacing.get();
            TileState state = (TileState) block.getState();
            lock = ObjectLock.getObjectLock(state.getPersistentDataContainer());
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠글 수 없습니다.", Constants.ERROR));
            return;
        }

        if (lock.isPresent()) {
            ObjectLock l = lock.get();
            player.sendMessage(Component.text(
                String.format(
                    "소유자 : %s\n허가된 플레이어 목록 :\n",
                    l.ownerName
                ) + l.allowedPlayers.stream().map((uuid) -> '-' + Objects.requireNonNull(player.getServer().getPlayer(uuid)).getName() + '\n').reduce(String::concat),
                Constants.INFO
            ));
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠궈지지 않았습니다.", Constants.ERROR));
        }

    }

    private boolean onUnLockCommand(@NotNull Player player, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Only unlock
        ItemStack item = player.getInventory().getItemInMainHand();
        Optional<Block> targetBlock = Optional.ofNullable(player.getTargetBlock(100));
        if (ObjectLock.isLockable(item.getType())) {
            Optional<ObjectLock> objectLock = ObjectLock.getObjectLock(item.getItemMeta().getPersistentDataContainer());
            objectLock.ifPresent((lock) -> lock.removeLock(item));
            player.sendMessage(Component.text(
                String.format(
                    "%s 아이템을 잠금 해제했습니다!",
                    item.getType().getKey().getKey()
                ),
                Constants.INFO
            ));
        }
        else if (targetBlock.isPresent() && ObjectLock.isLockable(targetBlock.get().getType())) {
            Block block = targetBlock.get();
            TileState state = (TileState) block.getState();
            Optional<ObjectLock> objectLock = ObjectLock.getObjectLock(state.getPersistentDataContainer());
            objectLock.ifPresent((lock) -> lock.removeLock(targetBlock.get()));
            player.sendMessage(Component.text(
                String.format(
                    "%s 블럭을 잠금 해제했습니다!",
                    targetBlock.get().getType().getKey().getKey()
                ),
                Constants.INFO
            ));
        }
        else {
            player.sendMessage(Component.text("이 아이템/블럭은 잠그거나 잠금 해제할 수 없습니다.", Constants.ERROR));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabCompletion = new ArrayList<>();
        if (command.getName().equals(Constants.CMD_LOCK)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                switch (args.length) {
                    case 1:
                        tabCompletion.add(Constants.CMD_ALLOW);
                        tabCompletion.add(Constants.CMD_DENY);
                        tabCompletion.add(Constants.CMD_LOCK_VIEW);
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
