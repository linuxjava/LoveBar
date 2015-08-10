package xiao.love.bar.component.util;

import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;


/**
 * Created by guochang on 2015/4/17.
 */
public class SoundUtil {
    public static void playSound(Context c, int resId){
        MediaPlayer mediaPlayer = MediaPlayer.create(c, resId);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.start();
    }

    public static void vibrator(Context c){
        Vibrator vibrator=(Vibrator)c.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0,50}, -1);
    }
}
