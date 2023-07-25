package javacc.compilation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class ThePluginIsCompatibleWithAllSpportedJavaccVersions {

    @Test
    public void givenVersion4WhenExecuteAllTasksFilesAreGenerated() throws URISyntaxException, IOException {
        givenVersionWhenExecuteAllTasksFilesAreGenerated("net.java.dev.javacc:javacc:4.0", true);
    }

    @Test
    public void givenVersion5WhenExecuteAllTasksFilesAreGenerated() throws URISyntaxException, IOException {
        givenVersionWhenExecuteAllTasksFilesAreGenerated("net.java.dev.javacc:javacc:5.0", true);
    }

    @Test
    public void givenVersion6WhenExecuteAllTasksFilesAreGenerated() throws URISyntaxException, IOException {
        givenVersionWhenExecuteAllTasksFilesAreGenerated("net.java.dev.javacc:javacc:6.1.2", true);
    }

    @Test
    public void givenVersion7WhenExecuteAllTasksFilesAreGenerated() throws URISyntaxException, IOException {
        givenVersionWhenExecuteAllTasksFilesAreGenerated("net.java.dev.javacc:javacc:7.0.12", true);
    }

    @Test
    public void givenVersion8WhenExecuteAllTasksFilesAreGenerated() throws URISyntaxException, IOException {
        givenVersionWhenExecuteAllTasksFilesAreGenerated("org.javacc.generator:java:8.0.1", false);
    }

    public void givenVersionWhenExecuteAllTasksFilesAreGenerated(String javaccDependency, boolean simpleNode)
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuildWithVersion");
        steps.withArguments("clean", ":subprojects:subproject1:compileJavacc", ":subprojects:subproject1:jjdoc",
            "-PjavaccDep=" + javaccDependency).execute();

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileWasGenerated("Hello.java");
        steps.andAssertFileWasGenerated("HelloTokenManager.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("HelloConstants.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("Token.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        if (simpleNode) {
            steps.andAssertFileWasGenerated("SimpleNode.java");
        }
        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjdoc");
        steps.andAssertFileWasGenerated("MyParser.html");
        // clean again after
        steps.withArguments("clean", "-PjavaccDep=" + javaccDependency).execute();
    }

}
