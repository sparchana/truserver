package api.http.httpResponse.hirewand;

/**
 * Created by hawk on 14/1/17.
 */
public class UploadResumeResponse {
    String resume;
    Integer status;
    String msg;
    String key;

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
