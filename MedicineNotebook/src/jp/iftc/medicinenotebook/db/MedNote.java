/*
 * Copyright c 2011 Infotec Inc. All Rights Reserved.
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

package jp.iftc.medicinenotebook.db;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;

/**
 * @author 
 *
 *         1レコード分のデータを保持するクラス
 */
public class MedNote implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static final String TAG = MedNote.class.getSimpleName();
    private final MedNote self = this;

    // TableName
    public static final String TABLE_NAME = "med_note";

    // カラム名定義
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MED_NOTE_DATE = "med_note_date";
    public static final String COLUMN_MED_NOTE_HOSPNAME = "med_note_hospname";
    public static final String COLUMN_MED_NOTE_PHARNAME = "med_note_pharname";


    // カラム名変数の初期値
    private Long id = null;
    private Long mednotedate = null;
    private String mednotehospname = null;
    private String mednotepharname = null;


    /**
     * @return id
     */
    public Long getId() {
       return id;
    }

    /**
     * @return mednotedate
     */
    public Long getMedNoteDate() {
       return mednotedate;
    }

    /**
     * @param context
     * @return mednotedate 日付フォーマット文字列
     */
    public String getMedNoteDateFormat(Context context) {
    	int COMBINE_FLAGS = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_ALL;
    	String date = DateUtils.formatDateTime(context, mednotedate, COMBINE_FLAGS);

        return date;
    }

    /**
     * @return mednotehospname
     */
    public String getMedNoteHospname() {
       return mednotehospname;
    }

    /**
     * @return mednotepharname
     */
    public String getMedNotePharname() {
       return mednotepharname;
    }



    /**
     * @param id
     *            セットする id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param mednotedate
     *            セットする mednotedate
     */
    public void setMedNoteDate(Long mednotedate) {
        this.mednotedate = mednotedate;
    }

    /**
     * @param mednotehospname
     *            セットする mednotehospname
     */
    public void setMedNoteHospname(String mednotehospname) {
        this.mednotehospname = mednotehospname;
    }

    /**
     * @param mednotepharname
     *            セットする mednotepharname
     */
    public void setMedNotePharname(String mednotepharname) {
        this.mednotepharname = mednotepharname;
    }



}
