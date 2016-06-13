import models.entity.Candidate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;


/**
 *
 * Simple (JUnit) tests that can call all parts of a Play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
public class ApplicationTest {

    @Test
    public void aTest() {
        assertEquals(2, 1 + 1); // A really important thing to test
    }

    @Test
    public void testUsers() {
        running(fakeApplication(), () -> {
            Candidate existingCandidate = Candidate.find.where().eq("t0.candidateMobile", "+918787878787").findUnique();
            assertTrue(existingCandidate == null);
        });
    }
}
