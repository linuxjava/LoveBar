package xiao.love.bar.im.chat.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;


import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xiao.love.bar.R;

/**
 * Created by guochang on 2015/9/9.
 */
public class EmojiParse {
    //表情匹配正则表达式
    public static final String EMOJI_REX = "emoji_[\\d]{0,4}";

    /**
     * 将字符串转换为表情
     *
     * @param context
     * @param str
     * @return
     */
    public static SpannableString parseString(Context context, String str) {
        // 封装文本消息
        SpannableString spannableString = new SpannableString(str);
        // 封装正则表达式
        Pattern patten = Pattern.compile(EMOJI_REX, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context, spannableString, patten, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替 规则[...]
     */
    public static void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start)
            throws Exception {
        // 开始匹配真正则
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) { // 如果找到
            String key = matcher.group();
            // 从指定的位置开始匹配
            if (matcher.start() < start) {
                continue;
            }

            int resId = getResIDByEmojiName(key);
            if (resId != 0) {
//                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
//                bitmap = zoom(bitmap, emojiSize, emojiSize);

                int end = matcher.start() + key.length();
                // 使用[]进行约束
                spannableString.setSpan(new EmojiconSpan(context, resId), matcher.start() - 1,
                        end + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (end < spannableString.length()) {
                    dealExpression(context, spannableString, patten, end);
                }
                break;
            }
        }
    }

    /**
     * 解析一个表情符
     *
     * @param context
     * @param emojiName 表情的资源名称
     * @return
     */
    public static SpannableString parseOneEmoji(Context context, String emojiName) {
        int resId = getResIDByEmojiName(emojiName);

        if (resId != 0) {
//            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
//            bitmap = zoom(bitmap, emojiSize, emojiSize);
            SpannableString spannableString = new SpannableString("[" + emojiName + "]");
            spannableString.setSpan(new EmojiconSpan(context, resId), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spannableString;
        }

        return new SpannableString("");
    }

    /**
     * 缩放bitmap
     *
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
//    private static Bitmap zoom(Bitmap bitmap, float w, float h) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        Matrix matrix = new Matrix();
//        matrix.postScale(w / width, h / height); //长和宽放大缩小的比例
//        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//
//        return resizeBmp;
//    }

    /**
     * 通过表情名获取资源id
     *
     * @param emojiName
     * @return
     */
    public static int getResIDByEmojiName(String emojiName) {
        int resId = 0;

        // 得到图片在R 文件中的属性
        try {
            Field field = R.drawable.class.getDeclaredField(emojiName);
            // 得到该属性的值
            resId = Integer.parseInt(field.get(null).toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return resId;
    }

    private static class EmojiconSpan extends DynamicDrawableSpan {
        private Context mContext;
        private int mResId;

        public EmojiconSpan(Context context, int resId){
            mContext = context;
            mResId = resId;
        }

        @Override
        public Drawable getDrawable() {
            Drawable drawable = mContext.getResources().getDrawable(mResId);
            int size = mContext.getResources().getDimensionPixelSize(R.dimen.input_emoji_size);
            drawable.setBounds(0, 0, size, size);
            return drawable;
        }
    }
}
