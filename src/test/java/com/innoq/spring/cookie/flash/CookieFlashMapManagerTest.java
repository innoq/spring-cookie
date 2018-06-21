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

import com.innoq.spring.cookie.flash.codec.jackson.JacksonFlashMapListCodec;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.FlashMap;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class CookieFlashMapManagerTest {

    CookieFlashMapManager sut = new CookieFlashMapManager(
        JacksonFlashMapListCodec.create());

    @Test
    void retrieveFlashMaps_withNoCookiePresent_returnsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");

        List<FlashMap> flashMaps = sut.retrieveFlashMaps(request);

        assertThat(flashMaps).isNull();
    }

    @Test
    void retrieveFlashMaps_withValidCookie_returnsFlashMaps() {
        String cookieValue = "W3siYXR0cmlidXRlcyI6eyJmb28iOm51bGwsImJhciI6NDcxMSwiYmF6IjoibG9yZW0gaXBzdW0ifSwiZXhwaXJhdGlvblRpbWUiOjQ3MTEsInRhcmdldFJlcXVlc3RQYXJhbXMiOnsiZm9vIjpbXSwiYmFyIjpbImZvbyJdLCJiYXoiOlsibG9yZW0iLCJpcHN1bSJdfSwidGFyZ2V0UmVxdWVzdFBhdGgiOiIvZm9vIn1dCg==";

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setCookies(new Cookie("flash", cookieValue));

        List<FlashMap> flashMaps = sut.retrieveFlashMaps(request);

        assertThat(flashMaps).hasSize(1);

        FlashMap flashMap = flashMaps.get(0);
        assertThat((Map<String, Object>) flashMap).containsOnly(
            entry("foo", null), entry("bar", 4711), entry("baz", "lorem ipsum"));
        assertThat(flashMap.getExpirationTime()).isEqualTo(4711);
        assertThat(flashMap.getTargetRequestParams()).containsOnly(
            entry("bar", asList("foo")), entry("baz", asList("lorem", "ipsum")));
        assertThat(flashMap.getTargetRequestPath()).isEqualTo("/foo");
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
        assertThat(cookieValue).isEqualTo("W3siYXR0cmlidXRlcyI6e30sImV4cGlyYXRpb25UaW1lIjotMSwidGFyZ2V0UmVxdWVzdFBhcmFtcyI6e30sInRhcmdldFJlcXVlc3RQYXRoIjpudWxsfV0=");
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
