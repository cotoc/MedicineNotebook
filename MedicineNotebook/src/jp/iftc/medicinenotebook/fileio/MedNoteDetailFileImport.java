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

package jp.iftc.medicinenotebook.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import jp.iftc.medicinenotebook.db.MedNoteDetail;
import jp.iftc.medicinenotebook.db.MedNoteDetailDAO;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * @author
 *
 */
public class MedNoteDetailFileImport {

    private static final String TAG = MedNoteDetailFileImport.class.getSimpleName();
    @SuppressWarnings("unused")
    private final MedNoteDetailFileImport self = this;

    //DBアクセスクラス
    MedNoteDetailDAO dao;

    // 読み込みファイル名
    public static final String FILE_NAME = "med_note_detail.xml";

    public static final int FILE_NOTFOUND = -9;
    public static final int XML_FILE_FORMAT_ERROR = -8;
    public static final int DBACCESS_ERROR = -7;
    public static final int SD_NOT_READY = -6;
    public static final int IMPORT_ERROR = -1;

    private Context mContext;

    private List<MedNoteDetail> mListMedNoteDetail;

    /**
     * @param context
     */
    public MedNoteDetailFileImport(Context context) {
        mContext = context;
        dao = new MedNoteDetailDAO(context);
    }

    /**
     * XMLファイルを読み込み、DBに登録する
     * @return saveRecCount 登録データ件数　/ エラーの場合 < 0
     */
    public int xmlFileImport() {

        CheckStorageState chkSDState = new CheckStorageState(mContext);
        if( chkSDState.checkState() !=  CheckStorageState.AVAILABLE){
            return SD_NOT_READY;
        }

        int recCount = 0;
        int result = 0;
        result = xmlFileLoad();
        if (mListMedNoteDetail == null) {
            return result;
        }
        recCount = mListMedNoteDetail.size();
        Log.d(TAG,"Load Rec Couont --" + Integer.toString(recCount));

        int saveRecCount;
        // 読み込んだデータをDBに登録する
        saveRecCount = dao.ReplaceList(mListMedNoteDetail);
        if (saveRecCount < 0) {
            // ERROR
            Log.d(TAG, "登録エラー");
            saveRecCount= DBACCESS_ERROR;
        }
        Log.d(TAG,"Save Rec Couont --" + Integer.toString(saveRecCount));
        return saveRecCount;
    }

    /**
     * ファイルインポート 　FILE_NAMEで指定した、XMLファイルを読み込む
     * @return 読み込み結果
     *             0：成功
     *             FILE_NOTFOUND：ファイルなし
     *             XML_FILE_FORMAT_ERROR：ファイル形式不正
     *             IMPORT_ERROR：その他 ファイル読み込みエラー
     */
    public int xmlFileLoad() {

        // SDカードパスを取得する。
        String SDFile = Environment.getExternalStorageDirectory() + "/data/" + FILE_NAME;
        File file = new File(SDFile);

        List<MedNoteDetail> lisetMedNoteDetail = new ArrayList<MedNoteDetail>();

        int result = 0;

        FileInputStream fis;
        try {
            fis = new FileInputStream(file);

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, "UTF-8");

            // タグ名
            String tag = "";
            // 値
            String value = "";
            // 1レコード分のデータを保管
            MedNoteDetail mednotedetail = null;
            // XMLの解析
            for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
                    .next()) {

                switch (type) {
                case XmlPullParser.START_TAG: // 開始タグ
                    tag = parser.getName();
                    if (parser.getName().equals("Android_Export")) {
                        mednotedetail = new MedNoteDetail();
                    }
                    break;
                case XmlPullParser.TEXT: // タグの内容
                    value = parser.getText();
                    // 空白で取得したものは全て処理対象外とする
                    if (value.trim().length() != 0) {
                        // 取得した結果をmednotedetailに設定
                        if (tag.equals("ID")) {
                            mednotedetail.setId(Long.parseLong(value));
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_LINKNO)) {
                            mednotedetail.setMedDetailLinkno(Long.parseLong(value));
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_SHAPE)) {
                            mednotedetail.setMedDetailShape(value);
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_UNIT)) {
                            mednotedetail.setMedDetailUnit(value);
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_INTAKE)) {
                            mednotedetail.setMedDetailIntake(value);
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_DOSE)) {
                            mednotedetail.setMedDetailDose(Integer.parseInt(value));
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_TOTAL)) {
                            mednotedetail.setMedDetailTotal(Integer.parseInt(value));
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_ETC)) {
                            mednotedetail.setMedDetailEtc(Integer.parseInt(value));
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_TIMEZONE)) {
                            mednotedetail.setMedDetailTimezone(Integer.parseInt(value));
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_TIMING)) {
                            mednotedetail.setMedDetailTiming(Integer.parseInt(value));
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_COMMENT)) {
                            mednotedetail.setMedDetailComment(value);
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_ALARM)) {
                            mednotedetail.setMedDetailAlarm(Integer.parseInt(value));
                        } else if (tag.equals(MedNoteDetail.COLUMN_MED_DETAIL_ENDDAY)) {
                            mednotedetail.setMedDetailEndday(Long.parseLong(value));
                        }

                    }
                    break;
                case XmlPullParser.END_TAG: // 終了タグ
                    if (parser.getName().equals("Android_Export")
                            && mednotedetail != null) {
                        lisetMedNoteDetail.add(mednotedetail);
                        mednotedetail = null;
                    }
                    break;
                }
            }

            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            lisetMedNoteDetail = null;
            result = FILE_NOTFOUND;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            lisetMedNoteDetail = null;
            result = XML_FILE_FORMAT_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            lisetMedNoteDetail = null;
            result = IMPORT_ERROR;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            lisetMedNoteDetail = null;
            result = IMPORT_ERROR;
        } finally {

        }

        mListMedNoteDetail = lisetMedNoteDetail;

        return result;
    }



}
