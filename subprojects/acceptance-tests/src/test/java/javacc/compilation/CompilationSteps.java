package javacc.compilation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;

public class CompilationSteps {
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

        InputStream pluginClasspathStream = getClass().getClassLoader().getResourceAsStream("plugin-classpath.txt");
        if (pluginClasspathStream == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.");
        }
        List<File> pluginClasspath = new ArrayList<File>();
        List<String> pluginClasspathLines = IOUtils.readLines(pluginClasspathStream, Charsets.UTF_8.toString());
        for (String classpathItem : pluginClasspathLines) {
            pluginClasspath.add(new File(classpathItem));
        }

        buildRunner = GradleRunner.create().withProjectDir(projectDirectory).withPluginClasspath(pluginClasspath);
    }

    private void ensureGivens() {
        if ((projectDirectory == null) || (buildRunner == null)) {
            throw new IllegalStateException("JavaCC compilation steps assume a project exists. Use givenAProjectNamed(String projectName) first");
        }
    }
    
    public CompilationSteps withArguments(String... arguments) {
        ensureGivens();
        
        String[] defaultArguments = new String[] {
            "--info", "--project-dir", projectDirectory.getAbsolutePath(), "-b", "build.gradle"
        };
        
        String[] allArguments = ArrayUtils.addAll(defaultArguments, arguments);
        buildRunner.withArguments(allArguments);
        buildRunner.forwardOutput();

        return this;
    }
    
    public void execute() {
        ensureGivens();

        BuildResult buildResult = buildRunner.build();
        assertNotNull(buildResult);
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

    public void andAssertFileExistsButWasNotGenerated(String filename) throws IOException {
        File javaFile = new File(outputDirectory, filename);
        assertTrue(javaFile.exists());
        
        String fileContent = FileUtils.readFileToString(javaFile);
        assertTrue(fileContent.contains("public static final boolean IS_CUSTOM = true;"));
    }

    public void andAssertFileDoesNotExist(String filename) {
        File javaFile = new File(outputDirectory, filename);
        assertFalse(javaFile.exists());
    }
}
