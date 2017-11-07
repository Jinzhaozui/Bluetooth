package com.hexing.cardReaderBluetooth;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesTool {

    public static final String STRING_TYPE = "String";
    public static final String INT_TYPE = "Integer";
    public static final String LONG_TYPE = "Long";
    public static final String BOOLEAN_TYPE = "Boolean";

    public static void clearPreferences(Context ctx, String prefName) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = properties.edit();
        editor.clear();
    }

    public static void putValue(Context ctx, String prefName, String key, Object value) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = properties.edit();

        String typeName = value.getClass().getSimpleName();
        if (INT_TYPE.equals(typeName)) {
            editor.putInt(key, (Integer) value);
        } else if (BOOLEAN_TYPE.equals(typeName)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (STRING_TYPE.equals(typeName)) {
            editor.putString(key, (String) value);
        } else if (LONG_TYPE.equals(typeName)) {
            editor.putLong(key, (Long) value);
        } else {
            throw new RuntimeException();
        }
        editor.apply();
    }

    public static int getIntValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getInt(key, -1);
    }

    public static String getStringValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getString(key, null);
    }

    public static boolean getBooleanValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getBoolean(key, false);
    }

    public static long getLongValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getLong(key, -1);
    }

}
