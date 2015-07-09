package ca.coglinc.gradle.plugins.javacc.compilationresults;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

public class CompiledJavaccFilesDirectoryTest {

    @Test
    public void listFilesReturnsEmptyCollectionForEmptyDirectory() {
        CompiledJavaccFilesDirectory directory = new CompiledJavaccFilesDirectory(new File(getClass().getResource("/empty").getFile()), null, null, null);
        
        Collection<CompiledJavaccFile> files = directory.listFiles();
        
        assertThat(files, is(empty()));
    }
    
    @Test
    public void listFilesReturnsOnlyJavaFiles() {
        CompiledJavaccFilesDirectory directory = new CompiledJavaccFilesDirectory(new File(getClass().getResource("/compiledResults").getFile()), null, null, null);
        
        Collection<CompiledJavaccFile> files = directory.listFiles();
        
        assertThat(files, hasSize(1));
    }
    
    @Test
    public void listFilesReturnsJavaFilesRecursively() {
        CompiledJavaccFilesDirectory directory = new CompiledJavaccFilesDirectory(new File(getClass().getResource("/compiledResultsWithSubFolders").getFile()), null, null, null);
        
        Collection<CompiledJavaccFile> files = directory.listFiles();
        
        assertThat(files, hasSize(2));
    }
}
