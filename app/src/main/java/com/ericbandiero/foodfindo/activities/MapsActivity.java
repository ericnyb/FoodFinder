package com.ericbandiero.foodfindo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ericbandiero.foodfindo.AppConstant;
import com.ericbandiero.foodfindo.YelpRunner;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.ericbandiero.foodfindo.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Coordinates;
import com.yelp.fusion.client.models.Location;

import java.net.URL;
import java.util.ArrayList;




public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle("Your choice!");
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the SupportMapFragment and request notification
		// when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		System.out.println("Adding markers cnt:"+YelpRunner.listBusinesses.size());
		// Add a marker in Sydney, Australia,
		// and move the map's camera to the same location.
		LatLng first=new LatLng(YelpRunner.listBusinesses.get(0).getCoordinates().getLatitude(),
				YelpRunner.listBusinesses.get(0).getCoordinates().getLongitude());
		for (Business businesses : YelpRunner.listBusinesses) {
			Coordinates coordinates = businesses.getCoordinates();
			LatLng latLng=new LatLng(coordinates.getLatitude(),coordinates.getLongitude());
			//MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(businesses.getName().trim());
			Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(businesses.getName().trim()));
			marker.setTag(businesses);

			System.out.println(googleMap.toString());

		}

		googleMap.moveCamera(CameraUpdateFactory.newLatLng(first));

		googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
			Business business;
			@Override
			// Return null here, so that getInfoContents() is called next.
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				business= (Business) marker.getTag();
				System.out.println("In custom window...");
				// Inflate the layouts for the info window, title and snippet.
				View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
						(FrameLayout)findViewById(R.id.map), false);

				TextView title = ((TextView) infoWindow.findViewById(R.id.title));
				title.setText(business.getName());

				//Get rating
				double rating = business.getRating();

				//if (((Business)marker.getTag()).getRating()==null
				String ratingString=Double.toString(rating);
				TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
				//snippet.setText(marker.getSnippet());
				snippet.setText("Rating:"+ratingString);

				TextView address = ((TextView) infoWindow.findViewById(R.id.address));

				Location location=business.getLocation();
				location.getDisplayAddress();

				ArrayList<String> displayAddress = business.getLocation().getDisplayAddress();
				StringBuilder stringBuilder=new StringBuilder(displayAddress.size());
				for (String displayAddress1 : displayAddress) {
					stringBuilder.append(displayAddress1);
					stringBuilder.append(AppConstant.NEW_LINE);
				}

				stringBuilder.setLength(stringBuilder.length() - 1);
				address.setText(stringBuilder.toString());


				return infoWindow;
			}
		});


		googleMap.setOnInfoWindowClickListener(this);
//		LatLng sydney = new LatLng(-33.4480, 151.4445);
//		googleMap.addMarker(new MarkerOptions().position(sydney)
//				.title("Terrigal"));
//		googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
		//updateLocationUI();
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Business business=(Business)marker.getTag();
		String url=business.getUrl();

		Uri webPage = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}
	}

}
