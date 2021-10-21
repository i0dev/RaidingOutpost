package com.i0dev.RaidingOutpost.config;

import com.i0dev.RaidingOutpost.Heart;
import com.i0dev.RaidingOutpost.templates.AbstractConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MessageConfig extends AbstractConfiguration {

    String reloadUsage = "&c* &7/RaidingOutpost reload";
    String toggleUsage = "&c* &7/RaidingOutpost toggle";
    String setOwnerUsage = "&c* &7/RaidingOutpost setOwner <faction>";
    String setBreachRegionUsage = "&c* &7/RaidingOutpost setBreachRegion ";
    String resetWorldUsage = "&c* &7/RaidingOutpost resetWorld <resetOwner>";
    String tpUsage = "&c* &7/RaidingOutpost tp";
    String setTpLocUsage = "&c* &7/RaidingOutpost setTpLoc";
    String infoUsage = "&c* &7/RaidingOutpost info";

    //Rpost Related
    String alreadyOwnRPost = "&7Your faction already has the Raiding Outpost claimed.";
    String rPostDisabled = "&cRaiding Outpost is currently disabled.";
    String toggledRPost = "&7You have toggled RaidingOutpost to: {status}";
    String setOwner = "&7You have force set the owner of the Raiding Outpost to: &c{faction}";
    String cantEnterRPostWorldDisabled = "&cYou cannot enter the raiding outpost world because it is currently disabled.";
    String cantEnterRPostWorldResetting = "&cYou cannot enter the raiding outpost world because the world is currently resetting.";
    String setBreachRegion = "&7You have set the breach region successfully.";
    String sentToSpawnWorldResetting = "&7You have been sent to spawn due to the Raiding Outpost world resetting";
    String raidingOutpostWorldReset = "&7The Raiding Outpost world has been reset.";
    String youResetRPostWorld = "&7You have force reset the Raiding Outpost World";
    String needCaptureOutpostToTeleport = "&7You need to have the Raiding Outpost captured in order to teleport to the roof.";
    String youTeleportedToRPost = "&7You have been teleported to the roof of the Raiding Outpost.";
    String youSetTpLoc = "&7You have updated the Raiding Outpost teleport location.";
    String newReward = "&7Your faction has a new reward from the raiding outpost. Claim it with /RaidingOutpost claim";
    String noRewards = "&7Your faction doesn't have any current rewards to claim.";
    String youClaimedRewards = "&aYou claimed all your factions rewards.";
    String notHighEnoughRank = "&7You are not a high enough rank within your faction to claim rewards.";

    //Rpost announce
    List<String> rPostNewOwner = Arrays.asList(
            " ",
            "{prefix} &7The raiding outpost has been captured by: &c&l{faction}",
            " "
    );

    List<String> rPostNewNullOwner = Arrays.asList(
            " ",
            "{prefix} &7The raiding outpost no longer has an owner.",
            " "
    );

    List<String> rPostWorldResetting = Arrays.asList(
            " ",
            "{prefix} &7The raiding outpost world will reset in 10 seconds.",
            " "
    );

    List<String> rPostBroadcastMessage = Arrays.asList(
            "&8&m----------------&4&l Raiding Outpost &8&m----------------",
            " ",
            "&7                        &lCurrently Captured by:",
            "&4                                      &l%raidingoutpost_faction%",
            " ",
            "&7                             &lTime till reset:",
            "&c                               &l%raidingoutpost_timeUntilWorldReset%\n ",
            "&8&m---------------------------------------------"
    );


    // Other
    String reloadedConfig = "&7You have&a reloaded&7 the configuration.";
    String noPermission = "&cYou don not have permission to run that command.";
    String cantFindPlayer = "&cThe player: &f{player}&c cannot be found!";
    String invalidNumber = "&cThe number &f{num} &cis invalid! Try again.";
    String pluginWorldEditDisabled = "&7WorldEdit plugin not found. Please install world edit and try again.";
    String needSelection = "&7You don't have a region selected. Use world edit to make one and redo the command.";
    String cantConsole = "&7You cannot run this command as console.";
    //variable
    String prefix = "&c&lRaidingOutpost &8»";

    public MessageConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }
}
