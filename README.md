# Gradle Kit 

A collection of Gradle plugins that streamline Java project development and publication to Maven Central Repository via Sonatype.

## Overview

Gradle Kit provides four convention plugins designed to simplify CI/CD workflows for Java projects. These plugins automatically configure standard Gradle functionality with sensible defaults while allowing customization through environment variables, system properties, or project properties.

**Key Features:**
- Automatic configuration of Java compiler versions and toolchains
- Integrated code quality tools (JaCoCo, SpotBugs, Javadoc)
- Simplified Maven Central publishing via Sonatype's new API
- In-memory GPG signing for secure CI/CD pipelines
- CI/CD optimized with GitHub Actions support
- Flexible configuration via environment variables or project properties

## Plugins

<details markdown="1"><summary>java-kit</summary>

### Java Kit Plugin

Extends Gradle's standard `java` plugin with automatic configuration for modern Java projects.

**Features:**
* Automatically applies the `java` plugin
* Configures Java compiler, source, and target versions (default: Java 17)
* Integrates JaCoCo for code coverage reporting
* Adds SpotBugs for static analysis
* Configures Javadoc generation with modern styling
* Supports version tagging for releases
* Supports environment variable, system property, and project property overrides

**Usage:**
```groovy
plugins {
    id 'io.github.jonloucks.java-kit' version '1.2.2'
}
```

</details>

<details markdown="1"><summary>java-library-kit</summary>

### Java Library Kit Plugin

Extends Gradle's `java-library` plugin with the same automatic configuration as java-kit, optimized for library projects.

**Features:**
* Automatically applies the `java-library` plugin
* Configures Java compiler, source, and target versions (default: Java 17)
* Integrates JaCoCo for code coverage reporting
* Adds SpotBugs for static analysis
* Configures Javadoc generation with modern styling
* Supports version tagging for releases
* Supports environment variable, system property, and project property overrides

**Usage:**
```groovy
plugins {
    id 'io.github.jonloucks.java-library-kit' version '1.2.2'
}
```

</details>

<details markdown="1"><summary>maven-publish-kit</summary>

### Maven Publish Kit Plugin

Simplifies publishing to Maven Central Repository via Sonatype's new Central Publishing API.

**Features:**
* Automatically applies the `maven-publish` plugin
* Creates staging repositories for deployments
* Generates publication bundles for Sonatype Central
* Uploads bundles via REST API to `https://central.sonatype.com/publishing`
* Configures checksums and metadata
* Supports environment variable, system property, and project property overrides

**Usage:**
```groovy
plugins {
    id 'io.github.jonloucks.maven-publish-kit' version '1.2.2'
}
```

**Tasks:**
* `createPublisherBundle` - Creates a ZIP bundle of all publications
* `uploadPublisherBundle` - Uploads the bundle to Sonatype Central

</details>

<details markdown="1"><summary>signing-kit</summary>

### Signing Kit Plugin

Handles GPG signing for Maven publications using in-memory keys, ideal for CI/CD environments.

**Features:**
* Automatically applies the `signing` plugin
* Uses in-memory PGP keys for secure signing in CI/CD
* No need to store keyring files on build servers
* Supports environment variable, system property, and project property overrides

**Usage:**
```groovy
plugins {
    id 'io.github.jonloucks.signing-kit' version '1.2.2'
}
```

**Configuration:**
Provide GPG credentials via environment variables:
* `KIT_GPG_SECRET_KEY` - ASCII-armored private key
* `KIT_GPG_SECRET_KEY_PASSWORD` - Key password

</details>

## Quick Start

### For Application Projects
```groovy
plugins {
    id 'io.github.jonloucks.java-kit' version '1.2.2'
    id 'io.github.jonloucks.maven-publish-kit' version '1.2.2'
    id 'io.github.jonloucks.signing-kit' version '1.2.2'
}
```

### For Library Projects
```groovy
plugins {
    id 'io.github.jonloucks.java-library-kit' version '1.2.2'
    id 'io.github.jonloucks.maven-publish-kit' version '1.2.2'
    id 'io.github.jonloucks.signing-kit' version '1.2.2'
}
```

### CI/CD Example (GitHub Actions)
```yaml
- name: Build and Publish
  run: ./gradlew check jacocoTestReport publish createPublisherBundle uploadPublisherBundle
  env:
    KIT_PROJECT_WORKFLOW: 'main-release'
    KIT_GPG_SECRET_KEY: ${{ secrets.GPG_SECRET_KEY }}
    KIT_GPG_SECRET_KEY_PASSWORD: ${{ secrets.GPG_SECRET_KEY_PASSWORD }}
    KIT_OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
    KIT_OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
```


* [License](LICENSE.md)
* [Contributing](CONTRIBUTING.md)
* [Code of conduct](CODE_OF_CONDUCT.md)
* [Coding standards](CODING_STANDARDS.md)
* [Security policy](SECURITY.md)
* [Pull request template](PULL_REQUEST_TEMPLATE.md)
* [Variables](VARIABLES.md)
* [How to use Java API](https://jonloucks.github.io/gradle-kit/javadoc/)
* [Java test coverage report](https://jonloucks.github.io/gradle-kit/jacoco/)

## Badges
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/11551/badge)](https://www.bestpractices.dev/projects/11551)
[![Coverage Badge](https://raw.githubusercontent.com/jonloucks/gradle-kit/refs/heads/badges/main-coverage.svg)](https://jonloucks.github.io/gradle-kit/jacoco/)
[![Javadoc Badge](https://raw.githubusercontent.com/jonloucks/gradle-kit/refs/heads/badges/main-javadoc.svg)](https://jonloucks.github.io/gradle-kit/javadoc/)


