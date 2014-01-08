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

import android.text.TextUtils;
import android.text.format.Time;

/**
 * @author 
 *
 *         1レコード分のデータを保持するクラス
 */
public class MedDat implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static final String TAG = MedDat.class.getSimpleName();
    private final MedDat self = this;

    // TableName
    public static final String TABLE_NAME = "med_dat";

    // カラム名定義
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MED_DAT_NAME = "med_dat_name";
    public static final String COLUMN_MED_DAT_EFFICASY = "med_dat_efficasy";
    public static final String COLUMN_MED_DAT_SHAPE = "med_dat_shape";
    public static final String COLUMN_MED_DAT_UNIT = "med_dat_unit";
    public static final String COLUMN_MED_DAT_INTAKE = "med_dat_intake";


    // カラム名変数の初期値
    private Long id = null;
    private String meddatname = null;
    private String meddatefficasy = null;
    private String meddatshape = null;
    private String meddatunit = null;
    private String meddatintake = null;


    /**
     * @return id
     */
    public Long getId() {
       return id;
    }

    /**
     * @return meddatname
     */
    public String getMedDatName() {
       return meddatname;
    }

    /**
     * @return meddatefficasy
     */
    public String getMedDatEfficasy() {
       return meddatefficasy;
    }

    /**
     * @return meddatshape
     */
    public String getMedDatShape() {
       return meddatshape;
    }

    /**
     * @return meddatunit
     */
    public String getMedDatUnit() {
       return meddatunit;
    }

    /**
     * @return meddatintake
     */
    public String getMedDatIntake() {
       return meddatintake;
    }



    /**
     * @param id
     *            セットする id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param meddatname
     *            セットする meddatname
     */
    public void setMedDatName(String meddatname) {
        this.meddatname = meddatname;
    }

    /**
     * @param meddatefficasy
     *            セットする meddatefficasy
     */
    public void setMedDatEfficasy(String meddatefficasy) {
        this.meddatefficasy = meddatefficasy;
    }

    /**
     * @param meddatshape
     *            セットする meddatshape
     */
    public void setMedDatShape(String meddatshape) {
        this.meddatshape = meddatshape;
    }

    /**
     * @param meddatunit
     *            セットする meddatunit
     */
    public void setMedDatUnit(String meddatunit) {
        this.meddatunit = meddatunit;
    }

    /**
     * @param meddatintake
     *            セットする meddatintake
     */
    public void setMedDatIntake(String meddatintake) {
        this.meddatintake = meddatintake;
    }



}
