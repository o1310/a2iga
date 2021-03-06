// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.ComponentName;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.app.admin.DevicePolicyManager;
import android.text.Html;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.activities.MainActivity;
import ru.rx1310.app.a2iga.fragments.SettingsFragment;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import ru.rx1310.app.a2iga.services.OTAService;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    
	Toolbar oToolbar;
	EditText pkgNameDialogInput;
	CardView oUnsupportedApi22Card, oNotDefaultAssistCard;
	ImageView oAssistantAppIcon, oModuleIcon;
	String isAssistAppPkgName;
	TextView oAssistantAppName, oRandomPromt;
	FrameLayout oSettingsLayout;
	LinearLayout oCurrentAssistAppLayout, oBetaVersionInstalledMsgLayout, oModuleSettingsLayout;
	SharedPreferences oSharedPreferences;
	Intent oIntent;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		getFragmentManager().beginTransaction().replace(R.id.layoutSettings, new SettingsFragment()).commit();
	
		isAssistAppPkgName = SharedPrefUtils.getStringData(this, Constants.ASSIST_APP_PKGNAME);
		
		oIntent = getIntent();
		
		oSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		oSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		oModuleIcon = findViewById(R.id.iconModule);
		oSettingsLayout = findViewById(R.id.layoutSettings);
		
		/* ? Сообщение о статусе Beta
		 *   Отображается только если в versionName в build.gradle
		 *   обнаружена буква "b" */
		oBetaVersionInstalledMsgLayout = findViewById(R.id.toolbarBetaMessage);
		if (AppUtils.getVersionName(this, getPackageName()).contains("0.")) oBetaVersionInstalledMsgLayout.setVisibility(View.VISIBLE);
		else oBetaVersionInstalledMsgLayout.setVisibility(View.GONE);
		
		oToolbar = findViewById(R.id.toolbar);
		
		setSupportActionBar(oToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ? Включение кнопки Back в Toolbar
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logo); // ? Замена иконки Back в Toolbar на свою
		getSupportActionBar().setDisplayShowHomeEnabled(true); // ? Вязанное с setDisplayHomeAsUpEnabled
		getSupportActionBar().setTitle(R.string.activity_main); // ? Заголовок Toolbar
		
		oCurrentAssistAppLayout = findViewById(R.id.toolbarCurrentAssistAppLayout);
		oCurrentAssistAppLayout.setOnClickListener(this);
		
		oAssistantAppName = findViewById(R.id.name);
		oAssistantAppIcon = (ImageView) findViewById(R.id.icon);
		
		if (isAssistAppPkgName == null) {
			
			oAssistantAppName.setText(R.string.current_assistant_null);
			oAssistantAppIcon.setImageDrawable(getDrawable(R.drawable.ic_appslist));
			
		} else {
			
			// ? Отображение иконки индикатора модуля
			if (isAssistAppPkgName.contains("a2iga.module.")) oModuleIcon.setVisibility(View.VISIBLE);
			else oModuleIcon.setVisibility(View.INVISIBLE);
			
			oAssistantAppName.setText(AppUtils.getAppName(this, isAssistAppPkgName));
			
			try {
				Drawable drawable = getPackageManager().getApplicationIcon(isAssistAppPkgName);
				oAssistantAppIcon.setImageDrawable(drawable);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
				oAssistantAppIcon.setImageDrawable(getDrawable(android.R.mipmap.sym_def_app_icon));
			}
			
		}
		
		oNotDefaultAssistCard = findViewById(R.id.cardNotDefaultAssist);
		oNotDefaultAssistCard.setOnClickListener(this);
		oNotDefaultAssistCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS));
			}
		});
		
		oUnsupportedApi22Card = findViewById(R.id.cardUnsupportedApi22);
		
		if (Build.VERSION.SDK_INT < 22) {
			oUnsupportedApi22Card.setVisibility(View.VISIBLE);
			oSettingsLayout.setVisibility(View.VISIBLE);
		} else {
			oUnsupportedApi22Card.setVisibility(View.GONE);
			oSettingsLayout.setVisibility(View.GONE);
		}
		
		// ?  
		String isIntentAction = oIntent.getAction();
		String isIntentType = oIntent.getType();
		
		if (Intent.ACTION_SEND.equals(isIntentAction) && isIntentType != null || "text/plain".equals(isIntentType)) setAssistantAppFromIntent(oIntent);

    }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (AppUtils.getCurrentAssist(this) != null) {
			
			if (AppUtils.getCurrentAssist(this).getClassName().toString().contains("a2iga")) {
				oNotDefaultAssistCard.setVisibility(View.GONE);
				oSettingsLayout.setVisibility(View.VISIBLE);
			} 
			
		} else {
			oNotDefaultAssistCard.setVisibility(View.VISIBLE);
			oSettingsLayout.setVisibility(View.GONE);
		}
		
	}
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        
		if (key.equals(Constants.ASSIST_APP_PKGNAME)) {
            recreate();
        }

	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.toolbarCurrentAssistAppLayout:
				startActivity(new Intent(MainActivity.this, AppsListActivity.class));
				break;
				
			default: break;

		}

	}
	
	/* ? Обработка нажатия на иконку в Toolbar
	 *   Отображается диалог, в котором можно вручную
	 *   указать имя пакета приложения, которое будет
	 *   запускать a2iga при вызове ассистента */
	@Override
	public boolean onSupportNavigateUp() {
		
		//onBackPressed();
		
		pkgNameDialogInput = new EditText(MainActivity.this);
		pkgNameDialogInput.setHint(getString(R.string.current_assistant_pkgname_dialog_hint) + " " + isAssistAppPkgName);

		android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AppTheme_Dialog_Alert);

		b.setTitle(R.string.current_assistant_pkgname_dialog_title);
		b.setMessage(R.string.current_assistant_pkgname_dialog_message);
		b.setView(pkgNameDialogInput, 50, 0, 50, 0); // 50, 0, 50, 0 — отступы EditText слева/справа
		b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (pkgNameDialogInput.getText().toString().isEmpty()) dialog.dismiss();
				else {
					SharedPrefUtils.saveData(MainActivity.this, Constants.ASSIST_APP_PKGNAME, pkgNameDialogInput.getText().toString());
					AppUtils.showToast(MainActivity.this, getString(R.string.app_selected_as_assistant));
				}
			}
		});
		b.setNegativeButton(android.R.string.cancel, null);
		b.create();
		b.show();

		return true;
		
	}
    
	/* ? Отображаем AlertDialog при получении данных.
	 *   Код передачи данных из приложения в A2IGA:
	 *   ---
	 *   1. Intent sendPackageName = new Intent();
	 *   2. sendPackageName.setAction(Intent.ACTION_SEND);
	 *   3. sendPackageName.setClassName("ru.rx1310.app.a2iga", "ru.rx1310.app.a2iga.activities.MainActivity").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 *   4. sendPackageName.putExtra(Intent.EXTRA_TEXT, "com.android.settings"); // "com.android.settings" - имя пакета, которое принимает A2IGA
	 *   5. sendPackageName.setType("text/plain");
	 *   6. startActivity(Intent.createChooser(sendPackageName, "Select «A2IGA»!")); */
	void setAssistantAppFromIntent(Intent intent){

		final String pkgName = intent.getStringExtra(Intent.EXTRA_TEXT);

		android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AppTheme_Dialog_Alert);

		b.setTitle(R.string.assistant_from_intent_dialog);
		b.setMessage(Html.fromHtml(String.format(getString(R.string.assistant_from_intent_dialog_desc), AppUtils.getAppName(this, pkgName))));
		
		try {
			Drawable drawable = getPackageManager().getApplicationIcon(pkgName);
			b.setIcon(drawable);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		
		b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPrefUtils.saveData(MainActivity.this, Constants.ASSIST_APP_PKGNAME, pkgName);
				AppUtils.showToast(MainActivity.this, getString(R.string.app_selected_as_assistant));
				finish();
			}
		});
		
		b.setNegativeButton(android.R.string.cancel, null);
		b.create();
		b.show();
		
	}
	
}
