package pro.akii.ks.core.nbtKeyCard.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import pro.akii.ks.core.nbtKeyCard.NBTKeyCard;
import pro.akii.ks.core.nbtKeyCard.utils.KeyCardUtil;
import pro.akii.ks.core.nbtKeyCard.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MineManager implements Listener {
    private final NBTKeyCard plugin;
    private final Logger logger;
    private final Map<String, Mine> mines = new HashMap<>();
    private final Map<UUID, Mine> playerMineCache = new HashMap<>();
    private final Map<String, Mine> codeToMine = new HashMap<>();

    public MineManager(NBTKeyCard plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        loadMines();
    }

    private void loadMines() {
        ConfigurationSection minesSection = plugin.getConfig().getConfigurationSection("mines");
        if (minesSection == null) {
            logger.warning("No 'mines' section found in config.yml!");
            return;
        }
        for (String key : minesSection.getKeys(false)) {
            ConfigurationSection mineSection = minesSection.getConfigurationSection(key);
            String worldName = mineSection.getString("world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                logger.warning("World '" + worldName + "' not found for mine '" + key + "'");
                continue;
            }
            int minX = mineSection.getInt("min.x");
            int minY = mineSection.getInt("min.y");
            int minZ = mineSection.getInt("min.z");
            int maxX = mineSection.getInt("max.x");
            int maxY = mineSection.getInt("max.y");
            int maxZ = mineSection.getInt("max.z");
            if (minX > maxX || minY > maxY || minZ > maxZ) {
                logger.warning("Invalid coordinates for mine '" + key + "': min > max");
                continue;
            }
            String access = mineSection.getString("access", "open");
            String requiredNbtKey = null;
            String requiredNbtValue = null;
            if ("restricted".equals(access)) {
                ConfigurationSection nbtSection = mineSection.getConfigurationSection("required_nbt");
                if (nbtSection != null) {
                    requiredNbtKey = nbtSection.getString("key");
                    requiredNbtValue = nbtSection.getString("value");
                } else {
                    logger.warning("Missing required_nbt for restricted mine '" + key + "'");
                    continue;
                }
            }
            String code = mineSection.getString("code");
            double shopPrice = mineSection.getDouble("shop_price", 100.0);
            Mine mine = new Mine(key, world, minX, minY, minZ, maxX, maxY, maxZ, access, requiredNbtKey, requiredNbtValue, code, shopPrice);
            mines.put(key, mine);
            if (code != null) codeToMine.put(code, mine);
        }
    }

    public void addMine(Mine mine) {
        mines.put(mine.getName(), mine);
        if (mine.getCode() != null) codeToMine.put(mine.getCode(), mine);
        // Persist to config (simplified for brevity)
        ConfigurationSection minesSection = plugin.getConfig().createSection("mines." + mine.getName());
        minesSection.set("world", mine.getWorld().getName());
        minesSection.set("min.x", mine.getMinX());
        minesSection.set("min.y", mine.getMinY());
        minesSection.set("min.z", mine.getMinZ());
        minesSection.set("max.x", mine.getMaxX());
        minesSection.set("max.y", mine.getMaxY());
        minesSection.set("max.z", mine.getMaxZ());
        minesSection.set("access", mine.getAccess());
        if ("restricted".equals(mine.getAccess())) {
            minesSection.set("required_nbt.key", mine.getRequiredNbtKey());
            minesSection.set("required_nbt.value", mine.getRequiredNbtValue());
        }
        plugin.saveConfig();
    }

    public Mine getMineAt(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        for (Mine mine : mines.values()) {
            if (mine.getWorld().equals(location.getWorld()) &&
                x >= mine.getMinX() && x <= mine.getMaxX() &&
                y >= mine.getMinY() && y <= mine.getMaxY() &&
                z >= mine.getMinZ() && z <= mine.getMaxZ()) {
                return mine;
            }
        }
        return null;
    }

    public Mine getMineByName(String name) {
        return mines.get(name);
    }

    public Map<String, Mine> getMines() {
        return mines;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        UUID playerId = event.getPlayer().getUniqueId();
        Mine fromMine = playerMineCache.get(playerId);
        Mine toMine = getMineAt(to);
        if (toMine != null && (fromMine == null || !fromMine.getName().equals(toMine.getName()))) {
            if ("restricted".equals(toMine.getAccess())) {
                Player player = event.getPlayer();
                if (!KeyCardUtil.hasValidKeyCard(player, toMine)) {
                    event.setCancelled(true);
                    Utils.sendDenialMessage(player, plugin.getConfig().getString("messages.denial", "&cYou need the appropriate Access Key Card to enter this mine!"));
                    Utils.spawnDenialParticles(player, to);
                    logger.info(player.getName() + " denied access to " + toMine.getName());
                } else {
                    playerMineCache.put(playerId, toMine);
                    logger.info(player.getName() + " entered " + toMine.getName());
                }
            } else {
                playerMineCache.put(playerId, toMine);
            }
        } else if (toMine == null) {
            playerMineCache.remove(playerId);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage().trim();
        if (codeToMine.containsKey(message)) {
            Player player = event.getPlayer();
            if (!Utils.hasPermission(player, "nbtkeycard.receive")) {
                Utils.sendMessage(player, "&cYou don't have permission to use mine codes!");
                event.setCancelled(true);
                return;
            }
            Mine mine = codeToMine.get(message);
            ItemStack keyCard = KeyCardUtil.createKeyCard(mine, null, null);
            player.getInventory().addItem(keyCard);
            Utils.sendMessage(player, "&aReceived Access Key Card for " + mine.getName());
            logger.info(player.getName() + " redeemed code '" + message + "' for " + mine.getName());
            event.setCancelled(true);
        }
    }
}