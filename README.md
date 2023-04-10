# Injest Core

This project is dead. Dead as disco. Dead as Kevin Spacey's career.

The latest "release" failed to build because of a Java version incompatibility that kind of served as the last straw. A misconfiguration? Sure, but also triggered a cascade of required dependency updates that frankly, we were not willing invest time on, since we were migrating away from Java.

**Do not clone, or use this library**. It is officially deprecated, not maintained and never will be. This repository will be archived on August 1, 2023.

## Why is this library hosted in a public repo?

Simply put, so we could use [jitpack.io](jitpack.io) for free, so we could install as a normal remote dependency with Maven and not as a locally-stored JAR.

## What is this library, anyway?

This project was conceived as a way to avoid using the Spring Boot framework for a rather monolithic API for a small company. Spring, as all-encompassing as it is, imposed a **huge** memory footprint of around 2G, with both it's Apache Tomcat HTTP container and **loads** of dependencies, only a small subset of which we actually used.

So we decided to slim down and write a small wrapper around the Undertow/XNIO library. Once built, the same API that consumed nearly 2G of memory was reduced to under 200M. Great Success.

This was in 2017. In the intervening years, maintaining this library became very high maintenance. Constant security patches for the Jackson library (It was _always_ Jackson) made it very annoying. As time marched on, we gradually moved services to cloud providers, and edge networks. Here came the issues. With Java's terrible concurrency model, it became more and more troublesome to maintain our Java-based applications. We gradually began re-writing services in other languages that were more cloud-friendly. 

Our company grew, and we began hiring more junior developers. We didn't have the capital to hire more expensive Java engineers, and we did not want the hassle of outsourcing work to India. So we just gave up on Java, and hence, this project is no longer worth the hassle.

So it's officially dead, deprecated, whatever term you prefer.

