package com.github.lapis0875.simplelock.models;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class NonNBTLock extends ObjectLock {

    public static Material[] SupportedBlocks = {
            Material.IRON_DOOR,
            Material.OAK_DOOR,
            Material.BIRCH_DOOR,
            Material.SPRUCE_DOOR,
            Material.JUNGLE_DOOR,
            Material.ACACIA_DOOR,
            Material.DARK_OAK_DOOR,
            Material.CRIMSON_DOOR,
            Material.WARPED_DOOR,
            Material.IRON_TRAPDOOR,
            Material.OAK_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.ACACIA_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.CRIMSON_TRAPDOOR,
            Material.WARPED_TRAPDOOR,
            Material.OAK_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.ACACIA_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.CRIMSON_FENCE_GATE,
            Material.WARPED_FENCE_GATE
    };

    private final String ownerName;
    private final UUID ownerUUID;
    private final List<UUID> allowedPlayers;
    private final Block block;

    public NonNBTLock(Player player, Block block) {
        this.ownerName = player.getName();
        this.ownerUUID = player.getUniqueId();
        this.allowedPlayers = new ArrayList<>();
        this.block = block;
    }

    public static boolean isLockable(Material material) {
        return Arrays.asList(NonNBTLock.SupportedBlocks).contains(material);
    }

    public static Optional<NonNBTLock> getLock(Block block) {
        return Optional.empty();
    }

    public static boolean hasLock(Block block) {
        Optional<NonNBTLock> optLock = NonNBTLock.getLock(block);
        return optLock.isPresent();
    }


    @Override
    public Player getOwner() {
        return Bukkit.getServer().getPlayer(this.ownerUUID);
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return null;
    }

    @Override
    public void applyLock(ItemStack item) {

    }

    @Override
    public void applyLock(Block block, @Nullable Block original) {

    }

    @Override
    public void removeLock(ItemStack item) {

    }

    @Override
    public void removeLock(Block block, @Nullable Block original) {

    }

    @Override
    public boolean isPlayerAllowed(Player player) {
        return false;
    }

    @Override
    public boolean isPlayerAllowed(UUID playerUUID) {
        return false;
    }

    @Override
    public void allowPlayer(Player player) {

    }

    @Override
    public void allowPlayer(UUID playerUUID) {

    }

    @Override
    public void denyPlayer(Player player) {

    }

    @Override
    public void denyPlayer(UUID playerUUID) {

    }
}
