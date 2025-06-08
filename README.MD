[![codecov](https://codecov.io/gh/magner669/jacoco-server/branch/main/graph/badge.svg)](https://codecov.io/gh/magner669/jacoco-server)
![example workflow](https://github.com/magner669/jacoco-server/actions/workflows/gradle.yml/badge.svg)

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Markdown](https://img.shields.io/badge/markdown-%23000000.svg?style=for-the-badge&logo=markdown&logoColor=white)


# JaCoCo Server
Table of contents:
- [What is JaCoCo?](#what-is-jacoco)
- [Why JaCoCo Server?](#why-jacoco-server)
- [Requirements](#requirements)
- [Usage](#usage)

## What is JaCoCo? <a name="what-is-jacoco"></a>

[JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/index.html) is a free Java code coverage library. It is the de-facto standard for code coverage in Java.
It provides tools to measure and report code coverage, which help developers verify that their tests are exercising the code as expected.

## Why JaCoCo Server? <a name="why-jacoco-server"></a>

Integration tests are much better than unit tests for ensuring that the
system works as a whole, but they normally _produce no code coverage_.

JaCoCo Server allows collecting code coverage emitted by Java processes while 
they are being tested by integration tests. The code coverage data can be used
to produce code coverage reports.

Code coverage reports can be used to verify that a change has been tested, and
to find gaps in testing.

Typical use cases for JaCoCo server include:
* Failures in production resulting from gaps in integration test coverage.
* Duplication of coverage by unit tests, and integration tests.
* A requirement to maintain high code coverage, which results in lots of unit
  tests tightly coupled to the implementation, making refactoring the 
  implementation much more difficult.

## Requirements <a name="requirements"></a>

* The [JaCoCo agent jar file](https://mvnrepository.com/artifact/org.jacoco/org.jacoco.agent)
  (any version would do), must be bundled with the processes under test, 
  and the command line of Java process changed to enable the JaCoCo agent.
* JaCoCo server running. Processes under test can connect to it using TCP,
  and users can download code coverage data.

## Usage <a name="usage"></a>

### Configuring the JaCoCo agent

Each process under test must be started with the [JaCoCo agent](https://www.eclemma.org/jacoco/trunk/doc/agent.html) 
enabled and configured to emit code coverage data to the JaCoCo server:
```bash
java -javaagent:[yourpath/]jacocoagent.jar=output=tcpclient,address=[yourhostname],sessionid=[yoursessionid] ...
```

Where:
* `yourpath` is the path to the jacoco agent jar file.
* `yourhostname` is the hostname for JaCoCo server.
* `yoursessionid` is the session id to use, must be the same for all the java processes. For example, 
   the build ID or a random UUID generated at the start of the build.

It should only be configured when running integration tests. It can be conditionally be configured using
[helm](https://helm.sh/), [Jinga](https://jinja.palletsprojects.com/en/stable/), etc.

An example pod running a process under test `myapp.jar`, where `jacocoagent.jar` is included in the container image,
and the JaCoCo server running behind the hostname `jacoco-server.default.svc.cluster.local`:
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-java-app
spec:
  containers:
    - name: java-app
      image: my-java-app:latest
      command: ["java"]
      args:
        - "-javaagent:/app/jacocoagent.jar=output=tcpclient,address=jacoco-server.default.svc.cluster.local,sessionid=my-build-id-123"
        - "-jar"
        - "/app/myapp.jar"
```
