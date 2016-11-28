package ca.coglinc.gradle.plugins.javacc;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Action;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.gradle.process.JavaExecSpec;

public class CompileJavaccTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "compileJavacc";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JavaCC files into Java files";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "javacc";
    private static final String SUPPORTED_FILE_SUFFIX = ".jj";

    public CompileJavaccTask() {
        super(CompileJavaccTask.DEFAULT_INPUT_DIRECTORY, CompileJavaccTask.DEFAULT_OUTPUT_DIRECTORY, "**/*" + SUPPORTED_FILE_SUFFIX);
    }

    @TaskAction
    public void run() {
        getTempOutputDirectory().mkdirs();

        compileSourceFilesToTempOutputDirectory();
        copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();
        copyNonJavaccFilesToOutputDirectory();
        FileUtils.deleteQuietly(getTempOutputDirectory());
    }

    @Override
    protected void augmentArguments(File inputDirectory, RelativePath inputRelativePath, ProgramArguments arguments) {
        arguments.add("OUTPUT_DIRECTORY", inputRelativePath.getFile(getTempOutputDirectory()).getParentFile().getAbsolutePath());
    }

    @Override
    protected String getProgramName() {
        return "JavaCC";
    }

    @Override
    protected void invokeCompiler(final ProgramArguments arguments) throws Exception {
        ExecResult execResult = this.getProject().javaexec(new Action<JavaExecSpec>() {
            @Override
            public void execute(JavaExecSpec executor) {
                executor.classpath(getClasspath());
                executor.setMain("org.javacc.parser.Main");
                executor.args((Object[]) arguments.toArray());
                executor.setIgnoreExitValue(true);
            }
        });

        if (execResult.getExitValue() != 0) {
            throw new IllegalStateException("Javacc failed with error code: [" + execResult.getExitValue() + "]");
        }
    }

    @Override
    protected FileVisitor getJavaccSourceFileVisitor() {
        return new JavaccSourceFileVisitor(this);
    }

    @Override
    protected String supportedSuffix() {
        return SUPPORTED_FILE_SUFFIX;
    }
}
