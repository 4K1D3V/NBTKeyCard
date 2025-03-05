package pro.akii.ks.core.nbtKeyCard.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pro.akii.ks.core.nbtKeyCard.gui.NBTGUI;
import pro.akii.ks.core.nbtKeyCard.managers.Mine;
import pro.akii.ks.core.nbtKeyCard.managers.MineManager;
import pro.akii.ks.core.nbtKeyCard.utils.KeyCardUtil;
import pro.akii.ks.core.nbtKeyCard.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NBTCommand implements CommandExecutor, TabCompleter {
    private final MineManager mineManager;
    private final Economy economy;

    public NBTCommand(MineManager mineManager, Economy economy) {
        this.mineManager = mineManager;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Utils.sendMessage(sender, "&cUsage: /nbtkeycard <give|gui|shop|show|mine>");
            return true;
        }

        if (args[0].equalsIgnoreCase("gui") && sender instanceof Player) {
            Player player = (Player) sender;
            if (!Utils.hasPermission(player, "nbtkeycard.gui")) {
                Utils.sendMessage(player, "&cYou don't have permission to use this command!");
                return true;
            }
            new NBTGUI(mineManager, economy).openGUI(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("shop") && sender instanceof Player) {
            Player player = (Player) sender;
            if (!Utils.hasPermission(player, "nbtkeycard.shop")) {
                Utils.sendMessage(player, "&cYou don't have permission to use this command!");
                return true;
            }
            new NBTGUI(mineManager, economy).openShop(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("give") && args.length == 3) {
            if (!Utils.hasPermission(sender, "nbtkeycard.give")) {
                Utils.sendMessage(sender, "&cYou don't have permission to use this command!");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                Utils.sendMessage(sender, "&cPlayer '" + args[1] + "' not found!");
                return true;
            }
            Mine mine = mineManager.getMineByName(args[2]);
            if (mine == null || !"restricted".equals(mine.getAccess())) {
                Utils.sendMessage(sender, "&cInvalid or non-restricted mine!");
                return true;
            }
            ItemStack keyCard = KeyCardUtil.createKeyCard(mine, null, null);
            target.getInventory().addItem(keyCard);
            Utils.sendMessage(sender, "&aGave Access Key Card for " + mine.getName() + " to " + target.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("show") && args.length == 2 && sender instanceof Player) {
            Player player = (Player) sender;
            if (!Utils.hasPermission(player, "nbtkeycard.admin")) {
                Utils.sendMessage(player, "&cYou don't have permission to use this command!");
                return true;
            }
            Mine mine = mineManager.getMineByName(args[1]);
            if (mine == null) {
                Utils.sendMessage(player, "&cMine '" + args[1] + "' not found!");
                return true;
            }
            Utils.showMineBoundaries(player, mine);
            Utils.sendMessage(player, "&aShowing boundaries for " + mine.getName() + " for 10 seconds.");
            return true;
        }

        if (args[0].equalsIgnoreCase("mine") && args.length >= 3 && sender instanceof Player) {
            Player player = (Player) sender;
            if (!Utils.hasPermission(player, "nbtkeycard.admin")) {
                Utils.sendMessage(player, "&cYou don't have permission to use this command!");
                return true;
            }
            if (args[1].equalsIgnoreCase("create") && args.length == 11) {
                String name = args[2];
                World world = Bukkit.getWorld(args[3]);
                if (world == null) {
                    Utils.sendMessage(player, "&cWorld '" + args[3] + "' not found!");
                    return true;
                }
                try {
                    int minX = Integer.parseInt(args[4]);
                    int minY = Integer.parseInt(args[5]);
                    int minZ = Integer.parseInt(args[6]);
                    int maxX = Integer.parseInt(args[7]);
                    int maxY = Integer.parseInt(args[8]);
                    int maxZ = Integer.parseInt(args[9]);
                    String access = args[10];
                    Mine mine = new Mine(name, world, minX, minY, minZ, maxX, maxY, maxZ, access, "access", name + "_access", null, 100.0);
                    mineManager.addMine(mine);
                    Utils.sendMessage(player, "&aCreated mine '" + name + "'!");
                    return true;
                } catch (NumberFormatException e) {
                    Utils.sendMessage(player, "&cInvalid coordinates!");
                    return true;
                }
            }
        }

        Utils.sendMessage(sender, "&cUsage: /nbtkeycard <give|gui|shop|show|mine>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "gui", "shop", "show", "mine").stream()
                    .filter(cmd -> sender.hasPermission("nbtkeycard." + cmd) || (cmd.equals("mine") && sender.hasPermission("nbtkeycard.admin")))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            if ("give".equalsIgnoreCase(args[0])) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            } else if ("show".equalsIgnoreCase(args[0]) || "mine".equalsIgnoreCase(args[0])) {
                return mineManager.getMines().keySet().stream().collect(Collectors.toList());
            }
        } else if (args.length == 3 && "give".equalsIgnoreCase(args[0])) {
            return mineManager.getMines().keySet().stream().collect(Collectors.toList());
        }
        return null;
    }
}