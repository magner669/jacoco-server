
# JaCocCo Server

Collect code coverage during integration tests, and make it available to the build agent so they can be merged with
unit test code coverage.

Table of contents:
- [What is JaCoCo?](#what-is-jacoco)
- [Why JaCoCo Server?](#why-jacoco-server)
- [Requirements](#requirements)
- [Usage](#usage)

## What is JaCoCo? <a name="what-is-jacoco"></a>

[JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/index.html) is a free Java code coverage library distributed under the
Eclipse Public License. It is the de-facto standard for code coverage in Java projects.
It provides tools to measure and report code coverage, which helps developers verify that their tests are exercising
the code as expected.

## Why JaCoCo Server? <a name="why-jacoco-server"></a>

Consider a system composed of a set of Java microservices running in Kubernetes.

The motivation for this project is to improve system reliability, reduce cost of ownership, and improve time to
market. It aims to do this by making it worthwhile and cheaper to develop integration tests.

Most organizations require a certain level of code coverage to be achieved by unit tests, which is usually measured by
tools like JaCoCo. Critical systems often require near 100% code coverage.

Integration tests are much better for ensuring that the system works as a whole, but they are also much more expensive
to write and maintain, and _they produce no code coverage_.

JaCoCo Server allows collecting code coverage from integration tests, which can then be merged with the unit test
code coverage. This way, a higher code coverage percentage can be archived without having to write a lot of unit tests.
This is compatible with SDLC that uses GitHub/BitBucket, and [SonarQube](https://www.sonarsource.com/) to process the
code coverage data, and provide code quality reports.

Typical use cases for JaCoCo Server include:
* Lots of unit tests and some integration tests, leading to higher effort due to duplication,and higher cost of
  ownership.
* Lots of unit tests tightly coupled to the implementation, that often need to be updated when the implementation
  changes, resulting is high cost of ownership, and slower time to market.
* Few or no unit tests, but some integration tests. Analyzing code coverage from the integration tests will help
  finding the gaps.
* Lots of unit tests and no integration tests, due to their relative high cost. Leading to concern before release that
  the system might not work as expected. Will the process even start? Is each microservice compatible with the others?

## Requirements <a name="requirements"></a>

* Java 22 for JaCoCo server, test processes can use any Java version compatible with JaCoCo.
* The [JaCoCo agent jar file](https://mvnrepository.com/artifact/org.jacoco/org.jacoco.agent) (any version would do), 
  must be bundled with the processes under test.
* JaCoCo server running, preferably in the same Kubernetes cluster as the processes under test. The server
  should be run as a service with a stable hostname, so that the test processes can connect to it. An ingress must set
  up to download the code coverage reports.

## Usage <a name="usage"></a>

### Configuring the JaCoCo agent

Each process under test must be started with the [JaCoCo agent](https://www.eclemma.org/jacoco/trunk/doc/agent.html) 
enabled and configured to emit code coverage data to the JaCoCo server. 
This is done by adding the following JVM argument to the command line when starting the process:
```
java -javaagent:[yourpath/]jacocoagent.jar=output=tcpclient,address=[yourhostname],sessionid=[yoursessionid]
```

Where:
* `yourpath` is the path to the jacoco agent jar file.
* `yourhostname` is the hostname for JaCoCo server.
* `yoursessionid` is the session id to use, must be the same for all the java processes. For example, 
   the build ID or a random UUID generated at the start of the build.
