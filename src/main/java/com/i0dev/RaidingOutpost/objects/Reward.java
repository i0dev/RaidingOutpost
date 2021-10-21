package com.i0dev.RaidingOutpost.objects;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Reward {

    String displayMaterial = "";
    String displayName = "";

    long capTime = 0;
    List<String> commands = new ArrayList<>();

}
