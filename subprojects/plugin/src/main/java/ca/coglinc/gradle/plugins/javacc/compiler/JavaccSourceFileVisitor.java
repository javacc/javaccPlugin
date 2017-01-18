package ca.coglinc.gradle.plugins.javacc.compiler;

import java.io.File;

import org.gradle.api.file.EmptyFileVisitor;
import org.gradle.api.file.FileVisitDetails;

/**
 * This implementation of {@link org.gradle.api.file.FileVisitor} visits only files supported by the provided {@code compiler}.
 */
class JavaccSourceFileVisitor extends EmptyFileVisitor {
    private SourceFileCompiler compiler;

    JavaccSourceFileVisitor(SourceFileCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public void visitFile(FileVisitDetails fileDetails) {
        if (isValidSourceFileForTask(fileDetails)) {
            compiler.compile(computeInputDirectory(fileDetails), fileDetails.getRelativePath());
        } else {
            compiler.getLogger().debug("Skipping file {} as it is not supported by program {}", fileDetails.getFile().getAbsolutePath(), compiler.getProgramName());
        }
    }

    private boolean isValidSourceFileForTask(FileVisitDetails fileDetails) {
        return fileDetails.getName().toLowerCase().endsWith(compiler.supportedSuffix());
    }

    private File computeInputDirectory(FileVisitDetails fileVisitDetails) {
        File fileAbsolute = fileVisitDetails.getFile();
        File fileRelative = new File(fileVisitDetails.getPath());

        return new File(fileAbsolute.getAbsolutePath().replace(fileRelative.getPath(), ""));
    }
}
