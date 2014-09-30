package javacc.compilation;

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
        URL resource = getClass().getResource("projects" + File.separator + projectName);
        projectDirectory = new File(resource.toURI());
    }
    
    public void whenIExecuteTask(String taskName) throws IOException {
        project = GradleConnector.newConnector().forProjectDirectory(projectDirectory).connect();
        BuildLauncher build = project.newBuild();
        build.forTasks(taskName).setStandardOutput(System.out);
        build.withArguments("--debug", "--project-dir", projectDirectory.getAbsolutePath(), "-b", "build.gradle");
        
        build.run();
    }
    
    public void thenAssertOutputDirectoryExists(String outputDirectory) {
        this.outputDirectory = new File(projectDirectory, outputDirectory);
        assertTrue(this.outputDirectory.exists());
    }

    public void andAssertFileWasGenerated(String filename) {
        assertTrue((new File(outputDirectory, filename)).exists());
    }
}
