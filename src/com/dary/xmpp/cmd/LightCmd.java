package com.dary.xmpp.cmd;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class LightCmd extends CmdBase {
	private static Camera sCamera = null;

	public static void Light(Chat chat, Message message) {
		if (!hasArgs(message) || getArgs(message).equals("on")) {
			if (sCamera == null) {
				sCamera = Camera.open();
			}

			Parameters params = sCamera.getParameters();
			params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			sCamera.setParameters(params);
			sendMessageAndUpdateView(chat, "Light On");

		} else if (getArgs(message).equals("off")) {
			if (sCamera == null) {
				sendMessageAndUpdateView(chat, "No Lighting!!!");
			} else {
				Parameters params = sCamera.getParameters();
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				sCamera.setParameters(params);
				sCamera.release();
				sCamera = null;
				sendMessageAndUpdateView(chat, "Light Off");
			}
		}
	}

}
