package controllers.truly;

import models.entity.truly.Truly;
import models.util.Base62;
import play.Logger;
import play.api.Play;

/**
 * Created by zero on 27/1/17.
 *
 * Non static service class for multiple instantiation and bulk use
 */
public class TrulyService {
    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());
    private String BASE_URL = "http://localhost:9000";

    public TrulyService() {
        if(!isDevMode) {
            BASE_URL = "https://trujobs.in";
        }
    }

    private final String POINT_URL = "/t/";
    public String getLongURL(String shortURL) {

        Logger.info("shortURL: "+ shortURL);
        int trulyId = getIdFromShortURL(shortURL);
        Truly truly = Truly.find.where().eq("trulyId", trulyId).findUnique();
        if(truly == null){
            return null;
        }
        // update hit rate count
        truly.setHitRate(truly.getHitRate()+1);
        truly.save();

        return truly.getLongUrl();
    }

    private int getIdFromShortURL(String shortUrl) {
        if(shortUrl == null || shortUrl.trim().isEmpty()) return 0;
        if(shortUrl.contains("/t/")) {
            shortUrl = shortUrl.substring(shortUrl.indexOf("/t/")+3);
        }
        return Base62.toBase10(shortUrl);
    }

    /**
     * @param longURL       takes a long url and generates a shorter url
     * @return
     */
    public String generateShortURL(String longURL) {
        if(longURL == null) {
            return null;
        }

        Truly truly = Truly.find.where().eq("longUrl", longURL).findUnique();

        if(truly == null) {
            truly = new Truly();
            truly.save();

            truly.setLongUrl(longURL);
            truly.setHash(Base62.fromBase10(truly.getTrulyId()));
            truly.setShortUrl(BASE_URL + POINT_URL +truly.getHash());
            truly.save();
        }
        return BASE_URL + POINT_URL + truly.getHash();
    }

}
