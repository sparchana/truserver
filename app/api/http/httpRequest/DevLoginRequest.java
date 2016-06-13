package api.http.httpRequest;

/**
 * Created by zero on 29/4/16.
 */
public class DevLoginRequest {
        private String adminid;

        private String adminpass;

        public String getAdminid() {
                return adminid;
        }

        public String getAdminpass() {
                return adminpass;
        }

        public void setAdminid(String adminid) {
                this.adminid = adminid;
        }

        public void setAdminpass(String adminpass) {
                this.adminpass = adminpass;
        }
}
