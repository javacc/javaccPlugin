package org.javacc.plugin.gradle.javacc.compilationresults;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

public class CompiledJavaccFilesDirectoryTest {

    @Test
    public void listFilesReturnsEmptyCollectionForEmptyDirectory() {
        CompiledJavaccFilesDirectory directory = new CompiledJavaccFilesDirectory(new File(getClass().getResource("/empty").getFile()), null, null);

        Collection<CompiledJavaccFile> files = directory.listFiles();

        assertThat(files, is(empty()));
    }

    @Test
    public void listFilesReturnsAllFiles() {
        CompiledJavaccFilesDirectory directory = new CompiledJavaccFilesDirectory(new File(getClass().getResource("/compiledResults").getFile()), null, null);

        Collection<CompiledJavaccFile> files = directory.listFiles();

        final int numberOfFilesInFolder = 2;
        assertThat(files, hasSize(numberOfFilesInFolder));
    }

    @Test
    public void listFilesReturnsAllFilesRecursively() {
        CompiledJavaccFilesDirectory directory = new CompiledJavaccFilesDirectory(new File(getClass().getResource("/compiledResultsWithSubFolders").getFile()),  null, null);

        Collection<CompiledJavaccFile> files = directory.listFiles();

        final int numberOfFilesInFolder = 3;
        assertThat(files, hasSize(numberOfFilesInFolder));
    }

    @Test
    public void toStringReturnsAbsoluteDirectoryPath() {
        File outputDirectory = new File(getClass().getResource("/compiledResults").getFile());
        CompiledJavaccFilesDirectory directory = new CompiledJavaccFilesDirectory(outputDirectory,  null, null);

        String stringValue = directory.toString();

        assertEquals(outputDirectory.getAbsolutePath(), stringValue);
    }
}
