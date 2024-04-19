package net.myitian.mineshell.util;

public enum CSIMode {
    Unknown('\0'),
    CNL('E'),
    SGR('m'),
    ;
    public final char ch;

    CSIMode(char ch) {
        this.ch = ch;
    }

    public static CSIMode createCSIMode(char ch) {
        for (var v : CSIMode.values()) {
            if (v.ch == ch)
                return v;
        }
        return Unknown;
    }
}
