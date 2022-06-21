package net.flytre.aim_plus.config;

import net.flytre.flytre_lib.api.config.annotation.MemberLocalizationFunction;

public enum FirePattern {

    AUTO("auto"),
    BURST("burst"),
    RPS_0_8("rps_0.8"),
    RPS_1("rps_1"),
    RPS_1_5("rps_1.5");

    private final String name;

    FirePattern(String name) {
        this.name = name;
    }

    @MemberLocalizationFunction
    public String getKey() {
        return "aim_plus.fire_pattern." + name;
    }

}
