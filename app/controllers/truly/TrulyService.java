package controllers.truly;

import models.entity.truly.Truly;
import models.util.Base62;

/**
 * Created by zero on 27/1/17.
 *
 * Non static service class for multiple instantiation and bulk use
 */
public class TrulyService {
    public String getLongURL(String shortURL) {
        int trulyId = getIdFromShortURL(shortURL);
        Truly truly = Truly.find.where().eq("trulyId", trulyId).findUnique();
        if(truly == null){
            return null;
        }
        return truly.getLongUrl();
    }

    private int getIdFromShortURL(String shortUrl) {
        return Base62.toBase10(shortUrl);
    }

    /**
     * @param longURL       takes a long url and generates a shorter url
     * @return
     */
    protected String generateShortURL(String longURL) {
        if(longURL == null) {
            return null;
        }

        Truly truly = Truly.find.where().eq("longUrl", longURL).findUnique();

        if(truly == null) {
            truly = new Truly();
            truly.save();

            truly.setLongUrl(longURL);
            truly.setHash(Base62.fromBase10(truly.getTrulyId()));
            truly.setShortUrl("https://trujobs.in/u/"+truly.getHash());
            truly.save();
        } 
        return truly.getShortUrl();
    }

}
