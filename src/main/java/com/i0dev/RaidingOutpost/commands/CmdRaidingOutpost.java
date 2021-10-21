package com.i0dev.RaidingOutpost.commands;

import com.i0dev.RaidingOutpost.Heart;
import com.i0dev.RaidingOutpost.hooks.MCoreFactionsHook;
import com.i0dev.RaidingOutpost.managers.MessageManager;
import com.i0dev.RaidingOutpost.managers.RpostManager;
import com.i0dev.RaidingOutpost.objects.UnclaimedReward;
import com.i0dev.RaidingOutpost.templates.AbstractCommand;
import com.i0dev.RaidingOutpost.utility.ConfigUtil;
import com.i0dev.RaidingOutpost.utility.Utility;
import com.massivecraft.factions.entity.Rank;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Salmon;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdRaidingOutpost extends AbstractCommand {

    public CmdRaidingOutpost(Heart heart, String command) {
        super(heart, command);
    }

    @SneakyThrows
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            heart.msger().msg(sender, heart.msg().getReloadUsage());
            heart.msger().msg(sender, heart.msg().getToggleUsage());
            heart.msger().msg(sender, heart.msg().getSetOwnerUsage());
            heart.msger().msg(sender, heart.msg().getSetBreachRegionUsage());
            heart.msger().msg(sender, heart.msg().getResetWorldUsage());
            heart.msger().msg(sender, heart.msg().getTpUsage());
            heart.msger().msg(sender, heart.msg().getSetTpLocUsage());
            heart.msger().msg(sender, heart.msg().getInfoUsage());
            return;
        }
        if (args[0].equalsIgnoreCase("setBreachRegion")) {
            if (!sender.hasPermission("raidingoutpost.setbaseregion.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            WorldEditPlugin we = ((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"));
            if (we == null) {
                heart.msger().msg(sender, heart.msg().getPluginWorldEditDisabled());
                return;
            }
            Player player = ((Player) sender);
            LocalSession session = we.getSession(player);
            Region selection = session.getSelection(session.getSelectionWorld());
            if (selection == null) {
                heart.msger().msg(sender, heart.msg().getNeedSelection());
                return;
            }
            BlockVector3 p1 = selection.getMinimumPoint();
            BlockVector3 p2 = selection.getMaximumPoint();
            Utility.Cuboid cuboid = new Utility.Cuboid(p1.getX(), p1.getY(), p1.getZ(), p2.getX(), p2.getY(), p2.getZ(), selection.getWorld().getName());
            heart.cnf().setBreachRegion(cuboid);
            ConfigUtil.save(heart.cnf());
            heart.msger().msg(sender, heart.msg().getSetBreachRegion());
            return;
        }
        if (args[0].equalsIgnoreCase("setOwner")) {
            if (!sender.hasPermission("raidingoutpost.setowner.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            if (args.length < 2) {
                heart.msger().msg(sender, heart.msg().getSetOwnerUsage());
                return;
            }
            String faction = "null";
            if (heart.isUsingMCoreFactions()) {
                Object fac = null;
                if (MCoreFactionsHook.isFactionReal(args[1])) {
                    fac = MCoreFactionsHook.getFaction(args[1]);
                    if (fac != null)
                        faction = MCoreFactionsHook.getFactionName(fac);
                }
                heart.getManager(RpostManager.class).setNewOwner(fac);
                ConfigUtil.save(heart.storage());
            }
            heart.msger().msg(sender, heart.msg().getSetOwner(), new MessageManager.Pair<>("{faction}", faction));
            return;
        }
        if (args[0].equalsIgnoreCase("toggle")) {
            if (!sender.hasPermission("raidingoutpost.toggle.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            if (heart.cnf().isRaidingOutpostEnabled())
                getHeart().cnf().setRaidingOutpostEnabled(false);
            else if (!heart.cnf().isRaidingOutpostEnabled())
                getHeart().cnf().setRaidingOutpostEnabled(true);
            ConfigUtil.save(heart.cnf());
            heart.msger().msg(sender, heart.msg().getToggledRPost(), new MessageManager.Pair<>("{status}", heart.cnf().isRaidingOutpostEnabled() ? "&a&lEnabled" : "&c&lDisabled"));
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("raidingoutpost.reload.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            getHeart().reload();
            heart.msger().msg(sender, heart.msg().getReloadedConfig());
            return;
        }
        if (args[0].equalsIgnoreCase("resetWorld")) {
            if (!sender.hasPermission("raidingoutpost.resetWorld.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            boolean newOwner = false;
            if (args.length > 1) {
                newOwner = Boolean.parseBoolean(args[1]);
            }
            heart.getManager(RpostManager.class).resetWorld(newOwner);
            heart.msger().msg(sender, heart.msg().getYouResetRPostWorld());
            return;
        }
        if (args[0].equalsIgnoreCase("setTpLoc")) {
            if (!sender.hasPermission("raidingoutpost.setTpLoc.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            if (!(sender instanceof Player)) {
                heart.msger().msg(sender, heart.msg().getCantConsole());
                return;
            }
            Location l = ((Player) sender).getLocation();
            heart.cnf().setTpLocation(new Utility.LocationYawPitch(l.getX(), l.getY(), l.getZ(), l.getWorld().getName(), l.getYaw(), l.getPitch()));
            ConfigUtil.save(heart.cnf());
            heart.msger().msg(sender, heart.msg().getYouSetTpLoc());
            return;
        }
        if (args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission("raidingoutpost.info.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            heart.msger().msg(sender, heart.msg().getRPostBroadcastMessage());
            return;
        }
        if (args[0].equalsIgnoreCase("tp")) {
            if (!sender.hasPermission("raidingoutpost.tp.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            if (!(sender instanceof Player)) {
                heart.msger().msg(sender, heart.msg().getCantConsole());
                return;
            }

            if (heart.isUsingMCoreFactions()) {
                if (!MCoreFactionsHook.isInFaction(((Player) sender), heart.storage().getRaidingOutpostOwnerID())) {
                    heart.msger().msg(sender, heart.msg().getNeedCaptureOutpostToTeleport());
                    return;
                }
            }
            ((Player) sender).teleport(heart.getManager(RpostManager.class).getTpLocation());
            heart.msger().msg(sender, heart.msg().getYouTeleportedToRPost());
            return;
        }
        if (args[0].equalsIgnoreCase("claim")) {
            if (!sender.hasPermission("raidingoutpost.claim.cmd")) {
                heart.msger().msg(sender, heart.msg().getNoPermission());
                return;
            }
            if (!(sender instanceof Player)) {
                heart.msger().msg(sender, heart.msg().getCantConsole());
                return;
            }
            com.massivecraft.factions.entity.MPlayer mPlayer = com.massivecraft.factions.entity.MPlayer.get(sender);
            if (!heart.cnf().getFactionRanksAllowedToClaim().contains(mPlayer.getRank().getName().toUpperCase())) {
                heart.msger().msg(sender, heart.msg().getNotHighEnoughRank());
                return;
            }

            if (mPlayer.getFaction().isNone()) {
                heart.msger().msg(sender, heart.msg().getNoRewards());
                return;
            }
            List<UnclaimedReward> rewards = heart.storage().getUnclaimedRewards().stream().filter(unclaimedReward -> unclaimedReward.getFactionID().equalsIgnoreCase(mPlayer.getFaction().getId())).collect(Collectors.toList());
            if (rewards.size() == 0) {
                heart.msger().msg(sender, heart.msg().getNoRewards());
                return;
            }
            rewards.forEach(unclaimedReward -> unclaimedReward.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", sender.getName()))));
            rewards.forEach(heart.storage().getUnclaimedRewards()::remove);
            ConfigUtil.save(heart.storage());
            heart.msger().msg(sender, heart.msg().getYouClaimedRewards());

            return;
        }
    }

    List<String> blank = new ArrayList<>();

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return tabCompleteHelper(args[0], Arrays.asList("reload", "help", "info", "toggle", "setOwner", "setBreachRegion", "resetWorld", "tp", "setTpLoc", "claim"));
        if (args.length == 2 && args[0].equalsIgnoreCase("setOwner"))
            if (heart.isUsingMCoreFactions()) return MCoreFactionsHook.getFactionNames(args[1]);
        if (args.length == 2 && args[0].equalsIgnoreCase("resetWorld"))
            if (heart.isUsingMCoreFactions()) return tabCompleteHelper(args[1], Arrays.asList("true", "false"));
        return blank;
    }
}
