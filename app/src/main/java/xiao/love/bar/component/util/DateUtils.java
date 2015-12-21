package xiao.love.bar.component.util;

import com.easemob.util.TimeInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import xiao.love.bar.LoveApp;

/**
 * Created by guochang on 2015/3/7.
 */
public class DateUtils {
    private static final String TIME_PATTERN = "yyyy-MM-dd_HH:mm:ss";
    private static final long INTERVAL_IN_MILLISECONDS = 30 * 1000;

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

        switch (dayForWeek) {
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

    public static String getTimestampString(Date messageDate) {
        Locale curLocale = LoveApp.sAppInstance.getResources().getConfiguration().locale;

        String languageCode = curLocale.getLanguage();

        boolean isChinese = languageCode.contains("zh");

        String format = null;

        long messageTime = messageDate.getTime();
        if (isSameDay(messageTime)) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(messageDate);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            format = "HH:mm";

            if (hour > 17) {
                if (isChinese) {
                    format = "晚上 hh:mm";
                }

            } else if (hour >= 0 && hour <= 6) {
                if (isChinese) {
                    format = "凌晨 hh:mm";
                }
            } else if (hour > 11 && hour <= 17) {
                if (isChinese) {
                    format = "下午 hh:mm";
                }

            } else {
                if (isChinese) {
                    format = "上午 hh:mm";
                }
            }
        } else if (isYesterday(messageTime)) {
            if (isChinese) {
                format = "昨天 HH:mm";
            } else {
                format = "MM-dd HH:mm";
            }

        } else {
            if (isChinese) {
                format = "M月d日 HH:mm";
            } else {
                format = "MM-dd HH:mm";
            }
        }

        if (isChinese) {
            return new SimpleDateFormat(format, Locale.CHINA).format(messageDate);
        } else {
            return new SimpleDateFormat(format, Locale.US).format(messageDate);
        }
    }

    public static boolean isCloseEnough(long time1, long time2) {
        // long time1 = date1.getTime();
        // long time2 = date2.getTime();
        long delta = time1 - time2;
        if (delta < 0) {
            delta = -delta;
        }
        return delta < INTERVAL_IN_MILLISECONDS;
    }

    private static boolean isSameDay(long inputTime) {

        TimeInfo tStartAndEndTime = getTodayStartAndEndTime();
        if (inputTime > tStartAndEndTime.getStartTime() && inputTime < tStartAndEndTime.getEndTime())
            return true;
        return false;
    }

    private static boolean isYesterday(long inputTime) {
        TimeInfo yStartAndEndTime = getYesterdayStartAndEndTime();
        if (inputTime > yStartAndEndTime.getStartTime() && inputTime < yStartAndEndTime.getEndTime())
            return true;
        return false;
    }

    public static Date StringToDate(String dateStr, String formatStr) {
        DateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param timeLength Millisecond
     * @return
     */
    public static String toTime(int timeLength) {
        timeLength /= 1000;
        int minute = timeLength / 60;
        int hour = 0;
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        int second = timeLength % 60;
        // return String.format("%02d:%02d:%02d", hour, minute, second);
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * @param timeLength second
     * @return
     */
    public static String toTimeBySecond(int timeLength) {
//      timeLength /= 1000;
        int minute = timeLength / 60;
        int hour = 0;
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        int second = timeLength % 60;
        // return String.format("%02d:%02d:%02d", hour, minute, second);
        return String.format("%02d:%02d", minute, second);
    }


    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -1);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    public static TimeInfo getBeforeYesterdayStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -2);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -2);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    /**
     * endtime为今天
     *
     * @return
     */
    public static TimeInfo getCurrentMonthStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
//      calendar2.set(Calendar.HOUR_OF_DAY, 23);
//      calendar2.set(Calendar.MINUTE, 59);
//      calendar2.set(Calendar.SECOND, 59);
//      calendar2.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    public static TimeInfo getLastMonthStartAndEndTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DATE, 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.MONTH, -1);
        calendar2.set(Calendar.DATE, 1);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        calendar2.roll(Calendar.DATE, -1);
        Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }
}
