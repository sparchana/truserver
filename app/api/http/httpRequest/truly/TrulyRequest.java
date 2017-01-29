package api.http.httpRequest.truly;

/**
 * Created by zero on 28/1/17.
 */
public class TrulyRequest {
    public String longUrl;
    public String shortUrl;

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
