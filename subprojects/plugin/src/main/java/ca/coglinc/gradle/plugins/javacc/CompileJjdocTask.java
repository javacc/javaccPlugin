package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.tasks.TaskAction;
import org.javacc.jjdoc.JJDocGlobals;
import org.javacc.jjdoc.JJDocMain;

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
        compileSourceFilesToTempOutputDirectory();
    }

    @Override
    protected void invokeCompiler(String[] arguments) throws Exception {
        int errorCode = JJDocMain.mainProgram(arguments);
        if (errorCode != 0) {
            throw new IllegalStateException("JJDoc failed with error code: [" + errorCode + "]");
        } else {
            File jjdocOutputFile = new File(JJDocGlobals.output_file);
            FileUtils.moveFile(jjdocOutputFile, new File(getOutputDirectory(), jjdocOutputFile.getName()));
        }
    }

    @Override
    protected FileVisitor getJavaccSourceFileVisitor() {
        return new JavaccSourceFileVisitor(this);
    }

    @Override
    protected void augmentArguments(File inputDirectory, RelativePath inputRelativePath, Map<String, String> arguments) {
        // nothing to augment
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
