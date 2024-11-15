package javacc.compilation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;

public class CompilationSteps {

    private static final int JAVA_11 = 11;
    private File projectDirectory;
    private GradleRunner buildRunner;
    private File outputDirectory;

    public void givenAProjectNamed(String projectName) throws URISyntaxException, IOException {
        String projectPath = "projects" + File.separator + projectName;
        URL resource = CompilationSteps.class.getResource(projectPath);
        if (resource == null) {
            throw new AssertionError(String.format("Project at path [%s] does not exist.", projectPath));
        }

        projectDirectory = new File(resource.toURI());

        String gradleVersion = Runtime.version().feature() > JAVA_11 ? "7.3" : "6.4";
        buildRunner = GradleRunner.create()
                                  .withGradleVersion(gradleVersion)
                                  .withProjectDir(projectDirectory)
                                  .withPluginClasspath();
    }

    private void ensureGivens() {
        if ((projectDirectory == null) || (buildRunner == null)) {
            throw new IllegalStateException("JavaCC compilation steps assume a project exists. Use givenAProjectNamed(String projectName) first");
        }
    }

    public CompilationSteps withArguments(String... arguments) {
        ensureGivens();

        String[] defaultArguments = new String[] {
            "--info", "--stacktrace", "--project-dir", projectDirectory.getAbsolutePath(), "-b", "build.gradle"
        };

        String[] allArguments = ArrayUtils.addAll(defaultArguments, arguments);
        buildRunner.withArguments(allArguments);
        buildRunner.forwardOutput();

        return this;
    }

    public BuildResult execute() {
        ensureGivens();

        BuildResult buildResult = buildRunner.build();
        assertNotNull(buildResult);

        return buildResult;
    }

    public void thenAssertOutputDirectoryExists(String outputDirectory) {
        this.outputDirectory = new File(projectDirectory, outputDirectory);
        assertExists(this.outputDirectory);
    }

    public void thenAssertOutputDirectoryDoesNotExists(String outputDirectory) {
        assertFalse(new File(projectDirectory, outputDirectory).exists());
    }

    public void andAssertFileWasGenerated(String filename) {
        assertExists(new File(outputDirectory, filename));
    }

    private void assertExists(File file) {
        assertTrue("File " + file + " should exist", file.exists());
    }

    public void andAssertFileExistsButWasNotGenerated(String filename) throws IOException {
        File javaFile = new File(outputDirectory, filename);
        assertExists(javaFile);

        String fileContent = FileUtils.readFileToString(javaFile);
        assertTrue(fileContent.contains("public static final boolean IS_CUSTOM = true;"));
    }

    public void andAssertFileDoesNotExist(String filename) {
        File javaFile = new File(outputDirectory, filename);
        assertFalse(javaFile.exists());
    }

    public void thenAssertTaskStatus(BuildResult buildResult, String taskPath, TaskOutcome expectedOutcome) {
        assertEquals(expectedOutcome, buildResult.task(taskPath).getOutcome());
    }
}
