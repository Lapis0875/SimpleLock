package com.github.lapis0875.simplelock.models;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.Simplelock;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.*;
import java.util.List;


public class ObjectLock {

    public static class LockableMaterials {
        // Doors are not tile entity! How to store data in them?
//        public static Material[] DOORS = {
//                Material.IRON_DOOR,
//                Material.OAK_DOOR,
//                Material.BIRCH_DOOR,
//                Material.SPRUCE_DOOR,
//                Material.JUNGLE_DOOR,
//                Material.ACACIA_DOOR,
//                Material.DARK_OAK_DOOR,
//                Material.CRIMSON_DOOR,
//                Material.WARPED_DOOR,
//                Material.IRON_TRAPDOOR,
//                Material.OAK_TRAPDOOR,
//                Material.BIRCH_TRAPDOOR,
//                Material.SPRUCE_TRAPDOOR,
//                Material.JUNGLE_TRAPDOOR,
//                Material.ACACIA_TRAPDOOR,
//                Material.DARK_OAK_TRAPDOOR,
//                Material.CRIMSON_TRAPDOOR,
//                Material.WARPED_TRAPDOOR,
//                Material.OAK_FENCE_GATE,
//                Material.BIRCH_FENCE_GATE,
//                Material.SPRUCE_FENCE_GATE,
//                Material.JUNGLE_FENCE_GATE,
//                Material.ACACIA_FENCE_GATE,
//                Material.DARK_OAK_FENCE_GATE,
//                Material.CRIMSON_FENCE_GATE,
//                Material.WARPED_FENCE_GATE
//        };

        public Material[] CHESTS = {
                Material.CHEST,
                Material.BARREL,
                Material.SHULKER_BOX
        };

        public Material[] FURNACES = {
                Material.FURNACE,
                Material.BLAST_FURNACE,
                Material.SMOKER
        };

        public static Material[] ALL = {
//            Material.IRON_DOOR,
//            Material.OAK_DOOR,
//            Material.BIRCH_DOOR,
//            Material.SPRUCE_DOOR,
//            Material.JUNGLE_DOOR,
//            Material.ACACIA_DOOR,
//            Material.DARK_OAK_DOOR,
//            Material.CRIMSON_DOOR,
//            Material.WARPED_DOOR,
//            Material.IRON_TRAPDOOR,
//            Material.OAK_TRAPDOOR,
//            Material.BIRCH_TRAPDOOR,
//            Material.SPRUCE_TRAPDOOR,
//            Material.JUNGLE_TRAPDOOR,
//            Material.ACACIA_TRAPDOOR,
//            Material.DARK_OAK_TRAPDOOR,
//            Material.CRIMSON_TRAPDOOR,
//            Material.WARPED_TRAPDOOR,
//            Material.OAK_FENCE_GATE,
//            Material.BIRCH_FENCE_GATE,
//            Material.SPRUCE_FENCE_GATE,
//            Material.JUNGLE_FENCE_GATE,
//            Material.ACACIA_FENCE_GATE,
//            Material.DARK_OAK_FENCE_GATE,
//            Material.CRIMSON_FENCE_GATE,
//            Material.WARPED_FENCE_GATE,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.BARREL,
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER,
            Material.DISPENSER,
            Material.DROPPER,
            Material.HOPPER,
            Material.CAMPFIRE,
            Material.SOUL_CAMPFIRE,
            Material.LECTERN,
            Material.BREWING_STAND,
            Material.JUKEBOX,
            Material.DAYLIGHT_DETECTOR,
            Material.BEEHIVE
        };
    }
    public final String ownerName;
    public final UUID ownerUUID;
    // Permissions
    public final List<UUID> allowedPlayers;

    public ObjectLock(Player owner) {
        this.ownerName = owner.getName();
        this.ownerUUID = owner.getUniqueId();
        this.allowedPlayers = new ArrayList<>();
        this.allowedPlayers.add(this.ownerUUID);
    }

    public ObjectLock(String ownerName, UUID ownerUUID) {

        this.ownerName = ownerName;
        this.ownerUUID = ownerUUID;
        this.allowedPlayers = new ArrayList<>();
        this.allowedPlayers.add(this.ownerUUID);
    }

    public static boolean isLockable(Material material) {
        return Arrays.asList(LockableMaterials.ALL).contains(material);
    }

    public static Optional<ObjectLock> getObjectLock(PersistentDataContainer dataContainer) {
        return Optional.ofNullable(dataContainer.get(
            new NamespacedKey(
                Simplelock.instance(),
                Constants.LOCK_KEY
            ),
            new ObjectLockDataType()
        ));
    }

    public void applyLock(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
                this.getNamespacedKey(),
                new ObjectLockDataType(),
                this
        );
        ArrayList<Component> lores = new ArrayList<>();
        lores.add(Component.text("잠겨있습니다.", Constants.INFO));
        lores.add(Component.text("소유주 : " + this.ownerName, Constants.INFO));
        meta.lore(lores);
        item.setItemMeta(meta);

    }

    public void applyLock(Block block) {
        if (!ObjectLock.isLockable(block.getType())) return;
        TileState state = (TileState) block.getState();
        state.getPersistentDataContainer().set(
                this.getNamespacedKey(),
                new ObjectLockDataType(),
                this
        );
        state.update();
    }

    public void removeLock(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(
                this.getNamespacedKey()
        );
        meta.lore(null);
        item.setItemMeta(meta);
    }

    public void removeLock(Block block) {
        if (!ObjectLock.isLockable(block.getType())) return;
        TileState state = (TileState) block.getState();
        state.getPersistentDataContainer().remove(
                this.getNamespacedKey()
        );
        state.update();
    }

    public boolean isPlayerAllowed(Player player) {
        return this.allowedPlayers.contains(player.getUniqueId());
    }

    public void allowPlayer(Player player) {
        this.allowedPlayers.add(player.getUniqueId());
    }

    public void allowPlayer(UUID playerUUID) {
        this.allowedPlayers.add(playerUUID);
    }

    public void denyPlayer(Player player) {
        this.allowedPlayers.remove(player.getUniqueId());
    }

    public void denyPlayer(UUID playerUUID) {
        this.allowedPlayers.remove(playerUUID);
    }

    public Player getOwner() {
        return Bukkit.getServer().getPlayer(this.ownerUUID);
    }

    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(Simplelock.instance(), Constants.LOCK_KEY);
    }

    public PersistentDataContainer toNBTTag(PersistentDataAdapterContext context) {
        PersistentDataContainer tag = context.newPersistentDataContainer();
        tag.set(
            new NamespacedKey(
                Simplelock.instance(),
                Constants.LOCK_OWNER_NAME
            ),
            PersistentDataType.STRING,
            this.ownerName
        );
        tag.set(
            new NamespacedKey(
                Simplelock.instance(),
                Constants.LOCK_OWNER_UUID
            ),
            new UUIDDataType(),
            this.ownerUUID
        );
        PersistentDataContainer allowedPlayersTag = context.newPersistentDataContainer();
        int index = 1;
        this.allowedPlayers.forEach(
            (uuid) -> {
                allowedPlayersTag.set(
                    new NamespacedKey(
                            Simplelock.instance(),
                            Constants.ALLOWED_PLAYER(index)
                    ),
                    new UUIDDataType(),
                    uuid
                );
            }
        );
        tag.set(
            new NamespacedKey(
                Simplelock.instance(),
                Constants.LOCK_ALLOWED_PLAYERS
            ),
            PersistentDataType.TAG_CONTAINER,
            allowedPlayersTag
        );
        return tag;
    }
}


