package controllers;

/**
 * Created by zero on 30/4/16.
 */

import models.entity.Developer;
import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        String sessionId = ctx.session().get("sessionId");
        Developer developer = Developer.find.where().eq("developersessionid", sessionId).findUnique();
        if(developer!= null && developer.developerSessionId.equals(sessionId)){
            return ctx.session().get("sessionId");
        } else {
         return null;
        }
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.supportAuth());
    }
}
