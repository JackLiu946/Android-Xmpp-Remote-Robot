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

		// ��������
		if (message.getBody().indexOf(":") == -1) {
			OldVolume = ServiceManager.audManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			System.out.println(ServiceManager.audManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			// �������С��12�͵���Ϊ12,���Ϊ15.
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
			// ������Ҫ���ж�һ���Ƿ��ٲ�����
			if (mediaPlayer.isPlaying()) {
				// ������ԭ���û����õ�����
				ServiceManager.audManager.setStreamVolume(AudioManager.STREAM_MUSIC, OldVolume, 0);
				mediaPlayer.stop();
				sendMessageAndUpdateView(chat, "Ring Stop");

			}
			// mediaPlayer.release();
		}
	}
}
