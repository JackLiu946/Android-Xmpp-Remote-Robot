
package com.dary.xmpp.cmd;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.dary.xmpp.application.MyApp;

import org.jivesoftware.smack.Chat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RejectCmd extends CmdBase {
    public static void Reject(Chat chat) {
        com.android.internal.telephony.ITelephony telephonyService = null;
        TelephonyManager telManager = (TelephonyManager) MyApp.getContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        Class<?> c = null;
        try {
            c = Class.forName(telManager.getClass().getName());
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
        Method m = null;
        try {
            m = c.getDeclaredMethod("getITelephony");
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
        m.setAccessible(true);
        try {
            telephonyService = (ITelephony) m.invoke(telManager);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        telephonyService.endCall();

        sendMessageAndUpdateView(chat, "Reject Done");

    }
}
