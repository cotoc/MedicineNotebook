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

package jp.iftc.medicinenotebook.fileio;

import jp.iftc.medicinenotebook.HistoryListActivity;
import jp.iftc.medicinenotebook.R;
import jp.iftc.medicinenotebook.fileio.FileImportTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author 0a6055
 *
 */
public class DataIOActivity extends Activity implements OnClickListener {
	private static final String TAG = DataIOActivity.class.getSimpleName();
	@SuppressWarnings("unused")
	private final DataIOActivity self = this;

    private Button btn_meddat;
    private Button btn_mednote;
    private Button btn_mednote_detail;
    private Button btn_end;

	/*
	 * (非 Javadoc)
	 *
	 * @see jp.iftc.androidasset.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataio_layout);

		btn_meddat = (Button) findViewById(R.id.btn_meddat);
		btn_mednote = (Button) findViewById(R.id.btn_mednote);
        btn_mednote_detail = (Button) findViewById(R.id.btn_mednote_detail);
        btn_end = (Button) findViewById(R.id.btn_end);

        btn_meddat.setOnClickListener(this);
        btn_mednote.setOnClickListener(this);
        btn_mednote_detail.setOnClickListener(this);
        btn_end.setOnClickListener(this);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
	    int DbNo = 0;
		// TODO 自動生成されたメソッド・スタブ
		switch (v.getId()) {
		case R.id.btn_meddat:
			// 処方薬データテーブル
			Log.v(TAG, "btn_meddat click!");
			DbNo=1;
			break;
        case R.id.btn_mednote:
            // お薬手帳情報テーブル
            Log.v(TAG, "btn_mednote click!");
            // データ取得タスクの実行
            DbNo=2;
            break;
        case R.id.btn_mednote_detail:
            // お薬手帳情報 明細テーブル
            Log.v(TAG, "btn_mednote_detail click!");
            // データ取得タスクの実行
            DbNo=3;
            break;
        case R.id.btn_end:
            // お薬手帳情報 明細テーブル
            Log.v(TAG, "btn_mednote_detail click!");
            // データ取得タスクの実行
            DbNo=0;
            break;
		default:
			break;
		}

		if (DbNo > 0){
	        FileImportTask inpTask = new FileImportTask(this,DbNo);
	        inpTask.execute();
		} else {
            Intent intent = new Intent(getApplicationContext(), HistoryListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
		}


	}
}
