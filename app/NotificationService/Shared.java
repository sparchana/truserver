package NotificationService;

import play.*;
/**
 * Created by dodo on 12/12/16.
 */
public class Shared {
    private static Global globalSettings;

    public static Global getGlobalSettings() {
        return globalSettings;
    }

    public static void setGlobalSettings(Global globalSettings) {
        Shared.globalSettings = globalSettings;
    }
}
