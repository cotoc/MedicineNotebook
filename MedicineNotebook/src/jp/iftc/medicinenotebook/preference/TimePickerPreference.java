package jp.iftc.medicinenotebook.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerPreference extends OriginalDialogPreference {
    //追加属性の名称
    private final static String STR_ATTR_DEFAULT_HOUR = "defaultHour";
    private final static String STR_ATTR_DEFAULT_MINUTE = "defaultMinute";
    private final static String STR_ATTR_IS_24HOUR = "is24Hour";
     
    //プリファレンス保存時のキー名の追加文字列
    private final static String STR_KEY_HOUR = "_Hour";     
    private final static String STR_KEY_MINUTE = "_Minute";
     
    private int _defaultHour = 0;           //設定値（時）
    private int _defaultMinute = 0;         //設定値（分）
    private boolean _is24HourView = false;  //設定値（２４時間表示）
     
    private TimePicker picker;
 
    /**
     * 24時間表示か
     * @param _is24HourView the _is24HourView to set
     */
    public void setIs24HourView(boolean _is24HourView) {
        this._is24HourView = _is24HourView;
    }
 
    /**
     * 24時間表示か
     * @return the _is24HourView
     */
    public boolean is24HourView() {
        return _is24HourView;
    }
 
     
    /**
     * コンストラクタ
     * @param context
     * @param attrs
     */
    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
 
        String temp;
         
        //時間を取得
        temp = attrs.getAttributeValue(null, STR_ATTR_DEFAULT_HOUR);
        if(temp != null){
            _defaultHour = Integer.valueOf(temp);
        }
        //分を取得
        temp = attrs.getAttributeValue(null, STR_ATTR_DEFAULT_MINUTE);
        if(temp != null){
            _defaultMinute = Integer.valueOf(temp);
        }
        //２４時間表示を取得
        temp = attrs.getAttributeValue(null, STR_ATTR_IS_24HOUR);
        if(temp == null){
        }else if(temp.toLowerCase().compareTo("true") != 0){
        }else{
            _is24HourView = true;           
        }
         
    }
 
    /**
     * 表示したときに呼ばれる
     */
    @Override
    protected void onBindView(View view) {
        //設定を読み込み
        SharedPreferences pref = getSharedPreferences();
        if(pref == null){
        }else{
            _defaultHour = pref.getInt(getKey() + STR_KEY_HOUR, _defaultHour);
            _defaultMinute = pref.getInt(getKey() + STR_KEY_MINUTE, _defaultMinute);
        }
 
        //サマリーに現在値を設定
        String summary = "";
        if(_is24HourView){
            summary = String.format("%s%02d:%02d", getDefaultSummary(), _defaultHour, _defaultMinute);
        }else{
            if(_defaultHour > 12){
                summary = String.format("%s%02d:%02d PM", getDefaultSummary(), _defaultHour - 12, _defaultMinute);
            }else{
                summary = String.format("%s%02d:%02d AM", getDefaultSummary(), _defaultHour, _defaultMinute);
            }
        }
        setSummary((CharSequence) (summary));
 
        //これはなぜか最後じゃないとイケないらしい
        super.onBindView(view);
    }
// 
//    /**
//     * プリファレンスのクリックイベント
//     */
//    @Override
//    protected void onClick(){
// 
//        //ダイアログ表示
//        picker = new TimePicker(getContext());
//        picker.setIs24HourView(_is24HourView);
//        picker.setCurrentHour(_defaultHour);
//        picker.setCurrentMinute(_defaultMinute);
//        showCustumDialog(getContext(), (String)getDialogTitle(), (String)getDialogMessage()
//                            , picker, new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // 設定保存
//                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putInt(getKey() + STR_KEY_HOUR, picker.getCurrentHour());
//                editor.putInt(getKey() + STR_KEY_MINUTE, picker.getCurrentMinute());
//                editor.commit();
//                 
//                //表示を更新
//                notifyChanged();
//            }
//        });
// 
//    }
    
    @Override
    protected View onCreateDialogView() {
        
        //ダイアログ表示
        picker = new TimePicker(getContext());
        picker.setIs24HourView(_is24HourView);
        picker.setCurrentHour(_defaultHour);
        picker.setCurrentMinute(_defaultMinute);

        return picker;
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
    	if (positiveResult) {
    		if(picker.hasFocus()){
    			picker.clearFocus();
    		}
    		
	        // 設定保存
	        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
	        SharedPreferences.Editor editor = pref.edit();
	        editor.putInt(getKey() + STR_KEY_HOUR, picker.getCurrentHour());
	        editor.putInt(getKey() + STR_KEY_MINUTE, picker.getCurrentMinute());
	        editor.commit();

	        //サマリーに現在値を設定
	        String summary = "";
	        if(_is24HourView){
	            summary = String.format("%s%02d:%02d", getDefaultSummary(), picker.getCurrentHour(), picker.getCurrentMinute());
	        }else{
	            if(picker.getCurrentHour() > 12){
	                summary = String.format("%s%02d:%02d PM", getDefaultSummary(), picker.getCurrentHour() - 12, picker.getCurrentMinute());
	            }else{
	                summary = String.format("%s%02d:%02d AM", getDefaultSummary(), picker.getCurrentHour(), picker.getCurrentMinute());
	            }
	        }
	        setSummary((CharSequence) (summary));    	
    	}
    	super.onDialogClosed(positiveResult);
    }
}
