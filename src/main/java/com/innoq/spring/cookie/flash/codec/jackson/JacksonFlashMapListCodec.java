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
package com.innoq.spring.cookie.flash.codec.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.innoq.spring.cookie.flash.FlashMapListCodec;
import org.springframework.util.Assert;
import org.springframework.web.servlet.FlashMap;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public final class JacksonFlashMapListCodec implements FlashMapListCodec {

    private static TypeReference<List<FlashMap>> FLASH_MAPS_TYPE_REFERENCE =
        new TypeReference<List<FlashMap>>() {};

    private final ObjectMapper mapper;

    private JacksonFlashMapListCodec(ObjectMapper mapper) {
        Assert.notNull(mapper, "ObjectMapper must not be null");
        this.mapper = mapper;
    }

    @Override
    public List<FlashMap> decode(String encodedFlashMaps) {
        final byte[] json = fromBase64(encodedFlashMaps);
        return fromJson(json);
    }

    @Override
    public String encode(List<FlashMap> flashMaps) {
        final byte[] json = toJson(flashMaps);
        return toBase64(json);
    }

    private byte[] toJson(List<FlashMap> flashMaps) {
        try {
            return mapper.writeValueAsBytes(flashMaps);
        } catch (JsonProcessingException e) {
            // TODO: use own exception?
            throw new IllegalArgumentException("Unable to convert flash maps to JSON", e);
        }
    }

    private List<FlashMap> fromJson(byte[] json) {
        try {
            return mapper.readValue(json, FLASH_MAPS_TYPE_REFERENCE);
        } catch (IOException e) {
            // TODO: use own exception?
            throw new IllegalArgumentException("Unable to convert JSON to flash maps", e);
        }
    }

    private static byte[] fromBase64(String string) {
        return Base64.getDecoder().decode(string);
    }

    private static String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static JacksonFlashMapListCodec create() {
        final SimpleModule module = new SimpleModule();
        module.addSerializer(FlashMap.class, new FlashMapSerializer());
        module.addDeserializer(FlashMap.class, new FlashMapDeserializer());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        return create(mapper);
    }

    public static JacksonFlashMapListCodec create(ObjectMapper objectMapper) {
        return new JacksonFlashMapListCodec(objectMapper);
    }
}
