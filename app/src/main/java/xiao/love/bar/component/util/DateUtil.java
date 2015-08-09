package xiao.love.bar.component.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by guochang on 2015/3/7.
 */
public class DateUtil {
    private static final String TIME_PATTERN = "yyyy-MM-dd_HH:mm:ss";

    public static String getCurrentTime() {
        // SimpleDateFormat.getDateInstance(TIME_PATTERN).format(new
        // Date(System.currentTimeMillis()));
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN);
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }
    /**
     * 判断当前日期是星期几
     *
     * @param pTime 要判断的时间(2015-02-13)
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
    public static String dayForWeek(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }

        switch (dayForWeek){
            case 1:
                return "周一";
            case 2:
                return "周二";
            case 3:
                return "周三";
            case 4:
                return "周四";
            case 5:
                return "周五";
            case 6:
                return "周六";
            case 7:
                return "周日";
        }

        return "";
    }
}
