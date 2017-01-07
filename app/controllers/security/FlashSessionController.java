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
     * flash should be store for only one request
     */
    public static void setFlashInSession(String url) {
        if (url != null) {
                /* TODO find a better way to avoid adding 'non page rendering' api to flash memory*/
                /* since most of api don't render pages, they need not be saved for redirection */
            if (!(url.toLowerCase().startsWith("/get")) && !url.toLowerCase().contains("/api/")) {
                session().put("flash", url);
                Logger.info("flash set");
            }
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
            FlashSessionController.setFlashInSession(flashToPreserve);
        }
    }

    public static Result flashRedirect(){

        return redirect(getFlashFromSession());
    }

    public static void removeFlash(){
        Logger.info("flash removed");
        session().remove("flash");
    }
}
