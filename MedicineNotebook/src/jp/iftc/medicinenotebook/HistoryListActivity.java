package jp.iftc.medicinenotebook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.iftc.medicinenotebook.alarm.MyAlarmUtil;
import jp.iftc.medicinenotebook.db.MED_INFODbOpenHelper;
import jp.iftc.medicinenotebook.db.MedNote;
import jp.iftc.medicinenotebook.db.MedNoteDAO;
import jp.iftc.medicinenotebook.db.MedNoteDetail;
import jp.iftc.medicinenotebook.db.MedNoteDetailDAO;
import jp.iftc.medicinenotebook.preference.MyPreferenceUtil;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

/**
 * @author 0a6055
 *
 */
/**
 * @author 0a6055
 *
 */
public class HistoryListActivity extends ExpandableListActivity {
	@SuppressWarnings("unused")
	private static final String TAG = HistoryListActivity.class.getSimpleName();
	private final HistoryListActivity self = this;

	private static final int LIST_MODE_NOMAL = 0;
	private static final int LIST_MODE_DELETE = 1;


	HistoryExpandableListAdapter mExpAdapter;
	MyPreferenceUtil mMypreUtil;
    MyDateTimeUtil mMyDateTimeUtil;
    MyAlarmUtil mMyAlarmUtil;

	private int listMode = LIST_MODE_NOMAL;

    private boolean mAlarmFlag;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExpandableListView listView = getExpandableListView();

		LayoutInflater infalter = getLayoutInflater();
		ViewGroup header = (ViewGroup) infalter.inflate(
				R.layout.list_header_dataadd, listView, false);
		TextView txtAdd = (TextView) header.findViewById(R.id.text_head_add);
		txtAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 処方箋登録画面を表示
				Intent intent = new Intent(getApplicationContext(), PrescInputActivity.class);
	            startActivity(intent);
			}
		});

//		listView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
		listView.setItemsCanFocus(false);
		listView.addHeaderView(header, null, false);

		// 子ノードのリスト
		// 親ノードのリストにもう1個リストをかぶせた感じ
		ArrayList<List<MedNoteDetail>> medNoteDetail = new ArrayList<List<MedNoteDetail>>();
		// 親ノードのリスト
		ArrayList<MedNote> medNoteList = new ArrayList<MedNote>();

		//DBからデータを読み込む
		// 処方箋データ
		medNoteList = loadMedNoteList();
		// お薬データ
		medNoteDetail = loadMedNoteDetailList(medNoteList);

		mExpAdapter = new HistoryExpandableListAdapter(
				this, medNoteList, medNoteDetail, listMode);

		// アダプタを設定
		setListAdapter(mExpAdapter);
		// コンテキストメニュー登録
        registerForContextMenu(listView);

        // OnChildClickListenerを設定
		listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			/** 子がクリックされたときに呼ばれるアレ. */
			@SuppressWarnings("unchecked")
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				boolean alarmMode = false;
				// まずはAdapterを取得
				HistoryExpandableListAdapter adapter = (HistoryExpandableListAdapter) parent.getExpandableListAdapter();
				MedNoteDetail detail = adapter.getChild(groupPosition, childPosition);
				if(listMode == LIST_MODE_NOMAL){
					alarmMode = adapter.toggleAlarmMode(groupPosition, childPosition);
					updateAlarmMode(alarmMode, detail);
				} else {
					//削除モードの処理を作る予定
				}
				adapter.notifyDataSetChanged();

				getExpandableListView().invalidateViews();

//				Toast.makeText(getApplicationContext(), "onChildClick", Toast.LENGTH_LONG).show();
				return false;
			}
		});

	}

    @Override
    protected void onStart() {
        super.onStart();
        // Log.w(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//      Log.w(TAG, "onRestart");

    }

    @Override
    protected void onResume() {
        super.onResume();

        mMypreUtil = new MyPreferenceUtil(self);
        mAlarmFlag = mMypreUtil.getAlarmFlag();
    }

	// onPause → AlarmManager にアラームを登録する場所
    @Override
    protected void onPause() {
        super.onPause();
        mMyAlarmUtil = new MyAlarmUtil();

        mMyAlarmUtil.SetAlarmManager(self,mAlarmFlag);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Log.w(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//         Log.w(TAG, "onDestroy");
    }


	/**
	 * DBから処方箋データを読み込む
	 * @return
	 */
	private ArrayList<MedNote> loadMedNoteList(){

		ArrayList<MedNote> medNoteList = null;

		MedNoteDAO dao = new MedNoteDAO(getApplicationContext());

		medNoteList = (ArrayList<MedNote>) dao.list_desc();

		return medNoteList;
	}


	/**
	 * DBから処方薬データを読み込む
	 * @return
	 */
	private ArrayList<List<MedNoteDetail>> loadMedNoteDetailList(ArrayList<MedNote> medNoteList){

		if(medNoteList.isEmpty()){
			return new ArrayList<List<MedNoteDetail>>();
		}

		ArrayList<List<MedNoteDetail>> medNoteDetail = new ArrayList<List<MedNoteDetail>>();

		MedNoteDetailDAO dao = new MedNoteDetailDAO(getApplicationContext());

		for(int i = 0; i < medNoteList.size(); i++){
			List<MedNoteDetail> medDitail;
			medDitail  = dao.loadDetailListById(medNoteList.get(i).getId());
			if(medDitail != null){
				medNoteDetail.add(medDitail);
			}
		}

//		medNoteDetail = dao.getExpandableList();

		return medNoteDetail;
	}

	/*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     * メニュー生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	super.onCreateOptionsMenu(menu);
    	getMenuInflater().inflate(R.menu.menu, menu);

    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        int itemid = item.getItemId();
		switch (itemid) {
//		case R.id.menu_history:		//履歴表示
//			Log.v(TAG,"履歴表示 click!");
//            intent = new Intent(getApplicationContext(), HistoryDataActivity.class);
//            startActivity(intent);
//            break;
//		case R.id.menu_input_medicine:		//お薬追加
//			//処方箋登録画面
//			Log.v(TAG,"menu_input_medicine　click!");
//
//			intent = new Intent(getApplicationContext(), MedicineInputActivity.class);
//            startActivity(intent);
//			break;
//		case R.id.menu_delete:		//削除
//			Log.v(TAG,"削除 click!");
//			deleteSelectedDetail();
//
//	        break;

		case R.id.menu_input:		//処方箋登録
			//処方箋登録画面
			Log.v(TAG,"menu_input　click!");

			intent = new Intent(getApplicationContext(), PrescInputActivity.class);
            startActivity(intent);
			break;
		case R.id.menu_setting:		//設定
			//設定画面
            intent = new Intent(getApplicationContext(), jp.iftc.medicinenotebook.preference.SettingPreferenceActivity.class);
            startActivityForResult(intent, 0);
			Log.v(TAG,"btn_assetlist click!");
			break;
		case R.id.menu_exit:		//終了
			Log.v(TAG,"終了 click!");
			moveTaskToBack(true);
			break;
        case R.id.menu_import:       //データ取り込み
            Log.v(TAG,"menu_import　click!");

            intent = new Intent(getApplicationContext(), jp.iftc.medicinenotebook.fileio.DataIOActivity.class);
            startActivity(intent);
            break;
        case R.id.menu_about_this:       //バージョン情報
            Log.v(TAG,"menu_about_this　click!");

			LayoutInflater inflater = LayoutInflater.from(this);
			View layout = inflater.inflate(R.layout.about_this,
					(ViewGroup) findViewById(R.id.layout_root));

			AboutThisDialog dialog = new AboutThisDialog(this, android.R.style.Theme_Panel);
			dialog.setView(layout);
			dialog.show();


            break;

        default:
			Log.v(TAG,"else click!?");
			break;
		}
		return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	// TODO Auto-generated method stub
    	super.onCreateContextMenu(menu, v, menuInfo);

		ExpandableListContextMenuInfo menu_info = (ExpandableListContextMenuInfo) menuInfo;
		int type = ExpandableListView.getPackedPositionType(menu_info.packedPosition);
		final int grouppos = ExpandableListView.getPackedPositionGroup(menu_info.packedPosition);
        final int childpos = ExpandableListView.getPackedPositionChild(menu_info.packedPosition);

		// Only create a context menu for the child
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

	        menu.setHeaderTitle("レコード編集");
	        menu.add("編集").setOnMenuItemClickListener(new OnMenuItemClickListener(){
	            @Override
	            public boolean onMenuItemClick(MenuItem arg0) {
	                Intent intent = new Intent(self,MedicineInputActivity.class);
                    // インテントに値をセット
                    intent.putExtra("Mode", 1);
                    Long rowId = getDitailRowId(grouppos, childpos);
                    intent.putExtra("MedNoteDetailId", rowId );
	                startActivity(intent);
	                return true;
	            }
	        });
	        menu.add("削除").setOnMenuItemClickListener(new OnMenuItemClickListener(){
	            @Override
	            public boolean onMenuItemClick(MenuItem arg0) {

	            	if(! deleteDetail(grouppos, childpos)){
		                Toast.makeText(self,"削除に失敗しました", Toast.LENGTH_LONG).show();
	            	} else {
		                Toast.makeText(self,"削除しました。", Toast.LENGTH_LONG).show();
	            	}

	            	getExpandableListView().setSelectedGroup(grouppos);
	                return false;
	            }
	        });
		} else {
			menu.setHeaderTitle("レコード編集");
			menu.add("編集").setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem arg0) {
					Intent intent = new Intent(self, PrescInputActivity.class);
					// 　インテントに値をセット
					intent.putExtra("Mode", 1);
					Long rowId = getRowId(grouppos);
                    intent.putExtra("MedNoteId",rowId);
					startActivity(intent);
					return true;
				}
			});
			menu.add("削除").setOnMenuItemClickListener( new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem arg0) {

					deleteMedNote(grouppos);

//							Toast.makeText(self, "削除は実装してません。", Toast.LENGTH_LONG)
//									.show();
					return false;
				}
			});
			menu.add("コピー").setOnMenuItemClickListener( new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem arg0) {

					// 元データをコピー
					long newId = copyMedNote(grouppos);

					if(newId != 0){
						Intent intent = new Intent(self,
										PrescInputActivity.class);
						// 　インテントに値をセット
						intent.putExtra("Mode", 1);
	                    intent.putExtra("MedNoteId",newId);
						startActivity(intent);
					}
						return false;
				}
			});
		}

    }

    /*
     * お薬データを削除する
     */
    private boolean deleteDetail(int groupPosition, int childPosition){
		 // まずはAdapterを取得
    	HistoryExpandableListAdapter adapter = (HistoryExpandableListAdapter) getExpandableListAdapter();

		MedNoteDetail medNoteDetail = (MedNoteDetail) adapter.getChild(groupPosition, childPosition);

		MedNoteDetailDAO dao = new MedNoteDetailDAO(getApplicationContext());

		boolean ret = dao.delete(medNoteDetail);

		adapter.removeChild(groupPosition, childPosition);

		adapter.notifyDataSetChanged();

		getExpandableListView().invalidateViews();

		return ret;

    }

    private boolean deleteSelectedDetail(){
    	boolean ret = true;
    	int selectedCount = 0;
    	int deleteCount = 0;
    	HistoryExpandableListAdapter adapter = (HistoryExpandableListAdapter) getExpandableListAdapter();

    	List<MedNoteDetail> selectedDetail = new ArrayList<MedNoteDetail>();

    	ExpandableListView list = getExpandableListView();
    	List<SelectedDitail> selectedList = new ArrayList<HistoryListActivity.SelectedDitail>();

    	for(int i = 0 ; i < adapter.getGroupCount(); i ++){
    		//開いているGroupだったら、Childのチェックボックスを確認
    		if( list.isGroupExpanded(i) ){
    			for(int j = 0; j < adapter.getChildrenCount(i) - 1; j ++){
//    				long packedPosition = ExpandableListView.getPackedPositionForChild(i, j);
//    				int id = ExpandableListView.getPackedPositionChild(packedPosition);

    				MedNoteDetail detail = adapter.getChild(i, j);

    				if(detail.isChecked()){
    					selectedCount = selectedCount + 1;
    					ret = deleteDetail(i, j);

    					if( ret ){
    						deleteCount = deleteCount + 1;
    						selectedDetail.add(detail);
    						adapter.setChildChecked(i, j, false);
//    						adapter.removeChild(i, j);
    					}

//    					SelectedDitail selectedItem = new SelectedDitail(i, j, adapter.getChild(i, j).getId());
//    					selectedList.add(selectedItem);
    				}
    			}
    		}
    	}

    	if (selectedCount == 0) {
    		Toast.makeText(self,"データが選択されていません。", Toast.LENGTH_LONG).show();
    	} else if(deleteCount > 0 && deleteCount == selectedCount){
    		Toast.makeText(self, String.valueOf(deleteCount) + "件のデータを削除しました。", Toast.LENGTH_LONG).show();

    	} else if (deleteCount < selectedCount) {
    		Toast.makeText(self, String.valueOf(selectedCount) + "中　" + String.valueOf(deleteCount) + "件のデータを削除しました。\n" + String.valueOf(selectedCount - deleteCount) + "の削除は失敗しました。", Toast.LENGTH_LONG).show();

    	} else{
    		Toast.makeText(self, "削除に失敗しました。", Toast.LENGTH_LONG).show();
    	}

		adapter.notifyDataSetChanged();

		getExpandableListView().invalidateViews();

//		if(!selectedDetail.isEmpty()){
//			MedNoteDetailDAO dao = new MedNoteDetailDAO(getApplicationContext());
//			ret = dao.deleteList(selectedDetail);
//			if(ret){
//
//				for(int i = 0; i < selectedList.size(); i ++){
//					adapter.setChildChecked(selectedList.get(i).groupid, selectedList.get(i).childid, false);
//					adapter.removeChild(selectedList.get(i).groupid, selectedList.get(i).childid);
//				}
//
//				adapter.notifyDataSetChanged();
//
//				getExpandableListView().invalidateViews();
//
//	    		Toast.makeText(self, selectedDetail.size() + "件のデータを削除しました。", Toast.LENGTH_LONG).show();
//			} else {
//	    		Toast.makeText(self,"削除に失敗しました。", Toast.LENGTH_LONG).show();
//			}
//    	} else {
//    		Toast.makeText(self,"データが選択されていません。", Toast.LENGTH_LONG).show();
//    	}
    	return ret;
    }

    private long getDitailRowId(int groupPosition, int childPosition){
    	HistoryExpandableListAdapter adapter = (HistoryExpandableListAdapter) getExpandableListAdapter();

    	return adapter.getChildRowId(groupPosition, childPosition);
    }

    private long getRowId(int groupPosition){
    	HistoryExpandableListAdapter adapter = (HistoryExpandableListAdapter) getExpandableListAdapter();

    	return adapter.getGroupRowId(groupPosition);
    }

    private boolean updateAlarmMode(boolean alarmMode, MedNoteDetail medNoteDetail){
    	boolean ret = false;
    	MedNoteDetail result = null;

    	if(medNoteDetail == null) return ret;

    	if(medNoteDetail.getMedDetailEtc() == 1) return ret;

    	medNoteDetail.setMedDetailAlarmMode(alarmMode);

		MedNoteDetailDAO dao = new MedNoteDetailDAO(getApplicationContext());

		result = dao.save(medNoteDetail);

		if(result != null){
			Toast.makeText(self, "アラーム設定を更新しました。", Toast.LENGTH_LONG).show();
			ret = true;
		} else {
			Toast.makeText(self, "アラーム設定の更新に失敗しました。", Toast.LENGTH_LONG).show();

		}

		return ret;
    }

    private boolean deleteMedNote(int groupPosition){
    	HistoryExpandableListAdapter adapter = (HistoryExpandableListAdapter) getExpandableListAdapter();

		MedNote medNote = adapter.getGroup(groupPosition);

		MedNoteDAO dao = new MedNoteDAO(getApplicationContext());

		boolean ret = dao.deleteMedNote(medNote);

		if(ret){
			Toast.makeText(self, "選択した処方箋データを削除しました。", Toast.LENGTH_LONG)
			.show();
		} else {
			Toast.makeText(self, "処方箋データの削除に失敗しました。", Toast.LENGTH_LONG)
			.show();
		}


		adapter.removeGrpoup(groupPosition);

		adapter.notifyDataSetChanged();

		getExpandableListView().invalidateViews();

		return ret;
    }

    private long copyMedNote(int groupPosition){
    	boolean ret = false;
    	long newId = 0;
    	HistoryExpandableListAdapter adapter = (HistoryExpandableListAdapter) getExpandableListAdapter();

		MedNote medNote = adapter.getGroup(groupPosition);
		ArrayList<MedNoteDetail> medNoteDitail = adapter.getChild(groupPosition);


		MedNote medNoteRet = null;
		ArrayList<MedNoteDetail> medNoteDitailRet = null;

		MED_INFODbOpenHelper helper = new MED_INFODbOpenHelper(getApplicationContext());
        SQLiteDatabase db;

        try {
            db = helper.getWritableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return newId;
        }

        // トランザクション開始
        db.beginTransaction();

        try{
	        // お薬手帳データ コピー
	        MedNoteDAO meddao = new MedNoteDAO(getApplicationContext());
	        medNoteRet = meddao.copy(medNote, db);
	        if(medNoteRet == null){
	        	throw new Exception();
	        }

	        // お薬データコピー
	        MedNoteDetailDAO ditaildao = new MedNoteDetailDAO(getApplicationContext());
	        medNoteDitailRet = ditaildao.copyMedNoteList(medNoteDitail, medNoteRet.getId(), db);
	        if(medNoteDitailRet == null){
	        	throw new Exception();
	        }
	        // トランザクション完了
	        else {
	        	newId = medNoteRet.getId();
	        	db.setTransactionSuccessful();
	        }

        } catch (Exception e) {

        	Log.w(TAG, "MedNote Copy Error!");
        } finally{
	    	db.endTransaction();
	        db.close();
        }

        adapter.addGroup(medNoteRet, medNoteDitailRet);

		adapter.notifyDataSetChanged();

		getExpandableListView().invalidateViews();

		return newId;
    }


    static class SelectedDitail {
    	int groupid;
    	int childid;
    	Long recordid;

    	public SelectedDitail(int gid, int cid, Long rid) {
    		groupid = gid;
    		childid = cid;
    		recordid = rid;

		}
    }
}
