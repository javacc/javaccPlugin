package org.javacc.plugin.gradle.javacc.compiler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.EmptyFileVisitor;
import org.gradle.api.file.FileVisitDetails;

import org.javacc.plugin.gradle.javacc.JavaccTaskException;

/**
 * This implementation of {@link org.gradle.api.file.FileVisitor} visits only files that are not supported by the provided {@code compiler}.
 */
class NonJavaccSourceFileVisitor extends EmptyFileVisitor {
    private SourceFileCompiler compiler;

    NonJavaccSourceFileVisitor(SourceFileCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public void visitFile(FileVisitDetails fileDetails) {
        if (!isValidSourceFileForTask(fileDetails)) {
            File sourceFile = fileDetails.getFile();
            File destinationFile = new File(sourceFile.getAbsolutePath().replace(compiler.getInputDirectory().getAbsolutePath(), compiler.getOutputDirectory().getAbsolutePath()));

            copyFile(sourceFile, destinationFile);
        }
    }

    private void copyFile(File sourceFile, File destinationFile) {
        compiler.getLogger().debug("Copying non javacc source file from {} to {}", sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());

        try {
            FileUtils.copyFile(sourceFile, destinationFile);
        } catch (IOException e) {
            throw new JavaccTaskException(String.format("Could not copy file %s to %s", sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath()), e);
        }
    }

    private boolean isValidSourceFileForTask(FileVisitDetails fileDetails) {
        return fileDetails.getName().toLowerCase().endsWith(compiler.supportedSuffix());
    }
}
