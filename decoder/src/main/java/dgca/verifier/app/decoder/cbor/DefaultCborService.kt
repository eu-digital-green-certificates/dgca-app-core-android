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
 *  Created by Mykhailo Nester on 4/23/21 9:50 AM
 */

package dgca.verifier.app.decoder.cbor

import com.upokecenter.cbor.CBORObject
import dgca.verifier.app.decoder.cwt.CwtHeaderKeys
import dgca.verifier.app.decoder.model.GreenCertificate
import dgca.verifier.app.decoder.model.VerificationResult
import java.time.Instant
import java.time.ZoneOffset

/**
 * Decodes input as a CBOR structure
 */
class DefaultCborService(private val greenCertificateMapper: GreenCertificateMapper = DefaultGreenCertificateMapper()) :
    CborService {

    override fun decode(
        input: ByteArray,
        verificationResult: VerificationResult
    ): GreenCertificate? = decodeData(input, verificationResult)?.greenCertificate

    override fun decodeData(
        input: ByteArray,
        verificationResult: VerificationResult
    ): GreenCertificateData? {
        verificationResult.cborDecoded = false
        return try {
            val map = CBORObject.DecodeFromBytes(input)

            val issuingCountry: String? = map[CwtHeaderKeys.ISSUING_COUNTRY.asCBOR()]?.AsString()

            val issuedAt = Instant.ofEpochSecond(map[CwtHeaderKeys.ISSUED_AT.asCBOR()].AsInt64())
            verificationResult.isIssuedTimeCorrect = issuedAt.isBefore(Instant.now())

            val expirationTime = Instant.ofEpochSecond(map[CwtHeaderKeys.EXPIRATION.asCBOR()].AsInt64())
            verificationResult.isNotExpired = expirationTime.isAfter(Instant.now())

            val hcert = map[CwtHeaderKeys.HCERT.asCBOR()]

            val cborObject = hcert[CBORObject.FromObject(1)]

            val greenCertificate: GreenCertificate = greenCertificateMapper.readValue(cborObject)
                .also { verificationResult.cborDecoded = true }
            GreenCertificateData(
                issuingCountry,
                cborObject.ToJSONString(),
                greenCertificate,
                issuedAt.atZone(ZoneOffset.UTC),
                expirationTime.atZone(ZoneOffset.UTC)
            )
        } catch (e: Throwable) {
            null
        }
    }

    override fun getPayload(input: ByteArray): ByteArray? {
        return try {
            val map = CBORObject.DecodeFromBytes(input)
            val hcert = map[CwtHeaderKeys.HCERT.asCBOR()]
            hcert[CBORObject.FromObject(1)].EncodeToBytes()
        } catch (ex: Exception) {
            null
        }
    }
}