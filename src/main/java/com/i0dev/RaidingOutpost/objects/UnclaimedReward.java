package com.i0dev.RaidingOutpost.objects;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UnclaimedReward {

    String displayMaterial;
    String displayName;

    String factionID;
    List<String> commands;

}
