/*
 * Copyright 2018-2023 innoQ Deutschland GmbH
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

import com.innoq.spring.cookie.flash.codec.jackson.JacksonFlashMapListCodec;
import com.innoq.spring.cookie.security.CookieValueSigner;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.FlashMap;

import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class CookieFlashMapManagerTest {
    byte[] secretKeyForTests = {
        1, 2, 3, 4, 5, 6, 7, 8,
        9, 10, 11, 12, 13, 14, 15, 16,
        17, 18, 19, 20, 21, 22, 23, 24,
        25, 26, 27, 28, 29, 30, 31, 32,
        33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48,
        49, 50, 51, 52, 53, 54, 55, 56,
        57, 58, 59, 60, 61, 62, 63, 64
    };

    CookieFlashMapManager sut = new CookieFlashMapManager(
        JacksonFlashMapListCodec.create(), CookieValueSigner.hmacSha512(secretKeyForTests));

    @Test
    void retrieveFlashMaps_withNoCookiePresent_returnsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");

        List<FlashMap> flashMaps = sut.retrieveFlashMaps(request);

        assertThat(flashMaps).isNull();
    }

    @Test
    void retrieveFlashMaps_withValidCookie_returnsFlashMaps() {
        String cookieValue = "W3siYXR0cmlidXRlcyI6eyJiYXIiOjQ3MTEsImJheiI6ImxvcmVtIGlwc3VtIiwiZm9vIjpudWxsfSwiZXhwaXJhdGlvblRpbWUiOjE3NDcyNjMzODU0NjYsInRhcmdl" +
            "dFJlcXVlc3RQYXJhbXMiOnsiYmFyIjpbImZvbyJdLCJiYXoiOlsibG9yZW0iLCJpcHN1bSJdfSwidGFyZ2V0UmVxdWVzdFBhdGgiOiIvZm9vIn1d--82d4da6585ee8acd9f503fa9cdffafd" +
            "c6625791614883b166209aaef5d36d470492d8dc52ad785dcb9dbe7d9f3bab6fcfd0f306bf833a9d9cdf36738af945bf4";

        MockHttpServletRequest secondRequest = new MockHttpServletRequest("GET", "/");
        secondRequest.setCookies(new Cookie("flash", cookieValue));

        List<FlashMap> flashMaps = sut.retrieveFlashMaps(secondRequest);

        assertThat(flashMaps).hasSize(1);

        FlashMap flashMapOut = flashMaps.get(0);
        assertThat((Map<String, Object>) flashMapOut).containsOnly(
            entry("foo", null), entry("bar", 4711), entry("baz", "lorem ipsum"));
        assertThat(flashMapOut.getExpirationTime()).isEqualTo(1747263385466L);
        assertThat(flashMapOut.getTargetRequestParams()).containsOnly(
            entry("bar", asList("foo")), entry("baz", asList("lorem", "ipsum")));
        assertThat(flashMapOut.getTargetRequestPath()).isEqualTo("/foo");
    }

    @Test
    void updateFlashMaps_withSingleFlashMap_writesCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FlashMap flashMap = new FlashMap();

        sut.updateFlashMaps(asList(flashMap), request, response);

        assertThat(response.getCookies()).hasSize(1);

        assertThat(response.getCookie("flash"))
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "flash")
            .hasFieldOrPropertyWithValue("path", "/")
            .hasFieldOrPropertyWithValue("httpOnly", true);

        String cookieValue = response.getCookie("flash").getValue();
        assertThat(cookieValue).isEqualTo("W3siYXR0cmlidXRlcyI6e30sImV4cGlyYXRpb25UaW1lIjotMSwidGFyZ2V0UmVxdWVzdFBhcmFtcyI6e30sInRhcmdldFJlcXVlc3RQYXRoIjpudWxsfV0=--8dc134130c9f450deeef4499ace9dc950ecf342edabf77e7a8b002592413d8448dcb780d2b5f76d1a3b18152196a107654aebc0d2c7b5ef329e294b215bd0d27");
    }

    @Test
    void updateFlashMaps_withNoFlashMap_deletesCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        sut.updateFlashMaps(emptyList(), request, response);

        assertThat(response.getCookies()).hasSize(1);

        assertThat(response.getCookie("flash"))
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "flash")
            .hasFieldOrPropertyWithValue("value", null)
            .hasFieldOrPropertyWithValue("path", "/")
            .hasFieldOrPropertyWithValue("httpOnly", true)
            .hasFieldOrPropertyWithValue("maxAge", 0);
    }
}
