# Spring Cookie
*- Come to the dark side, we have cookies*

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.innoq/spring-cookie/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.innoq/spring-cookie)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Open Issues](https://img.shields.io/github/issues/innoq/spring-cookie.svg)](https://github.com/innoq/spring-cookie/issues)
[![Build Status](https://github.com/innoq/spring-cookie/actions/workflows/main.yml/badge.svg)](https://github.com/innoq/spring-cookie/actions/workflows/main.yml)
[![Code Coverage](https://codecov.io/gh/innoq/spring-cookie/branch/main/graph/badge.svg)](https://codecov.io/gh/innoq/spring-cookie)

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

Download library through Maven:

```xml
<dependency>
  <groupId>com.innoq</groupId>
  <artifactId>spring-cookie</artifactId>
  <version>1.1.0</version>
</dependency>
```

### 1. Register as a Spring Bean

To enable cookie-based Flash attributes, register the `CookieFlashMapManager` as a Spring `@Bean`. You can customize the codec and signing mechanism:

```java
@Configuration
public class FlashAttributeStrategy {

  @Bean
  public CookieFlashMapManager cookieFlashMapManager() {
    return new CookieFlashMapManager(
      JacksonFlashMapListCodec.create(),             // JSON serialization
      CookieValueSigner.hmacSha512(secretKeyBytes),  // Strong cookie signing
      "flash"                                        // Name of the cookie
    );
  }
}
```

Make sure to replace `secretKeyBytes` with a proper 64-byte key for HMAC-SHA-256 signing.

### 2. Usage in your application

This is a typical POST-to-GET redirect pattern: after a POST request performs an action, the user is redirected to a GET endpoint that displays a result message.

```java
@PostMapping("/send-message")
public String updateChangeRequestStatus(final RedirectAttributes redirectAttributes) {
    final String message = sendMessage()
        ? "Okay, your message was submitted."
        : "Sending your message failed.";

    redirectAttributes.addFlashAttribute("message", message);
    return "redirect:/messages";
}

@GetMapping("/messages")
@ResponseBody
public String showMessage(@ModelAttribute("message") String message) {
    return message;
}
```

The message is transferred via an HTTP cookie rather than session storage – making it suitable for stateless environments or APIs.

## Security Considerations

Spring Cookie stores serialized data directly in HTTP cookies. While this enables stateless architectures, it also introduces potential attack surfaces. To ensure safe use in production environments, follow these best practices:

### 1. Use a Strong Secret Key

The HMAC key should be **at least 256 bits (32 bytes)**, preferably **512 bits (64 bytes)** in length:

```java
KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
keyGen.init(512);
byte[] key = keyGen.generateKey().getEncoded();
```

Store and manage this key securely, ideally via environment variables or a vault.

### 2. Avoid Storing Sensitive Information

Even signed cookies are visible to the client. Do not store personal data, tokens, or confidential information in flash attributes.

OK: status messages like `"Saved successfully."`.

*Avoid:* user IDs, emails, access rights, etc.

## Release History

See [CHANGELOG.md](./CHANGELOG.md)


## Code of Conduct

[Contributor Code of Conduct](./CODE_OF_CONDUCT.md). By participating in this
project you agree to abide by its terms.


## License

Spring Cookie is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

