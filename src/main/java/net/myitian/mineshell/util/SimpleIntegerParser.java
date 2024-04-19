package net.myitian.mineshell.util;

import net.minecraft.util.Pair;

public final class SimpleIntegerParser {
    public static int parseInt(String s)
            throws NumberFormatException {
        Integer i = tryParseInt(s);
        if (i == null)
            throw new NumberFormatException();
        return i;
    }

    public static Integer tryParseInt(String s) {
        int i = 0, value = 0, length = s.length();
        for (; i < length; i++) {
            char c = s.charAt(++i);
            if (c >= '0' && c <= '9')
                value = value * 10 + (c ^ '0');
            else
                break;
        }
        return i == 0 ? null : value;
    }

    public static Pair<Integer, Integer> parseIntWithReturnLength(StringBuilder s, int start, int end) {
        int i = start, value = 0;
        for (; i < end; i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9')
                value = value * 10 + (c ^ '0');
            else
                break;
        }
        return new Pair<>(i, i == 0 ? null : value);
    }
}
