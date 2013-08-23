
package com.dary.xmpp.cmd;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;

import com.dary.xmpp.application.MyApp;

import org.jivesoftware.smack.Chat;

import java.io.IOException;

public class RingCmd extends CmdBase {

    private static MediaPlayer mediaPlayer;

    public static void Ring(Chat chat, String message) {
        // 不带参数
        if (!hasArgs(message)) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            try {
                mediaPlayer.setDataSource(MyApp.getContext(),
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            } catch (IllegalArgumentException e) {

                e.printStackTrace();
            } catch (SecurityException e) {

                e.printStackTrace();
            } catch (IllegalStateException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

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
            if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
                sendMessageAndUpdateView(chat, "No Ringing!");
            } else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                sendMessageAndUpdateView(chat, "Ring Stop");
            }
        }
    }
}
