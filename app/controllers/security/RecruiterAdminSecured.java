package controllers.security;

import api.ServerConstants;
import models.entity.Recruiter.RecruiterProfile;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import static play.mvc.Controller.session;

/**
 * Created by zero on 24/1/17.
 */
public class RecruiterAdminSecured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {

        if(ctx.session().get("sessionChannel") == null) {
            return null;
        }
        if(session().get("recruiterId") != null) {
            RecruiterProfile recruiterProfile = RecruiterProfile.find.where().eq("RecruiterProfileId", session().get("recruiterId")).findUnique();
            if (recruiterProfile != null && recruiterProfile.getRecruiterAccessLevel() == ServerConstants.RECRUITER_ACCESS_LEVEL_PRIVATE_ADMIN) {
                return ctx.session().get("recruiterId");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        // on Unauthorized access clear prev session data
        session().clear();

        // set flash for all non ajax call only
        if (!FlashSessionController.isRequestAjax(ctx.request())) {
            FlashSessionController.setFlashInSession(ctx.request().uri());
        }

        return redirect("/recruiter#signin");
    }
}
