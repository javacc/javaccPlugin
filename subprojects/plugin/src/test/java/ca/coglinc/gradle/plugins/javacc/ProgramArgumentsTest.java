package ca.coglinc.gradle.plugins.javacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ProgramArgumentsTest {
    private ProgramArguments arguments;

    @Before
    public void createProgramArguments() {
        arguments = new ProgramArguments();
    }
    
    @Test
    public void emptyArgumentsContainAddedArgument() {
        arguments.add("STATIC", "false");
        
        assertEquals(1, arguments.size());
        assertEquals("-STATIC=false", arguments.get(0));
    }
    
    @Test
    public void addingArgumentsSequentiallyPreservesOrder() {
        arguments.add("STATIC", "false");
        arguments.add("OUTPUT_DIRECTORY", "/tmp");
        
        assertEquals(2, arguments.size());
        assertEquals("-STATIC=false", arguments.get(0));
        assertEquals("-OUTPUT_DIRECTORY=/tmp", arguments.get(1));
    }
    
    @Test
    public void addingArgumentWithoutNameSimplyAddsTheValue() {
        arguments.add("", "/tmp/file.jj");
        arguments.add(null, "/tmp/file.jjt");
        
        assertEquals(2, arguments.size());
        assertEquals("/tmp/file.jj", arguments.get(0));
        assertEquals("/tmp/file.jjt", arguments.get(1));
    }

    @Test
    public void emptyArgumentsContainAllAddedArguments() {
        addMultipleArguments();
        
        assertEquals(2, arguments.size());
        assertEquals("-STATIC=false", arguments.get(0));
        assertEquals("-OUTPUT_DIRECTORY=/tmp", arguments.get(1));
    }

    private void addMultipleArguments() {
        Map<String, String> addedArguments = new LinkedHashMap<String, String>();
        addedArguments.put("STATIC", "false");
        addedArguments.put("OUTPUT_DIRECTORY", "/tmp");
        
        arguments.addAll(addedArguments);
    }
    
    @Test
    public void addingArgumentsAppendsTheseToAnyExistingArguments() {
        arguments.add("TEST", "test");
        
        int originalSize = arguments.size();
        
        addMultipleArguments();
        
        assertEquals(originalSize + 2, arguments.size());
        assertEquals("-TEST=test", arguments.get(0));
        assertEquals("-STATIC=false", arguments.get(1));
        assertEquals("-OUTPUT_DIRECTORY=/tmp", arguments.get(2));
    }
    
    @Test
    public void addingNullArgumentsDoesNotModifyExistingArguments() {
        arguments.add("TEST", "test");
        
        int originalSize = arguments.size();
        
        arguments.addAll(null);
        
        assertEquals(originalSize, arguments.size());
        assertEquals("-TEST=test", arguments.get(0));
    }
    
    @Test
    public void toArrayReturnsAStringArrayContainingTheArgumentsInOrder() {
        addMultipleArguments();
        
        String[] argumentsArray = arguments.toArray();
        
        assertEquals(2, argumentsArray.length);
        assertEquals("-STATIC=false", argumentsArray[0]);
        assertEquals("-OUTPUT_DIRECTORY=/tmp", argumentsArray[1]);
    }
    
    @Test
    public void emptyArguments() {
        assertTrue(arguments.isEmpty());
    }
    
    @Test
    public void nonEmptyArguments() {
        addMultipleArguments();
        
        assertFalse(arguments.isEmpty());
    }
    
    @Test
    public void filenameIsASimpleValueArgument() {
        arguments.addFilename("/tmp/Test.jj");
        
        assertEquals("/tmp/Test.jj", arguments.getFilename());
    }
    
    @Test
    public void filenameIsNotImpactedByAddingArguments() {
        arguments.add("STATIC", "false");
        
        arguments.addFilename("/tmp/Test.jj");
        
        arguments.add("OUTPUT_DIRECTORY", "/tmp");
        
        assertEquals("/tmp/Test.jj", arguments.getFilename());
    }
    
    @Test
    public void filenameCanBeModifiedAfterBeingAdded() {
        arguments.add("STATIC", "false");
        
        arguments.addFilename("/tmp/Test.jj");
        arguments.addFilename("/tmp/FixedTest.jj");
        
        assertEquals(2, arguments.size());
        assertEquals("/tmp/FixedTest.jj", arguments.getFilename());
    }
}
