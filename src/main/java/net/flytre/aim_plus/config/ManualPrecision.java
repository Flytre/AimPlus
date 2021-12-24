package net.flytre.aim_plus.config;

public enum ManualPrecision {


    GENERAL_DIRECTION("general_direction", 30),
    SOME("some", 12),
    CLOSE("close", 8),
    PRECISE("precise", 5);

    private final String name;
    private final double val;

    ManualPrecision(String name, double val) {
        this.name = name;
        this.val = val;
    }


    public double getVal() {
        return val;
    }

    public String getKey() {
        return "aim_plus.manual_precision." + name;
    }

}
