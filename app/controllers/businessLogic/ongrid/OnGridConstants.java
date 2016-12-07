package controllers.businessLogic.ongrid;

import play.api.Play;

/**
 * Created by archana on 11/24/16.
 */
public class OnGridConstants {

    private static boolean isDevMode = Play.isDev(Play.current()) || Play.isTest(Play.current());

    public static final Integer COMMUNITY_ID =
            isDevMode ?
            Integer.valueOf(play.Play.application().configuration().getString("ongrid.staging.communityid")) :
                    Integer.valueOf(play.Play.application().configuration().getString("ongrid.prod.communityid")) ;

    public static final String AUTH_STRING =
            isDevMode ?
                    play.Play.application().configuration().getString("ongrid.staging.auth") :
                    play.Play.application().configuration().getString("ongrid.prod.auth") ;


    public static final String BASE_URL =
            isDevMode ?
                    play.Play.application().configuration().getString("ongrid.staging.baseURL") :
                    play.Play.application().configuration().getString("ongrid.prod.baseURL") ;
}
