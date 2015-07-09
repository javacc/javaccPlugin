package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.EmptyFileVisitor;
import org.gradle.api.file.FileVisitDetails;

public class NonJavaccSourceFileVisitor extends EmptyFileVisitor {
    private AbstractJavaccTask task;

    public NonJavaccSourceFileVisitor(AbstractJavaccTask abstractJavaccTask) {
        this.task = abstractJavaccTask;
    }

    @Override
    public void visitFile(FileVisitDetails fileDetails) {
        if (!isValidSourceFileForTask(fileDetails)) {
            File sourceFile = fileDetails.getFile();
            File destinationFile = new File(sourceFile.getAbsolutePath().replace(task.getInputDirectory().getAbsolutePath(), task.getOutputDirectory().getAbsolutePath()));
            
            task.getLogger().debug("Copying non javacc source file from {} to {}", sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
            
            try {
                FileUtils.copyFile(sourceFile, destinationFile);
            } catch (IOException e) {
                throw new JavaccTaskException(String.format("Could not copy file %s to %s", sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath()), e);
            }
        }
    }

    private boolean isValidSourceFileForTask(FileVisitDetails fileDetails) {
        return fileDetails.getName().toLowerCase().endsWith(task.supportedSuffix());
    }
}
