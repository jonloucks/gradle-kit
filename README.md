# Gradle Kit 
Gradle plugins for building and publishing Java projects to Maven Central Repository.

## Overview
CI/CD For Java projects GitHub and publishing to Maven Central.
Either Environment variable overrides for standard plugins.

<details>
    <summary>
    java-kit
    </summary>

* Automatically applies 'java' plugin
* Changes default Java language levels
* Supports Environment, System, and project overriding

```
plugins {
    id 'io.github.jonloucks.java-kit' version '1.2.1'
}
```
</details>

<details>
    <summary>
    java-library-kit
    </summary>

* Automatically applies 'java-library' plugin
* Changes default Java language levels
* Supports Environment, System, and project overriding

```
plugins {
    id 'io.github.jonloucks.java-library-kit' version '1.2.1'
}
```
</details>

<details>
    <summary>
    maven-publish-kit
    </summary>

* Automatically applies 'publish' plugin
* Supports Environment, System, and project overriding

```
plugins {
    id 'io.github.jonloucks.maven-publish-kit' version '1.2.1'
}
```
</details>

<details>
    <summary>
    signing-kit
    </summary>

* Automatically applies 'signing' plugin
* Supports Environment, System, and project overriding

```
plugins {
    id 'io.github.jonloucks.signing-kit' version '1.2.1'
}
```
</details>

## Documentation
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


