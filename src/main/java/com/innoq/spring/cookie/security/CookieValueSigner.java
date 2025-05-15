/*
 * Copyright 2018-2025 innoQ Deutschland GmbH
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

public interface CookieValueSigner {

    String sign(String payload);

    /**
     * @deprecated SHA-1 is no longer considered secure.
     *             Use SHA-256 or higher instead.
     *             See: <a href="https://www.nist.gov/news-events/news/2022/12/nist-retires-sha-1-cryptographic-algorithm">https://www.nist.gov/news-events/news/2022/12/nist-retires-sha-1-cryptographic-algorithm</a>
     */
    @Deprecated
    static CookieValueSigner hmacSha1(String secret) {
        return new HmacCookieValueSigner("HmacSHA1", secret.getBytes(UTF_8));
    }

    /**
     * @param secret Secret key for signing, as a byte array.
     *               The key should be uniformly distributed and generated using a cryptographically secure random number generator.
     *               When using SHA-512, the recommended minimum length is 32 bytes;
     *               the ideal length is 64 bytes (i.e., full hash output size).
     */
    static CookieValueSigner hmacSha512(byte[] secret) {
        return new HmacCookieValueSigner("HmacSHA512", secret);
    }
}
