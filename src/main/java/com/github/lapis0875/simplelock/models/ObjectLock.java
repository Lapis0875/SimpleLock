package com.github.lapis0875.simplelock.models;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.Simplelock;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


public abstract class ObjectLock {

    public enum LockActionType {ACCESS, BREAK, REDSTONE, EXPLODE}
    static final HashMap<LockActionType, Component> denyTextMap = new HashMap<>();
    static final HashMap<LockActionType, Component> allowTextMap = new HashMap<>();
    static final HashMap<LockActionType, Component> ownerTextMap = new HashMap<>();

    static {
        denyTextMap.put(
            LockActionType.ACCESS,
            Component.text(
                "잠겨있는 블럭에 접근할 수 없습니다!",
                Constants.DENY_COLOR
            )
        );
        denyTextMap.put(
            LockActionType.BREAK,
            Component.text(
                "잠겨있는 블럭을 파괴할 수 없습니다!",
                Constants.DENY_COLOR
            )
        );
        denyTextMap.put(
            LockActionType.REDSTONE,
            Component.text(
                "잠겨있는 블럭에 레드스톤 신호를 보낼 수 없습니다!",
                Constants.DENY_COLOR
            )
        );
        denyTextMap.put(
            LockActionType.EXPLODE,
            Component.text(
                "잠겨있는 블럭을 폭파시킬 수 없습니다!",
                Constants.DENY_COLOR
            )
        );
        allowTextMap.put(
            LockActionType.ACCESS,
            Component.text(
                "접근이 허용되었습니다.",
                Constants.ALLOW_COLOR
            )
        );
        allowTextMap.put(
            LockActionType.BREAK,
            Component.text(
                "파괴가 허용되었습니다.",
                Constants.ALLOW_COLOR
            )
        );
        allowTextMap.put(
            LockActionType.REDSTONE,
            Component.text(
                "레드스톤 신호가 허용되었습니다.",
                Constants.ALLOW_COLOR
            )
        );
        allowTextMap.put(
            LockActionType.EXPLODE,
            Component.text(
                "폭파가 허용되었습니다.",
                Constants.ALLOW_COLOR
            )
        );
        ownerTextMap.put(
            LockActionType.ACCESS,
            Component.text(
                "님이 당신의 잠궈진 블럭에 접근했습니다.",
                Constants.INFO_COLOR
            )
        );
        ownerTextMap.put(
            LockActionType.BREAK,
            Component.text(
                "님이 당신의 잠궈진 블럭을 파괴하려 시도했습니다.",
                Constants.INFO_COLOR
            )
        );
        ownerTextMap.put(
            LockActionType.REDSTONE,
            Component.text(
                "님이 당신의 잠궈진 블럭에 레드스톤 신호를 보냈습니다.",
                Constants.INFO_COLOR
            )
        );
        ownerTextMap.put(
            LockActionType.EXPLODE,
            Component.text(
                "님이 당신의 잠궈진 블럭을 폭파시키려 시도했습니다.",
                Constants.INFO_COLOR
            )
        );
    }

    public abstract Player getOwner();

    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(Simplelock.instance(), Constants.LOCK_KEY);
    }

    public abstract void applyLock(ItemStack item);
    public abstract void applyLock(Block block);

    public abstract void removeLock(ItemStack item);
    public abstract void removeLock(Block block);

    public abstract boolean isPlayerAllowed(Player player);
    public abstract boolean isPlayerAllowed(UUID playerUUID);

    public abstract void allowPlayer(Player player);
    public abstract void allowPlayer(UUID playerUUID);

    public abstract void denyPlayer(Player player);
    public abstract void denyPlayer(UUID playerUUID);

    // Common Lock Methods are implemented in default.

    public static boolean isPlayerNotifiable(Player player) {
        Optional<Byte> notificationConfig = Optional.ofNullable(
            player.getPersistentDataContainer().get(
                Constants.NOTIFICATION_KEY(),
                PersistentDataType.BYTE
            )
        );
        AtomicBoolean isNotifiable = new AtomicBoolean(false);
        notificationConfig.ifPresentOrElse(
            (value) -> isNotifiable.set(value == 1),
            () -> {
                isNotifiable.set(false);
                player.getPersistentDataContainer().set(
                    Constants.NOTIFICATION_KEY(),
                    PersistentDataType.BYTE,
                    (byte) 0
                );
            }
        );
        return isNotifiable.get();
    }

    public static void setPlayerNotifiable(Player player, boolean value) {
        player.getPersistentDataContainer().set(
            Constants.NOTIFICATION_KEY(),
            PersistentDataType.BYTE,
            value ? (byte) 1 : (byte) 0
        );
    }

    public void sendLockAccessDeny(ObjectLock.LockActionType actionType, Player user) {
        user.playSound(user.getLocation(), Sound.BLOCK_CHEST_LOCKED, 70f, 0f);
        user.sendMessage(denyTextMap.get(actionType));
        if (!isPlayerNotifiable(this.getOwner())){
            return;
        }
        this.getOwner().sendMessage(
            Component.join(
                Constants.SPACE_JOIN_CONFIG,
                new Component[]{
                    user.displayName(),
                    ownerTextMap.get(actionType)
                }
            )
        );
    }

    public void sendLockAccessAllow(ObjectLock.LockActionType actionType, Player user) {
        user.sendMessage(allowTextMap.get(actionType));
        if (!isPlayerNotifiable(this.getOwner())){
            return;
        }
        this.getOwner().sendMessage(
            Component.join(
                Constants.SPACE_JOIN_CONFIG,
                new Component[]{
                    user.displayName(),
                    ownerTextMap.get(actionType)
                }
            )
        );
    }
}
