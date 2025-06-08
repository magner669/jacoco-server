[![codecov](https://codecov.io/gh/magner669/jacoco-server/branch/main/graph/badge.svg)](https://codecov.io/gh/magner669/jacoco-server)
# JaCocCo Server

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
* A repository required to maintain high code coverage, thus has many unit
  tests tightly coupled to the implementation, making refactoring the 
  implementation much more difficult.

## Requirements <a name="requirements"></a>

* Java 22 for JaCoCo server, test processes can use any Java version compatible with JaCoCo.
* The [JaCoCo agent jar file](https://mvnrepository.com/artifact/org.jacoco/org.jacoco.agent)
  (any version would do), must be bundled with the processes under test, 
  and the command line of Java process changed to enable the JaCoCo agent.
* JaCoCo server running. Processes under test can connect to it using TCP,
  and users can download code coverage data.

## Usage <a name="usage"></a>

### Configuring the JaCoCo agent

Each process under test must be started with the [JaCoCo agent](https://www.eclemma.org/jacoco/trunk/doc/agent.html) 
enabled and configured to emit code coverage data to the JaCoCo server.

It should only be configured when running integration tests. It can be conditionally be configured using
[helm](https://helm.sh/).

This is done by adding the following JVM argument to the command line when starting the process:
```
java -javaagent:[yourpath/]jacocoagent.jar=output=tcpclient,address=[yourhostname],sessionid=[yoursessionid]
```

Where:
* `yourpath` is the path to the jacoco agent jar file.
* `yourhostname` is the hostname for JaCoCo server.
* `yoursessionid` is the session id to use, must be the same for all the java processes. For example, 
   the build ID or a random UUID generated at the start of the build.

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
