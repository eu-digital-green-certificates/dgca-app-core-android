package dgca.verifier.app.decoder

import com.google.gson.annotations.SerializedName
import dgca.verifier.app.decoder.model.GreenCertificate

data class TestCase(

    @SerializedName("JSON")
    val eudgc: GreenCertificate? = null,

    @SerializedName("CBOR")
    val cborHex: String? = null,

    @SerializedName("COSE")
    val coseHex: String? = null,

    @SerializedName("COMPRESSED")
    val compressedHex: String? = null,

    @SerializedName("BASE45")
    val base45: String? = null,

    @SerializedName("PREFIX")
    val base45WithPrefix: String? = null,

    @SerializedName("2DCODE")
    val qrCodePng: String? = null,

    @SerializedName("TESTCTX")
    val context: TestContext,

    @SerializedName("EXPECTEDRESULTS")
    val expectedResult: TestExpectedResults
)

data class TestContext(

    @SerializedName("VERSION")
    val version: Int,

    @SerializedName("SCHEMA")
    val schema: String,

    @SerializedName("CERTIFICATE")
    val certificate: String?,

    @SerializedName("VALIDATIONCLOCK")
    val validationClock: String?,

    @SerializedName("DESCRIPTION")
    val description: String
)

data class TestExpectedResults(

    @SerializedName("EXPECTEDVALIDOBJECT")
    val schemaGeneration: Boolean? = null,

    @SerializedName("EXPECTEDSCHEMAVALIDATION")
    val schemaValidation: Boolean? = null,

    @SerializedName("EXPECTEDENCODE")
    val encodeGeneration: Boolean? = null,

    @SerializedName("EXPECTEDDECODE")
    val cborDecode: Boolean? = null,

    @SerializedName("EXPECTEDVERIFY")
    val coseSignature: Boolean? = null,

    @SerializedName("EXPECTEDUNPREFIX")
    val prefix: Boolean? = null,

    @SerializedName("EXPECTEDVALIDJSON")
    val json: Boolean? = null,

    @SerializedName("EXPECTEDCOMPRESSION")
    val compression: Boolean? = null,

    @SerializedName("EXPECTEDB45DECODE")
    val base45Decode: Boolean? = null,

    @SerializedName("EXPECTEDPICTUREDECODE")
    val qrDecode: Boolean? = null,

    @SerializedName("EXPECTEDEXPIRATIONCHECK")
    val expirationCheck: Boolean? = null,

    @SerializedName("EXPECTEDKEYUSAGE")
    val keyUsage: Boolean? = null
)