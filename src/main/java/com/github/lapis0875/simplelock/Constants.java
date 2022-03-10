package com.github.lapis0875.simplelock;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;

public class Constants {
    public static final String PLUGIN_NAME = "";
    // Commands
    public static final String CMD_LOCK = "lock";
    public static final String CMD_UNLOCK = "unlock";
    public static final String CMD_ALLOW = "allow";
    public static final String CMD_DENY = "deny";
    public static final String CMD_LOCK_VIEW = "view";
    // Lock Meta
    public static final String LOCK_KEY = "objectlock";
    public static final String LOCK_OWNER_NAME = "ownerName";
    public static final String LOCK_OWNER_UUID = "ownerUUID";
    public static final String LOCK_ALLOWED_PLAYERS = "allowedPlayers";
    public static final String ALLOWED_PLAYER_BASE = "player";
    public static String ALLOWED_PLAYER(int index) {
        return ALLOWED_PLAYER_BASE + index;
    }
    // Player Lock Config Meta
    public static final String NOTIFICATION = "notification";
    public static NamespacedKey NOTIFICATION_KEY() {
        return new NamespacedKey(Simplelock.instance(), NOTIFICATION);
    }
    // Component Util
    public static final JoinConfiguration SPACE_JOIN_CONFIG = JoinConfiguration.separator(Component.text(" "));
    public static final JoinConfiguration COLON_JOIN_CONFIG = JoinConfiguration.separator(Component.text(" : "));
    public static final TextColor ALLOW_COLOR = TextColor.color(171, 242, 0);
    public static final TextColor DENY_COLOR = TextColor.color(200, 0, 0);
    public static final TextColor INFO_COLOR = TextColor.color(220, 220, 220);
}
