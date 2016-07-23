package controllers.security;

import api.ServerConstants;
import controllers.routes;
import models.entity.Developer;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by batcoder1 on 13/7/16.
 */

/**
 * Authenticator class for 'Admin' role and above
 */
public class AdminSecured extends Security.Authenticator
{

@Override
public String getUsername(Http.Context ctx)
{
        String sessionId = ctx.session().get("sessionId");
        if (sessionId != null) {
            Developer developer = Developer.find.where().eq("developerSessionId", sessionId ).findUnique();

            if (developer != null && (developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPER_ADMIN
                    || developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_ADMIN))
            {
                return ctx.session().get("sessionId");
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
}

@Override
public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Application.supportAuth());
        }
        }
