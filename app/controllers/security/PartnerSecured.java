package controllers.security;

/**
 * Created by zero on 30/4/16.
 */

import api.ServerConstants;
import controllers.routes;
import models.entity.Developer;
import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Authenticator class for 'Support' role and above
 */
public class PartnerSecured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        String sessionId = ctx.session().get("sessionId");
        if(sessionId != null) {
            Developer developer = Developer.find.where().eq("developerSessionId", sessionId ).findUnique();

            if(developer != null ) {
                if (developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPER_ADMIN
                    || developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_ADMIN
                    || developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_REC
                    || developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_SUPPORT_ROLE
                    || developer.getDeveloperAccessLevel() == ServerConstants.DEV_ACCESS_LEVEL_PARTNER_ROLE)
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
    public Result onUnauthorized(Context ctx) {
        FlashSessionController.setFlashToSession(ctx.request().uri());

        return redirect(routes.Application.supportAuth());
    }
}
