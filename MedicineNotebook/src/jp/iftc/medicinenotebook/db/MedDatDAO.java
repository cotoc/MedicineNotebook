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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * @author データアクセスクラス
 */
public class MedDatDAO {

    private static final String TAG = MedDatDAO.class.getSimpleName();
    @SuppressWarnings("unused")
    private final MedDatDAO self = this;

    private MED_INFODbOpenHelper helper = null;
    private Context mContext;

    /*
     *
     */
    public MedDatDAO(Context context) {
        helper = new MED_INFODbOpenHelper(context);
        mContext = context;
    }

    /*
     * 情報の保存＜IDがNULLならInsert,それ以外ならUpdateで全項目更新＞
     *
     * @param MedDat 保存対象のデータ
     *
     * @return 保存したデータ　Error：null
     */
    public MedDat save(MedDat meddat) {
        SQLiteDatabase db;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        MedDat result = null;
        try {
            ContentValues values = new ContentValues();
                values.put(MedDat.COLUMN_ID, meddat.getId());
                values.put(MedDat.COLUMN_MED_DAT_NAME, meddat.getMedDatName());
                values.put(MedDat.COLUMN_MED_DAT_EFFICASY, meddat.getMedDatEfficasy());
                values.put(MedDat.COLUMN_MED_DAT_SHAPE, meddat.getMedDatShape());
                values.put(MedDat.COLUMN_MED_DAT_UNIT, meddat.getMedDatUnit());
                values.put(MedDat.COLUMN_MED_DAT_INTAKE, meddat.getMedDatIntake());


            Long rowId = meddat.getId();

            int updateCount = 0;

            // IDがnullの場合はinsert
            if (rowId == null) {
                rowId = db.insert(MedDat.TABLE_NAME, null, values);
                if (rowId < 0) {
                    // エラー処理
                    Log.w(TAG, "save Insert Error");
                    throw new SQLException();
                }
                Log.v(TAG, "save Insert Success!");
            } else {
                updateCount = db.update(MedDat.TABLE_NAME, values,
                    MedDat.COLUMN_ID + "=?",
                    new String[] { String.valueOf(rowId) });
                if (updateCount != 1) {
                    // エラー処理
                    Log.w(TAG,
                            "save UPDATE Error : Update ID = "
                                   + String.valueOf(rowId)
                                   + "| Update Count : "
                                   + String.valueOf(updateCount));
                    throw new SQLException();
                }
                Log.v(TAG, "save update Success!");
            }
            result = load(rowId);
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
            result = null;
        } finally {
            db.close();
        }
        return result;

    }

    /**
     * 1レコードの削除
     *
     * @param MedDat
     *            削除対象のオブジェクト
     */
    public void delete(MedDat meddat) {
        SQLiteDatabase db;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return;
        }

        int deleteCount = 0;
        try {
            deleteCount = db.delete(MedDat.TABLE_NAME, MedDat.COLUMN_ID
                    + "=?", new String[] { String.valueOf(meddat.getId()) });
            if (deleteCount != 1) {
                // エラー処理
                Log.w(TAG,
                        "delete Delete Error : Update ID = "
                                + String.valueOf(meddat.getId())
                                + "| Delete Count : "
                                + String.valueOf(deleteCount));
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
            db.close();
        }
    }

    /**
     * 全レコードの削除
     *
     * @param db
     */
    private void deleteMedDatAll(SQLiteDatabase db) {

        int deleteCount = 0;
        try {
            deleteCount = db.delete(MedDat.TABLE_NAME, null, null);
            if (deleteCount < 1) {
                // エラー処理
                Log.w(TAG,
                        "deleteAll Delete Error Delete Count : "
                                + String.valueOf(deleteCount));
            }
        } finally {
//            db.close();
        }
    }

    /**
     * IDで資産情報を読み込む
     *
     * @param rowId
     * @return
     */
    public MedDat load(Long rowId) {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        MedDat meddat = null;
        try {
            cursor = db.query(MedDat.TABLE_NAME, null,
                    MedDat.COLUMN_ID + "=?",
                    new String[] { String.valueOf(rowId) }, null, null, null);
            cursor.moveToFirst();
            meddat = getMedDat(cursor);
        } finally {
            cursor.close();
            db.close();
        }

        return meddat;
    }

    /**
     * 一覧を取得する
     *
     * @return 検索結果
     */
    public List<MedDat> list() {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        List<MedDat> meddatList;
        try {
            cursor = db.query(MedDat.TABLE_NAME, null, null, null,
                    null, null, MedDat.COLUMN_ID);
            meddatList = new ArrayList<MedDat>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    meddatList.add(getMedDat(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
            db.close();
        }
        return meddatList;
    }

    /**
     * カーソルからオブジェクトへの変換
     *
     * @param cursor
     * @return MedDat
     *             カーソルから読み込んだデータをMedDatクラスに編成
     */
    private MedDat getMedDat(Cursor cursor) {
        MedDat meddat = new MedDat();

        meddat.setId(cursor.getLong(cursor.getColumnIndex(MedDat.COLUMN_ID)));
        meddat.setMedDatName(cursor.getString(cursor.getColumnIndex(MedDat.COLUMN_MED_DAT_NAME)));
        meddat.setMedDatEfficasy(cursor.getString(cursor.getColumnIndex(MedDat.COLUMN_MED_DAT_EFFICASY)));
        meddat.setMedDatShape(cursor.getString(cursor.getColumnIndex(MedDat.COLUMN_MED_DAT_SHAPE)));
        meddat.setMedDatUnit(cursor.getString(cursor.getColumnIndex(MedDat.COLUMN_MED_DAT_UNIT)));
        meddat.setMedDatIntake(cursor.getString(cursor.getColumnIndex(MedDat.COLUMN_MED_DAT_INTAKE)));


        return meddat;
    }

    /**
     * リストのデータをすべてDBに登録する＜全件Insert＞
     *
     * @param MedDatList
     *            ： 登録対象のリスト
     * @return recCount : 登録したデータの件数 エラーの場合-1
     */
    public int saveList(List<MedDat> meddatList) {
        SQLiteDatabase db;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            Log.w(TAG, e.toString());
            return -1;
        }

        int recCount;
        db.beginTransaction();
        try {
            // テーブル内のデータを全件削除
            deleteMedDatAll(db);

            ContentValues values = new ContentValues();
            for (recCount = 0; recCount < meddatList.size(); recCount++) {

                MedDat meddat = meddatList.get(recCount);

                values.clear();

                values.put(MedDat.COLUMN_ID, meddat.getId());
                values.put(MedDat.COLUMN_MED_DAT_NAME, meddat.getMedDatName());
                values.put(MedDat.COLUMN_MED_DAT_EFFICASY, meddat.getMedDatEfficasy());
                values.put(MedDat.COLUMN_MED_DAT_SHAPE, meddat.getMedDatShape());
                values.put(MedDat.COLUMN_MED_DAT_UNIT, meddat.getMedDatUnit());
                values.put(MedDat.COLUMN_MED_DAT_INTAKE, meddat.getMedDatIntake());


                long rowId = db.insert(MedDat.TABLE_NAME, null, values);
                if (rowId < 0) {
                    Log.e(TAG,
                            "saveList insert Error　ID:" + String.valueOf(rowId));
                    // TODO エラー処理 ↓でいいのか？
                    throw new SQLException();
                }
            }
            db.setTransactionSuccessful();

            Log.v(TAG, "saveList insert is succeeded.");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            recCount = -1;
        } finally {
            db.endTransaction();
            db.close();
        }
        return recCount;
    }
    /**
     * 現在日時を取得する
     * @return String [年/月/日 時:分:秒]
     */
    private String getCurrent_DateTime(){
        final Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);
        final int ms = calendar.get(Calendar.MILLISECOND);

        Log.v("year/month/day hour:minute:second",
            year + "/" + (month + 1) + "/" + day + "/" + " " +
            hour + ":" + minute + ":" + second + "." + ms);
        return year + "/" + (month + 1) + "/" + day + "/" + " " +
                hour + ":" + minute + ":" + second + "." + ms;
    }

    /**
     * 指定カラムの一覧を取得する
     * @param column カラム名:String型
     * @return mednoteList 検索結果:ArrayList<String>型
     */
    public ArrayList<String> getColumnList( String column ) {
        SQLiteDatabase db;
        String[] columns = { column };
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }
        ArrayList<String> mednoteList = new ArrayList<String>();
        try {
            cursor = db.query(MedDat.TABLE_NAME, columns, null, null, column, null, null );
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if (cursor.getString( cursor.getColumnIndex( column ) ) != null ){
                        mednoteList.add( cursor.getString( cursor.getColumnIndex( column ) ) );
                    } else {
                        mednoteList.add( "null" );
                    }
                    cursor.moveToNext();
                }

            }
        } finally {
            cursor.close();
            db.close();
        }
        return mednoteList;
    }

}
