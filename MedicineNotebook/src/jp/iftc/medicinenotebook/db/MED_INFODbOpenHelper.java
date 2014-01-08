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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author DBオープン用 ヘルパークラス
 */
public class MED_INFODbOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = MED_INFODbOpenHelper.class.getSimpleName();
    @SuppressWarnings("unused")
    private final MED_INFODbOpenHelper self = this;

    // データベース名の定数
    private static final String DB_NAME = "MED_INFO";

    // バージョン
    private static final int VERSION = 1;

    /**
     * @param context
     */
    public MED_INFODbOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase) データベースを新規に作成した後に呼ばれる。
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 内部にテーブルを作成する。
        db.beginTransaction();

        try {
            // テーブルの生成
            db.execSQL(createMedDatTable().toString());
            db.execSQL(createMedNoteTable().toString());
            db.execSQL(createMedNoteDetailTable().toString());


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            db.endTransaction();
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
        case 1:

        case 2:

        default:
            break;
        }

    }

    private StringBuilder createMedDatTable() {
        StringBuilder createSql = new StringBuilder();
        createSql.append("create table " + MedDat.TABLE_NAME + " (");
        createSql.append(MedDat.COLUMN_ID + " integer primary key autoincrement not null,");
        createSql.append(MedDat.COLUMN_MED_DAT_NAME + " text,");
        createSql.append(MedDat.COLUMN_MED_DAT_EFFICASY + " text,");
        createSql.append(MedDat.COLUMN_MED_DAT_SHAPE + " text,");
        createSql.append(MedDat.COLUMN_MED_DAT_UNIT + " text,");
            createSql.append(MedDat.COLUMN_MED_DAT_INTAKE + " text");
        createSql.append(")");

        Log.v(TAG, "SQL:" + createSql);
        return createSql;

    }

    private StringBuilder createMedNoteTable() {
        StringBuilder createSql = new StringBuilder();
        createSql.append("create table " + MedNote.TABLE_NAME + " (");
        createSql.append(MedNote.COLUMN_ID + " integer primary key autoincrement not null,");
        createSql.append(MedNote.COLUMN_MED_NOTE_DATE + " integer,");
        createSql.append(MedNote.COLUMN_MED_NOTE_HOSPNAME + " text,");
            createSql.append(MedNote.COLUMN_MED_NOTE_PHARNAME + " text");
        createSql.append(")");

        Log.v(TAG, "SQL:" + createSql);
        return createSql;

    }

    private StringBuilder createMedNoteDetailTable() {
        StringBuilder createSql = new StringBuilder();
        createSql.append("create table " + MedNoteDetail.TABLE_NAME + " (");
        createSql.append(MedNoteDetail.COLUMN_ID + " integer primary key autoincrement not null,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_LINKNO + " integer,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_SHAPE + " text,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_UNIT + " text,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_INTAKE + " text,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_DOSE + " integer,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_TOTAL + " integer,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_ETC + " integer,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_TIMEZONE + " integer,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_TIMING + " integer,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_COMMENT + " text,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_ALARM + " integer,");
        createSql.append(MedNoteDetail.COLUMN_MED_DETAIL_ENDDAY + " integer");
        createSql.append(")");

        Log.v(TAG, "SQL:" + createSql);
        return createSql;

    }


}
