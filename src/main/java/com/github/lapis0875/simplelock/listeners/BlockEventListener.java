package com.github.lapis0875.simplelock.listeners;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.models.NBTLock;
import com.github.lapis0875.simplelock.models.ObjectLock;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BlockEventListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (!NBTLock.isLockable(item.getType())) return;

        Optional<NBTLock> lock = NBTLock.getLock(item.getItemMeta().getPersistentDataContainer());
        lock.ifPresent((objectLock) -> objectLock.applyLock(e.getBlockPlaced(), null));
    }

    /*
    * Handle when redstone signal is on locked object.
    */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockRedstone(BlockRedstoneEvent e) {
        if (!NBTLock.isLockable(e.getBlock().getType())) return;
        TileState state = (TileState) e.getBlock().getState();
        Optional<NBTLock> objectLock = NBTLock.getLock(state.getPersistentDataContainer());
        if (objectLock.isEmpty()) return;
        e.setNewCurrent(0);
    }

    /*
    * Only for player breaks locked object.
    */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (!NBTLock.isLockable(block.getType())) return;

        TileState state = (TileState) e.getBlock().getState();
        Optional<NBTLock> optionalLock = NBTLock.getLock(state.getPersistentDataContainer());
        optionalLock.ifPresent((lock) -> {
            Player player = e.getPlayer();
            if (player.getUniqueId().equals(lock.ownerUUID)) {
                lock.sendLockAccessAllow(ObjectLock.LockActionType.BREAK, e.getPlayer());
            }
            else {
                e.setCancelled(true);
                lock.sendLockAccessDeny(ObjectLock.LockActionType.BREAK, e.getPlayer());
            }
        });

    }

//    @EventHandler
//    public void onBlockDestroy(BlockDestroyEvent e) {
//        if (!ObjectLock.isLockable(e.getBlock().getType())) return;
//        TileState state = (TileState) e.getBlock().getState();
//        Optional<ObjectLock> objectLock = ObjectLock.getObjectLock(state.getPersistentDataContainer());
//        objectLock.ifPresent((lock) -> e.setCancelled(true));
//    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().removeIf(
            block ->
                block != null
                && NBTLock.isLockable(block.getType())
                && NBTLock.getLock(((TileState) block.getState()).getPersistentDataContainer()).isPresent()
        );
//        e.blockList().stream().filter(
//                (block) -> block != null && ObjectLock.isLockable(block.getType()) && ObjectLock.getObjectLock(((TileState) block.getState()).getPersistentDataContainer()).isPresent()
//        ).forEach((lockedBlock) -> e.blockList().remove(lockedBlock));
    }
}
