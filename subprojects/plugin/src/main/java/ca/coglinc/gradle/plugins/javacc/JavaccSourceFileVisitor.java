package ca.coglinc.gradle.plugins.javacc;

import java.io.File;

import org.gradle.api.file.EmptyFileVisitor;
import org.gradle.api.file.FileVisitDetails;

/**
 * This implementation of {@link org.gradle.api.file.FileVisitor} visits only files supported by the provided {@code task}.
 */
public class JavaccSourceFileVisitor extends EmptyFileVisitor {
    private AbstractJavaccTask task;

    public JavaccSourceFileVisitor(AbstractJavaccTask task) {
        this.task = task;
    }
    
    @Override
    public void visitFile(FileVisitDetails fileDetails) {
        if (isValidSourceFileForTask(fileDetails)) {
            task.compile(computeInputDirectory(fileDetails), fileDetails.getRelativePath());
        } else {
            task.getLogger().debug("Skipping file {} as it is not supported by program {}", fileDetails.getFile().getAbsolutePath(), task.getProgramName());
        }
    }

    private boolean isValidSourceFileForTask(FileVisitDetails fileDetails) {
        return fileDetails.getName().toLowerCase().endsWith(task.supportedSuffix());
    }

    private File computeInputDirectory(FileVisitDetails fileVisitDetails) {
        File fileAbsolute = fileVisitDetails.getFile();
        File fileRelative = new File(fileVisitDetails.getPath());

        return new File(fileAbsolute.getAbsolutePath().replace(fileRelative.getPath(), ""));
    }
}
