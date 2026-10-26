package com.levipack.freecam;

import java.nio.ByteBuffer;

public final class NativeFreecamBridge {
    static {
        System.loadLibrary("freecam_engine");
    }

    public static native void nativeUpdateCameraMatrix(
        double posX, double posY, double posZ,
        float yaw, float pitch,
        float fov, float alpha
    );

    public static native void nativeOnFreecamStateChanged(boolean active);
    public static native void nativeFreeDirectBuffer(ByteBuffer buffer);
}
