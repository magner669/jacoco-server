# JaCocCo Server

Collect code coverage during integration tests and make it available to the build agent to be merged with
unit test code coverage.

## Motivation

The motivation for this project is to make it cheaper to develop integration tests for a set of microservices
running in Kubernetes.

Integration tests are much better for ensuring that the system works as a whole, but they are also much more expensive
to write and maintain.

This project allows us to collect code coverage from integration tests, thus you don't need to write unit tests,
thus making a time budget available to invest in integration tests.

You can still write unit tests, but you don't have to just in order to get decent code coverage.

## Requirements

* Java 22 for JaCoCo server, test processes can use any Java version compatible with JaCoCo.
* The JaCoCo agent jar file, must be available with each of the processes under test.
* A JaCoCo server running, preferably in the same Kubernetes cluster as the processes under test.

## Usage

Each test process must be started with the JaCoCo agent enabled. This is done by adding the following JVM argument:
 ```
java -javaagent:[yourpath/]jacocoagent.jar=output=tcpclient,address=[yourhostname],sessionid=[yoursessionid]
```

Where:
* `yourpath` is the path to the jacoco agent jar file.
* `yourhostname` is the hostname for JaCoCo server.
* `yoursessionid` is the session Id to use, which should be the same for all the java processes. For example, 
   the build id or a random UUID.
