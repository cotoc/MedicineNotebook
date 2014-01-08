package jp.iftc.medicinenotebook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateTimeUtil {

    public static final Integer DATETIME_FULL = 0;
    public static final Integer DATE_FULL = 1;
    public static final Integer TIME_FULL = 2;
    public static final Integer DATE_SHORT = 3;

	private static final String datePattern[]
	        = { "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd", "HH:mm:ss", "M月d日"};

	public MyDateTimeUtil() {
		// TODO Auto-generated constructor stub
	}

    //Date日付型をString文字列型へ変換
    public String _date2string(Date date,int datatype) {
    	if( date == null) return null;

        SimpleDateFormat sdf = new SimpleDateFormat(datePattern[datatype]);
        return sdf.format(date);
    }

    //String文字列型をDate日付型へ変換
    public Date _string2date(String value,int datatype) {
    	if(value == null) return null;

        SimpleDateFormat sdf = new SimpleDateFormat(datePattern[datatype]);
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    // 現在日時のString型を返す
	public String getNow(int datatype){
		Date date = Calendar.getInstance().getTime();
		String strDate = _date2string(date,datatype);
		return strDate;
	}

    // 指定ミリ秒のString型を返す
	public String LongToDateString(Long millseconds,int datatype){
	    Date date = new Date(millseconds);
        return _date2string(date, datatype);
	}

	// 指定時刻(hh:mm)のミリ秒を返す
	public long getDiffTimeMillisecound(int _hour, int _minute, int _sec ){
        Calendar mCal_def;
        Calendar mCal;
        mCal_def = Calendar.getInstance();
        mCal = Calendar.getInstance();

        mCal_def.set( 1970, 1, 1, 0, 0, 0);
        mCal.set( 1970, 1, 1, _hour, _minute, _sec );

        return mCal.getTimeInMillis() - mCal_def.getTimeInMillis();
    }

    // 指定日時(yyyy/mm/dd)のミリ秒を返す
	public long getDayMillisecound(int _year, int _month, int _day ){
        Calendar mCal;
        mCal = Calendar.getInstance();

        mCal.set(_year, _month, _day, 0, 0, 0);

        return mCal.getTimeInMillis();
    }




}
