package javacc.compilation;

import static java.lang.String.format;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

public class CompilationSteps {
    private ProjectConnection project;
    private File projectDirectory;
    private File outputDirectory;

    public void givenAProjectNamed(String projectName) throws URISyntaxException {
        String projectPath = "projects" + File.separator + projectName;
        URL resource = getClass().getResource(projectPath);
        if (resource == null)
            throw new AssertionError(format("Project at path [%s] does not exist.", projectPath));

        projectDirectory = new File(resource.toURI());
    }

    public void whenIExecuteTask(String taskName) throws IOException {
        project = GradleConnector.newConnector().forProjectDirectory(projectDirectory).connect();

        BuildLauncher build = project.newBuild();
        build.forTasks("clean", taskName).setStandardOutput(System.out);
        build.withArguments("--debug", "--project-dir", projectDirectory.getAbsolutePath(), "-b", "build.gradle",
            "-Dplugin.version=" + System.getProperty("PLUGIN_VERSION"));
        build.run();
    }

    public void thenAssertOutputDirectoryExists(String outputDirectory) {
        this.outputDirectory = new File(projectDirectory, outputDirectory);
        assertTrue(this.outputDirectory.exists());
    }

    public void thenAssertOutputDirectoryDoesNotExists(String outputDirectory) {
        assertFalse(new File(projectDirectory, outputDirectory).exists());
    }

    public void andAssertFileWasGenerated(String filename) {
        assertTrue((new File(outputDirectory, filename)).exists());
    }
}
