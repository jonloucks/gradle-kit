# Gradle Kit Variables

<details>
    <summary>
    Enable Logging
    </summary>

#### Responsibility
Enable logging to provide more visibility.

#### Names
```
KIT_LOG_ENABLED
kit.log.enabled
```

#### Valid Values
* true
* false
#### Default Value
```
false
```

</details>

<details>
    <summary>
    Project Workflow
    </summary>

#### Responsibility
Declare CI/CD intent.

#### Names
```
KIT_PROJECT_WORKFLOW
kit.project.workflow 
```

#### Valid Values
A simple which can include dashes.

> [!NOTE]
> KIT_OSSRH_AUTHOR defaults to this value which is used in publishing

| Value             | Meaning                                      |
|:------------------|:---------------------------------------------|
| main-release      | GitHub release workflow                      |
| developer-release | Build and publish locally                    |
| main-pull-request | Github Pull Request Workflow                 |

#### Default Value
```
unknown
```

#### Example 
* GitHub release workflow yaml fragment 
``` 
  - name: Publish to GitHub Packages
    run: ./gradlew publish createPublisherBundle uploadPublisherBundle
    env:
      PROJECT_WORKFLOW: 'main-release'
```

</details>

<details>
    <summary>
    Java Compiler Version
    </summary>

#### Responsibility
Select the Java compiler version.

#### Names
```
KIT_JAVA_COMPILER_VERSION
kit.java.compiler.version 
```

#### Valid Values
A single number

#### Default Value
```
17
```

#### Example
* GitHub release workflow yaml fragment
``` 
    - name: Build with Gradle with Java compiler ${{ matrix.java-compiler-version }}
      working-directory: main-project
      run: ./gradlew check jacocoTestReport jacocoTestCoverageVerification spotbugsMain
      env:
        PROJECT_WORKFLOW: 'main-pull-request-matrix'
        KIT_JAVA_COMPILER_VERSION: ${{ matrix.java-compiler-version }}

```

</details>

<details>
    <summary>
    Java Source Version 
    </summary>

#### Responsibility
Select the Java source version

#### Names
```
KIT_JAVA_SOURCE_VERSION
kit.java.source.version 
```

#### Valid Values
a single number 

#### Default Value
```
9
```

#### Example
* GitHub release workflow yaml fragment
``` 
    - name: Build with Gradle with Java source ${{ matrix.java-source-version }}
      working-directory: main-project
      run: ./gradlew check jacocoTestReport jacocoTestCoverageVerification spotbugsMain
      env:
        PROJECT_WORKFLOW: 'main-pull-request-matrix'
        KIT_JAVA_SOURCE_VERSION: ${{ matrix.java-source-version }}
```

</details>

<details>
    <summary>
    Java Target Version 
    </summary>

#### Responsibility
Select the Java target version

#### Names
```
KIT_JAVA_TARGET_VERSION
kit.java.target.version 
```

#### Valid Values
a single number

#### Default Value
```
The runtime value of variable KIT_JAVA_SOURCE_VERSION
```

#### Example
* GitHub release workflow yaml fragment
``` 
    - name: Build with Gradle with Java target ${{ matrix.java-target-version }}
      working-directory: main-project
      run: ./gradlew check jacocoTestReport jacocoTestCoverageVerification spotbugsMain
      env:
        PROJECT_WORKFLOW: 'main-pull-request-matrix'
        KIT_JAVA_TARGET_VERSION: ${{ matrix.java-target-version }}
```

</details>

<details>
    <summary>
    Java Test Source Version 
    </summary>

#### Responsibility
Select the Java Test source version

#### Names
```
KIT_JAVA_TEST_SOURCE_VERSION
kit.java.test.source.version 
```

#### Valid Values
a single number

#### Default Value
```
9
```

#### Example
* GitHub release workflow yaml fragment
``` 
    - name: Build with Gradle with Java test source ${{ matrix.java-test-source-version }}
      working-directory: main-project
      run: ./gradlew check jacocoTestReport jacocoTestCoverageVerification spotbugsMain
      env:
        PROJECT_WORKFLOW: 'main-pull-request-matrix'
        KIT_JAVA_TEST_SOURCE_VERSION: ${{ matrix.java-test-source-version }}
```

</details>

<details>
    <summary>
    Java Test Target Version 
    </summary>

#### Responsibility
Select the Java test target version

#### Names
```
KIT_JAVA_TEST_TARGET_VERSION
kit.java.test.target.version 
```

#### Valid Values
a single number

#### Default Value
```
The runtime value of variable KIT_JAVA_TEST_SOURCE_VERSION
```

#### Example
* GitHub release workflow yaml fragment
``` 
    - name: Build with Gradle with Java test target ${{ matrix.java-target-target-version }}
      working-directory: main-project
      run: ./gradlew check jacocoTestReport jacocoTestCoverageVerification spotbugsMain
      env:
        PROJECT_WORKFLOW: 'main-pull-request-matrix'
        KIT_JAVA_TEST_TARGET_VERSION: ${{ matrix.java-test-target-version }}
```

</details>

<details>
    <summary>
    OSSRH URL
    </summary>

#### Responsibility
Define the URL to publish an OSSRH bundle.

#### Names
```
KIT_OSSRH_URL
kit.ossrh.url 
```

#### Valid Values
a valid url to Maven Central Repository Upload API

#### Default Value
```
https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED
```

#### Example
```
Do not put credentials in your URL, there are variables for that
```

</details>

<details>
    <summary>
    OSSRH Author
    </summary>

#### Responsibility
Define the author to publish an OSSRH bundle.

#### Names
```
KIT_OSSRH_AUTHOR
kit.ossrh.author
```

#### Valid Values
A text value that is valid as part of a Maven Central Repository bundle name.

#### Default Value
```
The runtime value of variable KIT_PROJECT_WORKFLOW
```

#### Example
```
  - name: Publish to GitHub Packages
    working-directory: main-project
    run: ./gradlew publish createPublisherBundle uploadPublisherBundle
    env:
      PROJECT_WORKFLOW: 'main-release'
      USERNAME: ${{ github.actor }}
      TOKEN: ${{ secrets.GITHUB_TOKEN }}
      KIT_JAVA_COMPILER_VERSION: ${{ matrix.java-compiler-version }}
      KIT_JAVA_SOURCE_VERSION: ${{ matrix.java-source-version }}
      KIT_OSSRH_GPG_SECRET_KEY: ${{ secrets.OSSRH_GPG_SECRET_KEY }}
      KIT_OSSRH_GPG_SECRET_KEY_PASSWORD: ${{ secrets.OSSRH_GPG_SECRET_KEY_PASSWORD }}
      KIT_OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
      KIT_OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
      KIT_OSSRH_AUTHOR: 'official-release'
```

</details>

<details>
    <summary>
    OSSRH User Name
    </summary>

#### Responsibility
Define the username to publish an OSSRH bundle.

#### Names
```
KIT_OSSRH_USERNAME
OSSRH_USERNAME 
kit.ossrh.username
```

#### Valid Values
A non-empty value

#### Default Value
```
```

#### Example
* GitHub release workflow yaml fragment
```
  - name: Publish to GitHub Packages
    working-directory: main-project
    run: ./gradlew publish createPublisherBundle uploadPublisherBundle
    env:
      PROJECT_WORKFLOW: 'main-release'
      USERNAME: ${{ github.actor }}
      TOKEN: ${{ secrets.GITHUB_TOKEN }}
      KIT_JAVA_COMPILER_VERSION: ${{ matrix.java-compiler-version }}
      KIT_JAVA_SOURCE_VERSION: ${{ matrix.java-source-version }}
      KIT_OSSRH_GPG_SECRET_KEY: ${{ secrets.OSSRH_GPG_SECRET_KEY }}
      KIT_OSSRH_GPG_SECRET_KEY_PASSWORD: ${{ secrets.OSSRH_GPG_SECRET_KEY_PASSWORD }}
      KIT_OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
      KIT_OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
```

</details>

<details>
    <summary>
    OSSRH User Password
    </summary>

#### Responsibility
Define the password to publish an OSSRH bundle.

#### Names
```
KIT_OSSRH_PASSWORD
OSSRH_PASSWORD 
kit.ossrh.password
```

#### Valid Values
A non-empty value

#### Default Value
```
```

#### Example
* GitHub release workflow yaml fragment
```
  - name: Publish to GitHub Packages
    working-directory: main-project
    run: ./gradlew publish createPublisherBundle uploadPublisherBundle
    env:
      PROJECT_WORKFLOW: 'main-release'
      USERNAME: ${{ github.actor }}
      TOKEN: ${{ secrets.GITHUB_TOKEN }}
      KIT_JAVA_COMPILER_VERSION: ${{ matrix.java-compiler-version }}
      KIT_JAVA_SOURCE_VERSION: ${{ matrix.java-source-version }}
      KIT_OSSRH_GPG_SECRET_KEY: ${{ secrets.OSSRH_GPG_SECRET_KEY }}
      KIT_OSSRH_GPG_SECRET_KEY_PASSWORD: ${{ secrets.OSSRH_GPG_SECRET_KEY_PASSWORD }}
      KIT_OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
      KIT_OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
```

</details>

<details>
    <summary>
    Signing GPG Secret Key
    </summary>

#### Responsibility
Select the GPG secret key for signing.

#### Names
```
KIT_OSSRH_GPG_SECRET_KEY
OSSRH_GPG_SECRET_KEY
kit.ossrh.gpg.secret.key
```

#### Valid Values
A non-empty value

#### Default Value
```
```

#### Example
* GitHub release workflow yaml fragment
```
  - name: Publish to GitHub Packages
    working-directory: main-project
    run: ./gradlew publish createPublisherBundle uploadPublisherBundle
    env:
      PROJECT_WORKFLOW: 'main-release'
      USERNAME: ${{ github.actor }}
      TOKEN: ${{ secrets.GITHUB_TOKEN }}
      KIT_JAVA_COMPILER_VERSION: ${{ matrix.java-compiler-version }}
      KIT_JAVA_SOURCE_VERSION: ${{ matrix.java-source-version }}
      KIT_OSSRH_GPG_SECRET_KEY: ${{ secrets.OSSRH_GPG_SECRET_KEY }}
      KIT_OSSRH_GPG_SECRET_KEY_PASSWORD: ${{ secrets.OSSRH_GPG_SECRET_KEY_PASSWORD }}
      KIT_OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
      KIT_OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
```

</details>

<details>
    <summary>
    Signing GPG Secret Key Password
    </summary>

#### Responsibility
Select the GPG secret key password for signing.

#### Names
```
KIT_OSSRH_GPG_SECRET_KEY_PASSWORD
OSSRH_GPG_SECRET_KEY_PASSWORD
kit.ossrh.gpg.secret.key.password
```

#### Valid Values
A non-empty value

#### Default Value
```
```

#### Example
* GitHub release workflow yaml fragment
```
  - name: Publish to GitHub Packages
    working-directory: main-project
    run: ./gradlew publish createPublisherBundle uploadPublisherBundle
    env:
      PROJECT_WORKFLOW: 'main-release'
      USERNAME: ${{ github.actor }}
      TOKEN: ${{ secrets.GITHUB_TOKEN }}
      KIT_JAVA_COMPILER_VERSION: ${{ matrix.java-compiler-version }}
      KIT_JAVA_SOURCE_VERSION: ${{ matrix.java-source-version }}
      KIT_OSSRH_GPG_SECRET_KEY: ${{ secrets.OSSRH_GPG_SECRET_KEY }}
      KIT_OSSRH_GPG_SECRET_KEY_PASSWORD: ${{ secrets.OSSRH_GPG_SECRET_KEY_PASSWORD }}
      KIT_OSSRH_USERNAME: ${{ secrets.OSSRH_USERNAME }}
      KIT_OSSRH_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
```

</details>


