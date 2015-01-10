package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Map;

import org.javacc.jjtree.JJTree;
import org.javacc.parser.Main;

public class CompileJJTreeTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "compileJJTree";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JJTree files into JavaCC files";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "jjtree";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "jjtree";

    public CompileJJTreeTask() {
        super(DEFAULT_INPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY, "**/*.jjt");
    }

    protected void augmentArguments(File inputFile, Map<String, String> arguments) {
        arguments.put("JJTREE_OUTPUT_DIRECTORY", computeInputFileOutputDirectory(inputFile).getAbsolutePath());
    }

    protected String getProgramName() {
        return "JJTree";
    }

    protected void invokeCompiler(String[] arguments) throws Exception {
        int errorCode = new JJTree().main(arguments);
        if (errorCode != 0)
            throw new IllegalStateException("JJTree failed with error code: [" + errorCode + "]");
    }
}
