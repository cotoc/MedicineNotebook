/*
 * Copyright © 2012 Infotec Inc. All Rights Reserved.
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

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * SDカードストレージの状態を確認する
 *
 * @author 0a6055
 *
 */
public class CheckStorageState {
	@SuppressWarnings("unused")
	private static final String TAG = CheckStorageState.class.getSimpleName();
	private final CheckStorageState self = this;

	private Context mContext;

	public static final int AVAILABLE = 0;
	public static final int NOT_AVAILABLE = 1;
	public static final int READ_ONLY = 2;

	public CheckStorageState(Context context){
		mContext = context;
	}

	/**
	 * SDカードの状態を確認する
	 * 状態の詳細を、Toastで表示する
	 * @return	：TRUE:使用可能/FALSE:使用不可能
	 *
	 */
	public int checkState(){

		String status = Environment.getExternalStorageState();
		int result = NOT_AVAILABLE;

		if (status.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
//		    Toast.makeText(context,
//		            "SDカードが装着されている",
//		            Toast.LENGTH_LONG).show();
			result = AVAILABLE;
		    //この状態が返ってきた場合は、読み書きが可能です。
		}
		else if (status.equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY)){
		    Toast.makeText(mContext,
		            "SDカードが装着されていますが、読み取り専用・書き込み不可です",
		            Toast.LENGTH_LONG).show();
		    result = READ_ONLY;
		}
		else if (status.equalsIgnoreCase(Environment.MEDIA_REMOVED)){
		    Toast.makeText(mContext,
		            "SDカードが装着されていません",
		            Toast.LENGTH_LONG).show();
		}
		else if (status.equalsIgnoreCase(Environment.MEDIA_SHARED)){
		    Toast.makeText(mContext,
		            "SDカードが装着されていますが、USBストレージとしてPCなどに" +
		            "マウント中です", Toast.LENGTH_LONG).show();
		}
		else if (status.equalsIgnoreCase(Environment.MEDIA_BAD_REMOVAL)){
		    Toast.makeText(mContext,
		            "SDカードのアンマウントをする前に、取り外しました",
		            Toast.LENGTH_LONG).show();
		}
		else if (status.equalsIgnoreCase(Environment.MEDIA_CHECKING)){
		    Toast.makeText(mContext,
		            "SDカードのチェック中です",
		            Toast.LENGTH_LONG).show();
		}
		else if (status.equalsIgnoreCase(Environment.MEDIA_NOFS)){
		    Toast.makeText(mContext,
		            "SDカードは装着されていますが、ブランクであるか、" +
		            "またはサポートされていないファイルシステムを利用しています",
		            Toast.LENGTH_LONG).show();
		}
		else if (status.equalsIgnoreCase(Environment.MEDIA_UNMOUNTABLE)){
		    Toast.makeText(mContext,
		            "SDカードは装着されていますが、マウントすることができません",
		            Toast.LENGTH_LONG).show();
		}
		else if (status.equalsIgnoreCase(Environment.MEDIA_UNMOUNTED)){
		    Toast.makeText(mContext,
		            "SDカードは存在していますが、マウントすることができません",
		            Toast.LENGTH_LONG).show();
		}
		else{
		    Toast.makeText(mContext,
		            "その他の要因で利用不可能",
		            Toast.LENGTH_LONG).show();
		}

		return result;
	}


}
