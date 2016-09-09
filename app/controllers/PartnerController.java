package controllers;

import play.mvc.Result;

import static play.mvc.Results.ok;

/**
 * Created by adarsh on 9/9/16.
 */
public class PartnerController {
    public static Result partnerIndex() {
        return ok(views.html.partner_index.render());
    }

    public static Result renderPagePartnerNavBar() {
        return ok(views.html.partner_nav_bar.render());
    }
}
