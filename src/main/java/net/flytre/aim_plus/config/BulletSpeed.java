package net.flytre.aim_plus.config;

import net.flytre.flytre_lib.api.config.annotation.MemberLocalizationFunction;

public enum BulletSpeed {

    SNOWBALL(1.5, "snowball"),
    ARROW(3, "arrow"),
    FAST(5, "fast"),
    EXTREMELY_FAST(10, "extremely_fast"),
    BULLET(20, "bullet"),
    INSTANT(10000000, "instant");

    private final double speed;
    private final String name;


    //units: m/t
    BulletSpeed(double velocity, String name) {
        this.speed = velocity;
        this.name = name;
    }

    public double getSpeed() {
        return speed;
    }

    @MemberLocalizationFunction
    public String getKey() {
        return "aim_plus.bullet_speed." + name;
    }

}
