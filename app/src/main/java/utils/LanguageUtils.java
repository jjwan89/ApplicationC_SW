package utils;

import android.content.res.Configuration;
import android.os.LocaleList;
import android.util.Log;

import java.lang.reflect.Method;

public class LanguageUtils {

    public  static  void changeSystemLanguage(LocaleList locale) {
        if (locale != null) {
            try {
                Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
                Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
                Object objIActivityManager = getDefault.invoke(classActivityManagerNative);
                Class classIActivityManager = Class.forName("android.app.IActivityManager");
                Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);
                config.setLocales(locale);
                Class[] clzParams = {Configuration.class};
                Method updateConfiguration = classIActivityManager.getDeclaredMethod("updatePersistentConfiguration", clzParams);
                updateConfiguration.invoke(objIActivityManager, config);
            } catch (Exception e) {
                Log.d("language", "changeSystemLanguage: " + e.getLocalizedMessage());
            }
        }
    }

}
