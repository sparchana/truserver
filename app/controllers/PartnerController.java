package controllers;

import api.ServerConstants;
import api.http.FormValidator;
import api.http.httpRequest.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.businessLogic.*;
import controllers.security.SecuredUser;
import models.entity.Candidate;
import models.entity.Lead;
import models.entity.Partner;
import models.entity.Static.PartnerType;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.util.List;

import static play.libs.Json.toJson;

import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

/**
 * Created by adarsh on 9/9/16.
 */
public class PartnerController {
    public static Result partnerIndex() {
        String sessionId = session().get("sessionId");
        if(sessionId != null){
            return redirect("/partner/home");
        }
        return ok(views.html.partner_index.render());
    }

    public static Result renderPagePartnerNavBar() {
        return ok(views.html.partner_nav_bar.render());
    }

    public static Result partnerSignUp() {
        JsonNode req = request().body().asJson();
        PartnerSignUpRequest partnerSignUpRequest = new PartnerSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            partnerSignUpRequest = newMapper.readValue(req.toString(), PartnerSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("JSON req: " + req);

        InteractionService.InteractionChannelType channelType = InteractionService.InteractionChannelType.SELF;
        return ok(toJson(PartnerService.signUpPartner(partnerSignUpRequest, channelType, ServerConstants.LEAD_SOURCE_WEBSITE)));

    }

    public static Result addPassword() {
        JsonNode req = request().body().asJson();
        PartnerSignUpRequest partnerSignUpRequest = new PartnerSignUpRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            partnerSignUpRequest = newMapper.readValue(req.toString(), PartnerSignUpRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("JSON req: " + req);

        String partnerMobile = partnerSignUpRequest.getpartnerAuthMobile();
        String partnerPassword = partnerSignUpRequest.getpartnerPassword();

        return ok(toJson(PartnerAuthService.savePassword(partnerMobile, partnerPassword, InteractionService.InteractionChannelType.SELF)));
    }

    public static Result loginSubmit() {
        JsonNode req = request().body().asJson();
        LoginRequest loginRequest = new LoginRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            loginRequest = newMapper.readValue(req.toString(), LoginRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("req JSON: " + req );
        String loginMobile = loginRequest.getCandidateLoginMobile();
        String loginPassword = loginRequest.getCandidateLoginPassword();
        return ok(toJson(PartnerService.login(loginMobile, loginPassword, InteractionService.InteractionChannelType.SELF)));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result partnerHome() {
        return ok(views.html.partner_home.render());
    }

    public static Result renderPagePartnerLoggedInNavbar() {
        return ok(views.html.partner_logged_in_nav_bar.render());
    }

    public static Result findPartnerAndSendOtp() {
        JsonNode req = request().body().asJson();
        ResetPasswordResquest resetPasswordResquest = new ResetPasswordResquest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            resetPasswordResquest = newMapper.readValue(req.toString(), ResetPasswordResquest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String partnerMobile = resetPasswordResquest.getResetPasswordMobile();
        Logger.info("==> " + partnerMobile);

        return ok(toJson(PartnerService.findPartnerAndSendOtp(partnerMobile, InteractionService.InteractionChannelType.SELF)));
    }

    public static Result getAllPartnerType() {
        List<PartnerType> partnerTypeList = PartnerType.find.all();
        return ok(toJson(partnerTypeList));
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result getPartnerProfileInfo() {
        Partner partner = Partner.find.where().eq("partner_id", session().get("partnerId")).findUnique();
        if(partner != null) {
            return ok(toJson(partner));
        }
        return ok("0");
    }

    public static Result checkPartnerSession() {
        String sessionPartnerId = session().get("partnerId");
        if(sessionPartnerId != null){
            return ok("1");
        } else{
            return ok("0");
        }
    }

    @Security.Authenticated(SecuredUser.class)
    public static Result partnerEditProfile() {
        return ok(views.html.partner_edit_proifile.render());
    }

    public static Result partnerUpdateBasicProfile() {
        JsonNode req = request().body().asJson();
        AddPartnerRequest addPartnerRequest = new AddPartnerRequest();
        ObjectMapper newMapper = new ObjectMapper();
        try {
            addPartnerRequest = newMapper.readValue(req.toString(), AddPartnerRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Req JSON : " + req);
        String partnerId = session().get("partnerId");
        Partner partner = Partner.find.where().eq("partner_id", partnerId).findUnique();
        if(partner != null){
            addPartnerRequest.setPartnerMobile(partner.getPartnerMobile());
            return ok(toJson(PartnerService.createPartnerProfile(addPartnerRequest, InteractionService.InteractionChannelType.SELF, ServerConstants.UPDATE_BASIC_PROFILE)));
        } else{
            return ok("0");
        }
    }
}
