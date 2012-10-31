package com.dary.xmpp.cmd;

import java.io.IOException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;

import com.dary.xmpp.MainService;
import com.dary.xmpp.MyApp;
import com.dary.xmpp.ServiceManager;

public class RingCmd extends CmdBase {

	private static int OldVolume;
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
			OldVolume = ServiceManager.audManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			System.out.println(ServiceManager.audManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			// 音量如果小于12就调整为12,最大为15.
			if (OldVolume < 12) {
				ServiceManager.audManager.setStreamVolume(AudioManager.STREAM_MUSIC, 12, 0);
			}
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
				// 调整回原先用户设置的音量
				ServiceManager.audManager.setStreamVolume(AudioManager.STREAM_MUSIC, OldVolume, 0);
				mediaPlayer.stop();
				sendMessageAndUpdateView(chat, "Ring Stop");

			}
			// mediaPlayer.release();
		}
	}
}
