package com.github.lapis0875.simplelock.models;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.Simplelock;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;


public class NBTLock extends ObjectLock {

    public static Material[] SupportedBlocks = {
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

    public final String ownerName;
    public final UUID ownerUUID;
    // Permissions
    public final List<UUID> allowedPlayers;

    public NBTLock(Player owner) {
        this.ownerName = owner.getName();
        this.ownerUUID = owner.getUniqueId();
        this.allowedPlayers = new ArrayList<>();
        this.allowedPlayers.add(this.ownerUUID);
    }

    public NBTLock(String ownerName, UUID ownerUUID) {
        this.ownerName = ownerName;
        this.ownerUUID = ownerUUID;
        this.allowedPlayers = new ArrayList<>();
        this.allowedPlayers.add(this.ownerUUID);
    }

    public static boolean isLockable(Material material) {
        return Arrays.asList(NBTLock.SupportedBlocks).contains(material);
    }

    public static Optional<NBTLock> getLock(PersistentDataContainer dataContainer) {
        return Optional.ofNullable(dataContainer.get(
            new NamespacedKey(
                Simplelock.instance(),
                Constants.LOCK_KEY
            ),
            new NBTLockDataType()
        ));
    }

    public static boolean hasLock(PersistentDataContainer dataContainer) {
        Optional<NBTLock> optLock = NBTLock.getLock(dataContainer);
        return optLock.isPresent();
    }

    @Override
    public void applyLock(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
                this.getNamespacedKey(),
                new NBTLockDataType(),
                this
        );
        ArrayList<Component> lores = new ArrayList<>();
        lores.add(Component.text("잠겨있습니다.", Constants.ALLOW_COLOR));
        lores.add(Component.text("소유주 : " + this.ownerName, Constants.ALLOW_COLOR));
        meta.lore(lores);
        item.setItemMeta(meta);

    }

    @Override
    public void applyLock(Block block, @Nullable Block original) {
        if (!NBTLock.isLockable(block.getType())) return;
        block.getWorld().sendMessage(Component.text("Type of block : ".concat(block.getType().toString())));
        if ((block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST))
                && ((Chest) block.getState()).getInventory() instanceof DoubleChestInventory
        ) {
            // Double Chest! Damn it.
            Chest state = (Chest) block.getState();
            ArrayList<Block> adjacentBlocks = new ArrayList<>();
            adjacentBlocks.add(block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0)));
            adjacentBlocks.add(block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0)));
            adjacentBlocks.add(block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1)));
            adjacentBlocks.add(block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1)));
            
            Block xUp = block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0));
            block.getWorld().sendMessage(Component.text(
                    String.format(
                            "Block at : (+1, 0, 0) : %s",
                            xUp.getType()
                    ),
                    Constants.INFO_COLOR
            ));

            Block xDown = block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0));
            block.getWorld().sendMessage(Component.text(
                    String.format(
                            "Block at : (-1, 0, 0) : %s",
                            xDown.getType()
                    ),
                    Constants.INFO_COLOR
            ));
            Block zUp = block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1));
            block.getWorld().sendMessage(Component.text(
                    String.format(
                            "Block at : (0, 0, +1) : %s",
                            zUp.getType()
                    ),
                    Constants.INFO_COLOR
            ));
            Block zDown = block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1));
            block.getWorld().sendMessage(Component.text(
                    String.format(
                            "Block at : (0, 0, -1) : %s",
                            zDown.getType()
                    ),
                    Constants.INFO_COLOR
            ));
            if (original == null) {
                for (Block b: adjacentBlocks) {
                    if (b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)) {
                        block.getWorld().sendMessage(Component.text(
                                String.format(
                                        "Found chest / trapped chest at (%d, %d, %d)",
                                        b.getX(), b.getY(), b.getZ()
                                ),
                                Constants.INFO_COLOR
                        ));
                        this.applyLock(b, block);
                    }
                }
            }
            state.getPersistentDataContainer().set(
                    this.getNamespacedKey(),
                    new NBTLockDataType(),
                    this
            );
            state.update();
//            // Check if chest is big chest
//            Chest state = (Chest) block.getState();
//            InventoryHolder holder = state.getInventory().getHolder();
//            if (holder instanceof DoubleChest) {
//            }
        }
        else {
            TileState state = (TileState) block.getState();

            state.getPersistentDataContainer().set(
                    this.getNamespacedKey(),
                    new NBTLockDataType(),
                    this
            );
            state.update();
        }
    }

    @Override
    public void removeLock(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(
                this.getNamespacedKey()
        );
        meta.lore(null);
        item.setItemMeta(meta);
    }

    @Override
    public void removeLock(Block block, @Nullable Block original) {
        if (!NBTLock.isLockable(block.getType())) return;
        if ((block.getType() == Material.CHEST
                || block.getType() == Material.TRAPPED_CHEST)
                && ((Chest) block.getState()).getInventory() instanceof DoubleChestInventory
        ) {
            // Check if chest is big chest
            Bukkit.getLogger().info("Double Chest Detected");
            Chest state = (Chest) block.getState();
            DoubleChest holder = (DoubleChest) state.getInventory().getHolder();
            // Double Chest! Damn it.
            block.getWorld().sendMessage(Component.text(
                    String.format(
                            "Double chest detected at : (%d, %d, %d)",
                            block.getX(),
                            block.getY(),
                            block.getZ()
                    )
            ));
            Location chestLeft = holder.getLeftSide().getInventory().getLocation();
            block.getWorld().sendMessage(Component.text(
                    String.format(
                            "Left > (%d, %d, %d)",
                            chestLeft.getBlockX(),
                            chestLeft.getBlockY(),
                            chestLeft.getBlockZ()
                    )
            ));
            Location chestRight = holder.getRightSide().getInventory().getLocation();
            block.getWorld().sendMessage(Component.text(
                    String.format(
                            "Right > (%d, %d, %d)",
                            chestRight.getBlockX(),
                            chestRight.getBlockY(),
                            chestRight.getBlockZ()
                    )
            ));

            state.getPersistentDataContainer().remove(
                    this.getNamespacedKey()
            );
            state.update();

        }
        else {
            TileState state = (TileState) block.getState();
            state.getPersistentDataContainer().remove(
                    this.getNamespacedKey()
            );
            state.update();
        }
    }

    @Override
    public boolean isPlayerAllowed(Player player) {
        return this.isPlayerAllowed(player.getUniqueId());
    }

    @Override
    public boolean isPlayerAllowed(UUID playerUUID) {
        return this.allowedPlayers.contains(playerUUID);
    }

    @Override
    public void allowPlayer(Player player) {
        this.allowedPlayers.add(player.getUniqueId());
    }

    @Override
    public void allowPlayer(UUID playerUUID) {
        this.allowedPlayers.add(playerUUID);
    }

    @Override
    public void denyPlayer(Player player) {
        this.allowedPlayers.remove(player.getUniqueId());
    }

    @Override
    public void denyPlayer(UUID playerUUID) {
        this.allowedPlayers.remove(playerUUID);
    }

    @Override
    @Nullable
    public Player getOwner() {
        return Bukkit.getServer().getPlayer(this.ownerUUID);
    }

    @Override
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
            (uuid) -> allowedPlayersTag.set(
                new NamespacedKey(
                        Simplelock.instance(),
                        Constants.ALLOWED_PLAYER(index)
                ),
                new UUIDDataType(),
                uuid
            )
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


