/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package o1310.rx1310.app.a2iga;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class LaunchAssistant extends Activity {

	SharedPreferences sharedPrefs;
	SharedPreferences.Editor sharedPrefsEditor;
	
	final String PREF_ASSISTANT_PACKAGE_NAME = "assistantPackageName";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sharedPrefs = getSharedPreferences("a2iga_settings", MODE_PRIVATE);
		
		String assistantPackageName = sharedPrefs.getString(PREF_ASSISTANT_PACKAGE_NAME, "");
		
		startAssistantApp(assistantPackageName);
		
		this.finish();
		
	}
	
	public void startAssistantApp(String packageName) {
		
		Intent i = getPackageManager().getLaunchIntentForPackage(packageName);

		if (i == null) {
			i = new Intent(Intent.ACTION_VIEW);
			Toast.makeText(this, R.string.message_app_not_found, Toast.LENGTH_LONG).show();
			i.setData(Uri.parse("market://details?id=" + packageName));
		}

		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(i);
		
	}
	
}