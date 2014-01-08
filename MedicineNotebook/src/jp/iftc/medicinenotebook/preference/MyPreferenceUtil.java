package jp.iftc.medicinenotebook.preference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.iftc.medicinenotebook.MyDateTimeUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MyPreferenceUtil {

    MyDateTimeUtil mMyDateTimeUtil;

    public static final int TIMEZONE_MORNING_BEFORE = 0;
    public static final int TIMEZONE_MORNING = 1;
    public static final int TIMEZONE_MORNING_AFTER = 2;
    public static final int TIMEZONE_MORNING_BETWEEN = 3;

    public static final int TIMEZONE_LUNCH_BEFORE = 4;
    public static final int TIMEZONE_LUNCH = 5;
    public static final int TIMEZONE_LUNCH_AFTER = 6;
    public static final int TIMEZONE_LUNCH_BETWEEN = 7;

    public static final int TIMEZONE_DINNER_BEFORE = 8;
    public static final int TIMEZONE_DINNER = 9;
    public static final int TIMEZONE_DINNER_AFTER = 10;
    public static final int TIMEZONE_DINNER_BETWEEN = 11;

//    public static final int TIMEZONE_NIGHT_BEFORE = 12;
//    public static final int TIMEZONE_NIGHT = 13;
//    public static final int TIMEZONE_NIGHT_AFTER = 14;
//    public static final int TIMEZONE_NIGHT_BETWEEN = 15;

    public static final int TIMEZONE_NIGHT = 12;

    public static final int BIT_MORNING = 1;
    public static final int BIT_LUNCH = 2;
    public static final int BIT_DINNER = 4;
    public static final int BIT_NIGHT = 8;

    public static final int CLM_TIMEZONE = 0;
    public static final int CLM_TIMING = 1;

    public static final int TIMING_BEFORE = 0;
    public static final int TIMING_AFTER = 1;
    public static final int TIMING_BETWEEN = 2;
    public static final int TIMING_JUST = 3;

    private long mAlarm_Morning;
    private long mAlarm_Lunch;
    private long mAlarm_Dinner;
    private long mAlarm_Night;

    private long mAlarm_Befor;
    private long mAlarm_After;
    private long mAlarm_Between;

    private boolean mAlarm_Flag;
//    private int  mAlarm_Sound;
    private boolean mAlarm_VibratorFlag;
    private String  mAlarm_Sound_Uri;
    private long mNextAlarmMilliSecond;
    private int mNextAlarmFlag;

	public MyPreferenceUtil(Context context) {
	    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	    mMyDateTimeUtil = new MyDateTimeUtil();

        mAlarm_Morning = mMyDateTimeUtil.getDiffTimeMillisecound(sharedPreferences.getInt("alerm_morning_Hour", 6),
                sharedPreferences.getInt("alerm_morning_Minute", 30), 0);

        mAlarm_Lunch = mMyDateTimeUtil.getDiffTimeMillisecound(sharedPreferences.getInt("alerm_lunch_Hour", 12),
                sharedPreferences.getInt("alerm_lunch_Minute", 0), 0);

        mAlarm_Dinner = mMyDateTimeUtil.getDiffTimeMillisecound(sharedPreferences.getInt("alerm_dinner_Hour", 18),
                sharedPreferences.getInt("alerm_dinner_Minute", 30), 0);

        mAlarm_Night = mMyDateTimeUtil.getDiffTimeMillisecound(sharedPreferences.getInt("alerm_night_Hour", 21),
                sharedPreferences.getInt("alerm_night_Minute", 00), 0);

        mAlarm_Befor = mMyDateTimeUtil.getDiffTimeMillisecound( 0,Integer.parseInt(sharedPreferences.getString("alerm_befor", "30")), 0);
        mAlarm_After = mMyDateTimeUtil.getDiffTimeMillisecound( 0,Integer.parseInt(sharedPreferences.getString("alerm_after", "30")), 0);
        mAlarm_Between = mMyDateTimeUtil.getDiffTimeMillisecound( Integer.parseInt(sharedPreferences.getString("alerm_between", "2")), 0, 0);

        mAlarm_Flag = sharedPreferences.getBoolean("alerm_on", false);
        mAlarm_Sound_Uri = sharedPreferences.getString("alarm_sound", null);
        mAlarm_VibratorFlag = sharedPreferences.getBoolean("alerm_vibrator", false);

    }

    public long getAlarmMorning() {
        return mAlarm_Morning;
    }

    public long getAlarmLunch() {
        return mAlarm_Lunch;
    }

    public long getAlarmDinner() {
        return mAlarm_Dinner;
    }

    public long getAlarmNight() {
        return mAlarm_Night;
    }

    public long getAlarmBefor() {
        return mAlarm_Befor;
    }

    public long getAlarmAfter() {
        return mAlarm_After;
    }

    public long getAlarmBetween() {
        return mAlarm_Between;
    }

    public boolean getAlarmFlag() {
    	return mAlarm_Flag;
    }

    public boolean getAlarmVibratorFlag() {
        return mAlarm_VibratorFlag;
    }

//    public int getAlarmSound() {
//        return mAlarm_Sound;
//    }

    public String getAlarmSoundUri() {
		return mAlarm_Sound_Uri;
    }

    public long getNextAlarmMilliSecond() {
        getNextAlarm();
        return mNextAlarmMilliSecond;
    }

    public int getNextAlarmFlag() {
        getNextAlarm();
        return mNextAlarmFlag;
    }

    public void getNextAlarm() {
        List<Long> TimeMap = new ArrayList<Long>();
        Long nowmillisec;

        Calendar mCal;
        mCal = Calendar.getInstance();


        mCal.setTimeInMillis(System.currentTimeMillis());
        nowmillisec = mMyDateTimeUtil.getDiffTimeMillisecound(mCal.get(Calendar.HOUR_OF_DAY) , mCal.get(Calendar.MINUTE), mCal.get(Calendar.SECOND));

        Log.d("Test", "Calendar:" + (mMyDateTimeUtil.LongToDateString(mCal.getTimeInMillis(),MyDateTimeUtil.TIME_FULL) ));
        Log.d("Test", "NowMilli:" + (mMyDateTimeUtil.LongToDateString(nowmillisec,MyDateTimeUtil.TIME_FULL) ));

        TimeMap.add(mMyDateTimeUtil.getDiffTimeMillisecound( 0, 0, 0 ));
        TimeMap.add(mAlarm_Morning - mAlarm_Befor);
        TimeMap.add(mAlarm_Morning);
        TimeMap.add(mAlarm_Morning + mAlarm_After);
        TimeMap.add(mAlarm_Morning + mAlarm_Between);

        TimeMap.add(mAlarm_Lunch - mAlarm_Befor);
        TimeMap.add(mAlarm_Lunch);
        TimeMap.add(mAlarm_Lunch + mAlarm_After);
        TimeMap.add(mAlarm_Lunch + mAlarm_Between);

        TimeMap.add(mAlarm_Dinner - mAlarm_Befor);
        TimeMap.add(mAlarm_Dinner);
        TimeMap.add(mAlarm_Dinner + mAlarm_After);
        TimeMap.add(mAlarm_Dinner + mAlarm_Between);

//        TimeMap.add(mAlarm_Night - mAlarm_Befor);
        TimeMap.add(mAlarm_Night);
//        TimeMap.add(mAlarm_Night + mAlarm_After);
//        TimeMap.add(mAlarm_Night + mAlarm_Between);

        TimeMap.add(mMyDateTimeUtil.getDiffTimeMillisecound( 23, 59, 59 ));

        for ( int i = 1;i < TimeMap.size();i++ ){

            if ( TimeMap.get( i - 1 ).longValue() < nowmillisec &&
                    nowmillisec <= TimeMap.get( i ).longValue() ){

                if ( i ==  TimeMap.size() -1 ){ // TimeMapの最後の場合は翌日判定
                    mNextAlarmFlag = 0 ;
                    mNextAlarmMilliSecond =
                            mMyDateTimeUtil.getDayMillisecound(mCal.get(Calendar.YEAR),mCal.get(Calendar.MONTH),mCal.get(Calendar.DAY_OF_MONTH) + 1 )
                            + TimeMap.get( 1 ).longValue();

                } else {
                    mNextAlarmFlag = i - 1 ;
                    mNextAlarmMilliSecond =
                            mMyDateTimeUtil.getDayMillisecound(mCal.get(Calendar.YEAR),mCal.get(Calendar.MONTH),mCal.get(Calendar.DAY_OF_MONTH))
                            + TimeMap.get( i ).longValue();
                }
            }
        }
    }

    public int[] getTimingFlag(int _timing ) {
        int flag[]={ 0, 0 };

        if ( _timing == TIMEZONE_MORNING_BEFORE ){
            flag[CLM_TIMEZONE] = BIT_MORNING;
            flag[CLM_TIMING] = TIMING_BEFORE;
        }

        if ( _timing == TIMEZONE_MORNING_AFTER ){
            flag[CLM_TIMEZONE] = BIT_MORNING;
            flag[CLM_TIMING] = TIMING_AFTER;
        }

        if ( _timing == TIMEZONE_MORNING_BETWEEN ){
            flag[CLM_TIMEZONE] = BIT_MORNING;
            flag[CLM_TIMING] = TIMING_BETWEEN;
        }

        if ( _timing == TIMEZONE_MORNING ){
            flag[CLM_TIMEZONE] = BIT_MORNING;
            flag[CLM_TIMING] = TIMING_JUST;
        }

        if ( _timing == TIMEZONE_LUNCH_BEFORE ){
            flag[CLM_TIMEZONE] = BIT_LUNCH;
            flag[CLM_TIMING] = TIMING_BEFORE;
        }

        if ( _timing == TIMEZONE_LUNCH_AFTER ){
            flag[CLM_TIMEZONE] = BIT_LUNCH;
            flag[CLM_TIMING] = TIMING_AFTER;
        }

        if ( _timing == TIMEZONE_LUNCH_BETWEEN ){
            flag[CLM_TIMEZONE] = BIT_LUNCH;
            flag[CLM_TIMING] = TIMING_BETWEEN;
        }

        if ( _timing == TIMEZONE_LUNCH ){
            flag[CLM_TIMEZONE] = BIT_LUNCH;
            flag[CLM_TIMING] = TIMING_JUST;
        }

        if ( _timing == TIMEZONE_DINNER_BEFORE ){
            flag[CLM_TIMEZONE] = BIT_DINNER;
            flag[CLM_TIMING] = TIMING_BEFORE;
        }

        if ( _timing == TIMEZONE_DINNER_AFTER ){
            flag[CLM_TIMEZONE] = BIT_DINNER;
            flag[CLM_TIMING] = TIMING_AFTER;
        }

        if ( _timing == TIMEZONE_DINNER_BETWEEN ){
            flag[CLM_TIMEZONE] = BIT_DINNER;
            flag[CLM_TIMING] = TIMING_BETWEEN;
        }

        if ( _timing == TIMEZONE_DINNER ){
            flag[CLM_TIMEZONE] = BIT_DINNER;
            flag[CLM_TIMING] = TIMING_JUST;
        }

//        if ( _timing == TIMEZONE_NIGHT_BEFORE ){
//            flag[CLM_TIMEZONE] = BIT_NIGHT;
//            flag[CLM_TIMING] = TIMING_BEFORE;
//        }

//        if ( _timing == TIMEZONE_NIGHT_AFTER ){
//            flag[CLM_TIMEZONE] = BIT_NIGHT;
//            flag[CLM_TIMING] = TIMING_AFTER;
//        }

//        if ( _timing == TIMEZONE_NIGHT_BETWEEN ){
//            flag[CLM_TIMEZONE] = BIT_NIGHT;
//            flag[CLM_TIMING] = TIMING_BETWEEN;
//        }

        if ( _timing == TIMEZONE_NIGHT ){
            flag[CLM_TIMEZONE] = BIT_NIGHT;
            flag[CLM_TIMING] = TIMING_JUST;
        }

        return flag;

    }

    public String getTimingString(int _timing ){
        String timingstring = "";

        switch(_timing){
            case BIT_MORNING:
                timingstring = "朝";
                break;
            case BIT_LUNCH:
                timingstring = "昼";
                break;
            case BIT_DINNER:
                timingstring = "夜";
                break;
            case BIT_NIGHT:
                timingstring = "就寝前";
                break;
        }
       return timingstring;
    }

}
