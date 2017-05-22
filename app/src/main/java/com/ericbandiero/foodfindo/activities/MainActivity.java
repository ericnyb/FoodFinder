package com.ericbandiero.foodfindo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.ericbandiero.foodfindo.AppConstant;
import com.ericbandiero.foodfindo.LocationGetter;
import android.Manifest;

import com.ericbandiero.foodfindo.NoLocationAlertDialogFragment;
import com.ericbandiero.foodfindo.UtilityFood;

import com.ericbandiero.foodfindo.R;
import com.ericbandiero.foodfindo.YelpRunner;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 24;
	private UtilityFood utilityFood=new UtilityFood();
	private Context context;
	private ProgressDialog progressDialog;
	private FloatingActionButton fab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_explore_black_24dp, this.getTheme()));
		} else {
			fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_explore_black_24dp));
		}

		setClickForFab();

		//Permissions!
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		}
	}
	/**
	 * Dispatch onResume() to fragments.  Note that for better inter-operation
	 * with older versions of the platform, at the point of this call the
	 * fragments attached to the activity are <em>not</em> resumed.  This means
	 * that in some cases the previous state may still be saved, not allowing
	 * fragment transactions that modify the state.  To correctly interact
	 * with fragments in their proper state, you should instead override
	 * {@link #onResumeFragments()}.
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			UtilityFood.AlertMessageSimple(this,this.getTitle().toString(),getString(R.string.setting_none));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog!=null){
			progressDialog.dismiss();
		}
	}

	private void setClickForFab(){
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//Check internet connection
				//Toast.makeText(context,"Checking connections and location",Toast.LENGTH_LONG).show();
				boolean haveConnection = utilityFood.checkConnection(context);

				if (!haveConnection){
					return;
				}

				//Toast.makeText(context,"Checking location status",Toast.LENGTH_SHORT).show();
				boolean locationEnabled = LocationGetter.getLocation(context);

				if (locationEnabled) {
					new DownloadMessage().execute();
				}
				else{
					{
						DialogFragment newFragment = NoLocationAlertDialogFragment.newInstance(
								R.string.alert_dialog_two_buttons_title);
						newFragment.show(getSupportFragmentManager(), "dialog");

					}
				}
			}
		});
	}

	public void doPositiveClick() {
		new DownloadMessage().execute();
	}

	public void doNegativeClick() {

	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				} else {
					// We want to remind them
					UtilityFood.AlertMessageSimple(this,getString(R.string.permission_request_title),getString(R.string.showmoreinforforrequest));
				}
			}
		}
	}


	private class DownloadMessage extends AsyncTask<URL, Integer, String> {

		YelpRunner yelpRunner;

		protected String doInBackground(URL... urls) {

			String dataFromYelp=null;

			try {
					//Gets the restaurant data using coordinates.
					yelpRunner=new YelpRunner(LocationGetter.getLatitudeLast(),LocationGetter.getLongitudeLast());
					dataFromYelp = yelpRunner.getDataFromYelp();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return dataFromYelp;
		}

		@Override
		protected void onPreExecute() {
			progressDialog=new ProgressDialog(context);
			progressDialog.setMessage(getString(R.string.progess_dialog_text));
			progressDialog.show();
		}

		protected void onProgressUpdate(Integer... progress) {
			//setProgressPercent(progress[0]);
		}

		protected void onPostExecute(String result) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			if (!result.equals(YelpRunner.WE_FETCHED_DATA)){
				UtilityFood.AlertMessageSimple(context,"Info","We could not get data:"+result);
				return;
			}


			if (yelpRunner.isUsedDefaultLocation()){
				if (AppConstant.DEBUG) Log.d(this.getClass().getSimpleName()+">","Location default is used...");
				Toast.makeText(context,"Could not get location...default location was used...",Toast.LENGTH_LONG).show();
			}
				//Show the map
				//Intent intent=new Intent(context,MapsActivityCurrentPlace.class);
				Intent intent=new Intent(context,MapsActivity.class);
				startActivity(intent);
		}
	}
}
