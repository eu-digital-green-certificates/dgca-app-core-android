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
 *  Created by osarapulov on 7/28/21 1:32 PM
 */

package dgca.verifier.app.decoder.cbor

import com.upokecenter.cbor.CBORObject
import dgca.verifier.app.decoder.model.GreenCertificate
import org.junit.Assert.assertEquals
import org.junit.Test

internal class DefaultGreenCertificateMapperTest {
    val JSON = "{\n" +
            "  \"t\": [\n" +
            "    {\n" +
            "      \"sc\": \"   2021-07-26T21:00:00Z   \",\n" +
            "      \"ma\": \"   1223   \",\n" +
            "      \"tt\": \"   LP217198-3   \",\n" +
            "      \"tc\": \"   Custom Testing Centre   \",\n" +
            "      \"co\": \"   FR   \",\n" +
            "      \"ci\": \"   URN:UVCI:V1:DE:O39BNVCVNHTRMKY0E9DYT4A43T   \",\n" +
            "      \"is\": \"   Custom Issuer Certifcate   \",\n" +
            "      \"tg\": \"   840539006   \",\n" +
            "      \"tr\": \"   260415000   \"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"nam\": {\n" +
            "    \"fnt\": \"   STANDARDISEDFAMILY   \"\n" +
            "  },\n" +
            "  \"ver\": \"   1.3.0   \",\n" +
            "  \"dob\": \"   \"\n" +
            "}"


    @Test
    fun test() {
        val greenCertificateMapper = DefaultGreenCertificateMapper()
        val cborObject = CBORObject.FromJSONString(JSON)
        val greenCertificate: GreenCertificate =
                greenCertificateMapper.readValue(cborObject)
        val testingCenter: String = greenCertificate.tests!!.first().testingCentre
        assertEquals(testingCenter.trim(), testingCenter)
    }
}