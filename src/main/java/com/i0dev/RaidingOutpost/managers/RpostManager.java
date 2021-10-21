package com.i0dev.RaidingOutpost.managers;

import com.i0dev.RaidingOutpost.Heart;
import com.i0dev.RaidingOutpost.hooks.MCoreFactionsHook;
import com.i0dev.RaidingOutpost.objects.UnclaimedReward;
import com.i0dev.RaidingOutpost.templates.AbstractManager;
import com.i0dev.RaidingOutpost.utility.ConfigUtil;
import com.i0dev.RaidingOutpost.utility.Utility;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
public class RpostManager extends AbstractManager {
    public RpostManager(Heart heart) {
        super(heart);
    }

    BukkitTask taskAssureRealOwner;
    BukkitTask taskResetWorld;
    BukkitTask taskBroadcast;
    BukkitTask taskGiveRewards;

    @Override
    public void initialize() {
        taskAssureRealOwner = Bukkit.getScheduler().runTaskTimerAsynchronously(heart, taskAssureOwnerIsReal, 20L * 10L, 20L * 30L);
        taskResetWorld = Bukkit.getScheduler().runTaskTimerAsynchronously(heart, taskResetRPostWorld, 20L * 60L, 20L * 120L);
        taskBroadcast = Bukkit.getScheduler().runTaskTimerAsynchronously(heart, taskBroadcastInfo, 20L * 60L, 20L * 30L);
        taskGiveRewards = Bukkit.getScheduler().runTaskTimerAsynchronously(heart, taskGiveRPostRewards, 20L, 20L);
        if (heart.isUsingMCoreFactions()) {
            MCoreFactionsHook.createRPostFaction("raidingoutpost", heart.cnf().getRPostFactionName(), heart.cnf().getRPostFactionDesc());
        }
    }

    @Override
    public void deinitialize() {
        if (taskAssureRealOwner != null) taskAssureRealOwner.cancel();
        if (taskResetWorld != null) taskResetWorld.cancel();
        if (taskBroadcast != null) taskBroadcast.cancel();
        if (taskGiveRewards != null) taskGiveRewards.cancel();
    }

    public void setNewOwner(Object fac) {
        if (fac == null) {
            heart.storage().setRaidingOutpostOwnerID("");
            heart.storage().setCaptureTime(0);
            ConfigUtil.save(heart.storage());
            heart.msger().msgAll(heart.msg().getRPostNewNullOwner());
            return;
        }

        if (heart.isUsingMCoreFactions()) {
            heart.storage().setRaidingOutpostOwnerID(MCoreFactionsHook.getFactionID(fac));
            heart.storage().setCaptureTime(System.currentTimeMillis());
            ConfigUtil.save(heart.storage());
            heart.msger().msgAll(heart.msg().getRPostNewOwner(),
                    new MessageManager.Pair<>("{faction}", MCoreFactionsHook.getFactionName(fac)));
        }
    }


    public Runnable taskAssureOwnerIsReal = () -> {
        if (heart.isUsingMCoreFactions()) {
            if ("".equalsIgnoreCase(heart.storage().getRaidingOutpostOwnerID()) && !MCoreFactionsHook.isFactionReal(heart.storage().getRaidingOutpostOwnerID())) {
                setNewOwner(null);
                heart.storage().setRaidingOutpostOwnerID("");
                heart.storage().setCaptureTime(0);
            }
            if (!MCoreFactionsHook.isFactionReal(heart.storage().getCapRecordID())) {
                heart.storage().setCapRecordID("");
                heart.storage().setCapRecordLength(0);
            }
            ConfigUtil.save(heart.storage());
        }
    };

    public Runnable taskResetRPostWorld = () -> {
        if (heart.storage().getLastWorldReset() == 0) {
            heart.storage().setLastWorldReset(System.currentTimeMillis());
            ConfigUtil.save(heart.storage());
            return;
        }
        if (System.currentTimeMillis() >= heart.storage().getLastWorldReset() + heart.cnf().getHowOftenToResetWorldMillis()) {
            resetWorld(true);
        }
    };

    public Runnable taskBroadcastInfo = () -> {
        if (heart.storage().getLastBroadcastMessage() == 0) {
            heart.storage().setLastBroadcastMessage(System.currentTimeMillis());
            ConfigUtil.save(heart.storage());
        } else if (System.currentTimeMillis() >= heart.storage().getLastBroadcastMessage() + heart.cnf().getHowOftenToBroadcastInfoMillis()) {
            heart.msger().msgAll(heart.msg().getRPostBroadcastMessage());
            heart.storage().setLastBroadcastMessage(System.currentTimeMillis());
            ConfigUtil.save(heart.storage());
        }
    };

    long lastRewardSend = 0;
    public Runnable taskGiveRPostRewards = () -> {
        long timeHeld = System.currentTimeMillis() - heart.storage().getCaptureTime();
        String ownerID = heart.storage().getRaidingOutpostOwnerID();
        if (heart.isUsingMCoreFactions())
            if (MCoreFactionsHook.getFactionById(ownerID) == null) return;
        if (System.currentTimeMillis() < lastRewardSend + 3000L) return;
        AtomicInteger rewardsCounter = new AtomicInteger(0);
        heart.cnf().getRewardList().forEach(reward -> {
            long lower = reward.getCapTime() - 1000;
            long higher = reward.getCapTime() + 1000;
            if (timeHeld > lower && timeHeld < higher) {
                heart.storage().getUnclaimedRewards().add(new UnclaimedReward(reward.getDisplayMaterial(), reward.getDisplayName(), ownerID, reward.getCommands()));
                if (heart.isUsingMCoreFactions())
                    MCoreFactionsHook.msgFaction(MCoreFactionsHook.getFactionById(ownerID), heart.msg().getNewReward());
                lastRewardSend = System.currentTimeMillis();
                rewardsCounter.getAndIncrement();
            }
        });
        if (rewardsCounter.get() > 0)
            ConfigUtil.save(heart.storage());
    };


    public void resetWorld(boolean resetOwner) {
        heart.storage().setWorldBeingReset(true);
        ConfigUtil.save(heart.storage());
        if (resetOwner)
            setNewOwner(null);
        heart.msger().msgAll(heart.msg().getRPostWorldResetting());
        Bukkit.getScheduler().runTaskLater(heart, () -> {
            unloadWorld(Bukkit.getWorld(heart.cnf().getRPostWorldName()));
            // unload all sand-bots
            if (heart.isUsingMCoreFactions()) {
                MCoreFactionsHook.unclaimLandInWorld(heart.cnf().getRPostWorldName());
            }
            cloneWorld();
            heart.storage().setLastWorldReset(System.currentTimeMillis());
            heart.storage().setWorldBeingReset(false);
            ConfigUtil.save(heart.storage());
            heart.msger().msgAll(heart.msg().getRaidingOutpostWorldReset());

        }, 20L * 10L);
    }

    public void unloadWorld(World world) {
        if (world == null) return;
        for (Player player : world.getPlayers()) {
            spawnPlayer(player);
            heart.msger().msg(player, heart.msg().getSentToSpawnWorldResetting());
        }
        Bukkit.unloadWorld(world, false);
    }

    public void spawnPlayer(Player player) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
    }

    public void cloneWorld() {
        String worldName = heart.cnf().getRPostWorldName();
        String backupWorldName = heart.cnf().getRPostWorldBackupName();
        File worldFile = new File(Bukkit.getWorldContainer().toPath() + "/" + worldName);
        File backupFile = new File(Bukkit.getWorldContainer().toPath() + "/" + backupWorldName);
        deleteWorldFiles(worldFile);
        copyWorld(backupFile, worldFile);

        //MultiVerse

        if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
            com.onarandombox.MultiverseCore.MultiverseCore mv = (com.onarandombox.MultiverseCore.MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
            if (mv == null) return;
            mv.getMVWorldManager().loadWorld(worldName);
        }
    }

    ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));

    public void deleteWorldFiles(File file) {
        for (String s : file.list()) {
            if (ignore.contains(s)) continue;
            File newFile = new File(file, s);
            newFile.delete();
        }
    }

    public Location getTpLocation() {
        Utility.LocationYawPitch l = heart.cnf().getTpLocation();
        return new Location(Bukkit.getWorld(l.getWorldName()), l.getX(), l.getY(), l.getY(), l.getYaw(), l.getPitch());
    }

    @SneakyThrows
    public void copyWorld(File source, File target) {
        if (!ignore.contains(source.getName())) {
            if (source.isDirectory()) {
                if (!target.exists())
                    target.mkdirs();
                String files[] = source.list();
                for (String file : files) {
                    File srcFile = new File(source, file);
                    File destFile = new File(target, file);
                    copyWorld(srcFile, destFile);
                }
            } else {
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0)
                    out.write(buffer, 0, length);
                in.close();
                out.close();
            }
        }
    }
}
