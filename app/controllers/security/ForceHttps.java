package controllers.security;

import play.mvc.Action;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by zero on 12/1/17.
 */
public class ForceHttps extends Action<Controller> {

    private static boolean enableSSL = (play.Play.application().configuration().getBoolean("enable.ssl"));

    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        final CompletionStage<Result> result;
        if (!ctx.request().secure() && enableSSL) {
            return CompletableFuture.supplyAsync(() -> redirect("https://" + ctx.request().host() + ctx.request().uri()) );
        }
        else {
            // let request proceed
            result = this.delegate.call(ctx);
        }
        return result;
    }
}