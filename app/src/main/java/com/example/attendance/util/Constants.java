package com.example.attendance.util;

import android.os.Build;

public class Constants {
    public static final String BASE_URL = "https://9da2b614.ngrok.io";

    public static final int TIMESTAMP_VALID_FOR = 3;

    public static final String NETWORK_ERROR_MSG = "Couldn't reach the server";

    public static final String DEVICE_ID = "35" + //we make this look like a valid IMEI
            Build.BOARD.length()%10+ Build.BRAND.length()%10 +
            Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
            Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
            Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
            Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
            Build.TAGS.length()%10 + Build.TYPE.length()%10 +
            Build.USER.length()%10;

    public static final int PERMISSIONS_REQUEST_CAMERA = 0;
}
