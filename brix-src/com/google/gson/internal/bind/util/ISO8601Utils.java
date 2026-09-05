package com.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import kotlin.text.Typography;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.lang3.time.TimeZones;

/* JADX INFO: loaded from: classes.dex */
public class ISO8601Utils {
    private static final String UTC_ID = "UTC";
    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone(UTC_ID);

    public static String format(Date date) {
        return format(date, false, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean millis) {
        return format(date, millis, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean millis, TimeZone tz) {
        Calendar calendar = new GregorianCalendar(tz, Locale.US);
        calendar.setTime(date);
        int capacity = "yyyy-MM-ddThh:mm:ss".length();
        StringBuilder formatted = new StringBuilder(capacity + (millis ? ".sss".length() : 0) + (tz.getRawOffset() == 0 ? "Z" : "+hh:mm").length());
        padInt(formatted, calendar.get(1), "yyyy".length());
        formatted.append('-');
        padInt(formatted, calendar.get(2) + 1, "MM".length());
        formatted.append('-');
        padInt(formatted, calendar.get(5), "dd".length());
        formatted.append('T');
        padInt(formatted, calendar.get(11), "hh".length());
        formatted.append(':');
        padInt(formatted, calendar.get(12), "mm".length());
        formatted.append(':');
        padInt(formatted, calendar.get(13), "ss".length());
        if (millis) {
            formatted.append('.');
            padInt(formatted, calendar.get(14), "sss".length());
        }
        int offset = tz.getOffset(calendar.getTimeInMillis());
        if (offset != 0) {
            int hours = Math.abs((offset / 60000) / 60);
            int minutes = Math.abs((offset / 60000) % 60);
            formatted.append(offset >= 0 ? '+' : '-');
            padInt(formatted, hours, "hh".length());
            formatted.append(':');
            padInt(formatted, minutes, "mm".length());
        } else {
            formatted.append('Z');
        }
        return formatted.toString();
    }

    /* JADX WARN: Code duplicated, block: B:112:0x0220  */
    /* JADX WARN: Code duplicated, block: B:113:0x0222  */
    /* JADX WARN: Code duplicated, block: B:116:0x023f  */
    /* JADX WARN: Code duplicated, block: B:118:0x0245  */
    public static Date parse(String date, ParsePosition pos) throws ParseException {
        Exception fail;
        String input;
        String msg;
        TimeZone timezone;
        int offset;
        char c;
        try {
            int offset2 = pos.getIndex();
            int offset3 = offset2 + 4;
            int year = parseInt(date, offset2, offset3);
            if (checkOffset(date, offset3, '-')) {
                offset3++;
            }
            int offset4 = offset3 + 2;
            int month = parseInt(date, offset3, offset4);
            if (checkOffset(date, offset4, '-')) {
                offset4++;
            }
            int offset5 = offset4 + 2;
            int day = parseInt(date, offset4, offset5);
            int hour = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;
            boolean hasT = checkOffset(date, offset5, 'T');
            if (!hasT) {
                try {
                    if (date.length() <= offset5) {
                        Calendar calendar = new GregorianCalendar(year, month - 1, day);
                        calendar.setLenient(false);
                        pos.setIndex(offset5);
                        return calendar.getTime();
                    }
                } catch (NumberFormatException e) {
                    e = e;
                    fail = e;
                    if (date == null) {
                        input = null;
                    } else {
                        input = Typography.quote + date + Typography.quote;
                    }
                    msg = fail.getMessage();
                    if (msg != null) {
                        msg = "(" + fail.getClass().getName() + ")";
                    } else {
                        msg = "(" + fail.getClass().getName() + ")";
                    }
                    ParseException ex = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                    ex.initCause(fail);
                    throw ex;
                } catch (IllegalArgumentException e2) {
                    e = e2;
                } catch (IndexOutOfBoundsException e3) {
                    e = e3;
                    fail = e;
                    if (date == null) {
                        input = null;
                    } else {
                        input = Typography.quote + date + Typography.quote;
                    }
                    msg = fail.getMessage();
                    if (msg != null) {
                        msg = "(" + fail.getClass().getName() + ")";
                    } else {
                        msg = "(" + fail.getClass().getName() + ")";
                    }
                    ParseException ex2 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                    ex2.initCause(fail);
                    throw ex2;
                }
            }
            if (hasT) {
                int offset6 = offset5 + 1;
                int offset7 = offset6 + 2;
                hour = parseInt(date, offset6, offset7);
                if (checkOffset(date, offset7, ':')) {
                    offset7++;
                }
                int offset8 = offset7 + 2;
                minutes = parseInt(date, offset7, offset8);
                if (!checkOffset(date, offset8, ':')) {
                    offset5 = offset8;
                } else {
                    offset5 = offset8 + 1;
                }
                if (date.length() > offset5 && (c = date.charAt(offset5)) != 'Z' && c != '+' && c != '-') {
                    int offset9 = offset5 + 2;
                    int seconds2 = parseInt(date, offset5, offset9);
                    seconds = (seconds2 <= 59 || seconds2 >= 63) ? seconds2 : 59;
                    if (!checkOffset(date, offset9, '.')) {
                        offset5 = offset9;
                    } else {
                        int offset10 = offset9 + 1;
                        offset5 = indexOfNonDigit(date, offset10 + 1);
                        int parseEndOffset = Math.min(offset5, offset10 + 3);
                        int fraction = parseInt(date, offset10, parseEndOffset);
                        switch (parseEndOffset - offset10) {
                            case 1:
                                milliseconds = fraction * 100;
                                break;
                            case 2:
                                milliseconds = fraction * 10;
                                break;
                            default:
                                milliseconds = fraction;
                                break;
                        }
                    }
                }
            }
            try {
                if (date.length() <= offset5) {
                    throw new IllegalArgumentException("No time zone indicator");
                }
                char timezoneIndicator = date.charAt(offset5);
                if (timezoneIndicator == 'Z') {
                    timezone = TIMEZONE_UTC;
                    offset = offset5 + 1;
                    month = month;
                } else if (timezoneIndicator == '+' || timezoneIndicator == '-') {
                    String timezoneOffset = date.substring(offset5);
                    String timezoneOffset2 = timezoneOffset.length() >= 5 ? timezoneOffset : timezoneOffset + TarConstants.VERSION_POSIX;
                    int offset11 = offset5 + timezoneOffset2.length();
                    if ("+0000".equals(timezoneOffset2) || "+00:00".equals(timezoneOffset2)) {
                        timezone = TIMEZONE_UTC;
                    } else {
                        String timezoneId = TimeZones.GMT_ID + timezoneOffset2;
                        timezone = TimeZone.getTimeZone(timezoneId);
                        String act = timezone.getID();
                        if (act.equals(timezoneId)) {
                            month = month;
                            offset11 = offset11;
                        } else {
                            month = month;
                            offset11 = offset11;
                            String cleaned = act.replace(":", "");
                            if (!cleaned.equals(timezoneId)) {
                                throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID());
                            }
                        }
                    }
                    offset = offset11;
                } else {
                    throw new IndexOutOfBoundsException("Invalid time zone indicator '" + timezoneIndicator + "'");
                }
                Calendar calendar2 = new GregorianCalendar(timezone);
                calendar2.setLenient(false);
                calendar2.set(1, year);
                calendar2.set(2, month - 1);
                calendar2.set(5, day);
                calendar2.set(11, hour);
                calendar2.set(12, minutes);
                calendar2.set(13, seconds);
                calendar2.set(14, milliseconds);
                pos.setIndex(offset);
                return calendar2.getTime();
            } catch (IllegalArgumentException e4) {
                e = e4;
            } catch (IndexOutOfBoundsException e5) {
                e = e5;
                fail = e;
                if (date == null) {
                    input = null;
                } else {
                    input = Typography.quote + date + Typography.quote;
                }
                msg = fail.getMessage();
                if (msg != null) {
                    msg = "(" + fail.getClass().getName() + ")";
                } else {
                    msg = "(" + fail.getClass().getName() + ")";
                }
                ParseException ex3 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                ex3.initCause(fail);
                throw ex3;
            } catch (NumberFormatException e6) {
                e = e6;
                fail = e;
                if (date == null) {
                    input = null;
                } else {
                    input = Typography.quote + date + Typography.quote;
                }
                msg = fail.getMessage();
                if (msg != null) {
                    msg = "(" + fail.getClass().getName() + ")";
                } else {
                    msg = "(" + fail.getClass().getName() + ")";
                }
                ParseException ex4 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
                ex4.initCause(fail);
                throw ex4;
            }
        } catch (IllegalArgumentException e7) {
            e = e7;
        } catch (IndexOutOfBoundsException e8) {
            e = e8;
        } catch (NumberFormatException e9) {
            e = e9;
        }
        fail = e;
        if (date == null) {
            input = null;
        } else {
            input = Typography.quote + date + Typography.quote;
        }
        msg = fail.getMessage();
        if (msg != null || msg.isEmpty()) {
            msg = "(" + fail.getClass().getName() + ")";
        }
        ParseException ex5 = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
        ex5.initCause(fail);
        throw ex5;
    }

    private static boolean checkOffset(String value, int offset, char expected) {
        return offset < value.length() && value.charAt(offset) == expected;
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        int digit = beginIndex;
        int result = 0;
        if (digit < endIndex) {
            int i = digit + 1;
            int digit2 = Character.digit(value.charAt(digit), 10);
            if (digit2 < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = -digit2;
            digit = i;
        }
        while (digit < endIndex) {
            int i2 = digit + 1;
            int digit3 = Character.digit(value.charAt(digit), 10);
            if (digit3 < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = (result * 10) - digit3;
            digit = i2;
        }
        return -result;
    }

    private static void padInt(StringBuilder buffer, int value, int length) {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--) {
            buffer.append('0');
        }
        buffer.append(strValue);
    }

    private static int indexOfNonDigit(String string, int offset) {
        for (int i = offset; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c < '0' || c > '9') {
                return i;
            }
        }
        int i2 = string.length();
        return i2;
    }
}
