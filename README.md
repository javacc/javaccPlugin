# JavaCC Compiler Plugin for Gradle 

Provides the ability to use [JavaCC](http://javacc.java.net/) via [Gradle](http://www.gradle.org/). If the 'java' plugin is also applied, JavaCompile tasks will depend upon the compileJavacc task.

## Installation

Simply grab the plugin from Maven Central:

Add the following lines to your `build.gradle` script:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath group: 'ca.coglinc', name: 'javacc-gradle-plugin', version: '2.0.2'
    }
}
apply plugin: 'ca.coglinc.javacc'
```

## Building

To build, simply run the following command in the directory where you checked out the plugin source:

`./gradlew clean build`

## Usage

Place your JavaCC code into `src/main/javacc`.
The generated Java code will be  put under `./build/generated/javacc` and will be compiled as part of the Java compile.

You can configure commandline args passed to JavaCC by specifying `javaccArguments` map in compileJavacc:
```
compileJavacc {
    javaccArguments = [grammar_encoding : 'UTF-8', static: 'false']
}
```

### Eclipse

If you are using Eclipse and would like your gradle project to compile nicely in eclipse and have the generated code in the build path, you can simply add the generated path to the main sourceSet and add a dependency on `compileJavacc` to `eclipseClasspath`.
```java
main {
    java {
        srcDir compileJavacc.outputDirectory
    }
}
    
eclipseClasspath.dependsOn("compileJavacc")
```

## Compatibility

This plugin requires Java 6+.

It has been tested with Gradle 1.11+. Please let us know if you have had success with other versions of Gradle.

## Signature

The artifacts for this plugin are signed using the [PGP key](http://pgp.mit.edu:11371/pks/lookup?op=get&search=0x321163AE83A4068A) for `jonathan.martel@coglinc.ca`.

## Changelog

### 2.0.2

- Improved the build system
- Added acceptance tests
- Support the gradle wrapper (Issue #10)
- Support passing optional arguments to Javacc (Issue #11)

### 2.0.1

Fixed the gradle-plugin attribute for the Bintray package version.

### 2.0.0

- Migrated to Gradle 2.0
- Plugin id changed to 'ca.coglinc.javacc'
- Plugin is now available via the [Gradle Plugins repository](http://plugins.gradle.org)

### 1.0.1

Updated JavaCC to 6.1.2.

### 1.0.0

Initial version with limited features. Simply generates JavaCC files to Java from a non-configurable location into a non-configurable location.
