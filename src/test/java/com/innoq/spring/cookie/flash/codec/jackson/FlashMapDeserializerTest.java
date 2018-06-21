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
package com.innoq.spring.cookie.flash.codec.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.innoq.spring.cookie.flash.codec.jackson.FlashMapDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.FlashMap;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class FlashMapDeserializerTest {

    ObjectMapper sut = new ObjectMapper()
        .registerModule(
            new SimpleModule().addDeserializer(FlashMap.class, new FlashMapDeserializer()));

    @Test
    void empty_flash_map_is_deserialized() throws Exception {
        String json =
            "{" +
                "\"attributes\":{}," +
                "\"expirationTime\":-1," +
                "\"targetRequestParams\":{}," +
                "\"targetRequestPath\":null" +
            "}";

        FlashMap flashMap = sut.readValue(json, FlashMap.class);

        assertThat((Map<String, Object>) flashMap).isEmpty();
        assertThat(flashMap.getExpirationTime()).isEqualTo(-1);
        assertThat(flashMap.getTargetRequestParams()).isEmpty();
        assertThat(flashMap.getTargetRequestPath()).isNull();
    }

    @Test
    void complex_flash_map_is_deserialized() throws Exception {
        String json =
            "{" +
                "\"attributes\":{" +
                    "\"foo\":null," +
                    "\"bar\":4711," +
                    "\"baz\":\"lorem ipsum\"" +
                "}," +
                "\"expirationTime\":4711," +
                "\"targetRequestParams\":{" +
                    "\"foo\":[]," +
                    "\"bar\":[\"foo\"]," +
                    "\"baz\":[\"lorem\",\"ipsum\"]" +
                "}," +
                "\"targetRequestPath\":\"/foo\"" +
            "}";

        FlashMap flashMap = sut.readValue(json, FlashMap.class);

        assertThat((Map<String, Object>) flashMap).containsOnly(
            entry("foo", null), entry("bar", 4711), entry("baz", "lorem ipsum"));
        assertThat(flashMap.getExpirationTime()).isEqualTo(4711);
        assertThat(flashMap.getTargetRequestParams()).containsOnly(
            entry("bar", asList("foo")), entry("baz", asList("lorem", "ipsum")));
        assertThat(flashMap.getTargetRequestPath()).isEqualTo("/foo");
    }

    @Test
    void unknown_fields_are_ignored() throws Exception {
        String json =
            "{" +
                "\"foo\":{}," +
                "\"bar\":-1," +
                "\"baz\":null," +
                "\"lorem\":[]" +
            "}";

        FlashMap flashMap = sut.readValue(json, FlashMap.class);

        assertThat((Map<String, Object>) flashMap).isEmpty();
        assertThat(flashMap.getExpirationTime()).isEqualTo(-1);
        assertThat(flashMap.getTargetRequestParams()).isEmpty();
        assertThat(flashMap.getTargetRequestPath()).isNull();
    }

    @Test
    void in_list_can_still_be_deserialized() throws Exception {
        String json =
            "[" +
                "{" +
                    "\"foo\":{}," +
                    "\"bar\":-1," +
                    "\"baz\":null," +
                    "\"lorem\":[]" +
                "}," +
                "{" +
                    "\"attributes\":{" +
                        "\"foo\":null," +
                        "\"bar\":4711," +
                        "\"baz\":\"lorem ipsum\"" +
                    "}," +
                    "\"expirationTime\":4711," +
                    "\"foo\":{\"bar\":{\"baz\":4711}}," +
                    "\"lorem\":[]," +
                    "\"targetRequestParams\":{" +
                        "\"foo\":[]," +
                        "\"bar\":[\"foo\"]," +
                        "\"baz\":[\"lorem\",\"ipsum\"]" +
                    "}," +
                    "\"targetRequestPath\":\"/foo\"" +
                "}," +
                "{" +
                    "\"attributes\":{}," +
                    "\"expirationTime\":-1," +
                    "\"targetRequestParams\":{}," +
                    "\"targetRequestPath\":null" +
                "}," +
                "{" +
                    "\"attributes\":{}," +
                    "\"foo\":{}," +
                    "\"expirationTime\":-1," +
                    "\"bar\":-1," +
                    "\"targetRequestParams\":{}," +
                    "\"baz\":null," +
                    "\"targetRequestPath\":null," +
                    "\"lorem\":[]" +
                "}" +
            "]";

        List<FlashMap> flashMaps = sut.readValue(json, new TypeReference<List<FlashMap>>() {});

        assertThat(flashMaps).hasSize(4);

        FlashMap flashMap = flashMaps.get(0);
        assertThat((Map<String, Object>) flashMap).isEmpty();
        assertThat(flashMap.getExpirationTime()).isEqualTo(-1);
        assertThat(flashMap.getTargetRequestParams()).isEmpty();
        assertThat(flashMap.getTargetRequestPath()).isNull();

        flashMap = flashMaps.get(1);
        assertThat((Map<String, Object>) flashMap).containsOnly(
            entry("foo", null), entry("bar", 4711), entry("baz", "lorem ipsum"));
        assertThat(flashMap.getExpirationTime()).isEqualTo(4711);
        assertThat(flashMap.getTargetRequestParams()).containsOnly(
            entry("bar", asList("foo")), entry("baz", asList("lorem", "ipsum")));
        assertThat(flashMap.getTargetRequestPath()).isEqualTo("/foo");

        flashMap = flashMaps.get(2);
        assertThat((Map<String, Object>) flashMap).isEmpty();
        assertThat(flashMap.getExpirationTime()).isEqualTo(-1);
        assertThat(flashMap.getTargetRequestParams()).isEmpty();
        assertThat(flashMap.getTargetRequestPath()).isNull();

        flashMap = flashMaps.get(3);
        assertThat((Map<String, Object>) flashMap).isEmpty();
        assertThat(flashMap.getExpirationTime()).isEqualTo(-1);
        assertThat(flashMap.getTargetRequestParams()).isEmpty();
        assertThat(flashMap.getTargetRequestPath()).isNull();
    }
}
