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
 *  Created by osarapulov on 5/10/21 11:29 PM
 */

package dgca.verifier.app.decoder

import COSE.HeaderKeys
import com.upokecenter.cbor.CBORObject
import dgca.verifier.app.decoder.base45.Base45Decoder
import dgca.verifier.app.decoder.cbor.DefaultGreenCertificateMapper
import dgca.verifier.app.decoder.cbor.GreenCertificateMapper
import dgca.verifier.app.decoder.cwt.CwtHeaderKeys
import dgca.verifier.app.decoder.model.CoseData
import dgca.verifier.app.decoder.model.GreenCertificate
import java.util.zip.InflaterInputStream

@ExperimentalUnsignedTypes
class DefaultCertificateDecoder(private val greenCertificateMapper: GreenCertificateMapper = DefaultGreenCertificateMapper(), private val base45Decoder: Base45Decoder) :
        CertificateDecoder {
    companion object {
        const val PREFIX = "HC1:"
    }

    override fun decodeCertificate(qrCodeText: String): CertificateDecodingResult {
        val withoutPrefix: String = if (qrCodeText.startsWith(PREFIX)) qrCodeText.drop(PREFIX.length) else qrCodeText
        val base45Decoded: ByteArray = try {
            base45Decoder.decode(withoutPrefix)
        } catch (error: Throwable) {
            return CertificateDecodingResult.Error(CertificateDecodingError.Base45DecodingError(error))
        }

        val decompressed: ByteArray = try {
            base45Decoded.decompressBase45DecodedData()
        } catch (error: Throwable) {
            return CertificateDecodingResult.Error(CertificateDecodingError.Base45DecompressionError(error))
        }

        val coseData: CoseData = try {
            decompressed.decodeCose()
        } catch (error: Throwable) {
            return CertificateDecodingResult.Error(CertificateDecodingError.CoseDataDecodingError(error))
        }

        val greenCertificate: GreenCertificate = try {
            coseData.cbor.decodeGreenCertificate()
        } catch (error: Throwable) {
            return CertificateDecodingResult.Error(CertificateDecodingError.GreenCertificateDecodingError(error))
        }


        return CertificateDecodingResult.Success(greenCertificate)
    }


    private fun ByteArray.decompressBase45DecodedData(): ByteArray {
        // ZLIB magic headers
        return if (this.size >= 2 && this[0] == 0x78.toByte() && (
                        this[1] == 0x01.toByte() || // Level 1
                                this[1] == 0x5E.toByte() || // Level 2 - 5
                                this[1] == 0x9C.toByte() || // Level 6
                                this[1] == 0xDA.toByte()
                        )
        ) {
            InflaterInputStream(this.inputStream()).readBytes()
        } else this
    }

    private fun ByteArray.decodeCose(): CoseData {
        val messageObject = CBORObject.DecodeFromBytes(this)
        val content = messageObject[2].GetByteString()
        val rgbProtected = messageObject[0].GetByteString()
        val rgbUnprotected = messageObject[1]
        val key = HeaderKeys.KID.AsCBOR()

        if (!CBORObject.DecodeFromBytes(rgbProtected).keys.contains(key)) {
            val objunprotected = rgbUnprotected.get(key).GetByteString()
            return CoseData(content, objunprotected)
        }
        val objProtected = CBORObject.DecodeFromBytes(rgbProtected).get(key).GetByteString()
        return CoseData(content, objProtected)
    }

    private fun ByteArray.decodeGreenCertificate(): GreenCertificate {
        val map = CBORObject.DecodeFromBytes(this)
        val hcert = map[CwtHeaderKeys.HCERT.asCBOR()]
        val cborObject = hcert[CBORObject.FromObject(1)]

        return greenCertificateMapper
                .readValue(cborObject)
    }
}