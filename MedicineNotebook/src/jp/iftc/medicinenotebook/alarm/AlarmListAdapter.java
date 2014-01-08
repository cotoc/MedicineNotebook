/*
 * Copyright © 2011 Infotec Inc. All Rights Reserved.
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

package jp.iftc.medicinenotebook.alarm;

import java.util.ArrayList;

import jp.iftc.medicinenotebook.R;
import jp.iftc.medicinenotebook.db.MedNoteDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * @author 0A7044
 *
 * 1レコード分のリストデータを保持するクラス
 */
public class AlarmListAdapter extends BaseAdapter {
	/**
	 *
	 */
	@SuppressWarnings("unused")
	private static final String TAG = AlarmListAdapter.class.getSimpleName();
	private final AlarmListAdapter self = this;

    private LayoutInflater mLayoutInfo = null;
    private ArrayList<MedNoteDetail> mMedNoteDetail = null;
    private ViewHolder mHolder;

    private Context cnt;

    public AlarmListAdapter(Context context, ArrayList<MedNoteDetail> _mMedNoteDetail) {

        //LayoutInflaterを取得
        mLayoutInfo = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mMedNoteDetail = _mMedNoteDetail;
        cnt = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //assetlist.xmlから1行分のレイアウトを生成
            convertView = mLayoutInfo.inflate(R.layout.alarmlist_layout, parent, false);

            mHolder = new ViewHolder();

            //各idの項目に値をセット
//            mHolder.list_id = (TextView) convertView.findViewById(R.id.txtVw_list_id);
            mHolder.med_name   = (TextView) convertView.findViewById(R.id.txtVw_med_name);
            mHolder.med_dose   = (TextView) convertView.findViewById(R.id.txtVw_med_dose);
            mHolder.imgvw_list = (ToggleButton) convertView.findViewById(R.id.toggle_alermon);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

//        mHolder.list_id.setText(String.valueOf(mMedNoteDetail.get(position).getId()));
        mHolder.med_name.setText(mMedNoteDetail.get(position).getMedDetailShape());

        mHolder.med_dose.setText(mMedNoteDetail.get(position).getMedDetailDose()
                + mMedNoteDetail.get(position).getMedDetailDoseUnit(cnt) );

        mHolder.imgvw_list.setChecked((mMedNoteDetail.get(position).getMedDetailAlarm() == 1));

        return convertView;
    }

    public int getCount() {
        return mMedNoteDetail.size();
    }

    public Object getItem(int position) {
        return mMedNoteDetail.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean toggleAlarmMode(int position ){
        boolean alarmMode;
        if(mMedNoteDetail.get(position).getMedDetailAlarm() == 1){
            mMedNoteDetail.get(position).setMedDetailAlarm(0);
            alarmMode = false;
        } else {
            mMedNoteDetail.get(position).setMedDetailAlarm(1);
            alarmMode = true;
        }
        return alarmMode;
    }


    static class ViewHolder {
//        TextView list_id;
        TextView med_name;
        TextView med_dose;
        ToggleButton imgvw_list;
    }

}
