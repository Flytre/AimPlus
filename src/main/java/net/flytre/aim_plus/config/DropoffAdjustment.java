package net.flytre.aim_plus.config;

import net.flytre.flytre_lib.api.config.annotation.MemberLocalizationFunction;

import java.util.function.Function;

public enum DropoffAdjustment {

    SNOWBALL("snowball", dist -> {
        double val = dist / 1.5;
        return 1.5 * 0.03 * (val) * (val + 1) / 2 * Math.pow(0.99, val);
        //velocity * gravity * half area
    }),
    ARROW("arrow", dist -> {
        double val = dist / 3.0;
        return 3.0 * 0.0225 * (val) * (val + 1) / 2 * Math.pow(0.99, val);
        //velocity * gravity * half area
    }),
    TINY("tiny", dist -> {
        double val = dist / 6.0;
        return 6.0 * 0.0225 * (val) * (val + 1) / 2 * Math.pow(0.99, val);
        //twice as fast as an arrow
    }),
    NONE("none", dist -> 0d);

    private final String name;
    private final Function<Double, Double> yIncrease;

    DropoffAdjustment(String name, Function<Double, Double> yIncrease) {
        this.name = name;
        this.yIncrease = yIncrease;
    }

    @MemberLocalizationFunction
    public String getKey() {
        return "aim_plus.dropoff_adjustment." + name;
    }

    public double getYIncrease(double dist) {
        return yIncrease.apply(dist);
    }
}
