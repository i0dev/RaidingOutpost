package com.i0dev.RaidingOutpost.hooks;

import com.i0dev.RaidingOutpost.utility.Utility;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MCoreFactionsHook {

    public static boolean isWilderness(Location location) {
        return BoardColl.get().getFactionAt(PS.valueOf(location)).isNone();
    }

    public static boolean isInFaction(Player player, String facID) {
        MPlayer mPlayer = MPlayer.get(player);
        if (mPlayer.getFaction().isNone()) return false;
        return mPlayer.getFaction().getId().equalsIgnoreCase(facID);
    }

    public static boolean isSystemFaction(Location location) {
        if (isWilderness(location)) return false;
        Faction faction = BoardColl.get().getFactionAt(PS.valueOf(location));
        String facID = faction.getId();
        return facID.equalsIgnoreCase(Factions.ID_SAFEZONE) || facID.equalsIgnoreCase(Factions.ID_WARZONE) || facID.equalsIgnoreCase("raidingoutpost");
    }



    public static boolean isSystemFaction(Faction fac) {
        if (fac == null) return false;
        if (fac.isNone()) return false;
        String facID = fac.getId();
        return facID.equalsIgnoreCase(Factions.ID_SAFEZONE) || facID.equalsIgnoreCase(Factions.ID_WARZONE) || facID.equalsIgnoreCase("raidingoutpost");
    }

    public static boolean isOwn(Location location, Player player) {
        return BoardColl.get().getFactionAt(PS.valueOf(location)).getId().equals(MPlayer.get(player).getFaction().getId());
    }

    public static Faction getFactionFromLocation(Location location) {
        return BoardColl.get().getFactionAt(PS.valueOf(location));
    }

    public static String getFactionID(Object faction) {
        return ((Faction) faction).getId();
    }

    public static Faction getFaction(String name) {
        return FactionColl.get().getByName(name);
    }

    public static Faction getFactionById(String name) {
        return FactionColl.get().get(name);
    }

    public static String getFactionName(Object faction) {
        return ((Faction) faction).getName();
    }

    public static boolean isFactionReal(String name) {
        return BoardColl.get().get(name) != null;
    }

    public static void msgFaction(Object faction, String msg) {
        Faction fac = ((Faction) faction);
        fac.msg(Utility.color(msg));
    }

    public static Faction createRPostFaction(String id, String name, String desc) {
        Faction faction = FactionColl.get().get(id);
        if (faction != null) {
            faction.setName(name);
            faction.setDescription(desc);
            return faction;
        }
        faction = FactionColl.get().create(id);
        faction.setName(name);
        faction.setDescription(desc);
        faction.setFlag(MFlag.getFlagOpen(), false);
        faction.setFlag(MFlag.getFlagPermanent(), true);
        faction.setFlag(MFlag.getFlagPeaceful(), false);
        faction.setFlag(MFlag.getFlagInfpower(), true);
        faction.setFlag(MFlag.getFlagPowerloss(), true);
        faction.setFlag(MFlag.getFlagPvp(), true);
        faction.setFlag(MFlag.getFlagFriendlyire(), false);
        faction.setFlag(MFlag.getFlagMonsters(), true);
        faction.setFlag(MFlag.getFlagAnimals(), true);
        faction.setFlag(MFlag.getFlagExplosions(), true);
        faction.setFlag(MFlag.getFlagOfflineexplosions(), true);
        faction.setFlag(MFlag.getFlagFirespread(), true);
        faction.setFlag(MFlag.getFlagEndergrief(), false);
        faction.setFlag(MFlag.getFlagZombiegrief(), false);
        return faction;
    }

    public static List<String> getFactionNames(String start) {
        List<String> ret = new ArrayList<>();
        FactionColl.get().getAll().stream().filter(faction -> {
            if (start.equalsIgnoreCase("") || start.equalsIgnoreCase(" "))
                return true;
            return faction.getName().toLowerCase().startsWith(start.toLowerCase());
        }).forEach(faction -> ret.add(ChatColor.stripColor(faction.getName())));
        ret.add("null");
        return ret;
    }

    public static void unclaimLandInWorld(String worldName) {
        Board map = BoardColl.get().getAll().stream().filter(board -> board.getId().equalsIgnoreCase(worldName)).findFirst().orElse(null);
        if (map == null || map.getMapRaw() == null) return;
        map.getFactionToChunks().forEach(((faction, ps) -> {
            if (faction != null && !isSystemFaction(faction) && !faction.getName().equalsIgnoreCase("RaidingOutpost")) {
                ps.stream().forEach(n -> BoardColl.get().setFactionAt(n, FactionColl.get().getNone()));
                return;
            }
        }));
    }

}
