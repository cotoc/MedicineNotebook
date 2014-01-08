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
public class MedNoteDetailDAO {

    private static final String TAG = MedNoteDetailDAO.class.getSimpleName();
    @SuppressWarnings("unused")
    private final MedNoteDetailDAO self = this;

    private MED_INFODbOpenHelper helper = null;
    private Context mContext;

    /*
     *
     */
    public MedNoteDetailDAO(Context context) {
        helper = new MED_INFODbOpenHelper(context);
        mContext = context;
    }

    /*
     * 情報の保存＜IDがNULLならInsert,それ以外ならUpdateで全項目更新＞
     *
     * @param MedNoteDetail 保存対象のデータ
     *
     * @return 保存したデータ　Error：null
     */
    public MedNoteDetail save(MedNoteDetail mednotedetail) {
        SQLiteDatabase db;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        MedNoteDetail result = null;
        try {
            ContentValues values = new ContentValues();
                values.put(MedNoteDetail.COLUMN_ID, mednotedetail.getId());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_LINKNO, mednotedetail.getMedDetailLinkno());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_SHAPE, mednotedetail.getMedDetailShape());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_UNIT, mednotedetail.getMedDetailUnit());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_INTAKE, mednotedetail.getMedDetailIntake());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_DOSE, mednotedetail.getMedDetailDose());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TOTAL, mednotedetail.getMedDetailTotal());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ETC, mednotedetail.getMedDetailEtc());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TIMEZONE, mednotedetail.getMedDetailTimezone());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TIMING, mednotedetail.getMedDetailTiming());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_COMMENT, mednotedetail.getMedDetailComment());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ALARM, mednotedetail.getMedDetailAlarm());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ENDDAY, mednotedetail.getMedDetailEndday());

            Long rowId = mednotedetail.getId();

            int updateCount = 0;

            // IDがnullの場合はinsert
            if (rowId == null) {
                rowId = db.insert(MedNoteDetail.TABLE_NAME, null, values);
                if (rowId < 0) {
                    // エラー処理
                    Log.w(TAG, "save Insert Error");
                    throw new SQLException();
                }
                Log.v(TAG, "save Insert Success!");
            } else {
                updateCount = db.update(MedNoteDetail.TABLE_NAME, values,
                    MedNoteDetail.COLUMN_ID + "=?",
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
     * @param MedNoteDetail
     *            削除対象のオブジェクト
     */
    public boolean delete(MedNoteDetail mednotedetail) {
        SQLiteDatabase db;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return false;
        }

        boolean ret = true;
        int deleteCount = 0;
        try {
            deleteCount = db.delete(MedNoteDetail.TABLE_NAME, MedNoteDetail.COLUMN_ID
                    + "=?", new String[] { String.valueOf(mednotedetail.getId()) });
            if (deleteCount != 1) {
                // エラー処理
                Log.w(TAG,
                        "delete Delete Error : Update ID = "
                                + String.valueOf(mednotedetail.getId())
                                + "| Delete Count : "
                                + String.valueOf(deleteCount));
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            ret = false;

        } finally {
            db.close();
        }
        return ret;
    }

    /**
     * 全レコードの削除
     *
     * @param db
     */
    private void deleteMedNoteDetailAll(SQLiteDatabase db) {

        int deleteCount = 0;
        try {
            deleteCount = db.delete(MedNoteDetail.TABLE_NAME, null, null);
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
    public MedNoteDetail load(Long rowId) {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        MedNoteDetail mednotedetail = null;
        try {
            cursor = db.query(MedNoteDetail.TABLE_NAME, null,
                    MedNoteDetail.COLUMN_ID + "=?",
                    new String[] { String.valueOf(rowId) }, null, null, null);
            cursor.moveToFirst();
            mednotedetail = getMedNoteDetail(cursor);
        } finally {
            cursor.close();
            db.close();
        }

        return mednotedetail;
    }


    /**
     * IDで資産情報を読み込む
     *
     * @param rowId
     * @return
     */
    public MedNoteDetail load(Long rowId, SQLiteDatabase db) {

        MedNoteDetail mednotedetail = null;
        Cursor cursor = null;
        try {
            cursor = db.query(MedNoteDetail.TABLE_NAME, null,
                    MedNoteDetail.COLUMN_ID + "=?",
                    new String[] { String.valueOf(rowId) }, null, null, null);
            cursor.moveToFirst();
            mednotedetail = getMedNoteDetail(cursor);
        } finally {
            cursor.close();
        }

        return mednotedetail;
    }

    /**
     * 一覧を取得する
     *
     * @return 検索結果
     */
    public List<MedNoteDetail> list() {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        List<MedNoteDetail> mednotedetailList;
        try {
            cursor = db.query(MedNoteDetail.TABLE_NAME, null, null, null,
                    null, null, MedNoteDetail.COLUMN_ID);
            mednotedetailList = new ArrayList<MedNoteDetail>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    mednotedetailList.add(getMedNoteDetail(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
            db.close();
        }
        return mednotedetailList;
    }

    /**
     * 一覧を取得する
     *
     * @return 検索結果
     */
    public List<MedNoteDetail> loadDetailListById(long link_no) {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        List<MedNoteDetail> mednotedetailList;
        try {
            cursor = db.query(MedNoteDetail.TABLE_NAME, null,
                    MedNoteDetail.COLUMN_MED_DETAIL_LINKNO + "=?",
                    new String[] { String.valueOf(link_no) }, null, null, MedNoteDetail.COLUMN_ID);

            mednotedetailList = new ArrayList<MedNoteDetail>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    mednotedetailList.add(getMedNoteDetail(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
            db.close();
        }
        return mednotedetailList;
    }

    /**
     * 処方箋ごとのArrayになった一覧を取得する
     *
     * @return 検索結果
     */
    public ArrayList<List<MedNoteDetail>> getExpandableList() {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        ArrayList<List<MedNoteDetail>> medExpandableList;

        try {
            cursor = db.query(MedNoteDetail.TABLE_NAME, null, null, null,
                    null, null, MedNoteDetail.COLUMN_MED_DETAIL_LINKNO + "," + MedNoteDetail.COLUMN_ID);

            medExpandableList = new ArrayList<List<MedNoteDetail>>();

            //処方箋ID 初期値
            Long meddetaillinkno = null;
            Long currentMeddetaillinkno = null;
            if( cursor.moveToFirst()) {

            	List<MedNoteDetail> medList = new ArrayList<MedNoteDetail>();
                while (!cursor.isAfterLast()) {

                	MedNoteDetail mednotedetail = getMedNoteDetail(cursor);
                	//　カーソルのデータと、直前の処方箋IDが違っていたら、
                	// ExpandableListに、１処方箋のデータをAddして、次の処方箋データを作成する、
                	if( meddetaillinkno != null &&
                			meddetaillinkno != mednotedetail.getMedDetailLinkno()){
                		if( medList != null){
                			medExpandableList.add(medList);
                		}
                		medList = new ArrayList<MedNoteDetail>();
                	}
                	if(mednotedetail != null){
                		medList.add(mednotedetail);
                	}
                	meddetaillinkno = mednotedetail.getMedDetailLinkno();
                    cursor.moveToNext();
                }
                if( medList != null){
        			medExpandableList.add(medList);
                }
            }
        } finally {
            cursor.close();
            db.close();
        }
        return medExpandableList;
    }

    /**
     * 1レコードの削除
     *
     * @param MedNoteDetail
     *            削除対象のオブジェクト
     */
    public boolean deleteList(List<MedNoteDetail> mednotedetailList) {

    	String whereArgs[];

    	if(mednotedetailList.isEmpty()){
    		return false;
    	} else {
    		whereArgs = new String[mednotedetailList.size()];
    		for(int i = 0; i < mednotedetailList.size(); i++){
    			whereArgs[i] = String.valueOf(mednotedetailList.get(i).getId());
    		}
    	}

    	SQLiteDatabase db;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return false;
        }

        boolean ret = true;
        int deleteCount = 0;

        db.beginTransaction();
        try {

    		for(int i = 0; i < mednotedetailList.size(); i++){
    			whereArgs[i] = String.valueOf(mednotedetailList.get(i).getId());

	            deleteCount = db.delete(MedNoteDetail.TABLE_NAME, MedNoteDetail.COLUMN_ID
	                    + "=?",  new String[] {String.valueOf(mednotedetailList.get(i).getId())});
	            if (deleteCount != 1) {
	                // エラー処理
	                Log.w(TAG,
	                        "delete Delete Error : Update ID = "
	                                + whereArgs
	                                + "| Delete Count : "
	                                + String.valueOf(deleteCount));
	                throw new SQLException();
	            }
    		}

    		db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
//            Log.e(TAG, e.getMessage());

            ret = false;

        } finally {
        	db.endTransaction();
            db.close();
        }
        return ret;
    }

    /**
     * 1処方箋お薬データの全削除
     *
     * @param linkNo
     *            削除条件となるリンクナンバー
     */
    public boolean deleteListByLinkNo(long linkNo, SQLiteDatabase db) {

//        SQLiteDatabase db;
//        try {
//            db = helper.getWritableDatabase();
//
//        } catch (SQLiteException e) {
//            // TODO: handle exception
//            Log.w(TAG, e.toString());
//            return false;
//        }

        boolean ret = true;
        int deleteCount = 0;
        try {
            deleteCount = db.delete(MedNoteDetail.TABLE_NAME, MedNoteDetail.COLUMN_MED_DETAIL_LINKNO
                    + "=?", new String[] { String.valueOf(linkNo) });
            if (deleteCount <= 0) {
            	ret = false;
                // エラー処理
                Log.w(TAG,
                        "delete Delete Error : LinkNo = "
                                + String.valueOf(linkNo)
                                + "| Delete Count : "
                                + String.valueOf(deleteCount));
                throw new SQLException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            ret = false;

        } finally {
//            db.close();
        }
        return ret;

    }

    /**
     * カーソルからオブジェクトへの変換
     *
     * @param cursor
     * @return MedNoteDetail
     *             カーソルから読み込んだデータをMedNoteDetailクラスに編成
     */
    private MedNoteDetail getMedNoteDetail(Cursor cursor) {
        MedNoteDetail mednotedetail = new MedNoteDetail();

        mednotedetail.setId(cursor.getLong(cursor.getColumnIndex(MedNoteDetail.COLUMN_ID)));
        mednotedetail.setMedDetailLinkno(cursor.getLong(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_LINKNO)));
        mednotedetail.setMedDetailShape(cursor.getString(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_SHAPE)));
        mednotedetail.setMedDetailUnit(cursor.getString(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_UNIT)));
        mednotedetail.setMedDetailIntake(cursor.getString(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_INTAKE)));
        mednotedetail.setMedDetailDose(cursor.getInt(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_DOSE)));
        mednotedetail.setMedDetailTotal(cursor.getInt(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_TOTAL)));
        mednotedetail.setMedDetailEtc(cursor.getInt(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_ETC)));
        mednotedetail.setMedDetailTimezone(cursor.getInt(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_TIMEZONE)));
        mednotedetail.setMedDetailTiming(cursor.getInt(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_TIMING)));
        mednotedetail.setMedDetailComment(cursor.getString(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_COMMENT)));
        mednotedetail.setMedDetailAlarm(cursor.getInt(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_ALARM)));
        mednotedetail.setMedDetailEndday(cursor.getLong(cursor.getColumnIndex(MedNoteDetail.COLUMN_MED_DETAIL_ENDDAY)));

        return mednotedetail;
    }

    /**
     * リストのデータをすべてDBに登録する
     *
     * @param MedNoteDetailList
     *            ： 登録対象のリスト
     * @return recCount : 登録したデータの件数 エラーの場合-1
     */
    public int saveList(List<MedNoteDetail> mednotedetailList) {
        SQLiteDatabase db;
        Long rowId;
        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            Log.w(TAG, e.toString());
            return -1;
        }

        int recCount;
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            for (recCount = 0; recCount < mednotedetailList.size(); recCount++) {

                MedNoteDetail mednotedetail = mednotedetailList.get(recCount);

                values.clear();

                values.put(MedNoteDetail.COLUMN_ID, mednotedetail.getId());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_LINKNO, mednotedetail.getMedDetailLinkno());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_SHAPE, mednotedetail.getMedDetailShape());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_UNIT, mednotedetail.getMedDetailUnit());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_INTAKE, mednotedetail.getMedDetailIntake());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_DOSE, mednotedetail.getMedDetailDose());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TOTAL, mednotedetail.getMedDetailTotal());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ETC, mednotedetail.getMedDetailEtc());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TIMEZONE, mednotedetail.getMedDetailTimezone());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TIMING, mednotedetail.getMedDetailTiming());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_COMMENT, mednotedetail.getMedDetailComment());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ALARM, mednotedetail.getMedDetailAlarm());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ENDDAY, mednotedetail.getMedDetailEndday());

                rowId = mednotedetail.getId();
                int updateCount = 0;

                // IDがnullの場合はinsert
                if (rowId == null) {
                    rowId = db.insert(MedNoteDetail.TABLE_NAME, null, values);
                    if (rowId < 0) {
                        // エラー処理
                        Log.w(TAG, "save Insert Error");
                        throw new SQLException();
                    }
                    Log.v(TAG, "save Insert Success!");
                } else {
                    updateCount = db.update(MedNoteDetail.TABLE_NAME, values,
                        MedNoteDetail.COLUMN_ID + "=?",
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
     * リストのデータをすべてDBに登録する＜全件Insert＞
     *
     * @param MedNoteDetail
     *            ： 登録対象のリスト
     * @return recCount : 登録したデータの件数 エラーの場合-1
     */
    public int ReplaceList(List<MedNoteDetail> mednotedetailList) {
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
            deleteMedNoteDetailAll(db);

            ContentValues values = new ContentValues();
            for (recCount = 0; recCount < mednotedetailList.size(); recCount++) {

                MedNoteDetail mednotedetail = mednotedetailList.get(recCount);

                values.clear();

                values.put(MedNoteDetail.COLUMN_ID, mednotedetail.getId());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_LINKNO, mednotedetail.getMedDetailLinkno());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_SHAPE, mednotedetail.getMedDetailShape());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_UNIT, mednotedetail.getMedDetailUnit());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_INTAKE, mednotedetail.getMedDetailIntake());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_DOSE, mednotedetail.getMedDetailDose());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TOTAL, mednotedetail.getMedDetailTotal());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ETC, mednotedetail.getMedDetailEtc());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TIMEZONE, mednotedetail.getMedDetailTimezone());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TIMING, mednotedetail.getMedDetailTiming());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_COMMENT, mednotedetail.getMedDetailComment());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ALARM, mednotedetail.getMedDetailAlarm());
                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ENDDAY, mednotedetail.getMedDetailEndday());

                long rowId = db.insert(MedNoteDetail.TABLE_NAME, null, values);
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
     * リンクNo内での現在位置を取得する
     * @param LinkNo リンクNo,rowId 対象のID
     * @return ChildNo:Integer型
     */
    public Integer getChildNo( Long LinkNo , Long rowId) {
        List<MedNoteDetail> childlist = getChildList(LinkNo);
        int ChildNo = 0;

        for(int i = 0;i<childlist.size();i++){
            if ( childlist.get(i).getId().equals(rowId) ) {
                ChildNo = i;
            }
        }
        return ChildNo;
    }

    /**
     * リンクNoの一覧を取得する
     * @param LinkNo リンクNo
     * @return ChildNo:List<MedNoteDetail>型
     */
    public List<MedNoteDetail> getChildList( Long LinkNo ) {
        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        List<MedNoteDetail> mednotedetailList;
        String where = MedNoteDetail.COLUMN_MED_DETAIL_LINKNO + " = " + LinkNo;
        try {
            cursor = db.query(MedNoteDetail.TABLE_NAME, null, where, null,
                    null, null, MedNoteDetail.COLUMN_ID);
            mednotedetailList = new ArrayList<MedNoteDetail>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    mednotedetailList.add(getMedNoteDetail(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
            db.close();
        }
        return mednotedetailList;
    }

    /**
     * パラメータで受け取ったデータを登録する
     * @param medNoteDitailList コピー元のデータリスト
     * @param linkNo コピー先のリンクナンバー
     * @return
     */
    public ArrayList<MedNoteDetail> copyMedNoteList( ArrayList<MedNoteDetail> medNoteDitailList, long linkNo, SQLiteDatabase db ) {

    	boolean ret = false;
    	String initialId = null;

    	ArrayList<MedNoteDetail> result = new ArrayList<MedNoteDetail>();
        try {

        	for(int i = 0; i < medNoteDitailList.size(); i ++){
	            ContentValues values = new ContentValues();
	                values.put(MedNoteDetail.COLUMN_ID, initialId);
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_LINKNO, linkNo);
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_SHAPE, medNoteDitailList.get(i).getMedDetailShape());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_UNIT, medNoteDitailList.get(i).getMedDetailUnit());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_INTAKE, medNoteDitailList.get(i).getMedDetailIntake());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_DOSE, medNoteDitailList.get(i).getMedDetailDose());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TOTAL, medNoteDitailList.get(i).getMedDetailTotal());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ETC, medNoteDitailList.get(i).getMedDetailEtc());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TIMEZONE, medNoteDitailList.get(i).getMedDetailTimezone());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_TIMING, medNoteDitailList.get(i).getMedDetailTiming());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_COMMENT, medNoteDitailList.get(i).getMedDetailComment());
	                values.put(MedNoteDetail.COLUMN_MED_DETAIL_ALARM, medNoteDitailList.get(i).getMedDetailAlarm());
                    values.put(MedNoteDetail.COLUMN_MED_DETAIL_ENDDAY, medNoteDitailList.get(i).getMedDetailEndday());

	            int updateCount = 0;

	            long rowId = db.insert(MedNoteDetail.TABLE_NAME, null, values);
	            if (rowId < 0) {
	                // エラー処理
	                Log.w(TAG, "save Insert Error");
	                throw new SQLException();
	            }
	            Log.v(TAG, "save Insert Success!");

	            result.add(load(rowId, db));
        	}

        } catch (SQLException e) {
            Log.e(TAG, e.toString());
            result = null;
        } finally {
//            db.close();
        }
        return result;
    }

    /**
     * アラームリスト表示用の一覧を取得する
     * @param  対象日付 today,抽出対象のフラグ flag[]
     * @return ChildNo:List<MedNoteDetail>型
     */
    public List<MedNoteDetail> getAlarmList( Long today, int[] flag ) {
        final int CLM_TIMEZONE = 0;
        final int CLM_TIMING = 1;

        SQLiteDatabase db;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        List<MedNoteDetail> mednotedetailList;
        String where;

        if ( flag[CLM_TIMING] == 3 ) {
            where = "( " + MedNoteDetail.COLUMN_MED_DETAIL_TIMING + " = " + flag[CLM_TIMING];
            where = where + " or " + MedNoteDetail.COLUMN_MED_DETAIL_TIMING + " = 4 )";
        } else {
            where = MedNoteDetail.COLUMN_MED_DETAIL_TIMING + " = " + flag[CLM_TIMING];
        }

        where = where + " and " + MedNoteDetail.COLUMN_MED_DETAIL_ENDDAY  + " >= "+ today;
        where = where + " and " + MedNoteDetail.COLUMN_MED_DETAIL_ETC  + " = 0";
        where = where + " and " + MedNoteDetail.COLUMN_MED_DETAIL_ALARM  + " = 1";

        try {
            cursor = db.query(MedNoteDetail.TABLE_NAME, null, where, null,
                    null, null, MedNoteDetail.COLUMN_ID);
            mednotedetailList = new ArrayList<MedNoteDetail>();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    MedNoteDetail mednotedetailbuf = new MedNoteDetail();
                    mednotedetailbuf = getMedNoteDetail(cursor);

                    if (( mednotedetailbuf.getMedDetailTimezone() & flag[CLM_TIMEZONE] ) == flag[CLM_TIMEZONE] ) {
                        mednotedetailList.add(getMedNoteDetail(cursor));
                    }

                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
            db.close();
        }
        return mednotedetailList;
    }

}
