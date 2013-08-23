
package com.dary.xmpp.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
        }
    }

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ex.printStackTrace();
                Toast.makeText(mContext, "Sorry,App Crash", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Map<String, String> deviceInfo = collectDeviceInfo(mContext);
                String crashInfo = collectCrashInfo(ex);
                String AllInfo = makeAllInfo(deviceInfo, crashInfo);
                saveAllInfo2File(AllInfo);
                sendMail(AllInfo);
                Looper.loop();
            }
        }.start();
        return true;
    }

    private Map<String, String> collectDeviceInfo(Context ctx) {
        Map<String, String> infos = new HashMap<String, String>();
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return infos;
    }

    private String collectCrashInfo(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        return result;
    }

    private String makeAllInfo(Map<String, String> deviceInfo, String crashInfo) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : deviceInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        sb.append("\n");
        sb.append(crashInfo);
        return sb.toString();
    }

    private String saveAllInfo2File(String allInfo) {

        try {
            String time = formatter.format(new Date());
            String fileName = "Crash-" + time + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Tools.getSDPath() + File.separator + "Android XMPP Remote Robot"
                        + File.separator + "Crash" + File.separator;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(allInfo.getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            return null;
        }
    }

    private void sendMail(String body) {
        String time = formatter.format(new Date());
        String title = "Crash-" + time;
        String mailSender = "anyofyou@gmail.com";
        String password = "";
        String mailReceiver = "anyofyou@gmail.com";
        if (!TextUtils.isEmpty(password)) {
            GMailSender sender = new GMailSender(mailSender, password);
            try {
                sender.sendMail(title, body, mailSender, mailReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
