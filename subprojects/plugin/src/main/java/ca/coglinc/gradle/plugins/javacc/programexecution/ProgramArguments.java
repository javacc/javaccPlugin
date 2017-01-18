package ca.coglinc.gradle.plugins.javacc.programexecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ProgramArguments {
    private static final String JAVACC_PROGRAM_ARGUMENT_FORMAT = "-%1$s=%2$s";

    private final List<String> programArguments = new ArrayList<String>();
    private boolean filenameAdded;

    public ProgramArguments() {
    }

    public ProgramArguments(ProgramArguments source) {
        if (source != null) {
            for (String keyValue : source.programArguments) {
                add(null, keyValue);
            }

            filenameAdded = source.filenameAdded;
        }
    }

    public void addAll(Map<String, String> arguments) {
        if (arguments != null) {
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                add(entry.getKey(), entry.getValue());
            }
        }
    }

    public void add(String name, String value) {
        String argument = value;
        if (!StringUtils.isEmpty(name)) {
            argument = String.format(JAVACC_PROGRAM_ARGUMENT_FORMAT, name, value);
        }

        if (!filenameAdded) {
            programArguments.add(argument);
        } else {
            programArguments.add(programArguments.size() - 1, argument);
        }
    }

    public int size() {
        return programArguments.size();
    }

    public String get(int index) {
        return programArguments.get(index);
    }

    public String[] toArray() {
        return programArguments.toArray(new String[0]);
    }

    public boolean isEmpty() {
        return programArguments.isEmpty();
    }

    public void addFilename(String filename) {
        if (filenameAdded) {
            programArguments.remove(programArguments.size() - 1);
            filenameAdded = false;
        }

        add(null, filename);
        filenameAdded = true;
    }

    public String getFilename() {
        return programArguments.get(programArguments.size() - 1);
    }

    @Override
    public String toString() {
        return programArguments.toString();
    }
}
