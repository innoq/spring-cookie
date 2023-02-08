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
package com.innoq.spring.cookie.flash;

import com.innoq.spring.cookie.security.CookieValueSigner;
import org.springframework.util.Assert;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.AbstractFlashMapManager;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.MessageDigest.isEqual;
import static org.springframework.web.util.WebUtils.getCookie;

public final class CookieFlashMapManager extends AbstractFlashMapManager {

    private static final String DEFAULT_COOKIE_NAME = "flash";

    private final FlashMapListCodec codec;
    private final CookieValueSigner signer;
    private final String cookieName;

    public CookieFlashMapManager(FlashMapListCodec codec,
            CookieValueSigner signer) {
        this(codec, signer, DEFAULT_COOKIE_NAME);
    }

    public CookieFlashMapManager(FlashMapListCodec codec,
            CookieValueSigner signer, String cookieName) {
        Assert.notNull(codec, "FlashMapListCodec must not be null");
        Assert.notNull(signer, "CookieValueSigner must not be null");
        Assert.hasText(cookieName, "Cookie name must not be null or empty");
        this.codec = codec;
        this.signer = signer;
        this.cookieName = cookieName;
    }

    @Override
    protected List<FlashMap> retrieveFlashMaps(HttpServletRequest request) {
        final Cookie cookie = getCookie(request, cookieName);
        if (cookie == null) {
            return null;
        }

        final String value = cookie.getValue();
        return decode(value);
    }

    @Override
    protected void updateFlashMaps(List<FlashMap> flashMaps,
            HttpServletRequest request, HttpServletResponse response) {
        final Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        if (flashMaps.isEmpty()) {
            cookie.setMaxAge(0);
        } else {
            final String value = encode(flashMaps);
            cookie.setValue(value);
        }
        response.addCookie(cookie);
    }

    @Override
    protected Object getFlashMapsMutex(HttpServletRequest request) {
        return null;
    }

    private List<FlashMap> decode(String value) {
        final String[] signatureAndPayload = reverse(value).split("--", 2);
        if (signatureAndPayload.length != 2) {
            // TODO logging
            return null;
        }

        final String signature = reverse(signatureAndPayload[0]);
        final String payload = reverse(signatureAndPayload[1]);

        if (!isVerified(payload, signature)) {
            // TODO logging
            return null;
        }

        return codec.decode(payload);
    }


    private String encode(List<FlashMap> flashMaps) {
        final String payload = codec.encode(flashMaps);
        final String signature = signer.sign(payload);
        return payload + "--" + signature;
    }

    private boolean isVerified(String payload, String digest) {
        final String signature = signer.sign(payload);
        return isEqual(digest.getBytes(UTF_8), signature.getBytes(UTF_8));
    }

    private static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }
}
