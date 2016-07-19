package controllers;

/**
 * Created by zero on 30/4/16.
 */

import api.ServerConstants;
import models.entity.Developer;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        String sessionId = ctx.session().get("sessionId");
        if(sessionId != null){
            Developer developer = Developer.find.where().eq("developerSessionId", sessionId ).findUnique();
            if(developer != null && (developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPPORT_ROLE ||  developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPER_ADMIN ||  developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_REC)) {
                return ctx.session().get("sessionId");
            } else {
                return null;
            }
        } else {
         return null;
        }
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.supportAuth());
    }
}
