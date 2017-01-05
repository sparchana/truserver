package controllers.security;

import play.Logger;
import play.mvc.Result;

import static play.mvc.Controller.session;
import static play.mvc.Results.redirect;

/**
 * Created by zero on 5/1/17.
 */
public class FlashSessionController {

    public static String getFlashFromSession() {
        String flashUrl = null;

        if (session().get("flash") != null) {
            flashUrl = session().get("flash");
            session().remove("flash");
        }

        return flashUrl;
    }

    /**
     *
     * @param url
     * it only set if url is not null, else it would throw NPE
     * put method overrides flash value
     *
     */
    public static void setFlashToSession(String url) {

        if (url != null) {
            session().put("flash", url);
        }
    }

    public static String flashPeek() {

        return session().get("flash");
    }

    public static boolean isEmpty() {

        return session().get("flash") == null;
    }

    public static void clearSessionExceptFlash(){

        if (session()!= null) {

            String flashToPreserve = FlashSessionController.flashPeek();
            Logger.info("flashToPreserve: " + flashToPreserve);
            session().clear();
            FlashSessionController.setFlashToSession(flashToPreserve);
        }
    }

    public static Result flashRedirect(){

        return redirect(getFlashFromSession());
    }
}
