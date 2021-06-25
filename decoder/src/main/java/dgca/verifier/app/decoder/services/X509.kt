package dgca.verifier.app.decoder.services

import android.util.Base64
import dgca.verifier.app.decoder.model.CertificateType
import java.io.ByteArrayInputStream
import java.security.cert.*


class X509 {
    var OID_TEST = "1.3.6.1.4.1.1847.2021.1.1"
    var OID_ALT_TEST = "1.3.6.1.4.1.0.1847.2021.1.1"
    var OID_VACCINATION = "1.3.6.1.4.1.1847.2021.1.2"
    var OID_ALT_VACCINATION = "1.3.6.1.4.1.0.1847.2021.1.2"
    var OID_RECOVERY = "1.3.6.1.4.1.1847.2021.1.3"
    var OID_ALT_RECOVERY = "1.3.6.1.4.1.0.1847.2021.1.3"

    fun checkisSuitable(cert: String?, certType: CertificateType?): Boolean {
        val b64: ByteArray = org.bouncycastle.util.encoders.Base64.decode(cert)
        return isSuitable(b64, certType)
    }

    fun isSuitable(data: ByteArray?, certificateType: CertificateType?): Boolean {
        try {
            val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
            val cert: Certificate = cf.generateCertificate(ByteArrayInputStream(data))
            if (IsType(cert as X509Certificate)) {
                var extendedKeys = cert.getExtendedKeyUsage()
                when (certificateType) {
                    CertificateType.TEST-> return extendedKeys.contains(OID_TEST) || extendedKeys.contains(OID_ALT_TEST)
                    CertificateType.VACCINATION -> return extendedKeys.contains(OID_VACCINATION) || extendedKeys.contains(OID_ALT_VACCINATION)
                    CertificateType.RECOVERY -> return extendedKeys.contains(OID_RECOVERY) || extendedKeys.contains(OID_ALT_RECOVERY)
                }
            }
        } catch (e: CertificateException) {
            return false
        }
        return true
    }

    fun IsType(certificate: X509Certificate): Boolean {
        try {
            val extendedKeyUsage: List<String> = certificate.getExtendedKeyUsage() ?: return false

            return     extendedKeyUsage.contains(OID_TEST)
                    || extendedKeyUsage.contains(OID_ALT_TEST)
                    || extendedKeyUsage.contains(OID_RECOVERY)
                    || extendedKeyUsage.contains(OID_ALT_RECOVERY)
                    || extendedKeyUsage.contains(OID_VACCINATION)
                    || extendedKeyUsage.contains(OID_ALT_VACCINATION)
        } catch (e: CertificateParsingException) {
            return false
        }
        return false
    }
}