# Spring Cookie
*- Come to the dark side, we have cookies*

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.innoq/spring-cookie/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.innoq/spring-cookie)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Open Issues](https://img.shields.io/github/issues/innoq/spring-cookie.svg)](https://github.com/innoq/spring-cookie/issues)
[![Build Status](https://github.com/innoq/spring-cookie/actions/workflows/main.yml/badge.svg)](https://github.com/innoq/spring-cookie/actions/workflows/main.yml)
[![Code Coverage](https://codecov.io/gh/innoq/spring-cookie/branch/main/graph/badge.svg)](ht tps://codecov.io/gh/innoq/spring-cookie)

Some components for
[Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)
that use cookies instead of a HTTP session.


## Features

- [CookieFlashMapManager](./src/main/java/com/innoq/spring/cookie/flash/CookieFlashMapManager.java)

  A cookie based [FlashMapManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/FlashMapManager.html)
  implementation that can be used with different
  [serialization](./src/main/java/com/innoq/spring/cookie/flash/FlashMapListCodec.java)
  and [signing](./src/main/java/com/innoq/spring/cookie/security/CookieValueSigner.java)
  implementations.

  By default a [Jackson](https://github.com/FasterXML/jackson) based
  [JSON implementation](./src/main/java/com/innoq/spring/cookie/flash/codec/jackson/JacksonFlashMapListCodec.java)
  for serialization and
  [HMAC implementation](./src/main/java/com/innoq/spring/cookie/security/HmacCookieValueSigner.java)
  for signing is provided.


## Quick Start

Download the jar through Maven:

```xml
<dependency>
  <groupId>com.innoq</groupId>
  <artifactId>spring-cookie</artifactId>
  <version>0.1.0</version>
</dependency>
```


## Release History

See [CHANGELOG.md](./CHANGELOG.md)


## Code of Conduct

[Contributor Code of Conduct](./CODE_OF_CONDUCT.md). By participating in this
project you agree to abide by its terms.


## License

Spring Cookie is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

