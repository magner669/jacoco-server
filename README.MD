# JaCocCo Server

Collect code coverage during integration tests, and make it available to the build agent to be merged with
unit test code coverage.

## What is JaCoCo?

[JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/index.html) is a free Java code coverage library distributed under the
Eclipse Public License. It is the de-facto standard for code coverage in Java projects.
It provides tools to measure and report code coverage, which helps developers verify that their tests are exercising
the code as expected.

## Why JaCoCo Server?

The motivation for this project is to make it cheaper to develop integration tests for a set of microservices
running in Kubernetes.

Most organizations require a certain level of code coverage to be achieved by unit tests, which is usually measured by
tools like JaCoCo.

Integration tests are much better for ensuring that the system works as a whole, but they are also much more expensive
to write and maintain, and they produce no code coverage.

JaCoCo Server allows you to collect code coverage from integration tests, which can then be merged with the unit test
code coverage. This way, you can achieve a higher code coverage percentage without having to write a lot of unit tests.

## Requirements

* Java 22 for JaCoCo server, test processes can use any Java version compatible with JaCoCo.
* The JaCoCo agent jar file (any version would do), must be available to each of the processes under test.
* A JaCoCo server running, preferably in the same Kubernetes cluster as the processes under test. The server
  should be run as a service with a stable hostname, so that the test processes can connect to it.

## Usage

Each test process must be started with the JaCoCo agent enabled and configured to emit code coverage data
to JaCoCo server. This is done by adding the following JVM argument to the command line when starting the process:
```
java -javaagent:[yourpath/]jacocoagent.jar=output=tcpclient,address=[yourhostname],sessionid=[yoursessionid]
```

Where:
* `yourpath` is the path to the jacoco agent jar file.
* `yourhostname` is the hostname for JaCoCo server.
* `yoursessionid` is the session id to use, must be the same for all the java processes. For example, 
   the build id or a random UUID generated at the start of the build.