/*
 *  ---license-start
 *  eu-digital-green-certificates / dgca-verifier-app-android
 *  ---
 *  Copyright (C) 2021 T-Systems International GmbH and all other contributors
 *  ---
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ---license-end
 *
 *  Created by osarapulov on 7/28/21 1:23 PM
 */

package dgca.verifier.app.decoder.cbor

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.upokecenter.cbor.CBORObject
import dgca.verifier.app.decoder.model.GreenCertificate

class DefaultGreenCertificateMapper : GreenCertificateMapper, CBORMapper() {

    init {
        SimpleModule().apply {
            addDeserializer(String::class.java, object : JsonDeserializer<String>() {
                override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): String? = p?.valueAsString?.trim()
            })
            registerModule(this)
        }

    }

    override fun readValue(cborObject: CBORObject): GreenCertificate {
        val bytes = cborObject.EncodeToBytes()
        return readValue(bytes, GreenCertificate::class.java)
    }
}