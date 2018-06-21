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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.web.servlet.FlashMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

final class FlashMapDeserializer extends StdDeserializer<FlashMap> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FlashMapDeserializer() {
        super(FlashMap.class);
    }

    @Override
    public FlashMap deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        final JsonNode root = objectMapper.readTree(p);
        final FlashMap flashMap = new FlashMap();

        final JsonNode attributes = root.findValue("attributes");
        handleAttributes(flashMap, attributes);

        final JsonNode expirationTime = root.findValue("expirationTime");
        handleExpirationTime(flashMap, expirationTime);

        final JsonNode targetRequestParams = root.findValue("targetRequestParams");
        handleTargetRequestParams(flashMap, targetRequestParams);

        final JsonNode targetRequestPath = root.findValue("targetRequestPath");
        handleTargetRequestPath(flashMap, targetRequestPath);

        return flashMap;
    }

    private void handleAttributes(FlashMap flashMap, JsonNode node) {
        if (node == null || !node.isObject()) {
            return;
        }

        final Map<String, Object> attributes =
            objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
        flashMap.putAll(attributes);
    }

    private void handleExpirationTime(FlashMap flashMap, JsonNode node) {
        if (node == null || !node.isNumber()) {
            return;
        }

        final long expirationTime = node.asLong();
        flashMap.setExpirationTime(expirationTime);
    }

    private void handleTargetRequestParams(FlashMap flashMap, JsonNode node) {
        if (node == null || !node.isObject()) {
            return;
        }

        final Map<String, List<String>> targetRequestParams =
            objectMapper.convertValue(node, new TypeReference<Map<String, List<String>>>() {});
        targetRequestParams.forEach((key, values) ->
            values.forEach(value ->
                flashMap.addTargetRequestParam(key, value)));
    }

    private void handleTargetRequestPath(FlashMap flashMap, JsonNode node) {
        if (node == null || !node.isTextual()) {
            return;
        }

        final String targetRequestPath = node.asText();
        flashMap.setTargetRequestPath(targetRequestPath);
    }
}
