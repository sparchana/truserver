package controllers;

/**
 * Created by dodo on 12/12/16.
 */
public class SharedSettings {
    private static Global globalSettings;

    public static Global getGlobalSettings() {
        return globalSettings;
    }

    public static void setGlobalSettings(Global globalSettings) {
        SharedSettings.globalSettings = globalSettings;
    }
}
