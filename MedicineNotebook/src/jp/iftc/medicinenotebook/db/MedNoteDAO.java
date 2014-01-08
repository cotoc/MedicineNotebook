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
public class MedNoteDAO {

    private static final String TAG = MedNoteDAO.class.getSimpleName();
    @SuppressWarnings("unused")
    private final MedNoteDAO self = this;

    private MED_INFODbOpenHelper helper = null;
    private Context mContext;

    /*
     *
     */
    public MedNoteDAO(Context context) {
        helper = new MED_INFODbOpenHelper(context);
        mContext = context;
    }

    /*
     * 情報の保存＜IDがNULLならInsert,それ以外ならUpdateで全項目更新＞
     *
     * @param MedNote 保存対象のデータ
     *
     * @return 保存したデータ　Error：null
     */
    public MedNote save(MedNote mednote) {
        SQLiteDatabase db;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        MedNote result = null;
        try {
            ContentValues values = new ContentValues();
                values.put(MedNote.COLUMN_ID, mednote.getId());
                values.put(MedNote.COLUMN_MED_NOTE_DATE, mednote.getMedNoteDate());
                values.put(MedNote.COLUMN_MED_NOTE_HOSPNAME, mednote.getMedNoteHospname());
                values.put(MedNote.COLUMN_MED_NOTE_PHARNAME, mednote.getMedNotePharname());


            Long rowId = mednote.getId();

            int updateCount = 0;

            // IDがnullの場合はinsert
            if (rowId == null) {
                rowId = db.insert(MedNote.TABLE_NAME, null, values);
                if (rowId < 0) {
                    // エラー処理
                    Log.w(TAG, "save Insert Error");
                    throw new SQLException();
                }
                Log.v(TAG, "save Insert Success!");
            } else {
                updateCount = db.update(MedNote.TABLE_NAME, values,
                    MedNote.COLUMN_ID + "=?",
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
     * @param MedNote
     *            削除対象のオブジェクト
     */
    public void delete(MedNote mednote) {
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
            deleteCount = db.delete(MedNote.TABLE_NAME, MedNote.COLUMN_ID
                    + "=?", new String[] { String.valueOf(mednote.getId()) });
            if (deleteCount != 1) {
                // エラー処理
                Log.w(TAG,
                        "delete Delete Error : Update ID = "
                                + String.valueOf(mednote.getId())
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
     * 1レコードの削除
     *
     * @param MedNote
     *            削除対象のオブジェクト
     */
    public boolean deleteMedNote(MedNote mednote) {
    	boolean ret = false;
        SQLiteDatabase db;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return ret;
        }

        int deleteCount = 0;
        db.beginTransaction();

        try {
            deleteCount = db.delete(MedNote.TABLE_NAME, MedNote.COLUMN_ID
                    + "=?", new String[] { String.valueOf(mednote.getId()) });
            if (deleteCount != 1) {
                // エラー処理
                Log.w(TAG,
                        "delete Delete Error : Update ID = "
                                + String.valueOf(mednote.getId())
                                + "| Delete Count : "
                                + String.valueOf(deleteCount));
                throw new SQLException();
            }

            MedNoteDetailDAO detailDao = new MedNoteDetailDAO(mContext);
            ret = detailDao.deleteListByLinkNo(mednote.getId(), db);
            if(ret)            db.setTransactionSuccessful();

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
        	db.endTransaction();
            db.close();
        }

        return ret;
    }

    /**
     * 全レコードの削除
     *
     * @param db
     */
    private void deleteMedNoteAll(SQLiteDatabase db) {

        int deleteCount = 0;
        try {
            deleteCount = db.delete(MedNote.TABLE_NAME, null, null);
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
    public MedNote load(Long rowId) {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        MedNote mednote = null;
        try {
            cursor = db.query(MedNote.TABLE_NAME, null,
                    MedNote.COLUMN_ID + "=?",
                    new String[] { String.valueOf(rowId) }, null, null, null);
            cursor.moveToFirst();
            mednote = getMedNote(cursor);
        } finally {
            cursor.close();
            db.close();
        }

        return mednote;
    }


    /**
     * IDで資産情報を読み込む
     *
     * @param rowId
     * @return
     */
    public MedNote load(Long rowId, SQLiteDatabase db) {

        MedNote mednote = null;
        Cursor cursor = null;
        try {
            cursor = db.query(MedNote.TABLE_NAME, null,
                    MedNote.COLUMN_ID + "=?",
                    new String[] { String.valueOf(rowId) }, null, null, null);
            cursor.moveToFirst();
            mednote = getMedNote(cursor);
        } finally {
            cursor.close();
        }

        return mednote;
    }
    /**
     * 一覧を取得する
     *
     * @return 検索結果
     */
    public List<MedNote> list() {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        List<MedNote> mednoteList;
        try {
            cursor = db.query(MedNote.TABLE_NAME, null, null, null,
                    null, null, MedNote.COLUMN_ID);
            mednoteList = new ArrayList<MedNote>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    mednoteList.add(getMedNote(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
            db.close();
        }
        return mednoteList;
    }

    /**
     * 一覧を取得する
     *
     * @return 検索結果
     */
    public List<MedNote> list_desc() {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        List<MedNote> mednoteList;
        try {
            String orderby;
            orderby = MedNote.COLUMN_MED_NOTE_DATE + " desc";

            cursor = db.query(MedNote.TABLE_NAME, null, null, null,
                    null, null, orderby);
            mednoteList = new ArrayList<MedNote>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    mednoteList.add(getMedNote(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
            db.close();
        }
        return mednoteList;
    }



    /**
     * カーソルからオブジェクトへの変換
     *
     * @param cursor
     * @return MedNote
     *             カーソルから読み込んだデータをMedNoteクラスに編成
     */
    private MedNote getMedNote(Cursor cursor) {
        MedNote mednote = new MedNote();

        mednote.setId(cursor.getLong(cursor.getColumnIndex(MedNote.COLUMN_ID)));
        mednote.setMedNoteDate(cursor.getLong(cursor.getColumnIndex(MedNote.COLUMN_MED_NOTE_DATE)));
        mednote.setMedNoteHospname(cursor.getString(cursor.getColumnIndex(MedNote.COLUMN_MED_NOTE_HOSPNAME)));
        mednote.setMedNotePharname(cursor.getString(cursor.getColumnIndex(MedNote.COLUMN_MED_NOTE_PHARNAME)));


        return mednote;
    }

    /**
     * リストのデータをすべてDBに登録する＜全件Insert＞
     *
     * @param MedNoteList
     *            ： 登録対象のリスト
     * @return recCount : 登録したデータの件数 エラーの場合-1
     */
    public int saveList(List<MedNote> mednoteList) {
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
            deleteMedNoteAll(db);

            ContentValues values = new ContentValues();
            for (recCount = 0; recCount < mednoteList.size(); recCount++) {

                MedNote mednote = mednoteList.get(recCount);

                values.clear();

                values.put(MedNote.COLUMN_ID, mednote.getId());
                values.put(MedNote.COLUMN_MED_NOTE_DATE, mednote.getMedNoteDate());
                values.put(MedNote.COLUMN_MED_NOTE_HOSPNAME, mednote.getMedNoteHospname());
                values.put(MedNote.COLUMN_MED_NOTE_PHARNAME, mednote.getMedNotePharname());


                long rowId = db.insert(MedNote.TABLE_NAME, null, values);
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
            cursor = db.query(MedNote.TABLE_NAME, columns, null, null, column, null, null );
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

    /*
     * 情報のコピー
     *  ※ db制御(トランザクションは本関数外で管理)
     * @param MedNote 保存対象のデータ
     *
     * @return 保存したデータ　Error：null
     */
    public MedNote copy(MedNote mednote, SQLiteDatabase db) {
        MedNote result = null;
        String defaultId = null;
    	Calendar cal =  Calendar.getInstance();

    	try {
            ContentValues values = new ContentValues();
                values.put(MedNote.COLUMN_ID, defaultId);
                values.put(MedNote.COLUMN_MED_NOTE_DATE, cal.getTimeInMillis());
                values.put(MedNote.COLUMN_MED_NOTE_HOSPNAME, mednote.getMedNoteHospname());
                values.put(MedNote.COLUMN_MED_NOTE_PHARNAME, mednote.getMedNotePharname());


            Long rowId = mednote.getId();

            int updateCount = 0;

            rowId = db.insert(MedNote.TABLE_NAME, null, values);
            if (rowId < 0) {
                // エラー処理
                Log.w(TAG, "copy Insert Error");
                throw new SQLException();
            }
            Log.v(TAG, "copy Insert Success!");

            result = load(rowId, db);
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
            result = null;
        } finally {

        }
        return result;

    }
}
