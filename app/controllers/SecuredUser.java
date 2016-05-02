package controllers;

/**
 * Created by BatCoder_1 on 30/4/16.
 */

import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SecuredUser extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        Logger.info("dev session id in Secured Class is "+ctx.session().get("sessionId"));
        return ctx.session().get("sessionId");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.index());
    }
}
