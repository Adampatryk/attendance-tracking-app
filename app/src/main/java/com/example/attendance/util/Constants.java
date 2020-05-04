package com.example.attendance.util;

import android.os.Build;

public class Constants {
    public static final String BASE_URL = "https://19a88e47.ngrok.io";

    public static final String DEVICE_ID = "" +
            Build.BOARD.length()%10+ Build.BRAND.length()%10 +
            Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
            Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
            Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
            Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
            Build.TAGS.length()%10 + Build.TYPE.length()%10 +
            Build.USER.length()%10;

    public static final int PERMISSIONS_REQUEST_CAMERA = 0;
    public static final String NETWORK_ERROR_MSG = "Couldn't reach the server";
    public static final int TIMESTAMP_VALID_FOR = 3;
}
