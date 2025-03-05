package pro.akii.ks.core.nbtKeyCard.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pro.akii.ks.core.nbtKeyCard.NBTKeyCard;
import pro.akii.ks.core.nbtKeyCard.managers.Mine;

public class Utils {
    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendDenialMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
    }

    public static void spawnDenialParticles(Player player, Location location) {
        player.spawnParticle(Particle.REDSTONE, location, 10, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.RED, 1));
    }

    public static void showMineBoundaries(Player player, Mine mine) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 200) { // 10 seconds (20 ticks per second)
                    cancel();
                    return;
                }
                World world = mine.getWorld();
                for (int x = mine.getMinX(); x <= mine.getMaxX(); x++) {
                    for (int z = mine.getMinZ(); z <= mine.getMaxZ(); z++) {
                        if (x == mine.getMinX() || x == mine.getMaxX() || z == mine.getMinZ() || z == mine.getMaxZ()) {
                            player.spawnParticle(Particle.VILLAGER_HAPPY, 
                                new Location(world, x + 0.5, mine.getMinY() + 0.5, z + 0.5), 1);
                            player.spawnParticle(Particle.VILLAGER_HAPPY, 
                                new Location(world, x + 0.5, mine.getMaxY() + 0.5, z + 0.5), 1);
                        }
                    }
                }
                ticks += 2; // Update every 2 ticks for performance
            }
        }.runTaskTimer(NBTKeyCard.getInstance(), 0L, 2L);
    }
}