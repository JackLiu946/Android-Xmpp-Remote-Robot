
package com.dary.xmppremoterobot.cmd;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;

import com.dary.xmppremoterobot.service.MainService;
import com.dary.xmppremoterobot.tools.Tools;
import com.dary.xmppremoterobot.ui.MainActivity;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoCmd extends CmdBase {
    static Camera sCamera = null;
    static String fileName = null;
    static String filePath = null;

    @SuppressWarnings("deprecation")
    public static void Photo(Chat chat) {
        // 照片的名字和存储的地址.
        fileName = Tools.getTimeStrHyphen() + ".jpg";
        filePath = Tools.getSDPath();

        if (sCamera == null) {
            sCamera = Camera.open();
        }

        Parameters params = sCamera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_ON);
        params.setPictureFormat(PixelFormat.JPEG);
        sCamera.setParameters(params);

        try {
            sCamera.setPreviewDisplay(MainActivity.surfaceview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        sCamera.startPreview();

        Camera.ShutterCallback cb = new Camera.ShutterCallback() {
            public void onShutter() {
            }
        };

        PictureCallback pictureCallback = new PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

                File file = new File(filePath + "/" + fileName);
                try {
                    BufferedOutputStream bos = null;
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    bos.flush();
                    bos.close();

                    if (sCamera != null) {
                        sCamera.setPreviewDisplay(null);
                        sCamera.stopPreview();
                        sCamera.setPreviewCallback(null);
                        sCamera.unlock();
                        sCamera.release();
                        sCamera = null;
                    }

                    // 这里始终有问题,未解决.
                    // ConfigureProviderManager.configure(ProviderManager.getInstance());
                    // new ServiceDiscoveryManager(MainService.connection);

                    FileTransferManager FTmanager = new FileTransferManager(MainService.connection);
                    // 这里是完整的用户ID,包括资源名
                    OutgoingFileTransfer transfer = FTmanager
                            .createOutgoingFileTransfer(MainService.notifiedAddress);
                    transfer.sendFile(file, "You won't believe this!");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        sCamera.takePicture(cb, null, pictureCallback);
        sendMessageAndUpdateView(chat, "Take Photo Done,Save as " + filePath + "/" + fileName);
    }
}
