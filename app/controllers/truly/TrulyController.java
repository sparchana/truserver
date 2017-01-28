package controllers.truly;

import api.http.httpRequest.truly.TrulyRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.security.PartnerInternalSecured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;

/**
 * Created by zero on 27/1/17.
 */
public class TrulyController extends Controller {
    /**
     * @param shortUrl Responsible for resolving a given short url
     * @return
     */
    public static Result index(String shortUrl) {

        if(shortUrl == null || shortUrl.trim().isEmpty()) {
            return redirect("/pageNotFound");
        }

        TrulyService trulyService = new TrulyService();
        String longUrl = trulyService.getLongURL(shortUrl);

        if(longUrl == null) {
            return redirect("/pageNotFound");
        }


        if(longUrl.contains("//")){
            return redirect(longUrl);
        } else {
            return redirect("//" + longUrl);
        }
    }

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result renderGenerator() {
        return ok(views.html.truly.render());
    }

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result compress() {
        JsonNode req = request().body().asJson();
        ObjectMapper newMapper = new ObjectMapper();
        TrulyRequest trulyRequest = new TrulyRequest();

        try {
            trulyRequest = newMapper.readValue(req.toString(), TrulyRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String longUrl = trulyRequest.getLongUrl();
        if(longUrl == null) return badRequest();

        TrulyService trulyService = new TrulyService();

        String shortURL = trulyService.generateShortURL(longUrl);

        return ok(shortURL == null ? "error ": shortURL);
    }

    @Security.Authenticated(PartnerInternalSecured.class)
    public static Result expand(String shortUrl) {
        if(shortUrl == null) return badRequest();

        TrulyService trulyService = new TrulyService();

        if(trulyService == null) return ok("not found");

        String longURL = trulyService.getLongURL(shortUrl);
        return ok(longURL == null ? "not found ": longURL);
    }
}
