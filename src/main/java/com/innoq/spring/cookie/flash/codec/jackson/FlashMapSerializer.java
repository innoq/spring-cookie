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

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.servlet.FlashMap;

import java.util.HashMap;

final class FlashMapSerializer extends StdSerializer<FlashMap> {

    public FlashMapSerializer() {
        super(FlashMap.class);
    }

    @Override
    public void serialize(FlashMap value, JsonGenerator gen, SerializationContext serializers)
            throws JacksonException {
        gen.writeStartObject();
        gen.writePOJOProperty("attributes", new HashMap<>(value));
        gen.writeNumberProperty("expirationTime", value.getExpirationTime());
        gen.writePOJOProperty("targetRequestParams", value.getTargetRequestParams());
        gen.writeStringProperty("targetRequestPath", value.getTargetRequestPath());
        gen.writeEndObject();
    }
}
