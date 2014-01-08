package jp.iftc.medicinenotebook.alarm;

import java.util.Calendar;

import jp.iftc.medicinenotebook.MyDateTimeUtil;
import jp.iftc.medicinenotebook.preference.MyPreferenceUtil;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyAlarmUtil {
    @SuppressWarnings("unused")
    private static final String TAG = MyAlarmUtil.class.getSimpleName();
    private final MyAlarmUtil self = this;
    
	MyDateTimeUtil mMyDateTimeUtil;
	MyPreferenceUtil mMyPreferenceUtil;

	public MyAlarmUtil() {
		// TODO Auto-generated constructor stub
	}

    //AlarmManagerへ登録
    public void SetAlarmManager(Context context,boolean setFlag) {

        Intent intent = new Intent(context, jp.iftc.medicinenotebook.alarm.AlarmReceiver.class);
        Calendar calendar = Calendar.getInstance();

        mMyDateTimeUtil = new MyDateTimeUtil();
        mMyPreferenceUtil = new MyPreferenceUtil(context);

        // 以下は正規
        intent.putExtra("NextFlag", mMyPreferenceUtil.getNextAlarmFlag());
        calendar.setTimeInMillis(mMyPreferenceUtil.getNextAlarmMilliSecond());

        Log.d(TAG, "SetAlarmManager: mNextFlag:" + Integer.toString(mMyPreferenceUtil.getNextAlarmFlag()));
        // 以下はテスト用
//        intent.putExtra("NextFlag", MyPreferenceUtil.TIMEZONE_MORNING_AFTER);
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND, 10);

        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (setFlag) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            Toast.makeText(context,
                    "Start Alarm!"+ mMyDateTimeUtil.LongToDateString(calendar.getTimeInMillis(),mMyDateTimeUtil.DATETIME_FULL)
                    ,Toast.LENGTH_LONG).show();
        } else {
            alarmManager.cancel(sender);
            Toast.makeText(context,"Cansel Alarm!",Toast.LENGTH_LONG).show();
        }



    }

}
