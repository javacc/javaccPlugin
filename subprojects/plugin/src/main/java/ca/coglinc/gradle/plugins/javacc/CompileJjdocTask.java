package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Action;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.gradle.process.JavaExecSpec;

public class CompileJjdocTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "jjdoc";
    public static final String TASK_DESCRIPTION_VALUE = "Takes a JavaCC parser specification and produces documentation for the BNF grammar";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "jjdoc";
    private static final String SUPPORTED_FILE_SUFFIX = ".jj";

    public CompileJjdocTask() {
        super(CompileJjdocTask.DEFAULT_INPUT_DIRECTORY, CompileJjdocTask.DEFAULT_OUTPUT_DIRECTORY, "**/*" + SUPPORTED_FILE_SUFFIX);
    }

    @TaskAction
    public void run() {
        getTempOutputDirectory().mkdirs();

        compileSourceFilesToTempOutputDirectory();
        copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();
        FileUtils.deleteQuietly(getTempOutputDirectory());
    }

    @Override
    protected void invokeCompiler(final ProgramArguments arguments) throws Exception {
        ExecResult execResult = this.getProject().javaexec(new Action<JavaExecSpec>() {
            @Override
            public void execute(JavaExecSpec executor) {
                executor.classpath(getClasspath());
                executor.setMain("org.javacc.jjdoc.JJDocMain");
                executor.args((Object[]) arguments.toArray());
                executor.setIgnoreExitValue(true);
            }
        });

        if (execResult.getExitValue() != 0) {
            throw new IllegalStateException("JJDoc failed with error code: [" + execResult.getExitValue() + "]");
        }
    }

    @Override
    protected FileVisitor getJavaccSourceFileVisitor() {
        return new JavaccSourceFileVisitor(this);
    }

    @Override
    protected void augmentArguments(File inputDirectory, RelativePath inputRelativePath, ProgramArguments arguments) {
        File tempOutputFile = inputRelativePath.getFile(getTempOutputDirectory());
        tempOutputFile.mkdirs();

        String outputFileExtension = getJjdocOutputFileExtension(arguments);

        arguments.add("OUTPUT_FILE", tempOutputFile.getAbsolutePath().replace(SUPPORTED_FILE_SUFFIX, outputFileExtension));
    }

    private String getJjdocOutputFileExtension(ProgramArguments arguments) {
        String outputFileExtension = ".html";
        if (IterableUtils.matchesAny(Arrays.asList(arguments.toArray()), outputTextPredicate())) {
            outputFileExtension = ".txt";
        }
        return outputFileExtension;
    }

    private Predicate<String> outputTextPredicate() {
        return new Predicate<String>() {

            @Override
            public boolean evaluate(String argument) {
                return "-text=true".equals(argument.toLowerCase());
            }
        };
    }

    @Override
    protected String getProgramName() {
        return "JJDoc";
    }

    @Override
    protected String supportedSuffix() {
        return SUPPORTED_FILE_SUFFIX;
    }
}
