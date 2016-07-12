package controllers.businessLogic;

import api.ServerConstants;
import models.entity.Developer;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by zero on 11/7/16.
 */
public class SuperSecured extends Security.Authenticator {
    @Override
    public String getUsername(Http.Context ctx) {
        String sessionId = ctx.session().get("sessionId");
        if(sessionId != null){
            Developer developer = Developer.find.where().eq("developerSessionId", sessionId ).findUnique();
            if(developer != null && developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPER_ADMIN) {
                return ctx.session().get("sessionId");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(controllers.routes.Application.supportAuth());
    }
}
