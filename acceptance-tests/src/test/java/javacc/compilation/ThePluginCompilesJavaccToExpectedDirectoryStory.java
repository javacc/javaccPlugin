package javacc.compilation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class ThePluginCompilesJavaccToExpectedDirectoryStory {
    
    @Test
    public void givenASimpleProjectWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();
        
        steps.givenAProjectNamed("simpleTest");
        steps.whenIExecuteTask("compileJavacc");
        steps.thenAssertOutputDirectoryExists("build" + File.separator + "generated" + File.separator + "javacc");
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "ParseException.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "SimpleCharStream.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "Token.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "TokenMgrError.java");
    }
}
