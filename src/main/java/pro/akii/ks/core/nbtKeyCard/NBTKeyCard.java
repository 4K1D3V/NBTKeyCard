package pro.akii.ks.core.nbtKeyCard;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pro.akii.ks.core.nbtKeyCard.commands.NBTCommand;
import pro.akii.ks.core.nbtKeyCard.gui.NBTGUI;
import pro.akii.ks.core.nbtKeyCard.managers.MineManager;

public class NBTKeyCard extends JavaPlugin {
    private static NBTKeyCard instance;
    private MineManager mineManager;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null || (economy = rsp.getProvider()) == null) {
            getLogger().severe("Vault or an economy plugin is required but not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        mineManager = new MineManager(this);
        NBTGUI gui = new NBTGUI(mineManager, economy);
        getServer().getPluginManager().registerEvents(mineManager, this);
        getServer().getPluginManager().registerEvents(gui, this);
        getCommand("nbtkeycard").setExecutor(new NBTCommand(mineManager, economy));
        
        getLogger().info("NBTKeyCard plugin enabled!");
        getLogger().info("Made By Kit!");
    }

    public static NBTKeyCard getInstance() {
        return instance;
    }

    public MineManager getMineManager() {
        return mineManager;
    }

    public Economy getEconomy() {
        return economy;
    }
}