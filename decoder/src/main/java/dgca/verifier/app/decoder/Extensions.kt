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
 *  Created by mykhailo.nester on 4/30/21 4:38 PM
 */

package dgca.verifier.app.decoder

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.upokecenter.cbor.CBORObject
import dgca.verifier.app.decoder.model.KeyPairData
import java.io.ByteArrayInputStream
import java.security.*
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.ECGenParameterSpec

const val ECDSA_256 = -7
const val RSA_PSS_256 = -37

const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
const val SHA_256_WITH_ECDSA = "SHA256withECDSA"
const val SHA_256_WITH_RSA = "SHA256WithRSA"

fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }

fun String.hexToByteArray(): ByteArray = chunked(2)
    .map { it.toInt(16).toByte() }
    .toByteArray()

fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

fun String.toBase64(): String = Base64.encodeToString(this.toByteArray(), Base64.NO_WRAP)

fun String.base64ToX509Certificate(): X509Certificate? {
    val decoded = Base64.decode(this, Base64.NO_WRAP)
    val inputStream = ByteArrayInputStream(decoded)

    return CertificateFactory.getInstance("X.509").generateCertificate(inputStream) as? X509Certificate
}

fun ByteArray.toHash(): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(this)
        .toBase64()
}

private fun provideEcKeyPairData(alias: String): KeyPairData {
    val keyPairGen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEYSTORE_PROVIDER)
    val keyPairGeneratorSpec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    )
        .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
        .setDigests(
            KeyProperties.DIGEST_SHA256,
            KeyProperties.DIGEST_SHA384,
            KeyProperties.DIGEST_SHA512
        )
        .setKeySize(256)
        .build()
    keyPairGen.initialize(keyPairGeneratorSpec)
    return KeyPairData(SHA_256_WITH_ECDSA, keyPairGen.generateKeyPair())
}

private fun provideRsaKeyPairData(alias: String): KeyPairData {
    val keyPairGen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE_PROVIDER)
    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    )
        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        .setKeySize(2048)
        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
        .build()

    keyPairGen.initialize(keyGenParameterSpec)
    return KeyPairData(SHA_256_WITH_RSA, keyPairGen.generateKeyPair())
}

fun ByteArray.generateKeyPairFor(alias: String): KeyPairData? {
    val messageObject = CBORObject.DecodeFromBytes(this)
    val protectedHeader = messageObject[0].GetByteString()

    // get algorithm from header
    return when (CBORObject.DecodeFromBytes(protectedHeader).get(1).AsInt32Value()) {
        ECDSA_256 -> provideEcKeyPairData(alias)
        RSA_PSS_256 -> provideRsaKeyPairData(alias)
        else -> null
    }
}

fun getKeyPairFor(alias: String): KeyPairData {
    val ks: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER)
    ks.load(null)
    val entry: KeyStore.Entry = ks.getEntry(alias, null)
    val privateKey: PrivateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
    val publicKey: PublicKey = ks.getCertificate(alias).publicKey
    val keyPair = KeyPair(publicKey, privateKey)
    val algo = when (privateKey.algorithm) {
        KeyProperties.KEY_ALGORITHM_EC -> SHA_256_WITH_ECDSA
        KeyProperties.KEY_ALGORITHM_RSA -> SHA_256_WITH_RSA
        else -> throw IllegalArgumentException()
    }
    return KeyPairData(algo, keyPair)
}

fun deleteKeyPairFor(alias: String) {
    val ks: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER)
    ks.load(null)
    ks.deleteEntry(alias)
}
