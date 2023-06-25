package com.ivvlev.car.framework;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BundleHelper {

    private static final String SAVED_PREFS_BUNDLE_KEY_SEPARATOR = "§§";

    /**
     * Save a Bundle object to SharedPreferences.
     * <p>
     * NOTE: The editor must be writable, and this function does not commit.
     *
     * @param editor      SharedPreferences Editor
     * @param key         SharedPreferences key under which to store the bundle data. Note this key must
     *                    not contain '§§' as it's used as a delimiter
     * @param preferences Bundled preferences
     */
    public static void savePreferencesBundle(SharedPreferences.Editor editor, String key, Bundle preferences) {
        Set<String> keySet = preferences.keySet();
        Iterator<String> it = keySet.iterator();
        String prefKeyPrefix = key + SAVED_PREFS_BUNDLE_KEY_SEPARATOR;

        while (it.hasNext()) {
            String bundleKey = it.next();
            Object o = preferences.get(bundleKey);
            if (o == null) {
                editor.remove(prefKeyPrefix + bundleKey);
            } else if (o instanceof Integer) {
                editor.putInt(prefKeyPrefix + bundleKey, (Integer) o);
            } else if (o instanceof Long) {
                editor.putLong(prefKeyPrefix + bundleKey, (Long) o);
            } else if (o instanceof Boolean) {
                editor.putBoolean(prefKeyPrefix + bundleKey, (Boolean) o);
            } else if (o instanceof CharSequence) {
                editor.putString(prefKeyPrefix + bundleKey, ((CharSequence) o).toString());
            } else if (o instanceof Bundle) {
                savePreferencesBundle(editor, prefKeyPrefix + bundleKey, ((Bundle) o));
            } else {
                Log.w("BundleHelper", "Unsupported type: " + o.getClass().getCanonicalName());
            }
        }
    }

    /**
     * Load a Bundle object from SharedPreferences.
     * (that was previously stored using savePreferencesBundle())
     * <p>
     * NOTE: The editor must be writable, and this function does not commit.
     *
     * @param sharedPreferences SharedPreferences
     * @param key               SharedPreferences key under which to store the bundle data. Note this key must
     *                          not contain '§§' as it's used as a delimiter
     * @return bundle loaded from SharedPreferences
     */
    public static Bundle loadPreferencesBundle(SharedPreferences sharedPreferences, String key) {
        Bundle bundle = new Bundle();
        Map<String, ?> all = sharedPreferences.getAll();
        Iterator<String> it = all.keySet().iterator();
        String prefKeyPrefix = key + SAVED_PREFS_BUNDLE_KEY_SEPARATOR;
        Set<String> subBundleKeys = new HashSet<String>();

        while (it.hasNext()) {

            String prefKey = it.next();

            if (prefKey.startsWith(prefKeyPrefix)) {
                String bundleKey = StringUtils.removeStart(prefKey, prefKeyPrefix);

                if (!bundleKey.contains(SAVED_PREFS_BUNDLE_KEY_SEPARATOR)) {

                    Object o = all.get(prefKey);
                    if (o == null) {
                        // Ignore null keys
                    } else if (o instanceof Integer) {
                        bundle.putInt(bundleKey, (Integer) o);
                    } else if (o instanceof Long) {
                        bundle.putLong(bundleKey, (Long) o);
                    } else if (o instanceof Boolean) {
                        bundle.putBoolean(bundleKey, (Boolean) o);
                    } else if (o instanceof CharSequence) {
                        bundle.putString(bundleKey, ((CharSequence) o).toString());
                    }
                } else {
                    // Key is for a sub bundle
                    String subBundleKey = StringUtils.substringBefore(bundleKey, SAVED_PREFS_BUNDLE_KEY_SEPARATOR);
                    subBundleKeys.add(subBundleKey);
                }
            } else {
                // Key is not related to this bundle.
            }
        }

        // Recursively process the sub-bundles
        for (String subBundleKey : subBundleKeys) {
            Bundle subBundle = loadPreferencesBundle(sharedPreferences, prefKeyPrefix + subBundleKey);
            bundle.putBundle(subBundleKey, subBundle);
        }


        return bundle;
    }
}
