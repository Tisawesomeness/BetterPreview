package com.tisawesomeness.betterpreview.spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public class Util {

    // Permission checker that supports wildcards without needing Vault
    // Adapted from EssentialsChat
    // https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/main/java/com/earth2me/essentials/perm/impl/SuperpermsHandler.java

    /**
     * Checks if the player has the given permission, checking wildcards.
     * Use {@link Player#isPermissionSet(String)} to check if a permission is set.
     * @param player the player to check
     * @param permission the permission to check
     * @return whether the player has the permission
     */
    public static boolean hasPermission(Permissible player, String permission) {
        String perm = permission;
        String permToCheck = permission;
        int idx;
        while (true) {
            if (player.isPermissionSet(permToCheck) || isDeniedToOps(permToCheck)) {
                return player.hasPermission(permToCheck);
            }
            idx = perm.lastIndexOf('.');
            if (idx < 1) {
                return player.hasPermission("*");
            }
            perm = perm.substring(0, idx);
            permToCheck = perm + ".*";
        }
    }
    private static boolean isDeniedToOps(String node) {
        var perm = Bukkit.getServer().getPluginManager().getPermission(node);
        return perm != null && !perm.getDefault().getValue(true);
    }

}
