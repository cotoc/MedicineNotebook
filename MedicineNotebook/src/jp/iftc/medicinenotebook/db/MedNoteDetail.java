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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import jp.iftc.medicinenotebook.R;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;

/**
 * @author
 *
 *         1レコード分のデータを保持するクラス
 */
public class MedNoteDetail implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static final String TAG = MedNoteDetail.class.getSimpleName();
    private final MedNoteDetail self = this;

    // TableName
    public static final String TABLE_NAME = "med_note_detail";

    // カラム名定義
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MED_DETAIL_LINKNO = "med_detail_linkno";
    public static final String COLUMN_MED_DETAIL_SHAPE = "med_detail_shape";
    public static final String COLUMN_MED_DETAIL_UNIT = "med_detail_unit";
    public static final String COLUMN_MED_DETAIL_INTAKE = "med_detail_intake";
    public static final String COLUMN_MED_DETAIL_DOSE = "med_detail_dose";
    public static final String COLUMN_MED_DETAIL_TOTAL = "med_detail_total";
    public static final String COLUMN_MED_DETAIL_ETC = "med_detail_etc";
    public static final String COLUMN_MED_DETAIL_TIMEZONE = "med_detail_timezone";
    public static final String COLUMN_MED_DETAIL_TIMING = "med_detail_timing";
    public static final String COLUMN_MED_DETAIL_COMMENT = "med_detail_comment";
    public static final String COLUMN_MED_DETAIL_ALARM = "med_detail_alarm";
    public static final String COLUMN_MED_DETAIL_ENDDAY = "med_detail_endday";


    // カラム名変数の初期値
    private Long id = null;
    private Long meddetaillinkno = null;
    private String meddetailshape = null;
    private String meddetailunit = null;
    private String meddetailintake = null;
    private Integer meddetaildose = null;
    private Integer meddetailtotal = null;
    private Integer meddetailetc = null;
    private Integer meddetailtimezone = null;
    private Integer meddetailtiming = null;
    private String meddetailcomment = null;
    private Integer meddetailalarm = null;
    private Long meddetailendday = null;

    private final static int TIMEZONE_MORNING = 0x01;
    private final static int TIMEZONE_NOON = 0x02;
    private final static int TIMEZONE_NIGHT = 0x04;
    private final static int TIMEZONE_BEFORE_BEDTIME = 0x08;

    private boolean checked = false;

    /**
     * @return id
     */
    public Long getId() {
       return id;
    }

    /**
     * @return meddetaillinkno
     */
    public Long getMedDetailLinkno() {
       return meddetaillinkno;
    }

    /**
     * @return meddetailshape
     */
    public String getMedDetailShape() {
       return meddetailshape;
    }

    /**
     * @return meddetailunit
     */
    public String getMedDetailUnit() {
       return meddetailunit;
    }

    /**
     * @return meddetailintake
     */
    public String getMedDetailIntake() {
       return meddetailintake;
    }

    /**
     * @return meddetaildose
     */
    public Integer getMedDetailDose() {
       return meddetaildose;
    }

    public String getMedDetailDoseUnit(Context context) {

        String[] adp1 = context.getResources().getStringArray(R.array.SpnItm_med1);
        String[] adp2 = context.getResources().getStringArray(R.array.MedItmUnit);
        List<String> list1 =Arrays.asList(adp1);
        List<String> list2 =Arrays.asList(adp2);

        if(0 > list1.indexOf(meddetailunit) || list2.size() <= list1.indexOf(meddetailunit)){
        	return "個";
        } else {
        	return list2.get((list1.indexOf(meddetailunit))).toString();
        }
     }

    /**
     * @return meddetailtotal
     */
    public Integer getMedDetailTotal() {
       return meddetailtotal;
    }

    /**
     * @return meddetailetc
     */
    public Integer getMedDetailEtc() {
       return meddetailetc;
    }

    /**
     * @return meddetailtimezone
     */
    public Integer getMedDetailTimezone() {
       return meddetailtimezone;
    }

    /**
     * @return meddetailtimezone
     */
    public String getMedDetailTimezoneString() {
    	String out = "";
    	long judge = meddetailtimezone;   //(2)

        if ((meddetailtimezone & TIMEZONE_MORNING) == TIMEZONE_MORNING) {   //(4)
        	out = out + "朝";
        } else {
        	out = out + "－";
        }

        if ((meddetailtimezone & TIMEZONE_NOON) == TIMEZONE_NOON) {   //(4)
        	out = out + "/昼";
        } else {
        	out = out + "/－";
        }

        if ((meddetailtimezone & TIMEZONE_NIGHT) == TIMEZONE_NIGHT) {   //(4)
        	out = out + "/夜";
        } else {
        	out = out + "/－";
        }

        if ((meddetailtimezone & TIMEZONE_BEFORE_BEDTIME) == TIMEZONE_BEFORE_BEDTIME) {   //(4)
        	out = out + "/就寝前";
        } else {
        	out = out + "/－";
        }
    	return out;
    }

    /**
     * @return meddetailtiming
     */
    public Integer getMedDetailTiming() {
       return meddetailtiming;
    }

    /**
     * @param context
     * @return meddetailtiming
     */
    public String getMedDetailTimingString(Context context) {
		String[] timing_lst = context.getResources().getStringArray(R.array.SpnItm_med3);
		return timing_lst[meddetailtiming];
    }

    /**
     * @return meddetailcomment
     */
    public String getMedDetailComment() {
       return meddetailcomment;
    }

    /**
     * @return meddetailalarm
     */
    public Integer getMedDetailAlarm() {
       return meddetailalarm;
    }

    /**
     * @return meddetailendday
     */
    public Long getMedDetailEndday() {
       return meddetailendday;
    }


    /**
     * @param id
     *            セットする id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param meddetaillinkno
     *            セットする meddetaillinkno
     */
    public void setMedDetailLinkno(Long meddetaillinkno) {
        this.meddetaillinkno = meddetaillinkno;
    }

    /**
     * @param meddetailshape
     *            セットする meddetailshape
     */
    public void setMedDetailShape(String meddetailshape) {
        this.meddetailshape = meddetailshape;
    }

    /**
     * @param meddetailunit
     *            セットする meddetailunit
     */
    public void setMedDetailUnit(String meddetailunit) {
        this.meddetailunit = meddetailunit;
    }

    /**
     * @param meddetailintake
     *            セットする meddetailintake
     */
    public void setMedDetailIntake(String meddetailintake) {
        this.meddetailintake = meddetailintake;
    }

    /**
     * @param meddetaildose
     *            セットする meddetaildose
     */
    public void setMedDetailDose(Integer meddetaildose) {
        this.meddetaildose = meddetaildose;
    }

    /**
     * @param meddetailtotal
     *            セットする meddetailtotal
     */
    public void setMedDetailTotal(Integer meddetailtotal) {
        this.meddetailtotal = meddetailtotal;
    }

    /**
     * @param meddetailetc
     *            セットする meddetailetc
     */
    public void setMedDetailEtc(Integer meddetailetc) {
        this.meddetailetc = meddetailetc;
    }

    /**
     * @param meddetailtimezone
     *            セットする meddetailtimezone
     */
    public void setMedDetailTimezone(Integer meddetailtimezone) {
        this.meddetailtimezone = meddetailtimezone;
    }

    /**
     * @param meddetailtiming
     *            セットする meddetailtiming
     */
    public void setMedDetailTiming(Integer meddetailtiming) {
        this.meddetailtiming = meddetailtiming;
    }

    /**
     * @param meddetailcomment
     *            セットする meddetailcomment
     */
    public void setMedDetailComment(String meddetailcomment) {
        this.meddetailcomment = meddetailcomment;
    }

    /**
     * @param meddetailalarm
     *            セットする meddetailalarm
     */
    public void setMedDetailAlarm(Integer meddetailalarm) {
        this.meddetailalarm = meddetailalarm;
    }

    /**
     * @param meddetailendday
     *            セットする meddetailendday
     * @return meddetailendday
     */
    public void setMedDetailEndday(Long meddetailendday) {
       this.meddetailendday = meddetailendday;
    }

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void setMedDetailAlarmMode(boolean mode){
		if(mode){
			setMedDetailAlarm(1);
		} else {
			setMedDetailAlarm(0);
		}

	}

}
