package com.gemini.generic.utils;

public final class EnumUtils {

    /**
     * empty private constructor.
     */
    private EnumUtils() {
    }

    /**
     * Converts a string of a request Type option name
     * to the Command enum lookup name. .
     *
     * @param execute the request type
     * @return the Command enum lookup name.
     */
    public static String toEnumLookupValue(final String execute) {
        StringBuilder buf = new StringBuilder();
        if (execute == null) {
            return buf.toString();
        }
        for (char ch : execute.toCharArray()) {
            buf.append(Character.toUpperCase(ch));
        }
        return buf.toString();
    }
}
