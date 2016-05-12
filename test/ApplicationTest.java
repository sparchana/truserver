import models.entity.OM.IDProofReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 * Simple (JUnit) tests that can call all parts of a Play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertEquals(2,a);
    }

    @Test
    public void aTest() {
        assertEquals(2, 1 + 1); // A really important thing to test
    }

    @Test
    public void testUsers() {
        IDProofReference idProofReference = IDProofReference.find.where().eq("idproofreferenceid", 1).findUnique();
        assertEquals(idProofReference.idProof, 1);
    }
}
