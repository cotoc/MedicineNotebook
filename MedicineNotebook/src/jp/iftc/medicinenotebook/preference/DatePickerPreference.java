package jp.iftc.medicinenotebook.preference;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerPreference extends DialogPreference {

	private DatePicker datePicker;
	
	public static final long INITIAL_VALUE = Long.valueOf("-2209021200000");

	public DatePickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public DatePickerPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected View onCreateDialogView() {
		// TODO 自動生成されたメソッド・スタブ
		this.datePicker = new DatePicker(this.getContext());
		long dateLong = Long.valueOf(this.getPersistedString("-2209021200000"));

		int nowYear;
		int nowMonth;
		int nowDay;

		Calendar c = Calendar.getInstance();
		nowYear = c.get(Calendar.YEAR);
		nowMonth = c.get(Calendar.MONTH);
		nowDay = c.get(Calendar.DAY_OF_MONTH);
		nowMonth += 1;
		
		if (dateLong < 0) {
			// SetDate

			dateLong = c.getTimeInMillis();
		}

		Date date = new Date(dateLong);
		datePicker.init(date.getYear() + 1900, 
						date.getMonth(), 
						date.getDate(), new OnDateChangedListener() {

			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				// TODO 自動生成されたメソッド・スタブ

			}
		});
		
		return this.datePicker;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// TODO 自動生成されたメソッド・スタブ
		if (positiveResult) {
			int year = datePicker.getYear();
			int month = datePicker.getMonth();
			int day = datePicker.getDayOfMonth();
			Date date = new Date(year - 1900, month, day);
			persistString(String.valueOf(date.getTime()));
			this.setSummary(String.format("%d/%d/%d", year, month + 1, day));
		}
		super.onDialogClosed(positiveResult);
	}

	public String getValue() {
		long dateLong = Long.valueOf(this.getPersistedString("-2209021200000"));
		Date date = new Date(dateLong);
		return String.format("%04d/%02d/%02d %02d:%02d:%02d",
				date.getYear() + 1900, date.getMonth() + 1, date.getDate(),
				date.getHours(), date.getMinutes(), date.getSeconds());
	}

}
