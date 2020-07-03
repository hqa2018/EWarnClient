package com.taide.ewarn.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    public static final long SECOND_MILESECONDS = 1000L;
    public static final long MINUTE_MILESECONDS = 60 * SECOND_MILESECONDS;
    public static final long HOUR_MILESECONDS = 60 * MINUTE_MILESECONDS;
    public static final long DAY_MILESECONDS = 24 * HOUR_MILESECONDS;

    /** SimpleDateFormat 解析时是否严格,true--不严格，false--严格 **/
    public static boolean IS_LENIENT = true;

    /**
     * 根据年月日构建一个日期
     *
     * @param year
     *            int--年
     * @param month
     *            int--月
     * @param day
     *            int--日
     * @return Date--构建的日期
     */
    public static Date createDate(int year, int month, int day) {
        if (month == 0)
            month++;
        if (month > 12 || day > 31)
            throw new IllegalArgumentException("月份数不能超过12,天数不能超过31.(月份数:"
                    + month + ",天数:" + day + ")");
        if (month == 2) {
            if (isLeapYear(year) && day > 29)
                throw new IllegalArgumentException("闰年2月天数不能超过29.(天数:" + day
                        + ")");
            else if (day > 28)
                throw new IllegalArgumentException("平年2月天数不能超过28.(天数:" + day
                        + ")");
        }

        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    /**
     * 根据年月日时分秒构建一个日期
     *
     * @param year
     *            int--年
     * @param month
     *            int--月
     * @param day
     *            int--日
     * @param hour
     *            int--小时
     * @param minute
     *            int--分钟
     * @param seconds
     *            int--秒
     * @return Date--构建的日期
     */
    public static Date createDate(int year, int month, int day, int hour,
                                  int minute, int seconds) {
        if (hour > 24 || minute > 60 || seconds > 60)
            throw new IllegalArgumentException("小时数不能超过24,分钟数和秒数不能超过60.(小时数:"
                    + hour + ",分钟数:" + minute + ",秒数:" + seconds + ")");

        Calendar c = Calendar.getInstance();
        c.setTime(createDate(year, month, day));
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, seconds);
        return c.getTime();
    }

    /**
     * 获取日期的其中一部分，例如：获取日期的年，月，日，小时等
     *
     * @param date
     *            Date--源日期
     * @param type
     *            DateType--所获取的部分
     * @return int--日期该部分的值
     */
    public static int getBy(Date date, DateType type) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(type.getType());
    }

    /**
     * 判断字符串是否为日期格式，如yyy-MM-dd,yyyy-MM-dd HH:mm等
     * @param time
     * @return boolean
     */
    public static boolean isDateTime(String time){
        boolean isTime = true;
        try {
            DateUtil.parseDefault(time);
        } catch (Exception e) {
            isTime = false;
        }

        return isTime;
    }

    /**
     * 比较两个字符串日期的大小，如果两个字符串都不是日期格式则返回false，如果新字符串日期为日期格式而旧的不是则返回true
     * @param nTime
     * 				新日期
     * @param oTime
     * 				旧日期
     * @return boolean
     */
    public static boolean compareDataTime(String nTime, String oTime){

        if(isDateTime(nTime)&&isDateTime(oTime)){
            if(DateUtil.parseDefault(nTime).after(DateUtil.parseDefault(oTime)))
                return true;
            else
                return false;
        }else if(isDateTime(nTime)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 根据格式化格式格式化日期
     *
     * @param date
     *            java.util.Date-日期
     * @param format
     *            FormatType--日期格式
     * @return String--格式化后的日期
     */
    public static String format(Date date, FormatType format) {
        if(date != null)
            return getFormat(format).format(date);
        else
            return null;
    }

    public static String format(long date, FormatType format) {
        return getFormat(format).format(new Date(date));
    }

    public static String format(long date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(IS_LENIENT);
        return sdf.format(new Date(date));
    }

    /**
     * 根据日期格式将字符串形式的日期解析为日期
     *
     * @param dateStr
     *            String--日期字符串
     * @param parseFormat
     *            FormatType--日期格式
     * @throws IllegalArgumentException
     *             输入的日期与格式不匹配
     * @return Date--解析后的日期,如果解析日期格式不匹配则返回null
     */
    public static Date parse(String dateStr, FormatType parseFormat) {
        SimpleDateFormat sdf = getFormat(parseFormat);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
        }
        throw new IllegalArgumentException("您输入的日期与格式不匹配!(日期:" + dateStr
                + ",格式:" + parseFormat.getFormat() + ")");
    }

    /**
     * 用默认的格式解析日期，日期格式是默认日期格式的子集，如yyy-MM-dd,yyyy-MM-dd HH:mm等 默认日期格式为：yyyy-MM-dd
     * HH:mm:ss.SSS
     *
     * @param dateStr
     * @return
     */
    public static Date parseDefault(String dateStr) {
        int dateLength = dateStr.length();
        if (FormatType.yyyy_MM.getFormat().length() == dateLength)
            return parse(dateStr, FormatType.yyyy_MM);
        else if (FormatType.yyyy_MM_dd.getFormat().length() == dateLength)
            return parse(dateStr, FormatType.yyyy_MM_dd);
        else if (FormatType.yyyy_MM_dd_HH.getFormat().length() == dateLength)
            return parse(dateStr, FormatType.yyyy_MM_dd_HH);
        else if (FormatType.yyyy_MM_dd_HH_mm.getFormat().length() == dateLength)
            return parse(dateStr, FormatType.yyyy_MM_dd_HH_mm);
        else if (FormatType.yyyy_MM_dd_HH_mm_ss.getFormat().length() == dateLength)
            return parse(dateStr, FormatType.yyyy_MM_dd_HH_mm_ss);
        else if (FormatType.yyyy_MM_dd_HH_mm_ss_S.getFormat().length() == dateLength)
            return parse(dateStr, FormatType.yyyy_MM_dd_HH_mm_ss_S);
        else if (FormatType.yyyy_MM_dd_HH_mm_ss_SSS.getFormat().length() == dateLength)
            return parse(dateStr, FormatType.yyyy_MM_dd_HH_mm_ss_SSS);
        throw new IllegalArgumentException("您输入的日期:" + dateStr + "无法通过默认格式解析!");
    }

    /**
     * 获取所在日期的00:00:00
     *
     * @param dateStr
     *            String--日期字符串
     * @param parseFormat
     *            FormatType--日期格式
     * @throws IllegalArgumentException
     *             输入的日期与格式不匹配
     * @return 日期的00:00:00
     */
    public static Date parseToDayStart(String dateStr, FormatType parseFormat) {
        return getBeginOfDay(parse(dateStr, parseFormat));
    }

    /**
     * 获取所在日期的23:59:59
     *
     * @param dateStr
     *            String--日期字符串
     * @param parseFormat
     *            FormatType--日期格式
     * @throws IllegalArgumentException
     *             输入的日期与格式不匹配
     * @return 日期的23:59:59
     */
    public static Date parseToDayEnd(String dateStr, FormatType parseFormat) {
        return getEndOfDay(parse(dateStr, parseFormat));
    }

    /**
     * 将一种格式的日期字符串转换成另一种格式的日期字符串
     *
     * @param dateStr
     *            String--待转换的日子字符串
     * @param parseFormat
     *            FormatType--解析字符串的日期格式
     * @param format
     *            FormatType--格式化解析日期的字符串
     * @return String--转换格式后的日期字符串
     */
    public static String transformDate(String dateStr, FormatType parseFormat,
                                       FormatType format) {
        return format(parse(dateStr, parseFormat), format);
    }

    /**
     * 获取与date相隔interval的日期
     *
     * @param date
     *            Date--源日期
     * @param type
     *            DateType--相隔的日期类型，day or month or week...
     * @param interval
     *            int--相隔的数量,正数--往后加，负数--向前减
     * @return Date--处理后的日期
     */
    public static Date getIntervalDate(Date date, DateType type, int interval) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        c.add(type.getType(), interval);
        return c.getTime();
    }

    /**
     * 获取与date相隔interval的日期
     *
     * @param date
     *            Date--源日期
     * @param type
     *            DateType--相隔的日期类型，day or month or week...
     * @param interval
     *            int--相隔的数量,正数--往后加，负数--向前减
     * @return Date--处理后的日期
     */
    public static Date getIntervalDayDate(Date date, int interval) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, interval);
        return c.getTime();
    }

    /**
     * 获取与date相隔interval的日期,返回字符串形式的日期
     *
     * @param date
     *            Date--源日期
     * @param type
     *            DateType--相隔的日期类型，day or month or week...
     * @param interval
     *            int--相隔的数量,正数--往后加，负数--向前减
     * @param format
     *            FormatType--日期格式化的格式
     * @return String--处理后的日期
     */
    public static String getIntervalDateToString(Date date, DateType type,
                                                 int interval, FormatType format) {
        return format(getIntervalDate(date, type, interval), format);
    }

    /**
     * 获取与date相隔interval的日期,date为字符串形式的日期
     *
     * @param dateStr
     *            String--源日期的字符串形式
     * @param parseFormat
     *            FormatType--解析字符串日期的日期格式
     * @param type
     *            DateType--相隔的日期类型
     * @param interval
     *            int--相隔的数量,正数--往后加，负数--向前减
     * @return Date--处理后的日期,如果解析日期格式不匹配则返回null
     */
    public static Date getIntervalDate(String dateStr, FormatType parseFormat,
                                       DateType type, int interval) {
        Date date = parse(dateStr, parseFormat);
        return getIntervalDate(date, type, interval);
    }

    public static int dayForWeek(String pTime) throws Throwable {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date tmpDate = format.parse(pTime);

        Calendar cal = new GregorianCalendar();

        cal.set(tmpDate.getYear(), tmpDate.getMonth(), tmpDate.getDay());

        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static int dayForWeekOther(String pTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    public static String dayForWeekCname(String pTime){
        String weekCname = "";
        try {
            int week = dayForWeekOther(pTime);
            switch (week){
                case 1 :
                    weekCname = "星期一";
                    break;
                case 2 :
                    weekCname = "星期二";
                    break;
                case 3 :
                    weekCname = "星期三";
                    break;
                case 4 :
                    weekCname = "星期四";
                    break;
                case 5 :
                    weekCname = "星期五";
                    break;
                case 6 :
                    weekCname = "星期六";
                    break;
                case 7 :
                    weekCname = "星期天";
                    break;
                default:
                    weekCname = "";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weekCname;
    }

    /**
     * 获取与date相隔interval的日期,date为字符串形式的日期,返回字符串形式的日期
     *
     * @param dateStr
     *            String--源日期的字符串形式
     * @param parseFormat
     *            FormatType--解析字符串日期的日期格式
     * @param type
     *            DateType--相隔的日期类型
     * @param interval
     *            int--相隔的数量,正数--往后加，负数--向前减
     * @param format
     * @return String--处理后的日期的字符串形式,如果解析日期格式不匹配则返回null
     */
    public static String getIntervalDateToString(String dateStr,
                                                 FormatType parseFormat, DateType type, int interval,
                                                 FormatType format) {
        Date date = getIntervalDate(dateStr, parseFormat, type, interval);
        return format(date, format);
    }


    /**
     * 获取一天的开始日期 例如：2010-02-15 00:00:00
     *
     * @param date
     *            Date--源日期
     * @return Date--该天的开始日期
     */
    public static Date getBeginOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return clearHourMinuteSecond(c.getTime());
    }

    public static String getBeginOfDayToString(Date date, FormatType format) {
        return format(getBeginOfDay(date), format);
    }

    public static Date getBeginOfLastDay(Date date) {
        return getIntervalDate(clearHourMinuteSecond(date),
                DateType.DAY_OF_MONTH, -1);
    }

    public static Date getBeginOfNextDay(Date date) {
        return getIntervalDate(clearHourMinuteSecond(date),
                DateType.DAY_OF_MONTH, 1);
    }

    public static Date getBeginOfNextDay(String date, FormatType parseFOrmat) {
        return getBeginOfNextDay(parse(date, parseFOrmat));
    }

    /**
     * 获取一天的结束日期 例如：2010-02-15 23:59:59
     *
     * @param date
     *            Date--源日期
     * @return Date--该天的结束日期
     */
    public static Date getEndOfDay(Date date) {
        Date beginOfDay = getBeginOfDay(date);
        return new Date(beginOfDay.getTime() + DAY_MILESECONDS - 1000);
    }

    public static String getEndOfDayToString(Date date, FormatType format) {
        return format(getEndOfDay(date), format);
    }

    /**
     * 获取传入日期的当月的开始日期 例如：2010-02-01 00:00:00
     *
     * @param date
     *            Date--要获取开始日期的月份的所编一个日期
     * @return Date--该月的开始日期
     */
    public static Date getBeginOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return clearHourMinuteSecond(c.getTime());
    }

    public static Date getBeginOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return clearHourMinuteSecond(c.getTime());
    }

    /**
     *
     * 获取传入日期的当月的开始日期
     *
     * @param date
     *            Date--要获取开始日期的月份的所编一个日期
     * @return String--该月的开始日期
     */
    public static String getBeginOfMonthToString(Date date, FormatType format) {
        return format(getBeginOfMonth(date), format);
    }

    /**
     * 获取传入日期的当月的结束日期 例如：2010-03-31 23:59:59
     *
     * @param date
     *            Date--要获取结束日期的月份的所编一个日期
     * @return Date--该月的结束日期
     */
    public static Date getEndOfMonth(Date date) {
        Date beginOfMonth = getBeginOfMonth(date);
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(beginOfMonth);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.SECOND, -1);
        return c.getTime();
    }

    /**
     * 返回下一个月日期
     * @param date
     * @return
     */
    public static Date getNextMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        c.add(Calendar.MONTH, 1);
        return c.getTime();
    }

    /**
     * 返回上一个月日期
     * @param date
     * @return
     */
    public static Date getLastMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }


    /**
     * 获取间隔年份的日期
     * @param date
     * @param year
     * @return
     */
    public static Date getIntervalYear(Date date, int year){
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        c.add(Calendar.YEAR, year);
        return c.getTime();
    }


    /**
     * 返回当前时间减去day天后的日期
     *
     * @param day
     * @return
     */
    public static Date getBeforeDayByMius(String day) {

        Date curDate = new Date();
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(curDate);
        c.add(Calendar.DAY_OF_MONTH, Integer.valueOf(day).intValue());

        return c.getTime();
    }

    /**
     * 获取传入日期的当月的结束日期
     *
     * @param date
     *            Date--要获取结束日期的月份的所编一个日期
     * @return String--该月的结束日期
     */
    public static String getEndOfMonthToString(Date date, FormatType format) {
        return format(getEndOfMonth(date), format);
    }

    /**
     * 将一个日期的小时，分钟，秒，毫秒全部清零
     *
     * @param date
     *            Date--源日期
     * @return Date--清零后的日期
     */
    public static Date clearHourMinuteSecond(Date date) {
        Date zoroDate = clearMinuteSecond(date);
        Calendar c = Calendar.getInstance();
        c.setTime(zoroDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c.getTime();
    }

    /**
     * 将一个日期的分钟，秒，毫秒全部清零
     *
     * @param date
     *            Date--源日期
     * @return Date--清零后的日期
     */
    public static Date clearMinuteSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取两个日期之间的天数，如果天数不为整数，强行取整
     *
     * @param beginDate
     *            Date--起始日期
     * @param endDate
     *            Date--结束日期
     * @return 两个日期之间的间隔天数
     */
    public static int getDaysBetween(Date beginDate, Date endDate) {
        if (endDate.getTime() < beginDate.getTime())
            throw new IllegalArgumentException(
                    "endDate must be later than beginDate");
        return (int) ((endDate.getTime() - beginDate.getTime()) / DAY_MILESECONDS);
    }

    /**
     *
     * 获取两个日期之间的小时数，如果小时数不为整数，强行取整
     *
     * @param beginDate
     *            Date--起始日期
     * @param endDate
     *            Date--结束日期
     * @return int--两个日期之间的间隔天数
     */
    public static int getHoursBetween(Date beginDate, Date endDate) {
        if (endDate.getTime() < beginDate.getTime())
            throw new IllegalArgumentException(
                    "endDate must be later than beginDate");
        return (int) ((endDate.getTime() - beginDate.getTime()) / HOUR_MILESECONDS);
    }

    /**
     *
     * 获取两个日期之间的分钟数（无论先后），如果小时数不为整数，强行取整
     *
     * @param beginDate
     *            Date--起始日期
     * @param endDate
     *            Date--结束日期
     * @return int--两个日期之间的间隔天数
     */
    public static int fetchHoursBetween(Date beginDate, Date endDate) {
        int betwn = (int) ((endDate.getTime() - beginDate.getTime()) / HOUR_MILESECONDS);

        return Math.abs(betwn);
    }

    /**
     *
     * 获取两个日期之间的分钟数，如果小时数不为整数，强行取整
     *
     * @param beginDate
     *            Date--起始日期
     * @param endDate
     *            Date--结束日期
     * @return int--两个日期之间的间隔天数
     */
    public static int getMinutesBetween(Date beginDate, Date endDate) {
        if (endDate.getTime() < beginDate.getTime())
            throw new IllegalArgumentException(
                    "endDate must be later than beginDate");
        return (int) ((endDate.getTime() - beginDate.getTime()) / MINUTE_MILESECONDS);
    }

    /**
     *
     * 获取两个日期之间的分钟数（无论先后），如果小时数不为整数，强行取整
     *
     * @param beginDate
     *            Date--起始日期
     * @param endDate
     *            Date--结束日期
     * @return int--两个日期之间的间隔天数
     */
    public static int fetchMinutesBetween(Date beginDate, Date endDate) {
        int betwn = (int) ((endDate.getTime() - beginDate.getTime()) / MINUTE_MILESECONDS);

        return Math.abs(betwn);
    }


    /**
     *
     * 获取两个日期之间的秒数，如果秒数不为整数，强行取整
     *
     * @param beginDate
     *            Date--起始日期
     * @param endDate
     *            Date--结束日期
     * @return int--两个日期之间的间隔天数
     */
    public static int getSecondsBetween(Date beginDate, Date endDate) {
        if (endDate.getTime() < beginDate.getTime())
            throw new IllegalArgumentException(
                    "endDate must be later than beginDate");
        return (int) ((endDate.getTime() - beginDate.getTime()) / SECOND_MILESECONDS);
    }


    /**
     * 判断字符串型日期是否给闰年
     *
     * @param dateString
     *            String--日期的字符串形式
     * @param parseFormat
     *            FormatType--日期格式
     * @return boolean--是否为闰年
     */
    public static boolean isLeapYear(String dateString, FormatType parseFormat) {
        Date date = parse(dateString, parseFormat);
        return isLeapYear(date);
    }

    /**
     * 判断传入的日期是否为闰年
     *
     * @param date
     *            Date--日期
     * @return boolean--是否为闰年
     */
    public static boolean isLeapYear(Date date) {
        return isLeapYear(getBy(date, DateType.YEAR));
    }

    /**
     * 判断传入的年份数是否为闰年
     *
     * @param year
     *            int--年份数
     * @return boolean--是否为闰年
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * 获取给定日期所在年份的天数
     *
     * @param date
     *            Date--日期
     * @return int--所在年份的天数
     */
    public static int getYearDays(Date date) {
        return isLeapYear(date) ? 366 : 365;
    }

    /**
     * 获取给定年份的天数
     *
     * @param year
     *            int-年份
     * @return int--所在年份的天数
     */
    public static int getYearDays(int year) {
        return isLeapYear(year) ? 366 : 365;
    }

    /**
     * 获取给定日期的天数
     *
     * @param dateString
     *            String--日期的字符串形式
     * @param parseFormat
     *            FormatType--日期格式
     * @return int--所在年份的天数
     */
    public static int getYearDays(String dateString, FormatType parseFormat) {
        return isLeapYear(dateString, parseFormat) ? 366 : 365;
    }

    /**
     * 获取系统日期所在年份的天数
     *
     * @return int--当前年的天数
     */
    public static int getCurrYearDays() {
        return getYearDays(new Date());
    }

    /**
     * 获取所给日期所在月份的天数
     *
     * @param date
     *            Date--日期
     * @return int-所给日期月份的天数
     */
    public static int getMonthDays(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取所给日期所在月份的天数
     *
     * @param dateString
     *            String--日期字符串
     * @param parseFormat
     *            FormatType--日期格式
     * @return int-所给日期月份的天数
     */
    public static int getMonthDays(String dateString, FormatType parseFormat) {
        Date date = parse(dateString, parseFormat);
        return getMonthDays(date);
    }

    private static SimpleDateFormat getFormat(FormatType format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format.getFormat());
        sdf.setLenient(IS_LENIENT);
        return sdf;
    }

    /**
     * 获取当前时间超过整点的分钟数
     * @param date
     * @return
     */
    public static int getMinutesForOclock(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        int min = gc.get(gc.MINUTE);

        return min;
    }


    /**
     * 获取2个日期间间隔的月份数
     *
     * @param sDate
     * @param eDate
     * @return
     */
    public static int getMonthsBetween(Date sDate, Date eDate) {
        int sYear = getBy(sDate, DateType.YEAR);
        int eYear = getBy(eDate, DateType.YEAR);
        int sMonth = getBy(sDate, DateType.MONTH);
        int eMonth = getBy(eDate, DateType.MONTH);

        if (sYear == eYear)
            return eMonth - sMonth;

        return (eYear - sYear) * 12 + eMonth + 11 - sMonth;
    }

    // ////////////////////////////////////////////////////////////////

    /**
     * 日期类型，和Calendar中的类型对应
     *
     * @author t
     *
     */
    public static enum DateType {
        /** 指年份 */
        YEAR(Calendar.YEAR, 365 * 24 * 60 * 60 * 1000L),

        /** 指一年中的第几个月，第一个月的值为0 */
        MONTH(Calendar.MONTH, 30 * 24 * 60 * 60 * 1000L),

        /** 指一个月中的某天,它与 DAY_OF_MONTH 是同义词,一个月中第一天的值为 1。 */
        DAY(Calendar.DATE, 24 * 60 * 60 * 1000L),

        /** 指示一个月中的某天，它与 DATE 是同义词，一个月中第一天的值为 1。 */
        DAY_OF_MONTH(Calendar.DAY_OF_MONTH, 24 * 60 * 60 * 1000L),

        /** 指示当前年中的天数，一年中第一天的值为 1。 */
        DAY_OF_YEAR(Calendar.DAY_OF_YEAR, 24 * 60 * 60 * 1000L),

        /**
         * 指示一个星期中的某天。该字段可取的值为 SUNDAY、MONDAY、TUESDAY、WEDNESDAY、THURSDAY、FRIDAY 和
         * SATURDAY
         */
        DAY_OF_WEEK(Calendar.DAY_OF_WEEK, 24 * 60 * 60 * 1000L),

        /** 指示当前月中的第几个星期 */
        DAY_OF_WEEK_IN_MONTH(Calendar.DAY_OF_WEEK_IN_MONTH,
                24 * 60 * 60 * 1000L),

        /**
         * 示上午或下午的小时。HOUR 用于 12 小时制时钟 (0 - 11)。中午和午夜用 0 表示，不用 12 表示。 例如，在
         * 10:04:15.250 PM 这一时刻，HOUR 为 10。
         */
        HOUR(Calendar.HOUR, 60 * 60 * 1000L),

        /**
         * 指示一天中的小时。HOUR_OF_DAY 用于 24 小时制时钟。 例如，在 10:04:15.250 PM
         * 这一时刻，HOUR_OF_DAY 为 22
         */
        HOUR_OF_DAY(Calendar.HOUR_OF_DAY, 60 * 60 * 1000L),

        /** 指示一小时中的分钟。例如，在 10:04:15.250 PM 这一时刻，MINUTE 为 4。 */
        MINUTE(Calendar.MINUTE, 60 * 1000L),

        /** 指示一分钟中的秒。例如，在 10:04:15.250 PM 这一时刻，SECOND 为 15。 */
        SECOND(Calendar.SECOND, 1000L),

        /** 指示一秒中的毫秒。例如，在 10:04:15.250 PM 这一时刻，MILLISECOND 为 250。 */
        MILLISECOND(Calendar.MILLISECOND, 1L);

        private int type;
        private long time;

        DateType(int type, long time) {
            this.type = type;
            this.time = time;
        }

        public int getType() {
            return type;
        }

        public long getTime() {
            return time;
        }

    }

    /**
     * 日期格式,列举常见的日期格式
     *
     * @author t
     *
     */
    public static enum FormatType {
        /** 格式为：yyyyMM **/
        yyyyMM("yyyyMM"),

        /** 格式为：yyyy/MM **/
        yyyy__MM("yyyy/MM"),

        /** 格式为：yyyy-MM **/
        yyyy_MM("yyyy-MM"),

        /** 格式为: yyyy-MM-dd */
        yyyy_MM_dd("yyyy-MM-dd"),

        /** 格式为: yyyyMMdd */
        yyyyMMdd("yyyyMMdd"),

        /** 格式为: yyyy/MM/dd */
        yyyy__MM__dd("yyyy/MM/dd"),

        /** 格式为: HHmm */
        HHmm("HHmm"),

        /** 格式为: HHmmss */
        HHmmss("HHmmss"),

        /** 格式为: HHmmssS */
        HHmmssS("HHmmssS"),

        /** 格式为: HHmmssSSS */
        HHmmssSSS("HHmmssSSS"),

        /** 格式为: HH:mm */
        HH_mm("HH:mm"),

        /** 格式为: HH:mm:ss */
        HH_mm_ss("HH:mm:ss"),

        /** 格式为: HH:mm:ss.S */
        HH_mm_ss_S("HH:mm:ss.S"),

        /** 格式为: HH:mm:ss.SSS */
        HH_mm_ss_SSS("HH:mm:ss.SSS"),

        /** 格式为: yyyy-MM-dd HH */
        yyyy_MM_dd_HH("yyyy-MM-dd HH"),

        /** 格式为: yyyy-MM-dd HH:mm */
        yyyy_MM_dd_HH_mm("yyyy-MM-dd HH:mm"),

        /** 格式为: yyyy-MM-dd HH:mm:ss */
        yyyy_MM_dd_HH_mm_ss("yyyy-MM-dd HH:mm:ss"),

        /** 格式为: yyyy-MM-dd HH:mm:ss.S */
        yyyy_MM_dd_HH_mm_ss_S("yyyy-MM-dd HH:mm:ss.S"),

        /** 格式为: yyyy-MM-dd HH:mm:ss.SSS */
        yyyy_MM_dd_HH_mm_ss_SSS("yyyy-MM-dd HH:mm:ss.SSS"),

        /** 格式为: yyyy-MM-dd HHmm */
        yyyy_MM_dd_HHmm("yyyy-MM-dd HHmm"),

        /** 格式为: yyyy-MM-dd HHmmss */
        yyyy_MM_dd_HHmmss("yyyy-MM-dd HHmmss"),

        /** 格式为: yyyy-MM-dd HHmmss.S */
        yyyy_MM_dd_HHmmssS("yyyy-MM-dd HHmmss.S"),

        /** 格式为: yyyy-MM-dd HHmmssSSS */
        yyyy_MM_dd_HHmmssSSS("yyyy-MM-dd HHmmssSSS"),

        /** 格式为: yyyyMMddHHmm */
        yyyyMMddHHmm("yyyyMMddHHmm"),

        /** 格式为: yyyyMMddHHmmss */
        yyyyMMddHHmmss("yyyyMMddHHmmss"),

        /** 格式为: yyyyMMddHHmmssS */
        yyyyMMddHHmmssS("yyyyMMddHHmmssS"),

        /** 格式为: yyyyMMddHHmmssSSS */
        yyyyMMddHHmmssSSS("yyyyMMddHHmmssSSS"),

        /** 格式为: yyyy/MM/dd HH:mm */
        yyyy__MM__dd_HH_mm("yyyy/MM/dd HH:mm"),

        /** 格式为: yyyy/MM/dd HH:mm:ss */
        yyyy__MM__dd_HH_mm_ss("yyyy/MM/dd HH:mm:ss"),

        /** 格式为: yyyy/MM/dd HH:mm:ss.S */
        yyyy__MM__dd_HH_mm_ss_S("yyyy/MM/dd HH:mm:ss.S"),

        /** 格式为: yyyy/MM/dd HH:mm:ss.SSS */
        yyyy__MM__dd_HH_mm_ss_SSS("yyyy/MM/dd HH:mm:ss.SSS"),

        /** 格式为: yyyy/MM/dd HHmm */
        yyyy__MM__dd_HHmm("yyyy/MM/dd HHmm"),

        /** 格式为: yyyy/MM/dd HHmmss */
        yyyy__MM__dd_HHmmss("yyyy/MM/dd HHmmss"),

        /** 格式为: yyyy/MM/dd HHmmss.S */
        yyyy__MM__dd_HHmmssS("yyyy/MM/dd HHmmss.S"),

        /** 格式为: yyyy/MM/dd HHmmssSSS */
        yyyy__MM__dd_HHmmssSSS("yyyy/MM/dd HHmmssSSS");

        private String format;

        FormatType(String format) {
            this.format = format;
        }

        public String getFormat() {
            return this.format;
        }

    }

    /**
     * 取日期中的部分数值:年(yyyy)、月(MM)、日(dd)、时(HH)、分(mm)、秒(ss)、毫秒(SSS)
     *
     * @param formart
     * @param reply
     * @param o
     * @return
     */
    public static int getPartOfDate(String formart, String reply, Object o) {
        Calendar c = Calendar.getInstance();

        if (o instanceof String) {
            SimpleDateFormat sdf = new SimpleDateFormat(formart);
            try {
                c.setTime(sdf.parse((String) o));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (o instanceof Date) {
            c.setTime((Date) o);
        }

        if (reply.equals("yyyy"))
            return c.get(Calendar.YEAR);
        else if (reply.equals("MM"))
            return c.get(Calendar.MONTH) + 1;
        else if (reply.equals("dd"))
            return c.get(Calendar.DAY_OF_MONTH);
        else if (reply.equals("HH"))
            return c.get(Calendar.HOUR_OF_DAY);
        else if (reply.equals("mm"))
            return c.get(Calendar.MINUTE);
        else if (reply.equals("ss"))
            return c.get(Calendar.SECOND);
        else if (reply.equals("SSS"))
            return c.get(Calendar.MILLISECOND);

        return -1;
    }

    public static String getTimeStr(String datetime, String formart) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
        String s = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(formart);
        try {
            Date d = sdf.parse(datetime);
            s = sdf1.format(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    public static int getTimeWeek(Date datetime, String formart) {
        Calendar c = Calendar.getInstance();
        c.setTime(datetime);
        return c.get(Calendar.DAY_OF_WEEK);
    }


    public static String getTimeStr(Date datetime, String formart) {
        SimpleDateFormat sdf = new SimpleDateFormat(formart);
        return sdf.format(datetime);
    }

    public static String transmitOhterFormat(String dtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm");

        try {
            Date dt = sdf.parse(dtime);

            return sdf1.format(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date parseIntensityTime(String timestr) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        try {

            Date sdt = sdf1.parse(timestr);
            return sdt;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getMinuteOfDay(Date datetime) {
        Calendar c = Calendar.getInstance();
        c.setTime(datetime);
        return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
    }

    public static void main(String[] args) {
        //Date aa= getIntervalDate(new Date(), DateType.MINUTE, -1);
        //DateUtil.getIntervalDate()
        System.out.println(DateUtil.format(DateUtil.getLastMonth(new Date()), FormatType.yyyy_MM_dd_HH_mm_ss));

    }
}
