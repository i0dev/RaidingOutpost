package com.i0dev.RaidingOutpost.config;

import com.i0dev.RaidingOutpost.Heart;
import com.i0dev.RaidingOutpost.objects.UnclaimedReward;
import com.i0dev.RaidingOutpost.templates.AbstractConfiguration;
import com.i0dev.RaidingOutpost.utility.Utility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StorageConfig extends AbstractConfiguration {

    String raidingOutpostOwnerID = "";
    long captureTime = 0;
    long lastWorldReset = 0;
    long lastBroadcastMessage = 0;

    long capRecordLength = 0;
    String capRecordID = "";

    boolean worldBeingReset = false;

    List<UnclaimedReward> unclaimedRewards = new ArrayList<>();

    public StorageConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

}
