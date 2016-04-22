package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
    
    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result support() { return ok(views.html.support.render()); }
}
