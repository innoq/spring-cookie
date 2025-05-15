/*
 * Copyright 2025 innoQ Deutschland GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.innoq.spring.cookie.security;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This class contains static factory methods to create {@link CookieValueSigner}
 * instances with a preselected set of algorithms.
 * <br>
 * If your desired algorithm is not present here consider creating your own by
 * implementing {@link CookieValueSigner} yourself.
 */
public final class CookieValueSigners {

    private CookieValueSigners() {
    }

    /**
     * Constructs a {@link CookieValueSigner} that uses
     * <a href="https://en.wikipedia.org/wiki/HMAC">HMAC</a> with
     * <a href="https://en.wikipedia.org/wiki/SHA-1">SHA-1</a> as hash function.
     *
     * @param secret Secret key for signing, as a string.
     * @deprecated Please use {@link CookieValueSigners#hmacSha1(byte[])}.
     */
    @Deprecated(forRemoval = true)
    public static CookieValueSigner hmacSha1(String secret) {
        return hmacSha1(secret.getBytes(UTF_8));
    }

    /**
     * Constructs a {@link CookieValueSigner} that uses
     * <a href="https://en.wikipedia.org/wiki/HMAC">HMAC</a> with
     * <a href="https://en.wikipedia.org/wiki/SHA-1">SHA-1</a> as hash function.
     * <br>
     * As of my understanding even though SHA-1 is
     * <a href="https://www.nist.gov/news-events/news/2022/12/nist-retires-sha-1-cryptographic-algorithm">retired by NIST</a>
     * if used in combination with HMAC it should be considered safe.
     * If you want to be on the safe side please use {@link CookieValueSigners#hmacSha512(byte[])}.
     *
     * @param secret Secret key for signing, as a byte array.
     */
    public static CookieValueSigner hmacSha1(byte[] secret) {
        return new HmacCookieValueSigner("HmacSHA1", secret);
    }

    /**
     * Constructs a {@link CookieValueSigner} that uses
     * <a href="https://en.wikipedia.org/wiki/HMAC">HMAC</a> with
     * <a href="https://en.wikipedia.org/wiki/SHA-2">SHA-512</a> as hash function.
     *
     * @param secret Secret key for signing, as a byte array.
     *               The key should be uniformly distributed and generated using a cryptographically secure random number generator.
     *               When using SHA-512, the recommended minimum length is 32 bytes;
     *               the ideal length is 64 bytes (i.e., full hash output size).
     */
    public static CookieValueSigner hmacSha512(byte[] secret) {
        return new HmacCookieValueSigner("HmacSHA512", secret);
    }
}
