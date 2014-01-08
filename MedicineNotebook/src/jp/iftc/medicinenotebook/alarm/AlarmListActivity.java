/*
 * Copyright © 2012 Infotec Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jp.iftc.medicinenotebook.alarm;

import java.util.ArrayList;
import java.util.Calendar;

import jp.iftc.medicinenotebook.HistoryExpandableListAdapter;
import jp.iftc.medicinenotebook.MyDateTimeUtil;
import jp.iftc.medicinenotebook.R;
import jp.iftc.medicinenotebook.db.MedDat;
import jp.iftc.medicinenotebook.db.MedNote;
import jp.iftc.medicinenotebook.db.MedNoteDAO;
import jp.iftc.medicinenotebook.db.MedNoteDetail;
import jp.iftc.medicinenotebook.db.MedNoteDetailDAO;
import jp.iftc.medicinenotebook.preference.MyPreferenceUtil;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 0A7044
 * 処方場所登録画面
 * 　病院名、薬局名、処方日を入力する画面
 */
public class AlarmListActivity extends Activity {
    @SuppressWarnings("unused")
    private static final String TAG = AlarmListActivity.class.getSimpleName();
    private final AlarmListActivity self = this;

    private Integer mNextFlag; //起動時の時間帯フラグ
    private int[] mFlag;   //データ取得用時間帯フラグ

    private FrameLayout frameLayout;
    private ListView mListView;

    private Calendar mCal;
    private MyDateTimeUtil mMyDateTimeUtil;
    private MyPreferenceUtil mMyPreferenceUtil;
    private MyAlarmUtil mMyAlarmUtil;

    private MedNoteDetailDAO mDao;

    private TextView mTxtVw_date;
    private TextView mTxtVw_timing;
    private Button mBtn_end;
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alarmmain_layout);

        //データアクセスクラスの宣言
        mDao = new MedNoteDetailDAO(self);

        // インテントに保存されたデータを取得
        Intent intent = getIntent();
        mNextFlag = intent.getIntExtra("NextFlag", 0);

        // IntentExtraがちゃんと取れないときがあるので、↓ にしてみた。
//        mMyPreferenceUtil = new MyPreferenceUtil(getApplicationContext());
//        mNextFlag = mMyPreferenceUtil.getNextAlarmFlag();
//        if(mNextFlag > 0){
//        	mNextFlag = mNextFlag - 1;
//        } else {
//        	mNextFlag = MyPreferenceUtil.TIMEZONE_NIGHT_BETWEEN;
//        }

        frameLayout = (FrameLayout) self.findViewById(R.id.frame_alarmlist);
        mListView = new ListView(self);

        mTxtVw_date = (TextView) self.findViewById(R.id.txtVw_alarm_date);
        mTxtVw_timing = (TextView) self.findViewById(R.id.txtVw_alarm_timing);
        mBtn_end = (Button) self.findViewById(R.id.btn_alarm_end);
        mImageView = (ImageView) self.findViewById(R.id.image_timming);

        mCal = Calendar.getInstance();
        mMyDateTimeUtil = new MyDateTimeUtil();
        mMyPreferenceUtil = new MyPreferenceUtil(self);

        mTxtVw_date.setText( mMyDateTimeUtil.getNow(MyDateTimeUtil.DATE_SHORT) );

        mFlag = mMyPreferenceUtil.getTimingFlag( mNextFlag );


        Log.d(TAG, "onCreate: mNextFlag:" + Integer.toString(mNextFlag));



        Log.d(TAG, "onCreate: --NextFlag:" + Integer.toString(mNextFlag));

        String[] adp = getResources().getStringArray(R.array.SpnItm_med3);

        if (mFlag[MyPreferenceUtil.CLM_TIMING] == MyPreferenceUtil.TIMING_JUST){
            mTxtVw_timing.setText( mMyPreferenceUtil.getTimingString(mFlag[MyPreferenceUtil.CLM_TIMEZONE])
                    + "  " + adp[ mFlag[MyPreferenceUtil.CLM_TIMING] ].toString()
                    + "  " + adp[ mFlag[MyPreferenceUtil.CLM_TIMING] + 1 ].toString());
        } else {
            mTxtVw_timing.setText( mMyPreferenceUtil.getTimingString(mFlag[MyPreferenceUtil.CLM_TIMEZONE])
                    + "  " + adp[ mFlag[MyPreferenceUtil.CLM_TIMING] ].toString() );
        }

		switch (mFlag[MyPreferenceUtil.CLM_TIMEZONE]) {
		case MyPreferenceUtil.BIT_MORNING:
			mImageView.setImageResource(R.drawable.ic_tab_morning);
			break;
		case MyPreferenceUtil.BIT_LUNCH:
			mImageView.setImageResource(R.drawable.ic_tab_daytime);
			break;
		case MyPreferenceUtil.BIT_DINNER:
			mImageView.setImageResource(R.drawable.ic_tab_night);
			break;
		case MyPreferenceUtil.BIT_NIGHT:
			mImageView.setImageResource(R.drawable.ic_tab_sleep);
            mTxtVw_timing.setText( mMyPreferenceUtil.getTimingString(mFlag[MyPreferenceUtil.CLM_TIMEZONE]));

            break;

		default:
			mImageView.setImageResource(R.drawable.ic_tab_morning);
			break;
		}


        mBtn_end.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                boolean alarmMode = false;
                // まずはAdapterを取得
                AlarmListAdapter adapter = (AlarmListAdapter) parent.getAdapter();
                MedNoteDetail detail = (MedNoteDetail) adapter.getItem(position);

                alarmMode = adapter.toggleAlarmMode( position );

                updateAlarmMode(alarmMode, detail);
                adapter.notifyDataSetChanged();

            }

        });

        ArrayList<MedNoteDetail> subList = (ArrayList<MedNoteDetail>) mDao
                .getAlarmList(mMyDateTimeUtil.getDayMillisecound(mCal.get(Calendar.YEAR),mCal.get(Calendar.MONTH),mCal.get(Calendar.DAY_OF_MONTH))
                        , mFlag );
        AlarmListAdapter adapter = new AlarmListAdapter(self, subList);

        mListView.setAdapter(adapter);
        frameLayout.addView(mListView);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Log.w(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//      Log.w(TAG, "onRestart");

    }

    @Override
    protected void onResume() {
        super.onResume();
//      Log.w(TAG, "onResumet");
    }

    @Override
    protected void onPause() {
        super.onPause();
//      Log.w(TAG, "onPause");
        mMyAlarmUtil = new MyAlarmUtil();

        mMyAlarmUtil.SetAlarmManager(self,mMyPreferenceUtil.getAlarmFlag());

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Log.w(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//         Log.w(TAG, "onDestroy");
    }

    private boolean updateAlarmMode(boolean alarmMode, MedNoteDetail medNoteDetail){
        boolean ret = false;
        MedNoteDetail result = null;

        if(medNoteDetail == null) return ret;

        medNoteDetail.setMedDetailAlarmMode(alarmMode);

        MedNoteDetailDAO dao = new MedNoteDetailDAO(getApplicationContext());

        result = dao.save(medNoteDetail);

        if(result != null){
            Toast.makeText(self, "アラーム設定を更新しました。", Toast.LENGTH_LONG).show();
            ret = true;
        } else {
            Toast.makeText(self, "アラーム設定の更新に失敗しました。", Toast.LENGTH_LONG).show();

        }

        return ret;
    }

}
