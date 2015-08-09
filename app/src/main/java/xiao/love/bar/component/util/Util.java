package xiao.love.bar.component.util;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by guochang on 2015/1/11.
 */
public class Util {
    /**
     * 随机指定范围内N个不重复的数
     * 在初始化的无重复待选数组中随机产生一个数放入结果中，
     * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换
     * 然后从len-2里随机产生下一个随机数，如此类推
     * @param max  指定范围最大值
     * @param min  指定范围最小值
     * @param n  随机数个数
     * @return int[] 随机数结果集
     */
    public static int[] randomArray(int min,int max,int n){
        int len = max-min+1;

        if(max < min || n > len){
            return null;
        }

        //初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min+len; i++){
            source[i-min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            //待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }

    public static int random(int min, int max) {
        if (min < max) {
            return -1;
        }

        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static String get15Random() {
        String str = "";
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            str += random.nextInt(10);
        }
        return str;
    }

    /**
     * 显示查询错误提示信息
     *
     * @param errorInfoTv
     * @param info
     */
    public static void showErrInfo(final TextView errorInfoTv, String info) {
        errorInfoTv.setVisibility(View.VISIBLE);
        errorInfoTv.setText(info);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                errorInfoTv.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }
}
