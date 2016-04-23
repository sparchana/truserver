package controllers;

import api.AddLeadRequest;
import models.entity.Leads;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result support() {
        String test = request().body().asText();
        return ok(views.html.support.render());
    }

    public static Result addLead() {
        Form<AddLeadRequest> userForm = Form.form(AddLeadRequest.class);
        AddLeadRequest addLeadRequest = userForm.bindFromRequest().get();
        return ok(toJson(Leads.addLead(addLeadRequest)));
    }

    public static Result leadFrom() {
        return ok(views.html.addLead.render());
    }
}
