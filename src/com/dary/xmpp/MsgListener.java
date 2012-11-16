package com.dary.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Message;

import android.os.Bundle;

import com.dary.xmpp.cmd.CallLogCmd;
import com.dary.xmpp.cmd.CmdBase;
import com.dary.xmpp.cmd.CmdCmd;
import com.dary.xmpp.cmd.CopyCmd;
import com.dary.xmpp.cmd.GpsCmd;
import com.dary.xmpp.cmd.HelpCmd;
import com.dary.xmpp.cmd.InfoCmd;
import com.dary.xmpp.cmd.LightCmd;
import com.dary.xmpp.cmd.PhotoCmd;
import com.dary.xmpp.cmd.RejectCmd;
import com.dary.xmpp.cmd.RingCmd;
import com.dary.xmpp.cmd.RingerModeCmd;
import com.dary.xmpp.cmd.SelCmd;
import com.dary.xmpp.cmd.SmsCmd;
import com.dary.xmpp.cmd.SmsToCmd;

class MsgListener implements MessageListener {

	// ��Ϣ����
	public void processMessage(Chat chat, Message message) {
		// �յ���Ϣ֮����Ϣ���ݷ���bundle,������Ϣȥ����UI
		if (null != XmppActivity.MsgHandler) {
			android.os.Message msg = new android.os.Message();
			msg.what = XmppActivity.RECEIVE_MESSAGE;
			Bundle bundle = new Bundle();
			bundle.putString("msg", message.getBody());
			msg.setData(bundle);
			XmppActivity.MsgHandler.sendMessage(msg);
		}
		Roster roster = MainService.connection.getRoster();
		// ��ӡ�յ�����Ϣ
		System.out.println("���ܵ�����Ϣ: " + message.getBody());

		String cmd = getCmd(message);

		// Light����
		if (cmd.equals("light")) {
			LightCmd.Light(chat, message);
		}

		// Ring����
		else if (cmd.equals("ring")) {
			RingCmd.Ring(chat, message);
		}
		// CallLog����.����ҪMessage����
		else if (cmd.equals("calllog")) {
			CallLogCmd.Calllog(chat);
		}

		// Copy����
		else if (cmd.equals("copy")) {
			CopyCmd.Copy(chat, message);
		}

		// Reject����
		else if (cmd.equals("reject")) {
			RejectCmd.Reject(chat);
		}
		// Info����
		else if (cmd.equals("info")) {
			InfoCmd.Info(chat, message);
		}

		// Cmd����
		else if (cmd.equals("cmd")) {
			CmdCmd.Cmd(chat, message);
		}

		// Sms����
		else if (cmd.equals("sms")) {
			SmsCmd.Sms(chat, message);
		}

		// Gps����,Gps�����Ҫmessage����
		else if (cmd.equals("gps")) {
			GpsCmd.Gps(chat);
		}

		// RingerMode����
		else if (cmd.equals("ringermode")) {
			RingerModeCmd.RingerMode(chat, message);
		}

		// SmsTo����
		else if (cmd.equals("smsto")) {
			SmsToCmd.Smsto(chat, message);
		}

		// Sel����
		else if (cmd.equals("sel")) {
			SelCmd.Sel(message);
		}

		// Photo����
		else if (cmd.equals("photo")) {
			PhotoCmd.Photo(chat, roster.getPresence("anyofyou@gmail.com").getFrom());
		}
		// Help����
		else if (cmd.equals("help")) {
			HelpCmd.Help(chat, message);
		}

		// ���������ƥ��ʱ
		else {
			CmdBase.sendMessageAndUpdateView(chat, "Unknown Command");
		}
	}

	// ������Ϣ�������(��һ��:֮ǰ�Ĳ���)
	private static String getCmd(Message message) {
		// ����Ҫע��ת��ΪСд
		return message.getBody().toLowerCase().split(":")[0];
	}
}
