package com.dary.xmpp;

import java.util.Locale;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Message;

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
import com.dary.xmpp.cmd.USBStorage;

class MsgListener implements MessageListener {

	// 消息处理
	public void processMessage(Chat chat, Message message) {
		System.out.println("Receive Message :" + "\n" + message.getBody());
		// 收到消息之后将消息内容放入bundle,发送消息去更新UI
		XmppActivity.sendHandlerMessageToAddMsgView(DatabaseHelper.RECEIVE_MESSAGE, Tools.getAddress(message.getFrom()), message.getBody(), Tools.getTimeStr());
		// 插入数据库
		DatabaseHelper.insertMsgToDatabase(DatabaseHelper.RECEIVE_MESSAGE, Tools.getAddress(message.getFrom()), message.getBody(), Tools.getTimeStr());

		Roster roster = MainService.connection.getRoster();

		String cmd = getCmd(message);

		// Light命令
		if (cmd.equals("light")) {
			LightCmd.Light(chat, message);
		}

		// Ring命令
		else if (cmd.equals("ring")) {
			RingCmd.Ring(chat, message);
		}
		// CallLog命令.不需要Message参数
		else if (cmd.equals("calllog")) {
			CallLogCmd.Calllog(chat);
		}

		// Copy命令
		else if (cmd.equals("copy")) {
			CopyCmd.Copy(chat, message);
		}

		// Reject命令
		else if (cmd.equals("reject")) {
			RejectCmd.Reject(chat);
		}
		// Info命令
		else if (cmd.equals("info")) {
			InfoCmd.Info(chat, message);
		}

		// Cmd命令
		else if (cmd.equals("cmd")) {
			CmdCmd.Cmd(chat, message);
		}

		// Sms命令
		else if (cmd.equals("sms")) {
			SmsCmd.Sms(chat, message);
		}

		// Gps命令,Gps命令不需要message参数
		else if (cmd.equals("gps")) {
			GpsCmd.Gps(chat);
		}

		// RingerMode命令
		else if (cmd.equals("ringermode")) {
			RingerModeCmd.RingerMode(chat, message);
		}

		// SmsTo命令
		else if (cmd.equals("smsto")) {
			SmsToCmd.Smsto(chat, message);
		}

		// Sel命令
		else if (cmd.equals("sel")) {
			SelCmd.Sel(message);
		}

		// Photo命令
		else if (cmd.equals("photo")) {
			PhotoCmd.Photo(chat, roster.getPresence("anyofyou@gmail.com").getFrom());
		}
		// Help命令
		else if (cmd.equals("help")) {
			HelpCmd.Help(chat, message);
		}

		else if (cmd.equals("usb")) {
			USBStorage.OpenUSBStorage();
		}

		// 所有命令都不匹配时
		else {
			CmdBase.sendMessageAndUpdateView(chat, "Unknown Command");
		}
	}

	// 解析消息的命令部分(第一个:之前的部分)
	private static String getCmd(Message message) {
		// 这里要注意转换为小写
		return message.getBody().toLowerCase(Locale.getDefault()).split(":")[0];
	}
}
