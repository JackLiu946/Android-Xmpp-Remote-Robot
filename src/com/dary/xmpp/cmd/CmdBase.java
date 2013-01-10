package com.dary.xmpp.cmd;

import java.util.Locale;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import android.widget.Toast;

import com.dary.xmpp.DatabaseHelper;
import com.dary.xmpp.MainService;
import com.dary.xmpp.MyApp;
import com.dary.xmpp.Tools;
import com.dary.xmpp.ui.MainActivity;

public class CmdBase {
	public static void sendMessageAndUpdateView(Chat chat, String message) {

		try {
			chat.sendMessage(message);
			// 更新UI
			MainActivity.sendHandlerMessageToAddMsgView(DatabaseHelper.SEND_MESSAGE, Tools.getAddress(MainService.connection.getUser()), message, Tools.getTimeStr());
			// 插入数据库
			DatabaseHelper.insertMsgToDatabase(DatabaseHelper.SEND_MESSAGE, Tools.getAddress(MainService.connection.getUser()), message, Tools.getTimeStr());

		} catch (Exception e) {
			e.printStackTrace();
			Tools.doLog("Send Message Failed");
			Toast.makeText(MyApp.getContext(), "Send Message Failed", Toast.LENGTH_SHORT).show();
			// Intent mainserviceIntent = new Intent();
			// mainserviceIntent.setClass(MyApp.getContext(), MainService.class);
			// MyApp.getContext().stopService(mainserviceIntent);
			// Intent incallserviceIntent = new Intent();
			// incallserviceIntent.setClass(MyApp.getContext(), IncallService.class);
			// MyApp.getContext().stopService(incallserviceIntent);
			MainService.sendMsg(MainActivity.NOT_LOGGED_IN);
		}
	}

	// 解析消息的全部参数部分,区分大小写.
	static String getArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1);
		return message.getBody().split(":", 2)[1];
	}

	// 解析消息的全部参数部分,不区分大小写.
	static String getArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1).toLowerCase();
		return message.getBody().split(":", 2)[1].toLowerCase(Locale.getDefault());
	}

	// 解析消息的第一个参数部分,不区分大小写.
	static String getFirArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1, message.getBody().indexOf(":", message.getBody().indexOf(":") +
		// 1))
		// .toLowerCase();
		return message.getBody().split(":", -1)[1].toLowerCase(Locale.getDefault());
	}

	// 解析消息的第一个参数部分,区分大小写.
	static String getFirArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1, message.getBody().indexOf(":", message.getBody().indexOf(":") +
		// 1));
		return message.getBody().split(":", -1)[1];
	}

	// 解析消息的第二个参数部分,不区分大小写.
	static String getSecArgs(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":",
		// message.getBody().indexOf(":") + 1) + 1).toLowerCase();
		return message.getBody().split(":", -1)[2].toLowerCase(Locale.getDefault());
	}

	// 解析消息的第二个参数部分,区分大小写.
	static String getSecArgsCaseSensitive(Message message) {
		// return message.getBody().substring(message.getBody().indexOf(":",
		// message.getBody().indexOf(":") + 1) + 1);
		return message.getBody().split(":", -1)[2];
	}

	// 判断是否含有参数.(不去判断是否为空),也可去分割消息,判断所得数组的长度.
	static boolean hasArgs(Message message) {
		// return message.getBody().indexOf(":") != -1;
		return message.getBody().matches(".*:.*");
	}

	// 判断是否含有第2个参数.(不判断是否为空),也可去分割消息,判断所得数组的长度.
	static boolean hasSecArgs(Message message) {
		// return
		// message.getBody().indexOf(":",message.getBody().indexOf(":")+1) !=
		// -1;
		return message.getBody().matches(".*:.*:.*");
	}
}
