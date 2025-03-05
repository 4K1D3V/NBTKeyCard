package pro.akii.ks.core.nbtKeyCard.utils;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.akii.ks.core.nbtKeyCard.managers.Mine;

public class KeyCardUtil {
    public static ItemStack createKeyCard(Mine mine, Long expiration, Integer uses) {
        ItemStack keyCard = new ItemStack(Material.TRIPWIRE_HOOK);
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(keyCard);
        NBTTagCompound nbt = nmsItem.getOrCreateTag();
        nbt.setString("mine_access", mine.getRequiredNbtValue());
        if (expiration != null) nbt.setLong("expiration", expiration);
        if (uses != null) nbt.setInt("uses", uses);
        nmsItem.setTag(nbt);
        ItemStack result = CraftItemStack.asBukkitCopy(nmsItem);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + mine.getName() + " Key Card");
            result.setItemMeta(meta);
        }
        return result;
    }

    public static boolean hasValidKeyCard(Player player, Mine mine) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.TRIPWIRE_HOOK) {
                net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
                NBTTagCompound nbt = nmsItem.getTag();
                if (nbt != null && nbt.hasKey("mine_access") && 
                    nbt.getString("mine_access").equals(mine.getRequiredNbtValue())) {
                    if (nbt.hasKey("expiration") && nbt.getLong("expiration") < System.currentTimeMillis()) {
                        player.getInventory().remove(item);
                        Utils.sendMessage(player, "&cYour key card for " + mine.getName() + " has expired!");
                        continue;
                    }
                    if (nbt.hasKey("uses")) {
                        int uses = nbt.getInt("uses");
                        if (uses <= 0) {
                            player.getInventory().remove(item);
                            Utils.sendMessage(player, "&cYour key card for " + mine.getName() + " has no uses left!");
                            continue;
                        }
                        nbt.setInt("uses", uses - 1);
                        nmsItem.setTag(nbt);
                        player.getInventory().setItem(player.getInventory().first(item), CraftItemStack.asBukkitCopy(nmsItem));
                    }
                    return true;
                }
            }
        }
        return false;
    }
}