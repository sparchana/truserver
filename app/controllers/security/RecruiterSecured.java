package controllers.security;

import controllers.routes;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by zero on 22/12/16.
 */
public class RecruiterSecured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {

        //Logger.info("dev session id in Secured Class is "+ctx.session().get("sessionId"));
        /* TODO getSessionId and match it in auth table + separate partner from using this secured class, modify old partner secured class and make use of that for all partner api end-points */
        /*
        String sessionId = ctx.session().get("sessionId");
        String candidateId = ctx.session().get("candidateId");
        if(sessionId != null && !sessionId.isEmpty()){
            Auth auth = Auth.find.where().eq("authSessionId", sessionId).findUnique();
            if (auth != null && candidateId!=null && Long.parseLong(candidateId) == auth.getCandidateId()){
                return ctx.session().get("sessionId");
            }
        }
        */
        if(ctx.session().get("sessionChannel") == null) {
            return null;
        }
        return ctx.session().get("sessionId");
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.RecruiterController.recruiterIndex());
    }
}
