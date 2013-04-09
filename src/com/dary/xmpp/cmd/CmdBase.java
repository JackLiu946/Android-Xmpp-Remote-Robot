package com.dary.xmpp.cmd;

import java.util.Locale;

import org.jivesoftware.smack.Chat;

import android.widget.Toast;

import com.dary.xmpp.DatabaseHelper;
import com.dary.xmpp.MainService;
import com.dary.xmpp.MyApp;
import com.dary.xmpp.Tools;
import com.dary.xmpp.ui.MainActivity;

public class CmdBase {
	public static void sendMessageAndUpdateView(Chat chat, String message) {
		Tools.doLogJustPrint("Send Message :" + "\n" + message);
		try {
			// chat为null表示为DEBUG模式
			if (chat != null) {
				chat.sendMessage(message);
			}
			String from = MainService.loginAddress;
			if (from == null) {
				from = "Debug";
			}
			// String from = Tools.getAddress(MainService.connection.getUser());
			// 更新UI
			MainActivity.sendHandlerMessageToAddMsgView(DatabaseHelper.SEND_MESSAGE, from, message, Tools.getTimeStr());
			// 插入数据库
			DatabaseHelper.insertMsgToDatabase(DatabaseHelper.SEND_MESSAGE, from, message, Tools.getTimeStr());

		} catch (Exception e) {
			e.printStackTrace();
			Tools.doLogAll("Send Message Failed");
			Toast.makeText(MyApp.getContext(), "Send Message Failed", Toast.LENGTH_SHORT).show();
			// Intent mainserviceIntent = new Intent();
			// mainserviceIntent.setClass(MyApp.getContext(),
			// MainService.class);
			// MyApp.getContext().stopService(mainserviceIntent);
			// Intent incallserviceIntent = new Intent();
			// incallserviceIntent.setClass(MyApp.getContext(),
			// IncallService.class);
			// MyApp.getContext().stopService(incallserviceIntent);
			// 消息发送失败时不能简单设置为Not Logged In的状态
			// 可以考虑单独增加一个状态
			MainService.sendMsg(MainActivity.CONNECTION_FAILED);
		}
	}

	// 解析消息的全部参数部分,区分大小写.
	static String getArgsCaseSensitive(String message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1);
		return message.split(":", 2)[1];
	}

	// 解析消息的全部参数部分,不区分大小写.
	static String getArgs(String message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1).toLowerCase();
		return message.split(":", 2)[1].toLowerCase(Locale.getDefault());
	}

	// 解析消息的第一个参数部分,不区分大小写.
	static String getFirArgs(String message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1, message.getBody().indexOf(":", message.getBody().indexOf(":") +
		// 1))
		// .toLowerCase();
		return message.split(":", -1)[1].toLowerCase(Locale.getDefault());
	}

	// 解析消息的第一个参数部分,区分大小写.
	static String getFirArgsCaseSensitive(String message) {
		// return message.getBody().substring(message.getBody().indexOf(":") +
		// 1, message.getBody().indexOf(":", message.getBody().indexOf(":") +
		// 1));
		return message.split(":", -1)[1];
	}

	// 解析消息的第二个参数部分,不区分大小写.
	static String getSecArgs(String message) {
		// return message.getBody().substring(message.getBody().indexOf(":",
		// message.getBody().indexOf(":") + 1) + 1).toLowerCase();
		return message.split(":", -1)[2].toLowerCase(Locale.getDefault());
	}

	// 解析消息的第二个参数部分,区分大小写.
	static String getSecArgsCaseSensitive(String message) {
		// return message.getBody().substring(message.getBody().indexOf(":",
		// message.getBody().indexOf(":") + 1) + 1);
		return message.split(":", -1)[2];
	}

	// 判断是否含有参数.(不去判断参数是否为""),也可去分割消息,判断所得数组的长度.
	static boolean hasArgs(String message) {
		// return message.getBody().indexOf(":") != -1;
		return message.matches(".*:.*");
	}

	// 判断是否含有第二个参数.(不去判断参数是否为""),也可去分割消息,判断所得数组的长度.
	static boolean hasSecArgs(String message) {
		// return
		// message.getBody().indexOf(":",message.getBody().indexOf(":")+1) !=
		// -1;
		return message.matches(".*:.*:.*");
	}
}
