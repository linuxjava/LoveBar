/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xiao.love.bar.im.task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;

import java.io.File;

import xiao.love.bar.component.util.DensityUtils;
import xiao.love.bar.component.util.NetUtils;
import xiao.love.bar.im.chat.ShowBigImageActivity;
import xiao.love.bar.im.util.ImageCache;

public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
	private ImageView iv = null;
	String localFullSizePath = null;
	String thumbnailPath = null;
	String remotePath = null;
	EMMessage message = null;
	ChatType chatType;
	Activity activity;

	@Override
	protected Bitmap doInBackground(Object... args) {
		thumbnailPath = (String) args[0];
		localFullSizePath = (String) args[1];
		remotePath = (String) args[2];
		chatType = (ChatType) args[3];
		iv = (ImageView) args[4];
		// if(args[2] != null) {
		activity = (Activity) args[5];
		// }
		message = (EMMessage) args[6];

		int thumbImageSize = DensityUtils.dp2px(activity, 70f);

		//在图片缓存目录中，缩略图本地文件是否存在
		File file = new File(thumbnailPath);
		if (file.exists()) {
			return resizeImage2(thumbnailPath, thumbImageSize, thumbImageSize);
			//return ImageUtils.decodeScaleImage(thumbnailPath, thumbImageSize, thumbImageSize);
		} else {
			if (message.direct == EMMessage.Direct.SEND) {
				return resizeImage2(localFullSizePath, thumbImageSize, thumbImageSize);
				//return ImageUtils.decodeScaleImage(localFullSizePath, thumbImageSize, thumbImageSize);
			} else {
				return null;
			}
		}
	}

	public Bitmap resizeImage2(String path, int width,int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;//不加载bitmap到内存中
		BitmapFactory.decodeFile(path, options);
		int outWidth = options.outWidth;
		int outHeight = options.outHeight;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 1;

		if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0)
		{
			int sampleSize=(outWidth/width+outHeight/height)/2;
			options.inSampleSize = sampleSize;
		}

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	protected void onPostExecute(Bitmap image) {
		if (image != null) {
			iv.setImageBitmap(image);
			ImageCache.getInstance().put(thumbnailPath, image);
			iv.setClickable(true);
			iv.setTag(thumbnailPath);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (thumbnailPath != null) {

						Intent intent = new Intent(activity, ShowBigImageActivity.class);
						File file = new File(localFullSizePath);
						if (file.exists()) {
							Uri uri = Uri.fromFile(file);
							intent.putExtra("uri", uri);
						} else {
							// The local full size pic does not exist yet.
							// ShowBigImage needs to download it from the server
							// first
							intent.putExtra("remotepath", remotePath);
						}
						if (message.getChatType() != ChatType.Chat) {
							// delete the image from server after download
						}
						if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked && message.getChatType() != ChatType.GroupChat && message.getChatType() != ChatType.ChatRoom) {
							message.isAcked = true;
							try {
								// 看了大图后发个已读回执给对方
								EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						activity.startActivity(intent);
					}
				}
			});
		} else {
			if (message.status == EMMessage.Status.FAIL) {
				if (NetUtils.isConnected(activity)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							EMChatManager.getInstance().asyncFetchMessage(message);
						}
					}).start();
				}
			}

		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
}
