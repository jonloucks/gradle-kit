a# Gradle Kit 
Gradle plugins for building and publishing Java projects to Maven Central Repository.

## Overview
CI/CD For Java projects GitHub and publishing to Maven Central.
Either Environment variable overrides for standard plugins.

### java-kit 
* Automatically applies 'java' plugin
* Changes default Java language levels
* Provides Environment, System, and project overriding
```
plugins {
    id 'io.github.jonloucks.java-kit' version '1.2.1'
}
```

### java-library-kit
* Automatically applies 'java-library' plugin
* Changes default Java language levels
* Provides Environment, System, and project overriding
```
plugins {
    id 'io.github.jonloucks.java-library-kit' version '1.2.1'
}
```

### maven-publish-kit
* Automatically applies 'publish' plugin
* Provides Environment, System, and project overriding
```
plugins {
    id 'io.github.jonloucks.maven-publish-kit' version '1.2.1'
}
```

### signing-kit
* Automatically applies 'signing' plugin
* Provides Environment, System, and project overriding
```
plugins {
    id 'io.github.jonloucks.signing-kit' version '1.2.1'
}
```

## Defaults and Overrides
Note: Both Environment and Gradle properties can be used as a System variable.
Note: defaults beginning with * denote a link to another property value if not set.

| Name                              | Environment                       | Gradle                            |                                 Default |
|:----------------------------------|:----------------------------------|:----------------------------------|----------------------------------------:|
| Kit Log Enabled                   | KIT_LOG_ENABLED                   | kit.log.enabled                   |                                   false |
| Kit Project Workflow              | KIT_PROJECT_WORKFLOW              | kit.project.workflow              |                                 unknown |
| Kit Java Compiler Version         | KIT_JAVA_COMPILER_VERSION         | kit.java.compiler.version         |                                      17 |
| Kit Java Source Version           | KIT_JAVA_SOURCE_VERSION           | kit.java.source.version           |                                       9 |
| Kit Java Target Version           | KIT_JAVA_TARGET_VERSION           | kit.java.target.version           |                *KIT_JAVA_SOURCE_VERSION |
| Kit Java Test Source Version      | KIT_JAVA_TEST_SOURCE_VERSION      | kit.java.test.source.version      |                *KIT_JAVA_SOURCE_VERSION |
| Kit Java Test Target Version      | KIT_JAVA_TEST_TARGET_VERSION      | kit.java.test.target.version      |           *KIT_JAVA_TEST_SOURCE_VERSION |
| Kit OSSRH URL                     | KIT_OSSRH_URL                     | kit.ossrh.url                     |                Maven Central Repository |
| Kit OSSRH User Login Name         | KIT_OSSRH_USERNAME                | kit.ossrh.username                |                                         |
| Kit OSSRH Password                | KIT_OSSRH_PASSWORD                | kit.ossrh.password                |                                         |
| Kit OSSRH GPG Secret Key          | KIT_OSSRH_GPG_SECRET_KEY          | kit.ossrh.gpg.secret.key          |                                         |
| Kit OSSRH GPG Secret Key Password | KIT_OSSRH_GPG_SECRET_KEY_PASSWORD | kit.ossrh.gpg.secret.key.password |                                         |
| Kit Java Test Include Tags        | KIT_INCLUDE_TAGS                  | kit.include.tags                  |                                         |
| Kit Java Test Exclude Tags        | KIT_EXCLUDE_TAGS                  | kit.exclude.tags                  | unstable, slow, integration, functional |
| Kit Java Integration Exclude Tags | KIT_INTEGRATION_EXCLUDE_TAGS      | kit.integration.exclude.tags      |              unstable, slow, functional |
| Kit Java Functional Exclude Tags  | KIT_FUNCTIONAL_EXCLUDE_TAGS       | kit.functional.exclude.tags       |             unstable, slow, integration |

### Maven Central Repository
* https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED

## Documentation
* [License](LICENSE.md)
* [Contributing](CONTRIBUTING.md)
* [Code of conduct](CODE_OF_CONDUCT.md)
* [Coding standards](CODING_STANDARDS.md)
* [Security policy](SECURITY.md)
* [Pull request template](PULL_REQUEST_TEMPLATE.md)
* [How to use Java API](https://jonloucks.github.io/gradle-kit/javadoc/)
* [Java test coverage report](https://jonloucks.github.io/gradle-kit/jacoco/)

## Badges
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/11551/badge)](https://www.bestpractices.dev/projects/11551)
[![Coverage Badge](https://raw.githubusercontent.com/jonloucks/gradle-kit/refs/heads/badges/main-coverage.svg)](https://jonloucks.github.io/gradle-kit/jacoco/)
[![Javadoc Badge](https://raw.githubusercontent.com/jonloucks/gradle-kit/refs/heads/badges/main-javadoc.svg)](https://jonloucks.github.io/gradle-kit/javadoc/)


