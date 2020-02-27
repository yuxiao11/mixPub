package com.ifeng.recom.mixrecall.common.util;

import com.google.common.base.Preconditions;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public final static int TIME_DAY_MILLISECOND = 86400000;

    public final static int TIME_HOUR_MILLISECOND = 1000 * 60 * 60 * 1;

    public final static int TIME_SECONDS_MILLISECOND = 1000;


    // 定义时间日期显示格式
    public final static String DATE_FORMAT = "yyyy-MM-dd";

    public final static String DATE_FORMAT_MINUTE = "yyyy-MM-dd HH:mm";

    public final static String DATE_FORMAT_HOUR = "yyyy-MM-dd HH";

    public final static String DATE_FORMAT_CN = "yyyy年MM月dd日";

    public final static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static String TIME_FORMAT_SPACE = "yyyy MM dd HH:mm:ss";

    public final static String TIME_FORMAT_CN = "yyyy年MM月dd日 HH:mm:ss";

    public final static String MONTH_FORMAT = "yyyy-MM";

    public final static String DAY_FORMAT = "yyyyMMdd";

    public final static String TIME_FORMAT_NUM = "MMddHHmmss";

    public final static String TIME_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public final static String TIME_FORMAT_HHMMSS = "HH:mm:ss";


    public final static String TIME_FORMAT_DATE_BANK = "MMyy";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DateConstant.YYYY_MM_DD_HH_MM_SS);

    private static final DateTimeFormatter shortDateTimeFormatter = DateTimeFormat.forPattern(DateConstant.YYYY_MM_DD);

    private static final DateTimeFormatter bankDateTimeFormatter = DateTimeFormat.forPattern(TIME_FORMAT_DATE_BANK);


    public static String toStringOfBank(Date date) {
        DateTime dateTime = new DateTime(date);
        return bankDateTimeFormatter.print(dateTime);
    }

    public static String getYesterdayMonthFirstDayWithHMS() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMinimum(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return format.format(calendar.getTime());
    }


    /**
     * 判断昨天所在的月的第一天是
     *
     * @return
     */
    public static String getyesterDayMonthFirstDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMinimum(Calendar.DAY_OF_MONTH));


        return format.format(calendar.getTime());
    }


    public static Date getyesterDayMonthFirstDayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMinimum(Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }


    /**
     * 取得当前系统时间，返回java.util.Date类型
     *
     * @return java.util.Date 返回服务器当前系统时间
     * @see java.util.Date
     */
    public static java.util.Date getCurrDate() {
        return new java.util.Date();
    }

    /**
     * 取得明天的开始时间，返回java.util.Date类型
     *
     * @return java.util.Date
     * @see java.util.Date
     */
    public static java.util.Date getTomorrowBeginDate() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(DAY_FORMAT);
        String nowDate = sf.format(date);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sf.parse(nowDate));
            cal.add(Calendar.DAY_OF_YEAR, +1);
            return cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得今天的剩余秒数，返回剩余秒数
     *
     * @return int
     */
    public static int getCurrDateSurplusSeconds() {
        Date currDate = getCurrDate();
        Date tomorrowDate = getTomorrowBeginDate();
        return getSecondsBetweenDates(currDate, tomorrowDate);
    }


    /**
     * 取得当前系统时间戳
     *
     * @return java.sql.Timestamp 系统时间戳
     * @see java.sql.Timestamp
     */
    public static java.sql.Timestamp getCurrTimestamp() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    /**
     * 返回当前时间是上午还是下午
     *
     * @return
     */
    public static Integer getCurrDateAMorPM() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.AM_PM);
    }

    /**
     * 得到格式化后的日期，格式为yyyy-MM-dd，如2009-10-15
     *
     * @param currDate 要格式化的日期
     * @return String 返回格式化后的日期，默认格式为为yyyy-MM-dd，如2009-10-15
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getFormatDate(java.util.Date currDate) {
        return getFormatDate(currDate, DATE_FORMAT);
    }

    /**
     * 得到格式化后的日期，格式为yyyy-MM-dd，如2009-10-15
     *
     * @param currDate 要格式化的日期
     * @return Date 返回格式化后的日期，默认格式为为yyyy-MM-dd，如2009-10-15
     * @see #getFormatDate(java.util.Date)
     */
    public static Date getFormatDateToDate(java.util.Date currDate) {
        return getFormatDate(getFormatDate(currDate));
    }

    /**
     * 得到格式化后的日期，格式为yyyy年MM月dd日，如2009年02月15日
     *
     * @param currDate 要格式化的日期
     * @return String 返回格式化后的日期，默认格式为yyyy年MM月dd日，如2009年02月15日
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getFormatDate_CN(java.util.Date currDate) {
        return getFormatDate(currDate, DATE_FORMAT_CN);
    }

    /**
     * 得到格式化后的日期，格式为yyyy年MM月dd日，如2009年02月15日
     *
     * @param currDate 要格式化的日期
     * @return Date 返回格式化后的日期，默认格式为yyyy年MM月dd日，如2009年02月15日
     * @see #getFormatDate_CN(String)
     */
    public static Date getFormatDateToDate_CN(java.util.Date currDate) {
        return getFormatDate_CN(getFormatDate_CN(currDate));
    }

    /**
     * 得到格式化后的日期，格式为yyyy-MM-dd，如2009-10-15
     *
     * @param currDate 要格式化的日期
     * @return Date 返回格式化后的日期，默认格式为yyyy-MM-dd，如2009-10-15
     * @see #getFormatDate(String, String)
     */
    public static Date getFormatDate(String currDate) {
        return getFormatDate(currDate, DATE_FORMAT);
    }

    /**
     * 得到格式化后的日期，格式为yyyy年MM月dd日，如2009年02月15日
     *
     * @param currDate 要格式化的日期
     * @return 返回格式化后的日期，默认格式为yyyy年MM月dd日，如2009年02月15日
     * @see #getFormatDate(String, String)
     */
    public static Date getFormatDate_CN(String currDate) {
        return getFormatDate(currDate, DATE_FORMAT_CN);
    }

    /**
     * 根据格式得到格式化后的日期
     *
     * @param currDate 要格式化的日期
     * @param format   日期格式，如yyyy-MM-dd
     * @return String 返回格式化后的日期，格式由参数<code>format</code>
     * 定义，如yyyy-MM-dd，如2009-10-15
     * @see java.text.SimpleDateFormat#format(java.util.Date)
     */
    public static String getFormatDate(java.util.Date currDate, String format) {
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.format(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(DATE_FORMAT);
            try {
                return dtFormatdB.format(currDate);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * 得到格式化后的时间，格式为yyyy-MM-dd HH:mm:ss，如2009-10-15 15:23:45
     *
     * @param currDate 要格式化的时间
     * @return String 返回格式化后的时间，默认格式为yyyy-MM-dd HH:mm:ss，如2009-10-15 15:23:45
     * @see #getFormatDateTime(java.util.Date, String)
     */
    public static String getFormatDateTime(java.util.Date currDate) {
        return getFormatDateTime(currDate, TIME_FORMAT);
    }

    /**
     * 得到格式化后的时间，格式为yyyy-MM-dd HH:mm:ss，如2009-10-15 15:23:45
     *
     * @param currDate 要格式环的时间
     * @return Date 返回格式化后的时间，默认格式为yyyy-MM-dd HH:mm:ss，如2009-10-15 15:23:45
     * @see #getFormatDateTime(String)
     */
    public static Date getFormatDateTimeToTime(java.util.Date currDate) {
        return getFormatDateTime(getFormatDateTime(currDate));
    }

    /**
     * 得到格式化后的时间，格式为yyyy-MM-dd HH:mm:ss，如2009-10-15 15:23:45
     *
     * @param currDate 要格式化的时间
     * @return Date 返回格式化后的时间，默认格式为yyyy-MM-dd HH:mm:ss，如2009-10-15 15:23:45
     * @see #getFormatDateTime(String, String)
     */
    public static Date getFormatDateTime(String currDate) {
        return getFormatDateTime(currDate, TIME_FORMAT);
    }

    /**
     * 得到格式化后的时间，格式为yyyy年MM月dd日 HH:mm:ss，如2009年02月15日 15:23:45
     *
     * @param currDate 要格式化的时间
     * @return String 返回格式化后的时间，默认格式为yyyy年MM月dd日 HH:mm:ss，如2009年02月15日 15:23:45
     * @see #getFormatDateTime(java.util.Date, String)
     */
    public static String getFormatDateTime_CN(java.util.Date currDate) {
        return getFormatDateTime(currDate, TIME_FORMAT_CN);
    }

    /**
     * 得到格式化后的时间，格式为yyyy年MM月dd日 HH:mm:ss，如2009年02月15日 15:23:45
     *
     * @param currDate 要格式化的时间
     * @return Date 返回格式化后的时间，默认格式为yyyy年MM月dd日 HH:mm:ss，如2009年02月15日 15:23:45
     * @see #getFormatDateTime_CN(String)
     */
    public static Date getFormatDateTimeToTime_CN(java.util.Date currDate) {
        return getFormatDateTime_CN(getFormatDateTime_CN(currDate));
    }

    /**
     * 得到格式化后的时间，格式为yyyy年MM月dd日 HH:mm:ss，如2009年02月15日 15:23:45
     *
     * @param currDate 要格式化的时间
     * @return Date 返回格式化后的时间，默认格式为yyyy年MM月dd日 HH:mm:ss，如2009年02月15日 15:23:45
     * @see #getFormatDateTime(String, String)
     */
    public static Date getFormatDateTime_CN(String currDate) {
        return getFormatDateTime(currDate, TIME_FORMAT_CN);
    }

    /**
     * 根据格式得到格式化后的时间
     *
     * @param currDate 要格式化的时间
     * @param format   时间格式，如yyyy-MM-dd HH:mm:ss
     * @return String 返回格式化后的时间，格式由参数<code>format</code>定义，如yyyy-MM-dd HH:mm:ss
     * @see java.text.SimpleDateFormat#format(java.util.Date)
     */
    public static String getFormatDateTime(java.util.Date currDate, String format) {
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.format(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(TIME_FORMAT);
            try {
                return dtFormatdB.format(currDate);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * 根据格式得到格式化后的日期
     *
     * @param currDate 要格式化的日期
     * @param format   日期格式，如yyyy-MM-dd
     * @return Date 返回格式化后的日期，格式由参数<code>format</code>
     * 定义，如yyyy-MM-dd，如2009-10-15
     * @see java.text.SimpleDateFormat#parse(java.lang.String)
     */
    public static Date getFormatDate(String currDate, String format) {
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.parse(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(DATE_FORMAT);
            try {
                return dtFormatdB.parse(currDate);
            } catch (Exception ex) {
            }
        }
        return null;
    }


    /**
     * 根据格式得到格式化后的时间
     *
     * @param currDate 要格式化的时间
     * @param format   时间格式，如yyyy-MM-dd HH:mm:ss
     * @return Date 返回格式化后的时间，格式由参数<code>format</code>定义，如yyyy-MM-dd HH:mm:ss
     * @see java.text.SimpleDateFormat#parse(java.lang.String)
     */
    public static Date getFormatDateTime(String currDate, String format) {
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.parse(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(TIME_FORMAT);
            try {
                return dtFormatdB.parse(currDate);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * @param time Seconds 传入参数 秒
     * @return 返回 格式 hh:mm:ss 秒
     */
    public static String getFormatHourAndMinuteTime(long time) {
        if (time < 60) {
            return String.valueOf(time);
        }
        if (time < 60 * 60) {
            int seconds = (int) (time % 60l);
            int minutes = (int) (time / (60l));
            return String.valueOf(minutes) + ":" + (seconds < 10 ? ("0" + String.valueOf(seconds)) : String.valueOf(seconds));
        }
        int seconds = (int) (time % 60l);
        int minutes = (int) ((time / 60l) % 60l);
        int hours = (int) (time / (60l * 60l));
        return hours + ":" + (minutes < 10 ? ("0" + String.valueOf(minutes)) : String.valueOf(minutes)) + ":"
                + (seconds < 10 ? ("0" + String.valueOf(seconds)) : String.valueOf(seconds));
    }

    /**
     * 得到本日的上月时间 如果当日为2007-9-1,那么获得2007-8-1
     */
    public static String getDateBeforeMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return getFormatDate(cal.getTime(), DATE_FORMAT);
    }

    /**
     * 得到本日的前几个月时间 如果number=2当日为2007-9-1,那么获得2007-7-1
     */
    public static String getDateBeforeMonth(int number) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -number);
        return getFormatDate(cal.getTime(), DATE_FORMAT);
    }

    /**
     * 获得两个Date型日期之间相差的天数（第2个减第1个）
     *
     * @param first
     * @param second
     * @return int 相差的天数
     */
    public static int getDaysBetweenDates(Date first, Date second) {
        Date d1 = getFormatDateTime(getFormatDate(first), DATE_FORMAT);
        Date d2 = getFormatDateTime(getFormatDate(second), DATE_FORMAT);

        Long mils = (d2.getTime() - d1.getTime()) / (TIME_DAY_MILLISECOND);

        return mils.intValue();
    }

    /**
     * 获得两个Date型日期之间相差的小时数（第2个减第1个）
     *
     * @return int 相差的小时数
     */
    public static int getHoursBetweenDates(Date first, Date second) {

        Date d1 = getFormatDateTime(getFormatDate(first), DATE_FORMAT);
        Date d2 = getFormatDateTime(getFormatDate(second), DATE_FORMAT);

        Long mils = (d2.getTime() - d1.getTime()) / (TIME_HOUR_MILLISECOND);

        return mils.intValue();

    }

    public static int getSecondsBetweenDates(Date first, Date second) {
        Long mils = (second.getTime() - first.getTime()) / (TIME_SECONDS_MILLISECOND);
        return mils.intValue();
    }

    /**
     * 获得两个String型日期之间相差的天数（第2个减第1个）
     *
     * @param first
     * @param second
     * @return int 相差的天数
     */
    public static int getDaysBetweenDates(String first, String second) {
        Date d1 = getFormatDateTime(first, DATE_FORMAT);
        Date d2 = getFormatDateTime(second, DATE_FORMAT);

        Long mils = (d2.getTime() - d1.getTime()) / (TIME_DAY_MILLISECOND);

        return mils.intValue();
    }


    public static Date getDate(long times) {
        Date date = new Date(times);
        return date;
    }

    /**
     * @return 获取两个Date之间的天数的列表
     */
    public static List<Date> getDaysListBetweenDates(Date first, Date second) {
        List<Date> dateList = new ArrayList<Date>();
        Date d1 = getFormatDateTime(getFormatDate(first), DATE_FORMAT);
        Date d2 = getFormatDateTime(getFormatDate(second), DATE_FORMAT);
        if (d1.compareTo(d2) > 0) {
            return dateList;
        }
        do {
            dateList.add(d1);
            d1 = getDateBeforeOrAfter(d1, 1);
        } while (d1.compareTo(d2) <= 0);
        return dateList;
    }


    public static String getDateBeforeDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return getFormatDate(cal.getTime(), DATE_FORMAT);
    }

    /**
     * 得到格式化后的当前系统日期，格式为yyyy-MM-dd，如2009-10-15
     *
     * @return String 返回格式化后的当前服务器系统日期，格式为yyyy-MM-dd，如2009-10-15
     * @see #getFormatDate(java.util.Date)
     */
    public static String getCurrDateStr() {
        return getFormatDate(getCurrDate());
    }

    public static int getCurrDateHour() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 得到格式化后的当前系统时间，格式为yyyy-MM-dd HH:mm:ss，如2009-10-15 15:23:45
     *
     * @return String 返回格式化后的当前服务器系统时间，格式为yyyy-MM-dd HH:mm:ss，如2009-10-15
     * 15:23:45
     * @see #getFormatDateTime(java.util.Date)
     */
    public static String getCurrDateTimeStr() {
        return getFormatDateTime(getCurrDate());
    }

    public static String getCurrDateTimeStr(String format) {
        return getFormatDate(getCurrDate(), format);
    }

    /**
     * 得到格式化后的当前系统日期，格式为yyyy年MM月dd日，如2009年02月15日
     *
     * @return String 返回当前服务器系统日期，格式为yyyy年MM月dd日，如2009年02月15日
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getCurrDateStr_CN() {
        return getFormatDate(getCurrDate(), DATE_FORMAT_CN);
    }

    /**
     * 得到格式化后的当前系统时间，格式为yyyy年MM月dd日 HH:mm:ss，如2009年02月15日 15:23:45
     *
     * @return String 返回格式化后的当前服务器系统时间，格式为yyyy年MM月dd日 HH:mm:ss，如2009年02月15日
     * 15:23:45
     * @see #getFormatDateTime(java.util.Date, String)
     */
    public static String getCurrDateTimeStr_CN() {
        return getFormatDateTime(getCurrDate(), TIME_FORMAT_CN);
    }

    /**
     * 得到系统当前日期的前或者后几天
     *
     * @param iDate 如果要获得前几天日期，该参数为负数； 如果要获得后几天日期，该参数为正数
     * @return Date 返回系统当前日期的前或者后几天
     * @see java.util.Calendar#add(int, int)
     */
    public static Date getDateBeforeOrAfter(int iDate) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, iDate);
        return cal.getTime();
    }

    /**
     * 得到日期的前或者后几天
     *
     * @param iDate 如果要获得前几天日期，该参数为负数； 如果要获得后几天日期，该参数为正数
     * @return Date 返回参数<code>curDate</code>定义日期的前或者后几天
     * @see java.util.Calendar#add(int, int)
     */
    public static Date getDateBeforeOrAfter(Date curDate, int iDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DAY_OF_MONTH, iDate);
        return cal.getTime();
    }

    /**
     * 得到格式化后的月份，格式为yyyy-MM，如2009-02
     *
     * @param currDate 要格式化的日期
     * @return String 返回格式化后的月份，格式为yyyy-MM，如2009-02
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getFormatMonth(java.util.Date currDate) {
        return getFormatDate(currDate, MONTH_FORMAT);
    }

    /**
     * 得到格式化后的日，格式为yyyyMMdd，如20090210
     *
     * @param currDate 要格式化的日期
     * @return String 返回格式化后的日，格式为yyyyMMdd，如20090210
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getFormatDay(java.util.Date currDate) {
        return getFormatDate(currDate, DAY_FORMAT);
    }

    public static String getFormatTime(java.util.Date currDate) {
        return getFormatDate(currDate, TIME_FORMAT_NUM);
    }

    public static String getFormatTimeHHmmss(java.util.Date currDate) {
        return getFormatDate(currDate, TIME_FORMAT_HHMMSS);
    }

    /**
     * 得到格式化后的当月第一天，格式为yyyy-MM-dd，如2009-10-01
     *
     * @return String 返回格式化后的当月第一天，格式为yyyy-MM-dd，如2009-10-01
     * @see java.util.Calendar#getMinimum(int)
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        int firstDay = cal.getMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return getFormatDate(cal.getTime(), DATE_FORMAT);
    }

    /**
     * 得到格式化后的下月第一天，格式为yyyy-MM-dd，如2009-10-01
     * <p/>
     * 要格式化的日期
     *
     * @return String 返回格式化后的下月第一天，格式为yyyy-MM-dd，如2009-10-01
     * @see java.util.Calendar#getMinimum(int)
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getFirstDayOfNextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, +1);
        int firstDay = cal.getMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return getFormatDate(cal.getTime(), DATE_FORMAT);
    }

    /**
     * 得到格式化后的当月第一天，格式为yyyy-MM-dd，如2009-10-01
     *
     * @param currDate 要格式化的日期
     * @return String 返回格式化后的当月第一天，格式为yyyy-MM-dd，如2009-10-01
     * @see java.util.Calendar#getMinimum(int)
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getFirstDayOfMonth(Date currDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currDate);
        int firstDay = cal.getMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return getFormatDate(cal.getTime(), DATE_FORMAT);
    }

    /**
     * 得到格式化后的当月最后一天，格式为yyyy-MM-dd，如2009-10-28
     *
     * @param currDate 要格式化的日期
     * @return String 返回格式化后的当月最后一天，格式为yyyy-MM-dd，如2009-10-28
     * @see java.util.Calendar#getMinimum(int)
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getLastDayOfMonth(Date currDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currDate);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return getFormatDate(cal.getTime(), DATE_FORMAT);
    }

    /**
     * 得到格式化后的当月最后一天，格式为yyyy-MM-dd，如2009-10-28
     * <p/>
     * 要格式化的日期
     *
     * @return String 返回格式化后的当月最后一天，格式为yyyy-MM-dd，如2009-10-28
     * @see java.util.Calendar#getMinimum(int)
     * @see #getFormatDate(java.util.Date, String)
     */
    public static String getLastDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        return getFormatDate(cal.getTime(), DATE_FORMAT);
    }

    /**
     * 得到日期的前或者后几小时
     *
     * @param iHour 如果要获得前几小时日期，该参数为负数； 如果要获得后几小时日期，该参数为正数
     * @return Date 返回参数<code>curDate</code>定义日期的前或者后几小时
     * @see java.util.Calendar#add(int, int)
     */
    public static Date getDateBeforeOrAfterHours(Date curDate, int iHour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.HOUR_OF_DAY, iHour);
        return cal.getTime();
    }

    /**
     * 得到日期的前或者后几分钟
     *
     * @param iMinute 如果要获得前几小时日期，该参数为负数； 如果要获得后几小时日期，该参数为正数
     * @return Date 返回参数<code>curDate</code>定义日期的前或者后几小时
     * @see java.util.Calendar#add(int, int)
     */
    public static Date getDateBeforeOrAfterMinute(Date curDate, int iMinute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.MINUTE, iMinute);
        return cal.getTime();
    }

    /**
     * 判断日期是否在当前周内
     *
     * @param curDate
     * @param compareDate
     * @return
     */
    public static boolean isSameWeek(Date curDate, Date compareDate) {
        if (curDate == null || compareDate == null) {
            return false;
        }

        Calendar calSun = Calendar.getInstance();
        calSun.setTime(getFormatDateToDate(curDate));
        calSun.set(Calendar.DAY_OF_WEEK, 1);

        Calendar calNext = Calendar.getInstance();
        calNext.setTime(calSun.getTime());
        calNext.add(Calendar.DATE, 7);

        Calendar calComp = Calendar.getInstance();
        calComp.setTime(compareDate);
        if (calComp.after(calSun) && calComp.before(calNext)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSameDay(Date currentDate, Date compareDate) {
        if (currentDate == null || compareDate == null) {
            return false;
        }
        String current = getFormatDate(currentDate);
        String compare = getFormatDate(compareDate);
        if (current.equals(compare)) {
            return true;
        }
        return false;
    }

    public static Date setDateCustomBeginFix(Date date, int hour) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date setDateBeginFix(Date date) {
        return setDateCustomBeginFix(date, 0);
    }

    public static Date setDateCustomEndFix(Date date, int hour) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date setDateEndFix(Date date) {
        return setDateCustomEndFix(date, 23);
    }

    /**
     * 时间查询时,开始时间的 00:00:00
     */
    public static String addDateBeginfix(String datestring) {
        if ((datestring == null) || datestring.equals("")) {
            return null;
        }
        return datestring + " 00:00:00";
    }

    /**
     * 时间查询时,结束时间的 23:59:59
     */
    public static String addDateEndfix(String datestring) {
        if ((datestring == null) || datestring.equals("")) {
            return null;
        }
        return datestring + " 23:59:59";
    }

    /**
     * 返回格式化的日期
     *
     * @param dateStr 格式"yyyy-MM-dd 23:59:59";
     * @return
     */
    public static Date getFormatDateEndfix(String dateStr) {
        dateStr = addDateEndfix(dateStr);
        return getFormatDateTime(dateStr);
    }

    /**
     * 返回格式化的日期
     *
     * @param datePre 格式"yyyy-MM-dd HH:mm:ss";
     * @return
     */
    public static Date formatEndTime(String datePre) {
        if (datePre == null)
            return null;
        String dateStr = addDateEndfix(datePre);
        return getFormatDateTime(dateStr);
    }

    // date1加上compday天数以后的日期与当前时间比较，如果大于当前时间返回true，否则false
    public static Boolean compareDay(Date date1, int compday) {
        if (date1 == null)
            return false;
        Date dateComp = getDateBeforeOrAfter(date1, compday);
        Date nowdate = new Date();
        if (dateComp.after(nowdate))
            return true;
        else
            return false;
    }

    /**
     * 获得年份
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获得月份
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    /**
     * 获得天
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得hour
     *
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static Date fromDateString(String dataStr) {
        if (dataStr == "") {
            return new Date();
        }
        try {
            return dateTimeFormatter.parseDateTime(dataStr).toDate();
        } catch (Exception e) {
            try {
                return shortDateTimeFormatter.parseDateTime(dataStr).toDate();
            } catch (Exception ex) {
                ex.printStackTrace();
                return new Date();
            }
        }
    }

    public static Date fromDateStringWithYYYY_MM_DD_HH_MM_SS(String dateStr) {
        try {
            return dateTimeFormatter.parseDateTime(dateStr).toDate();
        } catch (Exception e) {
            return null;
        }
    }

    public static String toDateString(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTimeFormatter.print(dateTime);
    }

    public static String toShortDateString(Date date) {
        DateTime dateTime = new DateTime(date);
        return shortDateTimeFormatter.print(dateTime);
    }

    public static long compare(Date time, long lasttime) {
        time = Preconditions.checkNotNull(time);
        lasttime = Preconditions.checkNotNull(lasttime);
        return time.getTime() - lasttime;

    }

    public static long compare(Date currenttime, Date lasttime) {
        currenttime = Preconditions.checkNotNull(currenttime);
        lasttime = Preconditions.checkNotNull(lasttime);
        return compare(currenttime, lasttime.getTime());
    }

    public static Date yesterdayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date yesterdayLast() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date dayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date dayLast() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date dayBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date dayLast(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }


    public static Date add(int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static Date add(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public static boolean inOneDay(Date currentDay, Date compareDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDay);
        int currentDayYears = calendar.get(Calendar.YEAR);
        int currentDayDays = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(compareDay);
        int compareDayYears = calendar.get(Calendar.YEAR);
        int compareDayDays = calendar.get(Calendar.DAY_OF_YEAR);
        if (currentDayYears == compareDayYears && currentDayDays == compareDayDays) {
            return true;
        } else {
            return false;
        }
    }

    public static Date weekendBegin() {
        return getMonday(getCurrDate());
    }

    public static Date getMonday(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0)
            dayOfWeek = 7;
        calendar.add(Calendar.DATE, -dayOfWeek + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date weekendLast() {
        return getSunday(getCurrDate());
    }

    public static Date getSunday(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0)
            dayOfWeek = 7;
        calendar.add(Calendar.DATE, -dayOfWeek + 7);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getLastThursday(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 5;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        if (dayOfWeek < 0) {
            dayOfWeek = -dayOfWeek - 7;
        }
        if (dayOfWeek > 0) {
            dayOfWeek = -dayOfWeek;
        }
        calendar.add(Calendar.DATE, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取本周的起始日期
     *
     * @return
     */
    public static Pair<String, String> getCurrWeek() {
        Calendar c = Calendar.getInstance();
        int weekday = c.get(7) - 1;
        c.add(5, -weekday);
        String beginDate = DateUtils.getFormatDate(c.getTime());
        c.add(5, 6);
        String endDate = DateUtils.getFormatDate(c.getTime());
        return Pair.with(beginDate, endDate);
    }

    public static boolean isThisDaySaturday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int day = c.get(Calendar.DAY_OF_WEEK);

        if (day == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isThisDayMonday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int day = c.get(Calendar.DAY_OF_WEEK);

        if (day == Calendar.MONDAY) {
            return true;
        } else {
            return false;
        }

    }

    public static Date getAfterDate(int afterDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, afterDay);
        return calendar.getTime();
    }


    public static void main(String[] args) {
        BigDecimal rate = new BigDecimal(2).setScale(4, BigDecimal.ROUND_DOWN);
        BigDecimal price = new BigDecimal(999);
        if (rate != null && rate.doubleValue() > 0) {
            //乘以税率10块取整
            BigDecimal priceRate = price.multiply(rate.divide(new BigDecimal(1000)));
            priceRate = priceRate.setScale(0, BigDecimal.ROUND_UP).multiply(new BigDecimal(10));
            price = price.add(priceRate);
        }
    }
}
