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
package jp.iftc.medicinenotebook.preference;

import java.util.ArrayList;
import java.util.Arrays;

import jp.iftc.medicinenotebook.R;

import android.R.integer;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author 0A7044
 *
 */
public class SettingPreferenceActivity extends PreferenceActivity {
    @SuppressWarnings("unused")
    private static final String TAG = SettingPreferenceActivity.class.getSimpleName();
    private final SettingPreferenceActivity self = this;

    
    private String defaultSummary_alerm_befor;
    private String defaultSummary_alerm_after;
    private String defaultSummary_alerm_between;
    
    // テキストボックスPreferenceの PreferenceChangeリスナー
    private OnPreferenceChangeListener editTextPreference_OnPreferenceChangeListener =
        new OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return editTextPreference_OnPreferenceChange(preference,newValue);
            }};

            
    // テキストボックスPreferenceの PreferenceChangeリスナー
    private OnPreferenceChangeListener editTextPreference_Houer_OnPreferenceChangeListener =
        new OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return editTextPreference_Houer_OnPreferenceChange(preference,newValue);
            }};
                    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.apppreferences);

          // ListPreference の場合
//          ListPreference listPreference = (ListPreference)findPreference("alarm_sound");

		// // 項目の取得。 ArrayList と Arrayの変換
		// ArrayList<CharSequence> entriesList = new ArrayList<CharSequence>();
		// // 値の取得
		// ArrayList<CharSequence> entryValuesList = new
		// ArrayList<CharSequence>();
		//
		//
		// //アラーム音の一覧を取得
		// RingtoneManager ringtoneManager = new
		// RingtoneManager(getApplicationContext());
		// Cursor cursor = ringtoneManager.getCursor();
		// while (cursor.moveToNext()) {
		// Log.d("Ringtone", "TITLE: " +
		// cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
		// // 項目を追加する場合
		// entriesList.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
		// // 値を追加する場合
		// entryValuesList.add(String.valueOf(RingtoneManager.TITLE_COLUMN_INDEX));
		// }
		//
		// // ArrayList と Arrayの変換
		// CharSequence entries[] = entriesList.toArray(new CharSequence[]{});
		// CharSequence entryValues[] = entryValuesList.toArray(new
		// CharSequence[]{});
		//
		// // 各配列を再度当てはめる。
		// listPreference.setEntries(entries);
		// listPreference.setEntryValues(entryValues);
          
          EditTextPreference edit_alerm_befor = (EditTextPreference)findPreference("alerm_befor");
          EditTextPreference edit_alerm_after = (EditTextPreference)findPreference("alerm_after");
          EditTextPreference edit_alerm_between = (EditTextPreference)findPreference("alerm_between");

          edit_alerm_befor.setOnPreferenceChangeListener(editTextPreference_OnPreferenceChangeListener);
          edit_alerm_after.setOnPreferenceChangeListener(editTextPreference_OnPreferenceChangeListener);
          edit_alerm_between.setOnPreferenceChangeListener(editTextPreference_Houer_OnPreferenceChangeListener);
          
          defaultSummary_alerm_befor = (String) edit_alerm_befor.getSummary();
          defaultSummary_alerm_after = (String) edit_alerm_after.getSummary();
          defaultSummary_alerm_between = (String) edit_alerm_between.getSummary();
          
          SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
          String val = sharedPreferences.getString("alerm_befor", "30");
          edit_alerm_befor.setSummary(defaultSummary_alerm_befor + " " + val + "分");
          val = sharedPreferences.getString("alerm_after", "30");
          edit_alerm_after.setSummary(defaultSummary_alerm_after + " " + val + "分");
          val = sharedPreferences.getString("alerm_between", "2");
          edit_alerm_between.setSummary(defaultSummary_alerm_between + " " + val + "時間");
          
    }
    
    private boolean editTextPreference_OnPreferenceChange(Preference preference, Object newValue){
        String input = newValue.toString(); 

        if ( input != null && !input.equals("") && Integer.parseInt(input) > 0 && Integer.parseInt(input) < 60){
            
        	if( preference.getKey().equals("alerm_befor")) {
	        	//nullでなく1 ~ 60であれば要約を変更する
	            preference.setSummary(defaultSummary_alerm_befor + " "+ input + "分");
        	} else {
        	  	//nullでなく1 ~ 60であれば要約を変更する
	            preference.setSummary(defaultSummary_alerm_after + " " + input + "分");       		
        	}
            return true;
        } else {
            //nullまたは1 ~ 60以外はエラー
        	Toast.makeText(self, "入力した値は無効です。", Toast.LENGTH_LONG).show();
            return false;
        }  
    }

    private boolean editTextPreference_Houer_OnPreferenceChange(Preference preference, Object newValue){
        String input = newValue.toString(); 
        if ( input != null && !input.equals("") && Integer.parseInt(input) > 0 && Integer.parseInt(input) < 24){
            //nullでなく100以上であれば要約を変更する
            preference.setSummary(defaultSummary_alerm_between + input + "時間後");
            return true;
        } else {
            //nullまたは1 ~ 5以外はエラー
        	Toast.makeText(self, "入力した値は無効です。", Toast.LENGTH_LONG).show();
            return false;
        }  
    }


}
