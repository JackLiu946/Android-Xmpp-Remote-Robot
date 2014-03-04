
package com.dary.xmppremoterobot.xmpp;

import com.dary.xmppremoterobot.cmd.CallLogCmd;
import com.dary.xmppremoterobot.cmd.ClearCmd;
import com.dary.xmppremoterobot.cmd.CmdBase;
import com.dary.xmppremoterobot.cmd.CmdCmd;
import com.dary.xmppremoterobot.cmd.CopyCmd;
import com.dary.xmppremoterobot.cmd.GpsCmd;
import com.dary.xmppremoterobot.cmd.HelpCmd;
import com.dary.xmppremoterobot.cmd.InfoCmd;
import com.dary.xmppremoterobot.cmd.LightCmd;
import com.dary.xmppremoterobot.cmd.PhotoCmd;
import com.dary.xmppremoterobot.cmd.RejectCmd;
import com.dary.xmppremoterobot.cmd.RingCmd;
import com.dary.xmppremoterobot.cmd.RingerModeCmd;
import com.dary.xmppremoterobot.cmd.SelCmd;
import com.dary.xmppremoterobot.cmd.SmsCmd;
import com.dary.xmppremoterobot.cmd.SmsToCmd;
import com.dary.xmppremoterobot.cmd.USBStorage;
import com.dary.xmppremoterobot.databases.DatabaseHelper;
import com.dary.xmppremoterobot.service.MainService;
import com.dary.xmppremoterobot.tools.Tools;
import com.dary.xmppremoterobot.ui.MainActivity;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Locale;

public class MsgListener implements MessageListener {

    // 消息处理
    public void processMessage(Chat chat, Message message) {
        handleMessage(chat, message.getBody());
    }

    // 解析消息的命令部分(第一个:之前的部分)
    private static String getCmd(String message) {
        // 这里要注意转换为小写
        return message.toLowerCase(Locale.getDefault()).split(":")[0];
    }

    public static void handleMessage(Chat chat, String message) {
        Tools.doLogJustPrint("Receive Message :" + "\n" + message);
        String from = MainService.notifiedAddress;
        if (from == null) {
            from = "Debug";
        }
        // String from = Tools.getAddress(message.getFrom());
        // 收到消息之后将消息内容放入bundle,发送消息去更新UI
        long time = System.currentTimeMillis();
        MainActivity.sendHandlerMessageToAddMsgView(DatabaseHelper.RECEIVE_MESSAGE, from, message,
                time);
        // 插入数据库
        DatabaseHelper.insertMsgToDatabase(DatabaseHelper.RECEIVE_MESSAGE, from, message, time);

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
            SelCmd.Sel(chat, message);
        }

        // Photo命令
        else if (cmd.equals("photo")) {
            PhotoCmd.Photo(chat);
        }

        // Help命令
        else if (cmd.equals("help")) {
            HelpCmd.Help(chat, message);
        }

        // Usb命令
        else if (cmd.equals("usb")) {
            USBStorage.OpenUSBStorage();
        }

        // Cls命令
        else if (cmd.equals("cls")) {
            ClearCmd.Clear();
        }

        // 所有命令都不匹配时
        else {
            CmdBase.sendMessageAndUpdateView(chat, "Unknown Command");
        }
    }
}
