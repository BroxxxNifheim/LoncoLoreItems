package net.nifheim.yitan.itemlorestats.listeners;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.nifheim.beelzebu.rpgcore.utils.StatsSaveAPI;
import net.nifheim.yitan.itemlorestats.Main;
import net.nifheim.yitan.itemlorestats.PlayerStats;
import org.bukkit.Bukkit;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final Main plugin;
    private static PlayerStats ps;

    public PlayerJoinListener(Main main) {
        plugin = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player playerFinal = event.getPlayer();
        ps = new PlayerStats(playerFinal);

        // Async tasks
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Main.plugin.playersStats.put(playerFinal.getUniqueId(), ps);
            // ----------------- NEW DATA
            plugin.getDataManager(playerFinal).loadData();

            // ----------------- OLD DATA
            if (!new File(Main.plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + playerFinal.getName() + ".yml").exists()) {
                try {

                    Main.plugin.PlayerDataConfig = new YamlConfiguration();

                    Main.plugin.updateHealth(playerFinal);

                    Main.plugin.PlayerDataConfig.set("extra.logoutHealth", 20.0D);
                    Main.plugin.PlayerDataConfig.set("extra.maxHealth", 20.0D);
                    Main.plugin.PlayerDataConfig.set("extra.hunger", 20);
                    Main.plugin.PlayerDataConfig.set("extra.xp", 0.0F);
                    Main.plugin.PlayerDataConfig.set("extra.level", 0);
                    Main.plugin.PlayerDataConfig.save(Main.plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + playerFinal.getName() + ".yml");
                } catch (IOException e) {
                    System.out.println("*********** Failed to save player data for " + playerFinal.getName() + " when logging in! ***********");
                }
            } else if (new File(Main.plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + playerFinal.getName() + ".yml").exists()) {
                try {
                    Main.plugin.PlayerDataConfig = new YamlConfiguration();
                    Main.plugin.PlayerDataConfig.load(new File(Main.plugin.getDataFolder() + File.separator + "PlayerData" + File.separator + playerFinal.getName() + ".yml"));
                    ps.manaCurrent = Main.plugin.PlayerDataConfig.getDouble("extra.mana");
                    playerFinal.setMaxHealth(Main.plugin.PlayerDataConfig.getDouble("extra.maxHealth"));
                    playerFinal.setHealth(Main.plugin.PlayerDataConfig.getDouble("extra.logoutHealth"));
                    playerFinal.setFoodLevel(Main.plugin.PlayerDataConfig.getInt("extra.hunger"));

                    if (Main.plugin.PlayerDataConfig.get("extra.combatLogVisible") == null) {
                        Main.plugin.combatLogVisible.put(playerFinal.getName(), true);
                    } else {
                        Main.plugin.combatLogVisible.put(playerFinal.getName(), Main.plugin.PlayerDataConfig.getBoolean("extra.combatLogVisible"));
                    }

                    Main.plugin.updateHealth(playerFinal);
                } catch (IOException | InvalidConfigurationException e) {
                    System.out.println("*********** Failed to load player data for " + playerFinal.getName() + " when logging in! ***********");
                }
            }

            Main.plugin.updateHealth(playerFinal);
            Main.plugin.updatePlayerSpeed(playerFinal);
            try {
                StatsSaveAPI.setAllStats(event.getPlayer());
            } catch (SQLException ex) {
                if (ex.getSQLState().equals("closed")) {
                    Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Seems that the database connection is closed.");
                } else {
                    Logger.getLogger(PlayerJoinListener.class.getName()).log(Level.WARNING, "Something was wrong executing this query, the error code is: " + ex.getErrorCode(), ex.getCause());
                }
            }
        });

        Bukkit.getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
            ps.UpdateAll();
        }, 5L);
    }
}
