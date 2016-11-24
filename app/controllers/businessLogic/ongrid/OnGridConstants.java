package controllers.businessLogic.ongrid;

import play.api.Play;

/**
 * Created by archana on 11/24/16.
 */
public class OnGridConstants {

    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

    public static final Integer COMMUNITY_ID =
            Integer.valueOf(play.Play.application().configuration().getString("ongrid.communityid "));

    public static final String AUTH_STRING =
            isDevMode ?
                    play.Play.application().configuration().getString("ongrid.staging.auth") :
                    play.Play.application().configuration().getString("ongrid.prod.auth") ;
}
