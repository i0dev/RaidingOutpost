package com.i0dev.RaidingOutpost.templates;

import com.i0dev.RaidingOutpost.Heart;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractConfiguration {

    public transient Heart heart = null;
    public transient String path = "";

}
