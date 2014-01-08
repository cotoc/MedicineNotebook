package jp.iftc.medicinenotebook;

import java.util.ArrayList;
import java.util.List;

import jp.iftc.medicinenotebook.db.MedNote;
import jp.iftc.medicinenotebook.db.MedNoteDetail;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class HistoryExpandableListAdapter extends BaseExpandableListAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = HistoryExpandableListAdapter.class
			.getSimpleName();
	@SuppressWarnings("unused")
	private final HistoryExpandableListAdapter self = this;

	private final int groupHolderId = 1;
	private final int childHolderId = groupHolderId + 1;
	private final int childFooterHolderId = childHolderId + 1;

	private Context mContext;
	private Integer[] mImageid = { R.drawable.al, R.drawable.al_ng };

	private LayoutInflater mLayoutInfo = null;
	private ArrayList<List<MedNoteDetail>> mMedNoteDetail = null;
	private ArrayList<MedNote> mHistoryData = null;
	private HistoryViewHolder mGroupHolder;
	private DtailViewHolder mChildHolder;
	private DtailFooterViewHolder mDtailFooterHolder;

	private int list_mode;

	// コンストラクター
	public HistoryExpandableListAdapter(Context context,
			ArrayList<MedNote> _mHistoryData,
			ArrayList<List<MedNoteDetail>> _mMedNoteDetail,
			int mode) {
		mContext = context;

		// LayoutInflaterを取得
		mLayoutInfo = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mHistoryData = _mHistoryData;
		mMedNoteDetail = _mMedNoteDetail;
		list_mode = mode;
	}

	/**
	 *
	 * @return
	 */
	public View getGenericView() {
		// xmlをinflateしてViewを作成する
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.meddetailinfo_list_layout, null);
		return view;
	}

	public View getGroupGenericView() {
		// xmlをinflateしてViewを作成する
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.meddetailinfo_header_layout, null);

		return view;
	}

	@Override
	public MedNoteDetail getChild(int groupPosition, int childPosition) {
		return mMedNoteDetail.get(groupPosition).get(childPosition);
	}

	public ArrayList<MedNoteDetail> getChild(int groupPosition) {
		return (ArrayList<MedNoteDetail>) mMedNoteDetail.get(groupPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return childPosition;
	}

	public long getChildRowId(int groupPosition, int childPosition) {
		// お薬データのIDを返す
		return  mMedNoteDetail.get(groupPosition).get(childPosition).getId();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		Log.v(TAG, "getChildView() called" + groupPosition + ":" + childPosition );

		if (isLastChild) {
			// 最終行にお薬追加ボタンを挿入
			convertView = mLayoutInfo.inflate(R.layout.child_footer, parent,
						false);

			mDtailFooterHolder = new DtailFooterViewHolder();

//			final MedNote note = getGroup(groupPosition);
			final Long rowId = getGroupRowId(groupPosition);
			mDtailFooterHolder.btnMedAdd = (Button) convertView.findViewById(R.id.btn_med_add);
			mDtailFooterHolder.btnMedAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					Intent intent = new Intent(mContext, MedicineInputActivity.class);
//					intent.putExtra("MedNoteId",rowId);
//					mContext.startActivity(intent);

					Intent intent = new Intent(mContext, MedicineInputActivity.class);
					intent.putExtra("Mode", 2);
					intent.putExtra("MedNoteId",rowId);
					mContext.startActivity(intent);

				}
			});

		} else {
			if (convertView == null) {
				// assetlist.xmlから1行分のレイアウトを生成
				convertView = getGenericView();

				mChildHolder = new DtailViewHolder(convertView);

				convertView.setTag(mChildHolder);

			} else if((TextView) convertView
					.findViewById(R.id.txtVw_list_id) == null) {
				// assetlist.xmlから1行分のレイアウトを生成
				convertView = getGenericView();

				mChildHolder = new DtailViewHolder(convertView);
				convertView.setTag(mChildHolder);

			} else {
				mChildHolder = (DtailViewHolder) convertView.getTag();
			}

//			mChildHolder.list_id.setText(String.valueOf(mMedNoteDetail
//					.get(groupPosition).get(childPosition).getId()));

			mChildHolder.list_id.setText("No." + String.valueOf(childPosition + 1));

			mChildHolder.med_name.setText(String.valueOf(mMedNoteDetail
					.get(groupPosition).get(childPosition).getMedDetailShape()));

			if( mMedNoteDetail
					.get(groupPosition).get(childPosition).getMedDetailEtc() != 1){
				mChildHolder.imgvw_list.setChecked(true);
				mChildHolder.imgvw_list.setVisibility(View.VISIBLE);
//				mChildHolder.imgvw_list.setFocusable(true);
				mChildHolder.imgvw_list.setChecked((mMedNoteDetail
						.get(groupPosition).get(childPosition).getMedDetailAlarm() == 1) );
				mChildHolder.med_timezone.setText(mMedNoteDetail.get(groupPosition).get(childPosition).getMedDetailTimezoneString());

			} else {
				mChildHolder.imgvw_list.setChecked(false);
				mChildHolder.imgvw_list.setVisibility(View.INVISIBLE);
//				mChildHolder.imgvw_list.setFocusable(false);
				mChildHolder.med_timezone.setText("[その他]" + mMedNoteDetail.get(groupPosition).get(childPosition).getMedDetailComment());

			}
//			mChildHolder.med_timezone.setText(mMedNoteDetail.get(groupPosition).get(childPosition).getMedDetailTimezoneString());
			mChildHolder.med_timing.setText(mMedNoteDetail
					.get(groupPosition).get(childPosition).getMedDetailTimingString(mContext));
			mChildHolder.med_dose.setText(String.valueOf(mMedNoteDetail
					.get(groupPosition).get(childPosition).getMedDetailDose()) +
					mMedNoteDetail
                    .get(groupPosition).get(childPosition).getMedDetailDoseUnit(mContext));
			mChildHolder.med_total.setText(String.valueOf(mMedNoteDetail
					.get(groupPosition).get(childPosition).getMedDetailTotal()) + "日分");

//			mChildHolder.chkbx.setChecked(mMedNoteDetail
//					.get(groupPosition).get(childPosition).isChecked());
//
//			final int gid = groupPosition, cid = childPosition;
//			final MedNoteDetail detail = mMedNoteDetail.get(gid).get(cid);
//			final CheckBox chkbox = mChildHolder.chkbx;
//			chkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////					ExpandableListView list = (ExpandableListView) buttonView.getParent().getParent().getParent().getParent();
////					HistoryExpandableListAdapter adapter = (HistoryExpandableListAdapter) list.getAdapter();
////					MedNoteDetail detail = mMedNoteDetail.get(gid).get(cid);
//					detail.setChecked(isChecked);
//
//				}
//			});

			if( list_mode == 1 ){
				//削除モード
				mChildHolder.chkbx.setVisibility(View.VISIBLE);
			} else {
				mChildHolder.chkbx.setVisibility(View.INVISIBLE);
			}
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mMedNoteDetail.get(groupPosition).size() + 1;
	}

	@Override
	public MedNote getGroup(int groupPosition) {
		return mHistoryData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mHistoryData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	public long getGroupRowId(int groupPosition) {
		return mHistoryData.get(groupPosition).getId();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			// assetlist.xmlから1行分のレイアウトを生成
			convertView = mLayoutInfo.inflate(
					R.layout.meddetailinfo_header_layout, parent, false);

			mGroupHolder = new HistoryViewHolder(convertView);

			convertView.setTag(mGroupHolder);

		} else {
			mGroupHolder = (HistoryViewHolder) convertView.getTag();
		}

		mGroupHolder.txtvw_date.setText(mHistoryData.get(groupPosition).getMedNoteDateFormat(mContext));
		mGroupHolder.txtvw_Hospnametxt.setText(String.valueOf(mHistoryData.get(
				groupPosition).getMedNoteHospname()));
		mGroupHolder.txtvw_Pharnametxt.setText(String.valueOf(mHistoryData.get(
				groupPosition).getMedNotePharname()));

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	static class HistoryViewHolder {
		TextView txtvw_date;
		TextView txtvw_Hospnametxt;
		TextView txtvw_Pharnametxt;

		public HistoryViewHolder(View convertView) {
			// 各idの項目に値をセット
			txtvw_date = (TextView) convertView
					.findViewById(R.id.txtvw_date);
			txtvw_Hospnametxt = (TextView) convertView
					.findViewById(R.id.txtvw_Hospnametxt);
			txtvw_Pharnametxt = (TextView) convertView
					.findViewById(R.id.txtvw_Pharnametxt);

		}
	}

	static class DtailViewHolder {
		TextView list_id;
		TextView med_name;
		ToggleButton imgvw_list;
		TextView med_timezone;
		TextView med_timing;
		TextView med_dose;
		TextView med_total;
		CheckBox chkbx;

		public DtailViewHolder( View convertView) {
			// 各idの項目に値をセット
			list_id = (TextView) convertView
					.findViewById(R.id.txtVw_list_id);
			med_name = (TextView) convertView
					.findViewById(R.id.txtVw_med_name);
			imgvw_list = (ToggleButton) convertView
					.findViewById(R.id.toggle_alermon);
			med_timezone = (TextView) convertView
					.findViewById(R.id.txtVw_med_timezone);
			med_timing = (TextView) convertView
					.findViewById(R.id.txtVw_med_timing);
			med_dose = (TextView) convertView
					.findViewById(R.id.txtVw_med_dose);
			med_total = (TextView) convertView
					.findViewById(R.id.txtVw_med_total);
			chkbx = (CheckBox) convertView
					.findViewById(R.id.chkDelete);

		}
	}
	static class DtailFooterViewHolder {
		Button btnMedAdd;
	}

	public void removeChild(int groupPosition, int childPosition){
		mMedNoteDetail.get(groupPosition).remove(childPosition);
	}

	public void removeGrpoup(int groupPosition){
		mHistoryData.remove(groupPosition);
		mMedNoteDetail.remove(groupPosition);

	}

	public void setChildChecked(int groupPosition, int childPosition, boolean checked){
		mMedNoteDetail.get(groupPosition).get(childPosition).setChecked(checked);
	}

	public void setAlarmMode(int groupPosition, int childPosition, boolean checked){
		mMedNoteDetail.get(groupPosition).get(childPosition).setMedDetailAlarmMode(checked);
	}

	public boolean toggleAlarmMode(int groupPosition, int childPosition){
		boolean alarmMode;
		if(mMedNoteDetail.get(groupPosition).get(childPosition).getMedDetailAlarm() == 1){
			mMedNoteDetail.get(groupPosition).get(childPosition).setMedDetailAlarm(0);
			alarmMode = false;
		} else {
			mMedNoteDetail.get(groupPosition).get(childPosition).setMedDetailAlarm(1);
			alarmMode = true;
		}
		return alarmMode;
	}

	public void addGroup(MedNote medNote, List<MedNoteDetail> medNoteDitail){
		addGroup(medNote);
		addChild(medNoteDitail);
	}

	public void addGroup(MedNote medNote){
		mHistoryData.add(0, medNote);
	}

	public void addChild(List<MedNoteDetail> medNoteDitail){
		mMedNoteDetail.add(0, medNoteDitail);
	}
}
