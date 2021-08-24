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
 *  Created by Mykhailo Nester on 4/23/21 9:51 AM
 */

package dgca.verifier.app.decoder.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

/**
 * CBOR structure of the certificate
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GreenCertificate(

    @JsonProperty("ver")
    val schemaVersion: String,

    @JsonProperty("nam")
    val person: Person,

    @JsonProperty("dob")
    val dateOfBirth: String,

    @JsonProperty("v")
    val vaccinations: List<Vaccination>?,

    @JsonProperty("t")
    val tests: List<Test>?,

    @JsonProperty("r")
    val recoveryStatements: List<RecoveryStatement>?

) : Serializable {

    fun getDgci(): String {
        return try {
            return when {
                vaccinations?.isNotEmpty() == true -> vaccinations.last().certificateIdentifier
                tests?.isNotEmpty() == true -> tests.last().certificateIdentifier
                recoveryStatements?.isNotEmpty() == true -> recoveryStatements.last().certificateIdentifier
                else -> ""
            }
        } catch (ex: Exception) {
            ""
        }
    }

    fun getIssuingCountry(): String = try {
        when {
            vaccinations?.isNotEmpty() == true -> vaccinations.last().countryOfVaccination
            tests?.isNotEmpty() == true -> tests.last().countryOfVaccination
            recoveryStatements?.isNotEmpty() == true -> recoveryStatements.last().countryOfVaccination
            else -> ""
        }
    } catch (ex: Exception) {
        ""
    }.toLowerCase(Locale.ROOT)

    fun getType(): CertificateType {
        if (vaccinations?.isNotEmpty() == true)
            return CertificateType.VACCINATION
        if (tests?.isNotEmpty() == true) return CertificateType.TEST
        if (recoveryStatements?.isNotEmpty() == true) return CertificateType.RECOVERY
        return CertificateType.UNKNOWN
    }
}
