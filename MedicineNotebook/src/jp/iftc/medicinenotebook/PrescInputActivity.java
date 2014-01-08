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
import java.util.Calendar;
import java.util.List;

import jp.iftc.medicinenotebook.db.MedNote;
import jp.iftc.medicinenotebook.db.MedNoteDAO;
import jp.iftc.medicinenotebook.db.MedNoteDetail;
import jp.iftc.medicinenotebook.db.MedNoteDetailDAO;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * @author 0A7044
 * 処方場所登録画面
 * 　病院名、薬局名、処方日を入力する画面
 */
public class PrescInputActivity extends Activity {
    @SuppressWarnings("unused")
    private static final String TAG = PrescInputActivity.class.getSimpleName();
    private final PrescInputActivity self = this;

    private Integer mIntentData; //起動モード
    private Long mDataID; //メンテ対象ID

    private ArrayAdapter<String> mAdapter_hospital;
    private ArrayAdapter<String> mAdapter_pharmacy;
    private AutoCompleteTextView mEdit_hospital;
    private AutoCompleteTextView mEdit_pharmacy;
    private DatePicker mDatePick;
    private Calendar mCal;
    private MyDateTimeUtil mMyDateTimeUtil;

    private MedNoteDAO mDaoMedNote;
    private MedNoteDetailDAO mDaoMedNoteDetail;
    private ArrayList<String> mClmnList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescinput_layout);

        //データアクセスクラスの宣言
        mDaoMedNote = new MedNoteDAO(self);
        mDaoMedNoteDetail = new MedNoteDetailDAO(self);
        mClmnList = new ArrayList<String>();

        // インテントに保存されたデータを取得
        Intent intent = getIntent();
        mIntentData = intent.getIntExtra("Mode", 0);
        mDataID = intent.getLongExtra("MedNoteId", 0);

        mCal = Calendar.getInstance();
        mMyDateTimeUtil = new MyDateTimeUtil();

        Button btn1 = (Button) self.findViewById(R.id.presc_btn1);


        mClmnList = mDaoMedNote.getColumnList(MedNote.COLUMN_MED_NOTE_HOSPNAME);
        String[] arrayHosp = (String[])mClmnList.toArray(new String[0]);
        mAdapter_hospital = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayHosp);

        mEdit_hospital = (AutoCompleteTextView)findViewById(R.id.edit_hospital);
        mEdit_hospital.setAdapter(mAdapter_hospital);
        mEdit_hospital.setNextFocusDownId(R.id.edit_pharmacy);

        mClmnList = mDaoMedNote.getColumnList(MedNote.COLUMN_MED_NOTE_PHARNAME);
        String[] arrayPhar = (String[])mClmnList.toArray(new String[0]);
        mAdapter_pharmacy = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayPhar);
        mEdit_pharmacy = (AutoCompleteTextView)findViewById(R.id.edit_pharmacy);
        mEdit_pharmacy.setAdapter(mAdapter_pharmacy);
        mEdit_pharmacy.setNextFocusDownId(R.id.picker_date);

        mDatePick = (DatePicker)findViewById(R.id.picker_date);
        mDatePick.setNextFocusDownId(R.id.presc_btn1);

        if (mIntentData == 1){ // 編集モード時
            btn1.setText("登録");
            MedNote mednote = new MedNote();

            mednote = mDaoMedNote.load(mDataID);

            mEdit_hospital.setText(mednote.getMedNoteHospname());
            mEdit_pharmacy.setText(mednote.getMedNotePharname());
            String Date = mMyDateTimeUtil.LongToDateString(mednote.getMedNoteDate(), MyDateTimeUtil.DATE_FULL);
            String[] DateStr = Date.split("/");

            mDatePick.init(Integer.valueOf(DateStr[0]), Integer.valueOf(DateStr[1]) - 1, Integer.valueOf(DateStr[2]), null);

        }

        btn1.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                MedNote mednote = new MedNote();
                List<MedNoteDetail> mednotedetail = new ArrayList<MedNoteDetail>();
                int totalday = 0;
                Calendar bufCal;

                bufCal = Calendar.getInstance();


                if ( mEdit_hospital.getText().toString().equals("") || mEdit_pharmacy.getText().toString().equals("") ){
                    Toast.makeText(getApplicationContext(), "入力値が全て入力されていません。", Toast.LENGTH_LONG).show();
                } else {

                    mCal.set(mDatePick.getYear(), mDatePick.getMonth(), mDatePick.getDayOfMonth());

                    if (mIntentData == 1){
                        mednote.setId(mDataID);
                        mednote.setMedNoteHospname(mEdit_hospital.getText().toString());
                        mednote.setMedNotePharname(mEdit_pharmacy.getText().toString());
                        mednote.setMedNoteDate(mCal.getTimeInMillis());

                        mDaoMedNote.save(mednote);

                        mednotedetail = mDaoMedNoteDetail.getChildList(mDataID);

                        for(int i=0;i<mednotedetail.size();i++){
                            bufCal = mCal;
                            totalday = mednotedetail.get(i).getMedDetailTotal() - 1;
                            bufCal.add(Calendar.DAY_OF_MONTH, totalday);
                            mednotedetail.get(i).setMedDetailEndday(bufCal.getTimeInMillis());
                        }

                        mDaoMedNoteDetail.saveList(mednotedetail);

                        Intent intent = new Intent(getApplicationContext(), HistoryListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Intent intent = new Intent(self,MedicineInputActivity.class);
                        // 　インテントに値をセット
                        intent.putExtra("HospName", mEdit_hospital.getText().toString());
                        intent.putExtra("PharName", mEdit_pharmacy.getText().toString());
                        intent.putExtra("Date", mCal.getTimeInMillis());
                        startActivity(intent);
                    }
                }
            }
        });

    }


}
