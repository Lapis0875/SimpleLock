package com.github.lapis0875.simplelock.listeners;

import com.github.lapis0875.simplelock.Constants;
import com.github.lapis0875.simplelock.models.ObjectLock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
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
                "이 서버는 SimpleLock 플러그인을 사용중입니다. /lock 으로 편리한 셋홈과 사망 위치로의 귀환을 사용해보세요!",
                Constants.INFO
        ));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Optional<Block> optionalBlock = Optional.ofNullable(e.getClickedBlock());
        if (!optionalBlock.isPresent()) return;
        Block block = optionalBlock.get();
        if (ObjectLock.isLockable(block.getType())) {
            TileState state = (TileState) block.getState();
            Optional<ObjectLock> objectLock = ObjectLock.getObjectLock(state.getPersistentDataContainer());
            objectLock.ifPresent((lock) -> {
                if (!lock.isPlayerAllowed(e.getPlayer())) {
                    e.setCancelled(true);
                    e.setUseInteractedBlock(Event.Result.DENY);
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_CHEST_LOCKED, 70f, 0f);
                    e.getPlayer().showTitle(Title.title(
                            Component.text("잠겨있습니다!", Constants.ERROR),
                            Component.text("소유주 : " + lock.ownerName, Constants.INFO)
                    ));
                    e.getPlayer().sendMessage(Component.text("잠겨있습니다!", Constants.ERROR));
                    lock.getOwner().sendMessage(Component.text(e.getPlayer().getName() + " 님이 당신의 잠궈진 블럭에 접근했습니다.", Constants.ERROR));
                }
                else {
                    e.getPlayer().showTitle(Title.title(
                            Component.text("접근이 허가되었습니다.", Constants.INFO),
                            Component.text("소유주 : " + lock.ownerName, Constants.INFO)
                    ));
                    e.getPlayer().sendMessage(Component.text("접근이 허가되었습니다.", Constants.INFO));
                    lock.getOwner().sendMessage(Component.text(e.getPlayer().getName() + " 님이 당신의 잠궈진 블럭에 접근했습니다.", Constants.INFO));
                }
            });
        }
    }
}
