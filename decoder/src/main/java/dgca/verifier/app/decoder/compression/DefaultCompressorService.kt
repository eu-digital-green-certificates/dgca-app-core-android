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

package dgca.verifier.app.decoder.compression

import dgca.verifier.app.decoder.model.VerificationResult
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.zip.InflaterInputStream

/**
 * Decompresses input with ZLIB
 */
class DefaultCompressorService : CompressorService {

    /**
     * Decompresses input with ZLIB = inflating.
     *
     * If the [input] does not start with ZLIB magic numbers (0x78), no decompression happens
     */
    override fun decode(input: ByteArray, verificationResult: VerificationResult): ByteArray? {
        verificationResult.zlibDecoded = false
        if (input.size >= 2 && input[0] == 0x78.toByte() // ZLIB magic headers
                && (input[1] == 0x01.toByte() || // Level 1
                        input[1] == 0x5E.toByte() || // Level 2 - 5
                        input[1] == 0x9C.toByte() || // Level 6
                        input[1] == 0xDA.toByte()    // Level 7 - 9
                        )) {
            return try {
                val inflaterStream = InflaterInputStream(input.inputStream())
                val outputStream = ByteArrayOutputStream(DEFAULT_BUFFER_SIZE)
                val decodedBytes = inflaterStream.copyTo(outputStream)

                if (decodedBytes == ERROR_TOO_MANY_BYTES_READ) {
                    null
                } else {
                    verificationResult.zlibDecoded = true
                    outputStream.toByteArray()
                }
            } catch (e: Throwable) {
                input
            }
        }
        return input
    }
}

/*
 * Limit the byte array size after decompression to 5 MB.
 *
 * Reasoning:
 * 1. QR codes can hold at most < 4500 alphanumeric chars (https://www.qrcode.com/en/about/version.html)
 *    Sidenote: The EHN spec recommends a compression level of Q, which limits it to at most < 2500 alphanumeric chars
 * 	  (https://ec.europa.eu/health/sites/default/files/ehealth/docs/digital-green-certificates_v1_en.pdf#page=7)
 *    This is a lower bound (since any DCC should be encodable in both Aztec and QR codes).
 * 2. As an additional upper bound: base45 encodes 2 bytes into 3 chars (https://datatracker.ietf.org/doc/html/draft-faltstrom-base45-04#section-4)
 * 3.  zlib's maximum compression factor is roughly 1000:1 (http://www.zlib.net/zlib_tech.html)
 */
private const val MAX_DECOMPRESSED_SIZE = 5 * 1024 * 1024

private const val ERROR_TOO_MANY_BYTES_READ = -1L

// Adapted from kotlin-stdblib's kotlin.io.IOStreams.kt
private fun InflaterInputStream.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)
        // begin patch
        if (bytesCopied > MAX_DECOMPRESSED_SIZE) {
            return ERROR_TOO_MANY_BYTES_READ
        }
        // end patch
    }
    return bytesCopied
}