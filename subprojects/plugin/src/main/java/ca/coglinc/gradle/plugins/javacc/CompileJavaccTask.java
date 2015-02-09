package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Map;

import org.gradle.api.file.RelativePath;
import org.javacc.parser.Main;

public class CompileJavaccTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "compileJavacc";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JavaCC files into Java files";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "javacc";

    public CompileJavaccTask() {
        super(CompileJavaccTask.DEFAULT_INPUT_DIRECTORY, CompileJavaccTask.DEFAULT_OUTPUT_DIRECTORY, "**/*.jj");
    }

    @Override
    protected void augmentArguments(File inputDirectory, RelativePath inputRelativePath, Map<String, String> arguments) {
        arguments.put("OUTPUT_DIRECTORY", inputRelativePath.getFile(getOutputDirectory()).getParentFile().getAbsolutePath());
    }

    @Override
    protected String getProgramName() {
        return "JavaCC";
    }

    @Override
    protected void invokeCompiler(String[] arguments) throws Exception {
        int errorCode = Main.mainProgram(arguments);
        if (errorCode != 0) {
            throw new IllegalStateException("Javacc failed with error code: [" + errorCode + "]");
        }
    }
}
