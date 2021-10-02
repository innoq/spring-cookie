/*
 * Copyright 2018-2021 innoQ Deutschland GmbH
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

final class HmacCookieValueSigner implements CookieValueSigner {

    private final String algorithm;
    private final byte[] secret;

    HmacCookieValueSigner(String algorithm, byte[] secret) {
        this.algorithm = algorithm;
        this.secret = secret;
    }

    @Override
    public String sign(String payload) {
        try {
            final byte[] data  = payload.getBytes(UTF_8);
            final Key key = new SecretKeySpec(secret, algorithm);

            final Mac mac = Mac.getInstance(algorithm);
            mac.init(key);
            final byte[] result = mac.doFinal(data);

            return bytesToHex(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // TODO: use own exception?
            throw new IllegalStateException("Unable to sign payload", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        final StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(byteToHex(b));
        }
        return hex.toString();
    }

    private static String byteToHex(byte b) {
        return Integer.toString((b &  0xff) + 0x100, 16).substring(1);
    }
}
