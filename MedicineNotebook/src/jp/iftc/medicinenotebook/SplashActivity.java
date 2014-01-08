package jp.iftc.medicinenotebook;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.support.v4.app.NavUtils;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		Handler hdl = new Handler();

		// スプラッシュスクリーンの表示時間は1000ミリ秒
		hdl.postDelayed(new splashHandler(), 3000);

	}

	class splashHandler implements Runnable {
		public void run() {
			// スプラッシュスクリーンの後に表示されるActivityを指定
			Intent i = new Intent(getApplication(), HistoryListActivity.class);
			startActivity(i);
			SplashActivity.this.finish();
		}
	}
    

    
}
