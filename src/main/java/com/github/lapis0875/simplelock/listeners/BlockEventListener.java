package com.github.lapis0875.simplelock.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.models.ObjectLock;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
        if (!ObjectLock.isLockable(item.getType())) return;

        Optional<ObjectLock> lock = ObjectLock.getObjectLock(item.getItemMeta().getPersistentDataContainer());
        lock.ifPresent((objectLock) -> objectLock.applyLock(e.getBlockPlaced()));
    }

    /*
    * Handle when redstone signal is on locked object.
    */
    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent e) {
        if (!ObjectLock.isLockable(e.getBlock().getType())) return;
        TileState state = (TileState) e.getBlock().getState();
        Optional<ObjectLock> objectLock = ObjectLock.getObjectLock(state.getPersistentDataContainer());
        if (!objectLock.isPresent()) return;
        e.setNewCurrent(0);
    }

    /*
    * Only for player breaks locked object.
    */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (!ObjectLock.isLockable(block.getType())) return;

        TileState state = (TileState) e.getBlock().getState();
        Optional<ObjectLock> optionalLock = ObjectLock.getObjectLock(state.getPersistentDataContainer());
        optionalLock.ifPresent((lock) -> {
            Player player = e.getPlayer();
            if (player.getUniqueId().equals(lock.ownerUUID)) {
                player.sendMessage(Component.text("잠긴 아이템/블럭을 파괴했습니다.", Constants.INFO));
            }
            else {
                player.sendMessage(Component.text("소유주가 아니므로 잠긴 블럭을 파괴할 수 없습니다.", Constants.ERROR));
                e.setCancelled(true);
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
                && ObjectLock.isLockable(block.getType())
                && ObjectLock.getObjectLock(((TileState) block.getState()).getPersistentDataContainer()).isPresent()
        );
//        e.blockList().stream().filter(
//                (block) -> block != null && ObjectLock.isLockable(block.getType()) && ObjectLock.getObjectLock(((TileState) block.getState()).getPersistentDataContainer()).isPresent()
//        ).forEach((lockedBlock) -> e.blockList().remove(lockedBlock));
    }
}
