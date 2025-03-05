package pro.akii.ks.core.nbtKeyCard.gui;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pro.akii.ks.core.nbtKeyCard.managers.Mine;
import pro.akii.ks.core.nbtKeyCard.managers.MineManager;
import pro.akii.ks.core.nbtKeyCard.utils.KeyCardUtil;
import pro.akii.ks.core.nbtKeyCard.utils.Utils;

import java.util.Collections;

public class NBTGUI implements Listener {
    private final MineManager mineManager;
    private final Economy economy;

    public NBTGUI(MineManager mineManager, Economy economy) {
        this.mineManager = mineManager;
        this.economy = economy;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY + "Select a Mine");
        for (Mine mine : mineManager.getMines().values()) {
            if ("restricted".equals(mine.getAccess())) {
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + mine.getName());
                item.setItemMeta(meta);
                gui.addItem(item);
            }
        }
        player.openInventory(gui);
    }

    public void openShop(Player player) {
        Inventory shop = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY + "Key Card Shop");
        for (Mine mine : mineManager.getMines().values()) {
            if ("restricted".equals(mine.getAccess())) {
                ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + mine.getName() + " Key Card");
                meta.setLore(Collections.singletonList(ChatColor.GOLD + "Price: " + mine.getShopPrice()));
                item.setItemMeta(meta);
                shop.addItem(item);
            }
        }
        player.openInventory(shop);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(ChatColor.DARK_GRAY + "Select a Mine") || title.equals(ChatColor.DARK_GRAY + "Key Card Shop")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || (clicked.getType() != Material.PAPER && clicked.getType() != Material.TRIPWIRE_HOOK)) return;

            String mineName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).replace(" Key Card", "");
            Mine mine = mineManager.getMineByName(mineName);
            if (mine == null) return;

            Player player = (Player) event.getWhoClicked();
            if (title.equals(ChatColor.DARK_GRAY + "Select a Mine")) {
                ItemStack keyCard = KeyCardUtil.createKeyCard(mine, null, null);
                player.getInventory().addItem(keyCard);
                Utils.sendMessage(player, "&aReceived Access Key Card for " + mine.getName());
            } else if (title.equals(ChatColor.DARK_GRAY + "Key Card Shop")) {
                if (economy.withdrawPlayer(player, mine.getShopPrice()).transactionSuccess()) {
                    ItemStack keyCard = KeyCardUtil.createKeyCard(mine, null, null);
                    player.getInventory().addItem(keyCard);
                    Utils.sendMessage(player, "&aPurchased Key Card for " + mine.getName());
                } else {
                    Utils.sendMessage(player, "&cInsufficient funds!");
                }
            }
            player.closeInventory();
        }
    }
}