# Spring Cookie
*- Come to the dark side, we have cookies*

[![license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0) [![Build Status](https://travis-ci.org/innoq/spring-cookie.svg?branch=master)](https://travis-ci.org/innoq/spring-cookie)

Some components for
[Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)
that use cookies instead of a HTTP session.


## Features

* `com.innoq.spring.cookie.flash.CookieFLashMapManager` for `FlashMap` support


## Quick Start

Download the jar through Maven:

```xml
<dependency>
  <groupId>com.innoq</groupId>
  <artifactId>spring-cookie</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

Because there is no stable version in maven central right now you need to
configure Sonatype's OSS Nexus as snapshot repository.

```xml
<repository>
  <id>ossrh</id>
  <name>Sonatype OSS Snapshot Repository</name>
  <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>
```


## License

Spring Cookie is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

