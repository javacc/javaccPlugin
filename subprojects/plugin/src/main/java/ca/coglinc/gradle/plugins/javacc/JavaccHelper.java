package ca.coglinc.gradle.plugins.javacc;

import java.util.*;

public final class JavaccHelper {
    private JavaccHelper() {
        // Prevent instantiation
    }

    public static String[] prepareArguments(Map<String, String> arguments) {
        String[] argumentsForCommandLine;
        if (arguments != null && !arguments.isEmpty()) {
            argumentsForCommandLine = new String[arguments.size() + 2];

            int index = 1;
            for (Map.Entry<String, String> argumentEntry : arguments.entrySet()) {
                argumentsForCommandLine[index] = String.format("-%1$s=%2$s", argumentEntry.getKey(), argumentEntry.getValue());
                index = index + 1;
            }
        } else {
            argumentsForCommandLine = new String[2];
        }

        return argumentsForCommandLine;
    }
}
