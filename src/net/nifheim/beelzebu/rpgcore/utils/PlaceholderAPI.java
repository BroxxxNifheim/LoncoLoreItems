package net.nifheim.beelzebu.rpgcore.utils;

import java.text.DecimalFormat;
import net.nifheim.yitan.itemlorestats.Main;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends EZPlaceholderHook {

    private final Main plugin;
    private final DecimalFormat df;

    public PlaceholderAPI(Main plugin) {
        super(plugin, "rpgcore");
        this.df = new DecimalFormat("#.#");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String str) {
        if (p == null) {
            return "Player is needed!";
        }
        if (str.equals("mana")) {
            return String.valueOf(df.format(plugin.getPlayerStats(p).manaCurrent));
        }
        if (str.equals("mana_max")) {
            return String.valueOf(df.format(plugin.getPlayerStats(p).manaMax));
        }
        if (str.equals("defense")) {
            return String.valueOf(df.format(plugin.getPlayerStats(p).percentArmor * 100) + "%");

        }
        if (str.equals("damage")) {

            String damageMin = df.format(plugin.getPlayerStats(p).minDamage);
            String damageMax = df.format(plugin.getPlayerStats(p).maxDamage);
            return String.valueOf(damageMin + "-" + damageMax);
        }
        if (str.equals("magicpower")) {
            return String.valueOf(df.format(plugin.getPlayerStats(p).magicPower));
        }
        if (str.equals("dodge")) {
            return String.valueOf(df.format(plugin.getPlayerStats(p).dodge * 100) + "%");
        }
        if (str.equals("movementspeed")) {
            return String.valueOf(df.format(plugin.getPlayerStats(p).movementSpeed * 100) + "%");
        } else {
            return "Invalid Placeholder";
        }
    }
}
