package controllers;

/**
 * Created by zero on 30/4/16.
 */

import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        Logger.info("User LoggedIn:"+ctx.session().get("sessionId"));
        return ctx.session().get("sessionId");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.supportAuth());
    }
}
