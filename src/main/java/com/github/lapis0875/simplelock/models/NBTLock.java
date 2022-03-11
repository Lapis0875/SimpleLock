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

    private List<Block> getAdjacentBlocks(Block block) {
        ArrayList<Block> adjacentBlocks = new ArrayList<>();
        adjacentBlocks.add(block.getWorld().getBlockAt(block.getLocation().add(1, 0, 0)));
        adjacentBlocks.add(block.getWorld().getBlockAt(block.getLocation().add(-1, 0, 0)));
        adjacentBlocks.add(block.getWorld().getBlockAt(block.getLocation().add(0, 0, 1)));
        adjacentBlocks.add(block.getWorld().getBlockAt(block.getLocation().add(0, 0, -1)));
        return adjacentBlocks;
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
                && ((Chest) block.getState()).getInventory().getHolder() instanceof DoubleChest
        ) {
            Chest state = (Chest) block.getState();
            DoubleChest holder = (DoubleChest) state.getInventory().getHolder();

            Optional<Chest> optLeft = Optional.ofNullable((Chest) holder.getLeftSide());
            optLeft.ifPresent(
                    (left) -> block.getWorld().sendMessage(
                            Component.text(
                                    "Left side of chest : (%d, %d, %d)".formatted(
                                            left.getX(),
                                            left.getY(),
                                            left.getZ()
                                    ),
                                    Constants.INFO_COLOR
                            )
                    )
            );
            Optional<Chest> optRight = Optional.ofNullable((Chest) holder.getRightSide());
            optRight.ifPresent(
                    (right) -> block.getWorld().sendMessage(
                            Component.text(
                                    "Left side of chest : (%d, %d, %d)".formatted(
                                            right.getX(),
                                            right.getY(),
                                            right.getZ()
                                    ),
                                    Constants.INFO_COLOR
                            )
                    )
            );
            
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
            state.getPersistentDataContainer().set(
                    this.getNamespacedKey(),
                    new NBTLockDataType(),
                    this
            );
            state.update();
            if (original == null && optLeft.isPresent() && optRight.isPresent()) {
                Chest left = optLeft.get();
                Chest right = optRight.get();
                Location otherPart = block.getLocation().equals(left.getLocation()) ? right.getLocation() : left.getLocation();
                for (Block b: this.getAdjacentBlocks(block)) {
                    if (b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)) {
                        block.getWorld().sendMessage(Component.text(
                                String.format(
                                        "Found chest / trapped chest at (%d, %d, %d)",
                                        b.getX(), b.getY(), b.getZ()
                                ),
                                Constants.INFO_COLOR
                        ));
                        if (b.getLocation().equals(otherPart)) {
                            this.applyLock(b, block);
                        }
                    }
                }
            }
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
    /*
      @param block : block to remove lock in it.
     * @param original : parameter to indicate whether it is removing locks of adjacent blocks or the original block.
     */
    public void removeLock(Block block, @Nullable Block original) {
        if (!NBTLock.isLockable(block.getType())) return;
        if ((block.getType() == Material.CHEST
                || block.getType() == Material.TRAPPED_CHEST)
                && ((Chest) block.getState()).getInventory().getHolder() instanceof DoubleChest
        ) {
            // Double Chest! Damn it.
            Chest state = (Chest) block.getState();
            DoubleChest holder = (DoubleChest) state.getInventory().getHolder();

            Optional<Chest> optLeft = Optional.ofNullable((Chest) holder.getLeftSide());
            optLeft.ifPresent(
                    (left) -> block.getWorld().sendMessage(
                            Component.text(
                                    "Left side of chest : (%d, %d, %d)".formatted(
                                            left.getX(),
                                            left.getY(),
                                            left.getZ()
                                    ),
                                    Constants.INFO_COLOR
                            )
                    )
            );
            Optional<Chest> optRight = Optional.ofNullable((Chest) holder.getRightSide());
            optRight.ifPresent(
                    (right) -> block.getWorld().sendMessage(
                            Component.text(
                                    "Left side of chest : (%d, %d, %d)".formatted(
                                            right.getX(),
                                            right.getY(),
                                            right.getZ()
                                    ),
                                    Constants.INFO_COLOR
                            )
                    )
            );

            state.getPersistentDataContainer().remove(
                    this.getNamespacedKey()
            );
            state.update();
            if (original == null && optLeft.isPresent() && optRight.isPresent()) {
                Chest left = optLeft.get();
                Chest right = optRight.get();
                Location otherPart = block.getLocation().equals(left.getLocation()) ? right.getLocation() : left.getLocation();
                for (Block b: this.getAdjacentBlocks(block)) {
                    if (b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)) {
                        block.getWorld().sendMessage(Component.text(
                                String.format(
                                        "Found chest / trapped chest at (%d, %d, %d)",
                                        b.getX(), b.getY(), b.getZ()
                                ),
                                Constants.INFO_COLOR
                        ));
                        if (b.getLocation().equals(otherPart)) {
                            this.removeLock(b, block);
                        }
                    }
                }
            }

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


