package controllers.security;

/**
 * Created by BatCoder_1 on 30/4/16.
 */

import controllers.routes;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SecuredUser extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
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
        return ctx.session().get("sessionId");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.index());
    }
}
