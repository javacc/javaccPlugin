package ca.coglinc.gradle.plugins.javacc.programexecution;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.RelativePath;

/**
 * This {@link ProgramInvoker} implementation invokes the JJTree program to generate JavaCC grammar files from JJTree files.
 */
public class JjtreeProgramInvoker extends AbstractProgramInvoker {
    public static final String SUPPORTED_FILE_SUFFIX = ".jjt";

    public JjtreeProgramInvoker(Project project, Configuration classpath, File tempOutputDirectory) {
        super(project, classpath, tempOutputDirectory, JjtreeExecutorAction.class);
    }

    @Override
    public ProgramArguments augmentArguments(File inputDirectory, RelativePath inputRelativePath, ProgramArguments arguments) {
        ProgramArguments augmentedArguments = new ProgramArguments(arguments);
        augmentedArguments.add("JJTREE_OUTPUT_DIRECTORY", inputRelativePath.getFile(tempOutputDirectory).getParentFile().getAbsolutePath());
        return augmentedArguments;
    }

    @Override
    public String getProgramName() {
        return "JJTree";
    }

    @Override
    public String supportedSuffix() {
        return SUPPORTED_FILE_SUFFIX;
    }
}
