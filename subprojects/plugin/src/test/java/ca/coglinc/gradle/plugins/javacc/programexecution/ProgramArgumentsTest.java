package ca.coglinc.gradle.plugins.javacc.programexecution;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void creatingACopyOfAnExistingInstanceAddsAllArgumentsToTheCopy() {
        arguments.add("name", "value");

        ProgramArguments copy = new ProgramArguments(arguments);

        assertThat(copy.toArray(), is(not(sameInstance(arguments.toArray()))));
        assertArrayEquals(arguments.toArray(), copy.toArray());
    }

    @Test
    public void creatingACopyOfANullInstanceCreatesAnEmptyInstance() {
        ProgramArguments copy = new ProgramArguments(null);

        assertThat(copy.size(), is(0));
    }

    @Test
    public void creatingACopyOfAnInstanceWithTheFilenameAddedKeepsFilenameArgumentLast() {
        arguments.addFilename("file.txt");

        ProgramArguments copy = new ProgramArguments(arguments);
        copy.add("name", "value");

        assertThat(copy.getFilename(), is(equalTo("file.txt")));
    }

    @Test
    public void toStringReturnsEmptyListStringRepresentationIfNoArguments() {
        assertThat(arguments.toString(), is(equalTo("[]")));
    }

    @Test
    public void toStringReturnsAllElementsInTheListStyle() {
        arguments.add("name", "value");
        arguments.add("TEST", "test");

        assertThat(arguments.toString(), is(equalTo("[-name=value, -TEST=test]")));
    }
}
