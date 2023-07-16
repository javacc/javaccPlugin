package org.javacc.plugin.gradle.javacc.programexecution;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.gradle.api.internal.provider.DefaultProperty;
import org.gradle.api.internal.provider.PropertyHost;
import org.gradle.api.provider.Property;
import org.gradle.process.JavaExecSpec;

public class JavaExecFactory {
    public static JavaExecSpec createSpec() {
        JavaExecSpec spec = mock(JavaExecSpec.class);
        Property<String> mainClass = new DefaultProperty<>(mock(PropertyHost.class), String.class);
        when(spec.getMainClass()).thenReturn(mainClass);
        return spec;
    }
}
