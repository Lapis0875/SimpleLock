package com.github.lapis0875.simplelock.listeners;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.models.NonNBTLock;
import com.github.lapis0875.simplelock.models.ObjectLock;
import com.github.lapis0875.simplelock.models.NBTLock;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

public class PlayerEventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.sendMessage(Component.text(
                "이 서버는 SimpleLock 플러그인을 사용중입니다. /lock 으로 당신의 귀중한 블럭들을 잠가보세요!",
                Constants.ALLOW_COLOR
        ));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Optional<Block> optionalBlock = Optional.ofNullable(e.getClickedBlock());
        if (optionalBlock.isEmpty()) return;

        Block block = optionalBlock.get();
        if (NBTLock.isLockable(block.getType())) {
            TileState state = (TileState) block.getState();
            Optional<NBTLock> objectLock = NBTLock.getLock(state.getPersistentDataContainer());
            objectLock.ifPresent((lock) -> {
                if (!lock.isPlayerAllowed(e.getPlayer())) {
                    e.setCancelled(true);
                    e.setUseInteractedBlock(Event.Result.DENY);
                    lock.sendLockAccessDeny(ObjectLock.LockActionType.ACCESS, e.getPlayer());
                }
                else {
                    lock.sendLockAccessAllow(ObjectLock.LockActionType.ACCESS, e.getPlayer());
                }
            });
        }
        else if (NonNBTLock.isLockable(block.getType())) {
            // Do a barrel roll
        }
    }
}
