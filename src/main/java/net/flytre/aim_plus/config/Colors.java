package net.flytre.aim_plus.config;

import java.awt.*;


public enum Colors {
    BLACK("black"),
    WHITE("white"),
    GRAY("gray"),
    RED("red"),
    ORANGE("orange"),
    YELLOW("yellow"),
    GREEN("green"),
    BLUE("blue"),
    PURPLE("purple"),
    PINK("pink");

    private final String name;

    Colors(String name) {
        this.name = name;
    }

    public static Colors best(Color color) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float hue = hsb[0] * 360, saturation = hsb[1], brightness = hsb[2];
        System.out.println(hue + " " + saturation + " " + brightness);

        if (saturation < 0.1 && brightness > 0.9)
            return WHITE;

        if (brightness < 0.1)
            return BLACK;

        if (saturation < 0.1)
            return GRAY;

        if (hue > 336 || hue < 15)
            return RED;

        if (hue < 37)
            return ORANGE;

        if (hue < 64)
            return YELLOW;

        if (hue < 155)
            return GREEN;

        if (hue < 260)
            return BLUE;

        if (hue < 290)
            return PURPLE;

        return PINK;

    }

    public String getKey() {
        return "aim_plus.color." + name;
    }
}
