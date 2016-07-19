package api.http;

/**
 * Created by zero on 13/5/16.
 */
public class CandidateKnownLanguage {
    public String id;
    public int s;
    public int rw;
    public int u; // languageIntel in Model

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getRw() {
        return rw;
    }

    public void setRw(int rw) {
        this.rw = rw;
    }
}
