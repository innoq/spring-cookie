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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.AbstractFlashMapManager;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public final class CookieFlashMapManager extends AbstractFlashMapManager {

    private static final String DEFAULT_COOKIE_NAME = "flash";

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new SimpleModule()
            .addSerializer(FlashMap.class, new FlashMapSerializer())
            .addDeserializer(FlashMap.class, new FlashMapDeserializer()));
    private final String cookieName;

    public CookieFlashMapManager() {
        this(DEFAULT_COOKIE_NAME);
    }

    public CookieFlashMapManager(String cookieName) {
        // TODO: assert not null/empty
        this.cookieName = cookieName;
    }

    @Override
    protected List<FlashMap> retrieveFlashMaps(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            return null;
        }

        String value = cookie.getValue();
        byte[] payload = Base64.getDecoder().decode(value);

        try {
            return this.objectMapper.readValue(payload, new TypeReference<List<FlashMap>>() {});
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void updateFlashMaps(List<FlashMap> flashMaps, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        if (flashMaps.isEmpty()) {
            cookie.setMaxAge(0);
        } else {
            try {
                byte[] payload = this.objectMapper.writeValueAsBytes(flashMaps);
                String value = Base64.getEncoder().encodeToString(payload);

                // TODO: max-age?
                cookie.setValue(value);
            } catch (JsonProcessingException e) {
                // TODO
                e.printStackTrace();
            }
        }
        response.addCookie(cookie);
    }

    @Override
    protected Object getFlashMapsMutex(HttpServletRequest request) {
        return null;
    }
}
