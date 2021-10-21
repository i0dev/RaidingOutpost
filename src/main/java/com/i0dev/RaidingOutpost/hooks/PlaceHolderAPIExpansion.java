package com.i0dev.RaidingOutpost.hooks;

import com.i0dev.RaidingOutpost.Heart;
import com.i0dev.RaidingOutpost.utility.Utility;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaceHolderAPIExpansion extends PlaceholderExpansion {

    Heart heart;

    public PlaceHolderAPIExpansion(Heart heart) {
        this.heart = heart;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "raidingoutpost";
    }

    @Override
    public @NotNull String getAuthor() {
        return "i01";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("faction")) {
            if (heart.isUsingMCoreFactions()) {
                com.massivecraft.factions.entity.Faction fac = MCoreFactionsHook.getFactionById(heart.storage().getRaidingOutpostOwnerID());
                return fac == null ? "None" : fac.getName();
            }
            return "None";
        }

        if (params.equalsIgnoreCase("world")) {
            return heart.cnf().getRPostWorldName();
        }

        if (params.equalsIgnoreCase("timeHeld")) {
            if (heart.isUsingMCoreFactions()) {
                com.massivecraft.factions.entity.Faction fac = MCoreFactionsHook.getFactionById(heart.storage().getRaidingOutpostOwnerID());
                if (fac == null) return "Not Captured";
            }
            return Utility.formatTime(System.currentTimeMillis() - heart.storage().getCaptureTime());
        }

        if (params.equalsIgnoreCase("timeUntilWorldReset")) {
            long time = heart.storage().getLastWorldReset() + heart.cnf().getHowOftenToResetWorldMillis() - System.currentTimeMillis();
            return Utility.formatTime(time);
        }


        return null;
    }
}
