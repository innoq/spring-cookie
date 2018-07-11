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
package com.innoq.spring.cookie.flash.verification;

import org.springframework.web.servlet.FlashMap;

import java.util.List;

public interface CookieVerificationFailureHandler {

    List<FlashMap> onInvalidValue(String value);

    List<FlashMap> onInvalidSignature(String payload, String signature);

    static CookieVerificationFailureHandler ignoreFailures() {
        return IgnoreCookieVerificationFailureHandler.INSTANCE;
    }
}
