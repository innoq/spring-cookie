/**
 * Copyright 2018 innoQ Deutschland GmbH
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

import org.springframework.util.Assert;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.AbstractFlashMapManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.web.util.WebUtils.getCookie;

public final class CookieFlashMapManager extends AbstractFlashMapManager {

    private static final String DEFAULT_COOKIE_NAME = "flash";

    private final FlashMapListCodec codec;
    private final String cookieName;

    public CookieFlashMapManager(FlashMapListCodec codec) {
        this(codec, DEFAULT_COOKIE_NAME);
    }

    public CookieFlashMapManager(FlashMapListCodec codec, String cookieName) {
        Assert.notNull(codec, "FlashMapListCodec must not be null");
        Assert.hasText(cookieName, "Cookie name must not be null or empty");
        this.codec = codec;
        this.cookieName = cookieName;
    }

    @Override
    protected List<FlashMap> retrieveFlashMaps(HttpServletRequest request) {
        final Cookie cookie = getCookie(request, cookieName);
        if (cookie == null) {
            return null;
        }

        final String value = cookie.getValue();
        return codec.decode(value);
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
            final String value = codec.encode(flashMaps);
            cookie.setValue(value);
        }
        response.addCookie(cookie);
    }

    @Override
    protected Object getFlashMapsMutex(HttpServletRequest request) {
        return null;
    }
}
