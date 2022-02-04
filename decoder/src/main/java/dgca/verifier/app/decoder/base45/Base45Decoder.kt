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

package dgca.verifier.app.decoder.base45

// Lookup tables for faster processing
internal val ENCODING_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:".encodeToByteArray()
private val DECODING_CHARSET = ByteArray(256) { -1 }.also { charset ->
    ENCODING_CHARSET.forEachIndexed { index, byte ->
        charset[byte.toInt()] = index.toByte()
    }
}

/**
 *  The Base45 Data Decoding
 *
 *  https://datatracker.ietf.org/doc/draft-faltstrom-base45/?include_text=1
 */
@ExperimentalUnsignedTypes
class Base45Decoder {

    @Throws(Base45DecodeException::class)
    fun decode(input: String): ByteArray =
        input.toByteArray().asSequence().map {
            DECODING_CHARSET[it.toInt()].also { index ->
                if (index < 0) throw Base45DecodeException("Invalid characters in input.")
            }
        }.chunked(3) { chunk ->
            if (chunk.size < 2) throw Base45DecodeException("Invalid input length.")
            chunk.reversed().toInt(45).toBase(base = 256, count = chunk.size - 1).reversed()
        }.flatten().toList().toByteArray()

    /** Converts integer to a list of [count] integers in the given [base]. */
    @Throws(Base45DecodeException::class)
    private fun Int.toBase(base: Int, count: Int): List<Byte> =
        mutableListOf<Byte>().apply {
            var tmp = this@toBase
            repeat(count) {
                add((tmp % base).toByte())
                tmp /= base
            }
            if (tmp != 0) throw Base45DecodeException("Invalid character sequence.")
        }

    /** Converts list of bytes in given [base] to an integer. */
    private fun List<Byte>.toInt(base: Int): Int =
        fold(0) { acc, i -> acc * base + i.toUByte().toInt() }
}

/** Thrown when [Base45.decode] can't decode the input data. */
class Base45DecodeException(message: String) : IllegalArgumentException(message)