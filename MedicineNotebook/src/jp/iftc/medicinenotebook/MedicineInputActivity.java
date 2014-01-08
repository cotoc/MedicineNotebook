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
package jp.iftc.medicinenotebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.iftc.medicinenotebook.db.MedDat;
import jp.iftc.medicinenotebook.db.MedDatDAO;
import jp.iftc.medicinenotebook.db.MedNote;
import jp.iftc.medicinenotebook.db.MedNoteDAO;
import jp.iftc.medicinenotebook.db.MedNoteDetail;
import jp.iftc.medicinenotebook.db.MedNoteDetailDAO;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * @author 0A7044
 *
 */
public class MedicineInputActivity extends Activity {
    @SuppressWarnings("unused")
    private static final String TAG = MedicineInputActivity.class.getSimpleName();
    private final MedicineInputActivity self = this;

    private static final int REQUEST_CODE = 0;

    private TextView mTxtvw_Date;
    private TextView mTxtvw_HospName;
    private TextView mTxtvw_PharName;
    private TextView mTxtvw_RowNo;
    private TextView mTxtvw_Unit;

    private ViewSwitcher mSwitcher1;
    private Spinner spnunit;
    private Spinner spnintake;
    private Spinner spntiming;


    private EditText mEditDose;
    private EditText mEditTotal;
    private EditText mEditComment;

    private Button btn_voice_recognition;
    private Button btn2;
    private CheckBox chkBox_etc;
    private CheckBox chkBox_mon;
    private CheckBox chkBox_aft;
    private CheckBox chkBox_eve;
    private CheckBox chkBox_ngt;

    private ArrayAdapter<String> mAdapterMedicine;
    private AutoCompleteTextView edit_medicine;

    private Integer mIntentData; //起動モード
    private Long mMedNoteID; //追加時の親ID
    private Long mMedNoteDetailID; //メンテ対象薬ID
    private Long mMedNoteDetailLinkID; //編集モード時のリンクID


    private String mHospName; //受け渡し病院名
    private String mPharName; //受け渡し薬局名
    private Long mDate; //受け渡し処方日

    private MyDateTimeUtil mMyDateTimeUtil;
    private Calendar mCal;

    //データアクセスクラスの宣言
    MedDatDAO mMedDatDao = new MedDatDAO(self);
    MedNoteDAO mMedNoteDao = new MedNoteDAO(self);
    MedNoteDetailDAO mMedNoteDetailDao = new MedNoteDetailDAO(self);
    ArrayList<String> mClmnList = new ArrayList<String>();


    CharSequence[] voice_recognition_resultList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ソフトキーボード立ち上げない
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.medinput_layout);

        // インテントに保存されたデータを取得
        Intent intent = getIntent();
        mIntentData = intent.getIntExtra("Mode", 0);
        mMedNoteDetailID = intent.getLongExtra("MedNoteDetailId", 0);
        mMedNoteID = intent.getLongExtra("MedNoteId", 0);
        mHospName = intent.getStringExtra("HospName");
        mPharName = intent.getStringExtra("PharName");
        mDate = intent.getLongExtra("Date", System.currentTimeMillis());


        mMyDateTimeUtil = new MyDateTimeUtil();
        mCal = Calendar.getInstance();
        mCal.setTimeInMillis(mDate);

        // 薬名オートコンプリートの設定
        mClmnList = mMedDatDao.getColumnList(MedDat.COLUMN_MED_DAT_NAME);
        String[] arrayMedName = (String[])mClmnList.toArray(new String[0]);
        mAdapterMedicine = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayMedName);
        edit_medicine = (AutoCompleteTextView)findViewById(R.id.edit_medicine);
        edit_medicine.setAdapter(mAdapterMedicine);

        //オートコンプリート検索開始文字数を設定
        edit_medicine.setThreshold(1);

        mTxtvw_Date = (TextView) findViewById(R.id.txtvw_date);
        mTxtvw_HospName = (TextView) findViewById(R.id.txtvw_hospname);
        mTxtvw_PharName = (TextView) findViewById(R.id.txtvw_pharname);
        mTxtvw_RowNo = (TextView) findViewById(R.id.txtvw_rowno);
        mTxtvw_Unit = (TextView) findViewById(R.id.txtvw_unit);


        mEditDose = (EditText) findViewById(R.id.edit_dose);
        mEditTotal = (EditText) findViewById(R.id.edit_total);
        mEditComment = (EditText) findViewById(R.id.edit_comment);

        chkBox_etc = (CheckBox) findViewById(R.id.medinp_chkBox_etc);
        chkBox_mon = (CheckBox) findViewById(R.id.medinp_chkBox_mon);
        chkBox_aft = (CheckBox) findViewById(R.id.medinp_chkBox_aft);
        chkBox_eve = (CheckBox) findViewById(R.id.medinp_chkBox_eve);
        chkBox_ngt = (CheckBox) findViewById(R.id.medinp_chkBox_ngt);


        mSwitcher1 = (ViewSwitcher) findViewById(R.id.switcher1);
        spnunit = (Spinner) findViewById(R.id.medinput_Spn_unit);
        spnintake = (Spinner) findViewById(R.id.medinput_Spn_intake);
        spntiming = (Spinner) findViewById(R.id.medinput_Spn_timing);

        btn2 = (Button) self.findViewById(R.id.medinp_btn1);
        btn_voice_recognition = (Button) self.findViewById(R.id.btn_voice_recognition);

        btn2.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                MedNote mednote = new MedNote();
                MedNote mednoteresult = new MedNote();

                MedNoteDetail mednotedetail = new MedNoteDetail();

                if(edit_medicine.getText().toString().equals("") ||
                   mEditDose.getText().toString().equals("") ||
                   mEditTotal.getText().toString().equals("")) {

                   Toast.makeText(self,"入力されていない項目があります。", Toast.LENGTH_LONG).show();

                } else {
                    Integer chk_etc = 0;
                    Integer chk_timezone = 0;

                    if (chkBox_etc.isChecked() == true ){
                        chk_etc = 1;
                    }

                    if (chkBox_mon.isChecked() == true ){
                        chk_timezone += 1;
                    }

                    if (chkBox_aft.isChecked() == true ){
                        chk_timezone += 2;
                    }

                    if (chkBox_eve.isChecked() == true ){
                        chk_timezone += 4;
                    }

                    if (chkBox_ngt.isChecked() == true ){
                        chk_timezone += 8;
                    }

                    mednotedetail.setMedDetailShape(edit_medicine.getText().toString());
                    mednotedetail.setMedDetailUnit(spnunit.getSelectedItem().toString());
                    mednotedetail.setMedDetailIntake(spnintake.getSelectedItem().toString());
                    mednotedetail.setMedDetailDose(Integer.valueOf(mEditDose.getText().toString()));
                    mednotedetail.setMedDetailTotal(Integer.valueOf(mEditTotal.getText().toString()));
                    mednotedetail.setMedDetailEtc(chk_etc);
                    mednotedetail.setMedDetailTimezone(chk_timezone);
                    mednotedetail.setMedDetailTiming(spntiming.getSelectedItemPosition());
                    if (chk_etc.equals(true)){
                        mednotedetail.setMedDetailAlarm(0);
                    } else {
                        mednotedetail.setMedDetailAlarm(1);
                    }
                    mednotedetail.setMedDetailComment(mEditComment.getText().toString());

                    mCal.add(Calendar.DAY_OF_MONTH , ( Integer.valueOf(mEditTotal.getText().toString()) - 1 ));
                    mednotedetail.setMedDetailEndday( mCal.getTimeInMillis());

                    switch (mIntentData){
                        case 0:
                            mednote.setMedNoteHospname(mHospName);
                            mednote.setMedNotePharname(mPharName);
                            mednote.setMedNoteDate(mDate);

                            mednoteresult = mMedNoteDao.save(mednote);

                            mednotedetail.setMedDetailLinkno(mednoteresult.getId());

                            break;
                        case 1:
                            mednotedetail.setId(mMedNoteDetailID);
                            mednotedetail.setMedDetailLinkno(mMedNoteDetailLinkID);
                            break;
                        case 2:
                            mednotedetail.setMedDetailLinkno(mMedNoteID);
                            break;
                        default:
                            break;

                    }

                    mMedNoteDetailDao.save(mednotedetail);

                	Intent intent = new Intent(getApplicationContext(), HistoryListActivity.class);
                	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });

        btn_voice_recognition.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                try {

                	// インテント作成
                    Intent intent = new Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // ACTION_WEB_SEARCH
                    intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(
                            RecognizerIntent.EXTRA_PROMPT,
                            "VoiceRecognitionTest"); // お好きな文字に変更できます

                    // インテント発行
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    // このインテントに応答できるアクティビティがインストールされていない場合
                    Toast.makeText(MedicineInputActivity.this,
                        "ActivityNotFoundException", Toast.LENGTH_LONG).show();
                }

            }
        });

        // チェックボックスがクリックされた時に呼び出されるコールバックリスナーを登録します
        chkBox_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            // チェックボックスがクリックされた時に呼び出されます
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                // チェックボックスのチェック状態を取得します
                boolean checked = checkBox.isChecked();
                mSwitcher1.showPrevious();
            }
        });

        spnunit.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterview, View view, int i, long l) {
                Spinner spinner = (Spinner) adapterview;
                Integer pos = spinner.getSelectedItemPosition();
                String[] adp = getResources().getStringArray(R.array.MedItmUnit);
                List<String> list =Arrays.asList(adp);

                mTxtvw_Unit.setText(list.get(pos).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterview) {
                // TODO 自動生成されたメソッド・スタブ

            }
        });

        // 起動モードによる処理

        MedNoteDetail mednotedetail = new MedNoteDetail();
        MedNote mednote = new MedNote();

        switch (mIntentData){
            case 0: // 新規モード
                mTxtvw_HospName.setText(mHospName);
                mTxtvw_PharName.setText(mPharName);
                mTxtvw_Date.setText( mMyDateTimeUtil.LongToDateString(mDate,MyDateTimeUtil.DATE_FULL));
                break;
            case 1: // 編集モード
                mednotedetail = mMedNoteDetailDao.load(mMedNoteDetailID);
                mednote = mMedNoteDao.load(mednotedetail.getMedDetailLinkno());

                mMedNoteDetailLinkID = mednotedetail.getMedDetailLinkno();

                if(mednotedetail.equals(null)){
                    Toast.makeText(this, "読み込み失敗", Toast.LENGTH_LONG).show();
                    self.finish();
                } else {

                    mTxtvw_RowNo.setText("編集 No." + ( 1 + mMedNoteDetailDao.getChildNo(mednotedetail.getMedDetailLinkno(), mMedNoteDetailID) ) );

                    mTxtvw_HospName.setText(mednote.getMedNoteHospname());
                    mTxtvw_PharName.setText(mednote.getMedNotePharname());
                    mTxtvw_Date.setText( mMyDateTimeUtil.LongToDateString(mednote.getMedNoteDate(),MyDateTimeUtil.DATE_FULL));

                    edit_medicine.setText(mednotedetail.getMedDetailShape());

                    String[] adp = getResources().getStringArray(R.array.SpnItm_med1);
                    List<String> list =Arrays.asList(adp);
                    spnunit.setSelection(list.indexOf(mednotedetail.getMedDetailUnit()));

                    adp = getResources().getStringArray(R.array.SpnItm_med2);
                    list =Arrays.asList(adp);
                    spnintake.setSelection(list.indexOf(mednotedetail.getMedDetailIntake()));

                    mEditDose.setText(String.valueOf(mednotedetail.getMedDetailDose()));
                    mEditTotal.setText(String.valueOf(mednotedetail.getMedDetailTotal()));

                    adp = getResources().getStringArray(R.array.SpnItm_med3);
                    list =Arrays.asList(adp);
                    spntiming.setSelection(list.indexOf(mednotedetail.getMedDetailTimingString(self)));

                    if (mednotedetail.getMedDetailEtc().equals(1)){
                        chkBox_etc.setChecked(true);
                        mSwitcher1.showPrevious();
                    }

                    mEditComment.setText(mednotedetail.getMedDetailComment());

                    if ((mednotedetail.getMedDetailTimezone() & 1 ) == 1 ){
                        chkBox_mon.setChecked(true);
                    }

                    if ((mednotedetail.getMedDetailTimezone() & 2 ) == 2 ){
                        chkBox_aft.setChecked(true);
                    }

                    if ((mednotedetail.getMedDetailTimezone() & 4 ) == 4 ){
                        chkBox_eve.setChecked(true);
                    }

                    if ((mednotedetail.getMedDetailTimezone() & 8 ) == 8 ){
                        chkBox_ngt.setChecked(true);
                    }

                    mTxtvw_Unit.setText(mednotedetail.getMedDetailDoseUnit(self));

                }

                break;
            case 2: // お薬追加モード
                mTxtvw_RowNo.setText("追加");
                mednote = mMedNoteDao.load(mMedNoteID);

                mTxtvw_HospName.setText(mednote.getMedNoteHospname());
                mTxtvw_PharName.setText(mednote.getMedNotePharname());
                mTxtvw_Date.setText( mMyDateTimeUtil.LongToDateString(mednote.getMedNoteDate(),MyDateTimeUtil.DATE_FULL));

                break;

            default:
                break;

        }

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
//         Log.w(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Log.w(TAG, "onPause");
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

    // アクティビティ終了時に呼び出される
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 自分が投げたインテントであれば応答する
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String resultsString = "";

            // 結果文字列リスト
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            voice_recognition_resultList = (CharSequence[]) results.toArray(new CharSequence[0]);

            new AlertDialog.Builder(this)
            .setTitle("入力候補")
            .setItems(voice_recognition_resultList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	edit_medicine.setText(voice_recognition_resultList[which]);
                }
            })
            .setCancelable(true)
            .show();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
