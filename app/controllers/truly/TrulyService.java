package controllers.truly;

import models.entity.truly.Truly;
import models.util.Base62;

import static api.ServerConstants.BASE_URL;

/**
 * Created by zero on 27/1/17.
 *
 * Service class with not Static methods, for multiple instantiation and bulk use
 */
public class TrulyService {

    public TrulyService() {
    }

    private final String POINT_URL = "/t/";
    public String getLongURL(String shortURL) {

        long trulyId = getIdFromShortURL(shortURL);
        Truly truly = Truly.find.where().eq("trulyId", trulyId).findUnique();
        if(truly == null){
            return null;
        }
        // update hit rate count
        truly.setHitRate(truly.getHitRate()+1);
        truly.save();

        return truly.getLongUrl();
    }

    private long getIdFromShortURL(String shortUrl) {
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
