package dgca.verifier.app.decoder

import com.fasterxml.jackson.databind.ObjectMapper
import dgca.verifier.app.decoder.base45.Base45Service
import dgca.verifier.app.decoder.base45.DefaultBase45Service
import dgca.verifier.app.decoder.cbor.CborService
import dgca.verifier.app.decoder.cbor.DefaultCborService
import dgca.verifier.app.decoder.compression.CompressorService
import dgca.verifier.app.decoder.compression.DefaultCompressorService
import dgca.verifier.app.decoder.cose.CoseService
import dgca.verifier.app.decoder.cose.CryptoService
import dgca.verifier.app.decoder.cose.DefaultCoseService
import dgca.verifier.app.decoder.cose.VerificationCryptoService
import dgca.verifier.app.decoder.model.GreenCertificate
import dgca.verifier.app.decoder.model.VerificationResult
import dgca.verifier.app.decoder.prefixvalidation.DefaultPrefixValidationService
import dgca.verifier.app.decoder.prefixvalidation.PrefixValidationService
import dgca.verifier.app.decoder.schema.DefaultSchemaValidator
import dgca.verifier.app.decoder.schema.SchemaValidator
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalToIgnoringCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

@ExperimentalUnsignedTypes
class CertificateTestRunner {

    private lateinit var prefixValidationService: PrefixValidationService
    private lateinit var base45Service: Base45Service
    private lateinit var compressorService: CompressorService
    private lateinit var cryptoService: CryptoService
    private lateinit var coseService: CoseService
    private lateinit var schemaValidator: SchemaValidator
    private lateinit var cborService: CborService

    @BeforeEach
    fun setUp() {
        prefixValidationService = DefaultPrefixValidationService()
        base45Service = DefaultBase45Service()
        compressorService = DefaultCompressorService()
        cryptoService = VerificationCryptoService()
        coseService = DefaultCoseService()
        schemaValidator = DefaultSchemaValidator()
        cborService = DefaultCborService()
    }

    @ParameterizedTest
    @MethodSource("verificationProvider")
    fun verification(filename: String, case: TestCase) {
        println("Executing verification test case \"${filename}\": \"${case.context.description}\"")
        if (case.context.certificate == null) throw IllegalArgumentException("certificate")

        var greenCertificate: GreenCertificate? = null
        val verificationResult = VerificationResult()
        val qrCode = case.base45WithPrefix ?: ""
        val base45 = prefixValidationService.decode(qrCode, verificationResult)
        val compressedCose = base45Service.decode(base45, verificationResult)
        val cose = compressorService.decode(compressedCose, verificationResult)
        val coseData = coseService.decode(cose, verificationResult)
        if (coseData != null) {
            val kid = coseData.kid
            schemaValidator.validate(coseData.cbor, verificationResult)
            greenCertificate = cborService.decode(coseData.cbor, verificationResult)
            val certificate = case.context.certificate.base64ToX509Certificate()
            if (certificate != null) {
                cryptoService.validate(cose, certificate, verificationResult)
            }
        }

        case.expectedResult.qrDecode?.let {
            if (it) {
                assertThat(qrCode, equalTo(case.base45WithPrefix))
            } else {
                assertThat(verificationResult.isValid(), equalTo(false))
            }
        }
        case.expectedResult.prefix?.let {
            if (it) {
                assertThat(verificationResult.contextPrefix, notNullValue())
            } else {
                assertThat(verificationResult.contextPrefix, nullValue())
            }
        }
        case.expectedResult.base45Decode?.let {
            assertThat(verificationResult.base45Decoded, equalTo(it))
            if (it) {
                assertThat(base45, equalToIgnoringCase(case.base45))
            }
        }
        case.expectedResult.compression?.let {
            assertThat(verificationResult.zlibDecoded, equalTo(it))
            if (it) {
                assertThat(compressedCose.toHexString(), equalTo(case.compressedHex))
            }
        }
        case.expectedResult.coseSignature?.let {
            assertThat(verificationResult.coseVerified, equalTo(it))
            if (it) {
                assertThat(cose.toHexString(), equalTo(case.coseHex))
            }
        }
        case.expectedResult.cborDecode?.let {
            assertThat(verificationResult.cborDecoded, equalTo(it))
            if (it) {
                assertThat(greenCertificate, equalTo(case.eudgc))
            }
        }

//        "EXPECTEDDECODE": true,
//        "EXPECTEDVERIFY": true,
//        "EXPECTEDUNPREFIX": true,
//        "EXPECTEDVALIDJSON": true,
//        "EXPECTEDCOMPRESSION": true,
//        "EXPECTEDB45DECODE": true


//        case.expectedResult.json?.let {
//            assertThat(chainResult.eudgc.removeEmptyArrays(), equalTo(case.eudgc?.toEuSchema()))
//            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
//        }
//        case.expectedResult.schemaValidation?.let {
//            // TODO Implement schema validation
//            //assertThat(verificationResult.cborDecoded, equalTo(it))
//            //if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
//        }
//        case.expectedResult.expirationCheck?.let {
//            if (it) assertThat(decision, equalTo(VerificationDecision.GOOD))
//            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
//        }
//        case.expectedResult.keyUsage?.let {
//            if (it) assertThat(decision, equalTo(VerificationDecision.GOOD))
//            if (!it) assertThat(decision, equalTo(VerificationDecision.FAIL))
//        }
    }

    companion object {

        @JvmStatic
        @Suppress("unused")
        fun verificationProvider(): List<Arguments> {
            val testcaseFiles = mutableListOf<File>()
            File("src/test/resources/").walkTopDown().forEach {
                if (it.isFile) {
                    testcaseFiles.add(it)
                }
            }

            return testcaseFiles.map {
                println("Loading $it...")
                val text = it.bufferedReader().readText()
                Arguments.of(it.name, ObjectMapper().readValue(text, TestCase::class.java))
            }
        }
    }
}
