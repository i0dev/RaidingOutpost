package com.i0dev.RaidingOutpost.config;

import com.i0dev.RaidingOutpost.Heart;
import com.i0dev.RaidingOutpost.objects.Reward;
import com.i0dev.RaidingOutpost.templates.AbstractConfiguration;
import com.i0dev.RaidingOutpost.utility.Utility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class GeneralConfig extends AbstractConfiguration {


    boolean raidingOutpostEnabled = true;
    boolean resetWorldOnBreach = true;
    Utility.Cuboid breachRegion = new Utility.Cuboid(0, 0, 0, 0, 0, 0, "world");
    Utility.LocationYawPitch tpLocation = new Utility.LocationYawPitch(0, 0, 0, "world", 0, 0);
    List<String> worldsToDenyEntryWhenDisabled = Collections.singletonList("WorldRaidingOutpost");
    String rPostFactionDesc = "Raid the RaidingOutpost for epic loot";
    String rPostFactionName = "RaidingOutpost";
    String rPostWorldName = "WorldRaidingOutpost";
    String rPostWorldBackupName = "WorldRaidingOutpostBackup";
    long secondsToPreventPlayersWhileWorldResetting = 60;
    long howOftenToResetWorldMillis = 86400000;
    long howOftenToBroadcastInfoMillis = 3600000;

    List<String> factionRanksAllowedToClaim = Arrays.asList(
            "LEADER",
            "COLEADER",
            "OFFICER"
    );

    List<Reward> rewardList = Arrays.asList(
            new Reward(
                    Material.PAPER.toString(),
                    "chicken spawner",
                    60000L, Arrays.asList(
                    "ss give {player} chicken 1"
            )),
            new Reward(
                    Material.CREEPER_SPAWN_EGG.toString(),
                    "chicken spawner & piggies",
                    300000L, Arrays.asList(
                    "ss give {player} creeper 4",
                    "ss give {player} pig 10"
            ))
    );

    public GeneralConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

}
