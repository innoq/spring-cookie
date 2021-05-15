/*
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.innoq.spring.cookie.flash.codec.jackson.FlashMapSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.FlashMap;

import static org.assertj.core.api.Assertions.assertThat;

class FlashMapSerializerTest {

    ObjectMapper sut = new ObjectMapper()
        .registerModule(
            new SimpleModule().addSerializer(new FlashMapSerializer()));

    @Test
    void empty_flash_map_is_serialized() throws Exception {
        FlashMap flashMap = new FlashMap();

        String json = sut.writeValueAsString(flashMap);

        assertThat(json).isEqualTo(
            "{" +
                "\"attributes\":{}," +
                "\"expirationTime\":-1," +
                "\"targetRequestParams\":{}," +
                "\"targetRequestPath\":null" +
            "}"
        );
    }

    @Test
    void string_attribute_is_serialized() throws Exception {
        FlashMap flashMap = new FlashMap();
        flashMap.put("foo", "bar");

        String json = sut.writeValueAsString(flashMap);

        assertThat(json)
            .contains("\"attributes\":{\"foo\":\"bar\"}");
    }

    @Test
    void null_attribute_is_serialized() throws Exception {
        FlashMap flashMap = new FlashMap();
        flashMap.put("foo", null);

        String json = sut.writeValueAsString(flashMap);

        assertThat(json)
            .contains("\"attributes\":{\"foo\":null}");
    }

    @Test
    void expirationTime_is_serialized() throws Exception {
        FlashMap flashMap = new FlashMap();
        flashMap.setExpirationTime(4711);

        String json = sut.writeValueAsString(flashMap);

        assertThat(json)
            .contains("\"expirationTime\":4711");
    }

    @Test
    void targetRequestParams_with_single_param_and_value_is_serialized() throws Exception {
        FlashMap flashMap = new FlashMap();
        flashMap.addTargetRequestParam("foo", "bar");

        String json = sut.writeValueAsString(flashMap);

        assertThat(json)
            .contains("\"targetRequestParams\":{\"foo\":[\"bar\"]}");
    }

    @Test
    void targetRequestParams_with_single_param_and_multiple_values_is_serialized() throws Exception {
        FlashMap flashMap = new FlashMap();
        flashMap.addTargetRequestParam("foo", "bar");
        flashMap.addTargetRequestParam("foo", "baz");

        String json = sut.writeValueAsString(flashMap);

        assertThat(json)
            .contains("\"targetRequestParams\":{\"foo\":[\"bar\",\"baz\"]}");
    }

    @Test
    void targetRequestParams_with_multiple_params_is_serialized() throws Exception {
        FlashMap flashMap = new FlashMap();
        flashMap.addTargetRequestParam("foo", "bar");
        flashMap.addTargetRequestParam("bar", "baz");

        String json = sut.writeValueAsString(flashMap);

        assertThat(json)
            .contains("\"targetRequestParams\":{\"foo\":[\"bar\"],\"bar\":[\"baz\"]}");
    }

    @Test
    void targetRequestPath_is_serialized() throws Exception {
        FlashMap flashMap = new FlashMap();
        flashMap.setTargetRequestPath("/foo");

        String json = sut.writeValueAsString(flashMap);

        assertThat(json)
            .contains("\"targetRequestPath\":\"/foo\"");
    }
}
