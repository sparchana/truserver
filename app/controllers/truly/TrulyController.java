package controllers.truly;

import play.mvc.Result;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

/**
 * Created by zero on 27/1/17.
 */
public class TrulyController {

    public static Result index(String shortUrl) {

        // TODO sanity check in ShortUrl
        if(shortUrl == null) {
            return redirect("/pageNotFound");
        }

        TrulyService trulyService = new TrulyService();
        String longUrl = trulyService.getLongURL(shortUrl);

        if(longUrl == null) {
            return redirect("/pageNotFound");
        }

        return redirect(longUrl);
    }

    // TODO add security class for internal support only to generate url
    public static Result renderGenerator() {
        return ok(views.html.truly.render());
    }

    public static Result compress(String longUrl) {
        if(longUrl == null) return badRequest();

        TrulyService trulyService = new TrulyService();

        return ok(trulyService.generateShortURL(longUrl));
    }

    public static Result expand(String shortUrl) {
        if(shortUrl == null) return badRequest();

        TrulyService trulyService = new TrulyService();

        return ok(trulyService.getLongURL(shortUrl));
    }
}
