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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.servlet.FlashMap;

import java.io.IOException;
import java.util.HashMap;

final class FlashMapSerializer extends StdSerializer<FlashMap> {

    public FlashMapSerializer() {
        super(FlashMap.class);
    }

    @Override
    public void serialize(FlashMap value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("attributes", new HashMap<>(value));
        gen.writeNumberField("expirationTime", value.getExpirationTime());
        gen.writeObjectField("targetRequestParams", value.getTargetRequestParams());
        gen.writeStringField("targetRequestPath", value.getTargetRequestPath());
        gen.writeEndObject();
    }
}
