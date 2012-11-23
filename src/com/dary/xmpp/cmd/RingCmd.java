package com.dary.xmpp.cmd;

import java.io.IOException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;

import com.dary.xmpp.MyApp;

public class RingCmd extends CmdBase {

	private static MediaPlayer mediaPlayer = new MediaPlayer();

	public static void Ring(Chat chat, Message message) {
		try {
			mediaPlayer.setDataSource(MyApp.getContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		// 不带参数
		if (message.getBody().indexOf(":") == -1) {
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			mediaPlayer.setLooping(true);
			try {
				mediaPlayer.prepare();
			} catch (IllegalStateException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
			mediaPlayer.start();
			sendMessageAndUpdateView(chat, "Ring Start");

		} else if (getArgs(message).equals("stop")) {
			// 这里需要先判断一下是否再播放中
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
//				 mediaPlayer.release();
				sendMessageAndUpdateView(chat, "Ring Stop");
			}
			else
			{
				sendMessageAndUpdateView(chat,"No Ringing!");
			}
		}
	}
}
