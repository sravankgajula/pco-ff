package net.udsholt.peytz.firmafon;

// Docs on fragments wit support library
// http://developer.android.com/training/basics/fragments/creating.html

import java.util.ArrayList;

import net.udsholt.peytz.firmafon.api.RestApi;
import net.udsholt.peytz.firmafon.api.ApiExpception;
import net.udsholt.peytz.firmafon.async.AsyncWorker;
import net.udsholt.peytz.firmafon.async.IAsyncTask;
import net.udsholt.peytz.firmafon.domain.Reception;
import net.udsholt.peytz.firmafon.ui.ReceptionAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener, OnItemClickListener
{
	public final static String LOG_TAG = "peytzff";
	
	protected ReceptionAdapter listAdapter;
	protected RestApi firmafon;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        
        this.listAdapter = new ReceptionAdapter(this, R.layout.row_reception);
        
        ListView listView = (ListView) this.findViewById(R.id.list_receptions);
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(this);
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        
        // Setup firmafon api
        this.firmafon = new RestApi();
        this.firmafon.setBaseUrl(this.firmafon.defaultBaseUrl);
        this.firmafon.setAppKey(preferences.getString("firmafonAppKey", ""));
        this.firmafon.setUserKey(preferences.getString("firmafonUserKey", ""));
        
        if (preferences.getBoolean("enableDebug", false)) {
        	this.firmafon.setBaseUrl(preferences.getString("debugBaseUrl", ""));
        	this.firmafon.setAppKey(preferences.getString("debugAppKey", ""));
            this.firmafon.setUserKey(preferences.getString("debugUserKey", ""));
        }
    	
        this.updateReceptions();
    }
    
    protected void updateReceptions()
    {
    	this.updateReceptions(null);
    }
    
    protected void updateReceptions(final Reception cloakReception)
    {
    	final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Retrieving data ...", true);
    	                     progressDialog.setCancelable(true);
    	
    	new AsyncWorker().execute(new IAsyncTask() {
    		
    		private ArrayList<Reception> receptions = null;
    		private int cloakReceptionId = 0;
    		private ApiExpception exception = null;
    		
			@Override
			public void onProcess() {
				try {
					if (cloakReception != null) {
						firmafon.setCloakReceptionId(cloakReception.id);
					}
					receptions = firmafon.getPossibleCloakReceptions();
					cloakReceptionId = firmafon.getCloakReceptionId();
				} catch (ApiExpception e) {
					exception = e;
				}
			}

			@Override
			public void onComplete() 
			{
				progressDialog.hide();
				
				listAdapter.clear();
				
				if (exception != null) {
					Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
					return;
				}
				
				if (receptions != null) {
					for (Reception reception : receptions) {
						if (reception.id == cloakReceptionId) {
							reception.isCloak = true;
						}
						listAdapter.add(reception);
					}					
				}
			}
    	});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) {
    		case R.id.menu_settings:
    			Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
    			return true;
    		case R.id.menu_refresh:
    			updateReceptions();
    			return true;
    	}
    	
    	return false;
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	{
		if (this.firmafon != null) {
			this.firmafon.setBaseUrl(this.firmafon.defaultBaseUrl);
			this.firmafon.setAppKey(sharedPreferences.getString("firmafonAppKey", ""));
			this.firmafon.setUserKey(sharedPreferences.getString("firmafonUserKey", ""));
			
			if (sharedPreferences.getBoolean("enableDebug", false)) {
	        	this.firmafon.setBaseUrl(sharedPreferences.getString("debugBaseUrl", ""));
	        	this.firmafon.setAppKey(sharedPreferences.getString("debugAppKey", ""));
	            this.firmafon.setUserKey(sharedPreferences.getString("debugUserKey", ""));
	        }
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) 
	{
		try {
			Reception reception = (Reception) adapterView.getItemAtPosition(position);
			this.updateReceptions(reception);
			
		} catch (ClassCastException e) {
			Toast.makeText(getApplicationContext(), "Something strange happened there", Toast.LENGTH_LONG).show();
		}
	}
}
