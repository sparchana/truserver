import com.avaje.ebean.Ebean;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import play.Application;

import java.io.IOException;

import static play.test.Helpers.*;

/**
 * Created by zero on 7/6/16.
 */
public class BaseModelTest {
    public static Application app;

    public static String createDdl = "";
    public static String dropDdl = "";

    @BeforeClass
    public static void startApp() throws IOException {
        // Reading the evolution file
        String evolutionContent = FileUtils.readFileToString(
                app.getWrappedApplication().getFile("conf/evolutions/default/1.sql"));

        // Splitting the String to get Create & Drop DDL
        String[] splittedEvolutionContent = evolutionContent.split("# --- !Ups");
        String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
        createDdl = upsDowns[0];
        dropDdl = upsDowns[1];

        app = fakeApplication(inMemoryDatabase());
        start(app);
    }

    @AfterClass
    public static void stopApp() {
        stop(app);
    }

    @Before
    public void createCleanDb() {
        Ebean.execute(Ebean.createCallableSql(dropDdl));
        Ebean.execute(Ebean.createCallableSql(createDdl));
    }
}
