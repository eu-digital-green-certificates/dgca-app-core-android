package dgca.verifier.app.decoder;
import org.junit.Assert;
import org.junit.Test;


public class CertificateCheckTest {

    @Test
    public void TestTestValidity() {
        dgca.verifier.app.decoder.model.Test test = new dgca.verifier.app.decoder.model.Test("12", "", "", "", "2021-02-20T12:34:56Z", "", "260415000", "", "", "", "");

        Assert.assertTrue(test.isDateInThePast());
        Assert.assertTrue(test.isResultNegative());
    }

    @Test
    public void RecoveryTestValidity() {
        dgca.verifier.app.decoder.model.RecoveryStatement recovery = new dgca.verifier.app.decoder.model.RecoveryStatement("","","","","","2021-03-04","");

        Assert.assertTrue(recovery.isDateInThePast());

        recovery = new dgca.verifier.app.decoder.model.RecoveryStatement("","","","","","2030-02-04","");

        Assert.assertTrue(!recovery.isDateInThePast());

        recovery = new dgca.verifier.app.decoder.model.RecoveryStatement("","","","","","2021-02-20T12:34:56Z","");

        Assert.assertTrue(recovery.isDateInThePast());

        recovery = new dgca.verifier.app.decoder.model.RecoveryStatement("","","","","","2007-12-03T10:15:30+01:00","");

        Assert.assertTrue(recovery.isDateInThePast());
    }
}

