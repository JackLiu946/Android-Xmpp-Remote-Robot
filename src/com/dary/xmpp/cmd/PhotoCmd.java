package com.dary.xmpp.cmd;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jivesoftware.smack.Chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;

import com.dary.xmpp.Tools;
import com.dary.xmpp.XmppActivity;

public class PhotoCmd extends CmdBase {
	static Camera sCamera = null;
	static String fileName = null;
	static String filePath = null;
	static File file = null;

	public static void Photo(Chat chat, final String From) {
		// ��Ƭ�����ֺʹ洢�ĵ�ַ.
		fileName = Tools.getTimeStrHyphen() + ".jpg";
		filePath = "/sdcard";

		if (sCamera == null) {
			sCamera = Camera.open();
		}

		Parameters params = sCamera.getParameters();
		params.setFlashMode(Parameters.FLASH_MODE_ON);
		params.setPictureFormat(PixelFormat.JPEG);
		sCamera.setParameters(params);

		try {
			sCamera.setPreviewDisplay(XmppActivity.surfaceview.getHolder());
		} catch (IOException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
		sCamera.startPreview();

		Camera.ShutterCallback cb = new Camera.ShutterCallback() {
			public void onShutter() {
			}
		};

		PictureCallback pictureCallback = new PictureCallback() {

			public void onPictureTaken(byte[] data, Camera camera) {
				// FIXME Auto-generated method stub
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

				file = new File(filePath + "/" + fileName);
				try {
					BufferedOutputStream bos = null;
					bos = new BufferedOutputStream(new FileOutputStream(file));
					bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
					bos.flush();
					bos.close();

					if (sCamera != null) {
						sCamera.stopPreview();
						sCamera.setPreviewCallback(null);
						sCamera.unlock();
						sCamera.release();
						sCamera = null;
					}

					// ����ʼ��������,δ���.
					// ConfigureProviderManager.configure(ProviderManager.getInstance());
					// new ServiceDiscoveryManager(MainService.connection);
					// FileTransferManager FTmanager = new
					// FileTransferManager(MainService.connection);
					//
					// // �������������û�ID,������Դ��
					// OutgoingFileTransfer transfer =
					// FTmanager.createOutgoingFileTransfer(From);
					// try
					// {
					// System.out.println("sendFile");
					// transfer.sendFile(file, "You won't believe this!");
					// }
					// catch (XMPPException e)
					// {
					// // FIXME Auto-generated catch
					// // block
					// e.printStackTrace();
					// }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		sCamera.takePicture(cb, null, pictureCallback);
		sendMessageAndUpdateView(chat, "Take Photo Done,Save as " + filePath + "/" + fileName);
	}

}
