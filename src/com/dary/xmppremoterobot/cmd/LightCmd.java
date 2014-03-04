
package com.dary.xmppremoterobot.cmd;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;

import com.dary.xmppremoterobot.ui.MainActivity;

import org.jivesoftware.smack.Chat;

import java.io.IOException;

public class LightCmd extends CmdBase {
    private static Camera sCamera = null;

    private static void init() {
        if (sCamera == null) {
            sCamera = Camera.open();
        }
        SurfaceHolder mHolder = MainActivity.surfaceview.getHolder();
        try {
            sCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Light(Chat chat, String message) {

        if (!hasArgs(message) || getArgs(message).equals("on")) {
            init();
            Parameters params = sCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            sCamera.setParameters(params);
            sCamera.startPreview();
            sendMessageAndUpdateView(chat, "Light On");

        } else if (getArgs(message).equals("off")) {
            if (sCamera == null) {
                sendMessageAndUpdateView(chat, "No Lighting!!!");
            } else {
                Parameters params = sCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                sCamera.setParameters(params);
                sCamera.stopPreview();
                sCamera.release();
                sCamera = null;
                sendMessageAndUpdateView(chat, "Light Off");
            }
        }
    }
}
