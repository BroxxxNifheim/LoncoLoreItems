package net.nifheim.yitan.lorestats.utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.nifheim.yitan.lorestats.Main;
import org.bukkit.entity.Entity;

public class Util_Citizens {

    Main main;

    public Util_Citizens(Main instance) {
        this.main = instance;
    }

    public boolean checkVulnerability(Entity entity) {
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC npc = registry.getNPC(entity);

        if (entity != null) {
            if (npc.data().has("protected")) {
                return true;
            }
            return false;
        }

        return false;
    }
}
