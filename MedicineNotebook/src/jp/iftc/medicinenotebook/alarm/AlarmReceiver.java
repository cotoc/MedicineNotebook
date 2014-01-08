package jp.iftc.medicinenotebook.alarm;

import java.util.ArrayList;
import java.util.Calendar;

import jp.iftc.medicinenotebook.MyDateTimeUtil;
import jp.iftc.medicinenotebook.R;
import jp.iftc.medicinenotebook.db.MedNoteDetail;
import jp.iftc.medicinenotebook.db.MedNoteDetailDAO;
import jp.iftc.medicinenotebook.preference.MyPreferenceUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int nextflag;
        int[] mFlag;   //データ取得用時間帯フラグ

        Calendar mCal;
        MyDateTimeUtil mMyDateTimeUtil;
        MyPreferenceUtil mMyPreferenceUtil;
        MyAlarmUtil mMyAlarmUtil;
        MedNoteDetailDAO mDao;

        mCal = Calendar.getInstance();
        mMyDateTimeUtil = new MyDateTimeUtil();

        mDao = new MedNoteDetailDAO(context);
        mMyDateTimeUtil = new MyDateTimeUtil();
        mMyPreferenceUtil = new MyPreferenceUtil(context);
        mMyAlarmUtil = new MyAlarmUtil();

        nextflag = intent.getIntExtra("NextFlag", 0);
        mFlag = mMyPreferenceUtil.getTimingFlag( nextflag );
        Log.d("AlarmReceiver", "Alarm Received! nextflag: " + Integer.toString(nextflag));

        ArrayList<MedNoteDetail> subList = (ArrayList<MedNoteDetail>) mDao
                .getAlarmList(mMyDateTimeUtil.getDayMillisecound(mCal.get(Calendar.YEAR),mCal.get(Calendar.MONTH),mCal.get(Calendar.DAY_OF_MONTH))
                        , mFlag );

        if (subList.size() != 0){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(
            		R.drawable.ic_stat_notify_mednote,
            		"お薬の時間です。",
            		System.currentTimeMillis());

            Intent intent_alarm = new Intent(context, AlarmListActivity.class);
            intent_alarm.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_alarm.putExtra("NextFlag", nextflag);

            //intentの設定
            PendingIntent contentIntent =
          		PendingIntent.getActivity(context, 0, intent_alarm, PendingIntent.FLAG_CANCEL_CURRENT);

            //Preferenceのアラーム設定読み込み
            MyPreferenceUtil prefUtil = new MyPreferenceUtil(context);
            String ringtoneUrl = prefUtil.getAlarmSoundUri();
            if(ringtoneUrl == null || ringtoneUrl == ""){
            	notification.defaults |= Notification.DEFAULT_SOUND;
            } else {
            	Uri ringtoneUri = Uri.parse(ringtoneUrl);
            	notification.sound = ringtoneUri;
            }

            if( prefUtil.getAlarmVibratorFlag() ){
            	notification.defaults |= Notification.DEFAULT_VIBRATE;
            }

            notification.setLatestEventInfo(context, "お薬通知", "お薬の時間です。確認してください。 ", contentIntent);
            notificationManager.notify(R.string.app_name, notification);

        } else {

            // アラームセット時にミリ秒まで意識していないので1秒待つ
            try {
                synchronized (this) {
                    wait(1000);
                }
            } catch (InterruptedException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
            mMyAlarmUtil.SetAlarmManager(context,mMyPreferenceUtil.getAlarmFlag());
        }
    }
}
