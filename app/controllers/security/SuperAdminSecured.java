package controllers.security;

import api.ServerConstants;
import controllers.routes;
import models.entity.Developer;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Authenticator class for 'Super Admin' role
 */
public class SuperAdminSecured extends Security.Authenticator
{

@Override
public String getUsername(Http.Context ctx)
{
    String sessionId = ctx.session().get("sessionId");
    if (sessionId != null) {
        Developer developer = Developer.find.where().eq("developerSessionId", sessionId ).findUnique();

        if (developer != null) {
            if (developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPER_ADMIN)
            {
                return ctx.session().get("sessionId");
            }
            else {
                Logger.warn("SECURITY WARNING: User " + developer.getDeveloperName() + " tried accessing " + ctx.request().uri());
                return null;
            }
        }
    }
    return null;
}

@Override
public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Application.supportAuth());
        }
        }
