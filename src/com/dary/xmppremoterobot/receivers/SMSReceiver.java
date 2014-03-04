
package com.dary.xmppremoterobot.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.dary.xmppremoterobot.cmd.CmdBase;
import com.dary.xmppremoterobot.service.MainService;
import com.dary.xmppremoterobot.tools.Contact;
import com.dary.xmppremoterobot.ui.MainActivity;

public class SMSReceiver extends BroadcastReceiver {
    private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(mACTION)) {
            if (null != MainActivity.MsgHandler) {
                StringBuilder sb = new StringBuilder();
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] myOBJpdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] message = new SmsMessage[myOBJpdus.length];
                    for (int i = 0; i < myOBJpdus.length; i++) {
                        message[i] = SmsMessage.createFromPdu((byte[]) myOBJpdus[i]);
                    }
                    for (SmsMessage currentMessage : message) {
                        sb.append("Receive Message");
                        sb.append("\n");
                        sb.append("Number: ");
                        sb.append(Contact.getContactNameByNumber(currentMessage
                                .getDisplayOriginatingAddress()));
                        sb.append("\n");
                        sb.append("Body: ");
                        sb.append("\n");
                        sb.append(currentMessage.getDisplayMessageBody());
                        sb.append("\n");
                    }
                }
                CmdBase.sendMessageAndUpdateView(MainService.chat, sb.toString());
            }
        }
    }

}
