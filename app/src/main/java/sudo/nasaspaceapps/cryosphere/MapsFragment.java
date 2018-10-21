package sudo.nasaspaceapps.cryosphere;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sudo.nasaspaceapps.cryosphere.rest.api.WeatherApi;
import sudo.nasaspaceapps.cryosphere.rest.model.Weather;
//import sudo.nasaspaceapps.cryosphere.rest.api.WeatherApi;
//import sudo.nasaspaceapps.cryosphere.rest.model.Weather;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {
    //MAPS
    private GoogleMap mMap;
    PlaceAutocompleteFragment placeAutoComplete;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;

    private String latitude;
    private String longitude;
    private String city;
    TextView lat;
    TextView lon;
    TextView userplace;
    TextView temperature;
    char unit;
    String temp;
    String cit;
    Integer tempcheck;

    //UI elements
    TextView food;


    //Array
    double lonarray[] = {-0.150833,66.35,48.623056,35.4,42.739722,35.683333,-77.616667,-43.464444,-50.5,30.833333};
    double latarray[] = {37.3075,-38.2,-113.762222,77.1,44.473333,75.916667,162.983333,170.017778,-73.133333,79.166667};
    int smallestIndexGlacier;

    //UI elements
    //Button getnearestGlacier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        city  = intent.getStringExtra("place");
        lat = findViewById(R.id.latitude);
        lon = findViewById(R.id.longitude);
        userplace = findViewById(R.id.City);
        temperature = findViewById(R.id.temp);
        unit ='c';
        //Buttons
        //getnearestGlacier = findViewById(R.id.getNearestLocation);
        food = findViewById(R.id.food);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            food.setText(Html.fromHtml("<p> Animals are the main source of food in Cryospheric region<ul> <li> Food in winters:\n" +
                    "<ul>\n" +
                    "<li>Seals</li>\n" +
                    "<li>Walruses</li>\n" +
                    "<li>Beluga whales</li>\n" +
                    "<li>Narwhals</li>\n" +
                    "</li> </ul>\n" +
                    "<li>Food in summers:\n" +
                    "<ul>\n" +
                    "<li>Fishing: \n" +
                    "In summers the Ice melts down to snow and fishing is the most easiest hunting you can do.</li>\n" +
                    "<li>Walruses</li>\n" +
                    "<li>caribou</li>\n" +
                    "<li>Whales</li>\n" +
                    "<li>Narwhals</li>\n" +
                    "</ul>\n" +
                    "</ul>\n" +
                    "</p>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            food.setText(Html.fromHtml("<p> Animals are the main source of food in Cryospheric region<ul> <li> Food in winters:\n" +
                    "<ul>\n" +
                    "<li>Seals</li>\n" +
                    "<li>Walruses</li>\n" +
                    "<li>Beluga whales</li>\n" +
                    "<li>Narwhals</li>\n" +
                    "</li> </ul>\n" +
                    "<li>Food in summers:\n" +
                    "<ul>\n" +
                    "<li>Fishing: \n" +
                    "In summers the Ice melts down to snow and fishing is the most easiest hunting you can do.</li>\n" +
                    "<li>Walruses</li>\n" +
                    "<li>caribou</li>\n" +
                    "<li>Whales</li>\n" +
                    "<li>Narwhals</li>\n" +
                    "</ul>\n" +
                    "</ul>\n" +
                    "</p>"));
        }



        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.d("Maps", "Place selected: " + place.getName());
                LatLng latLng = place.getLatLng();
                //latLng.latitude();
                saveData(String.valueOf(latLng.longitude), String.valueOf(latLng.latitude));
                Log.d("Maps", "Lon: " + latLng.longitude+"Lat: " + latLng.latitude);

            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
                Toast.makeText(MapsFragment.this, "Please Check Internet Connection", Toast.LENGTH_LONG).show();
            }
        });

        lat.setText("Latitude: "+latitude);
        lon.setText("Longitude: "+longitude);
        userplace.setText(city);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getData();

        double temparr[];
        double temparr2[];
        temparr = new double[10];
        temparr = distance(Double.parseDouble(latitude), Double.parseDouble(longitude), latarray, lonarray);
        temparr2 = distance(Double.parseDouble(latitude),Double.parseDouble(longitude), latarray, lonarray);

        for(int i=0; i<=9; i++){
            Log.e("ARRAY", ""+temparr[i]+ "\n");
        }

        double shortestdistance=getSmallestelementinarray(temparr, 10);
        Log.e("TAG", ""+shortestdistance);

        smallestIndexGlacier = getSmallestindex(temparr2, shortestdistance,10);
        Log.e("Short Index", ""+smallestIndexGlacier);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng userLocation = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in "+city));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)), 12.0f));
    }

    protected synchronized void buildGoogleApiClient(){
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        lastlocation = location;
//        if(currentLocationmMarker != null)
//        {
//            currentLocationmMarker.remove();
//
//        }
//        Log.d("lat = ",""+latitude);
//        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Location");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        currentLocationmMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
//
//        if(client != null)
//        {
//            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
//        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Saving Latitude And Longitude in Shared Prefrences

    public void saveData(String Long, String Lat){
        SharedPreferences sharePref = getSharedPreferences("locationInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor shareEdit = sharePref.edit();
        shareEdit.putString("latitude", Lat );
        shareEdit.putString("longitude", Long);
        shareEdit.commit();
    }

    //Yaha Retrofit ko call kiya
    public void getData()
    {
        char unit = 'c';

        String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='" + unit + "'", city);
        WeatherApi.Factory.getInstance().getWeather(YQL, "json").enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Toast.makeText(MapsFragment.this, "Success", Toast.LENGTH_SHORT).show();
                try {
                    Weather list = response.body();
                    temperature.setText(response.body().getQuery().getResults().getChannel().getItem().getCondition().getTemp());

                }
                catch (NullPointerException e){
                    temperature.setText("0");
                }
                    tempcheck = Integer.valueOf((String) temperature.getText());
                    if (tempcheck <= 5) {
                        temperature.setText("This location is cryosphere");
                    } else {
                        temperature.setText("This location is not cryosphere");
                        MyAlertBuilder("The Given Location is not a cryosphere Do you want to continue?", "Yes", "No");

                    }

            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Toast.makeText(MapsFragment.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
//                progress.dismiss();
            }
        });
    }

//    public void getData()
//    {
//
//        final Call<OpenWeather> postList = OpenWeather.getService().getWeatherList("9fa787165ee336c2ade7783ea5e32e3b", latitude,longitude);
//
//        postList.enqueue(new Callback<OpenWeather>() {
//            //Yeh sab apne aap aaya
//            @Override
//            public void onResponse(Call<OpenWeather> call, Response<OpenWeather> response) {
//                OpenWeather list = response.body();
//                Toast.makeText(MapsFragment.this, "Success", Toast.LENGTH_SHORT).show();
//                temp = response.body().
//
//            }
//
//            @Override
//            public void onFailure(Call<OpenWeather> call, Throwable t) {
//                Toast.makeText(MapsFragment.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//

    void MyAlertBuilder(String message, String positive, String negative){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //perform any action
                Toast.makeText(getApplicationContext(), "Yes clicked", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //perform any action
                AlertDialog optionDialog = new AlertDialog.Builder(MapsFragment.this).create();
                optionDialog.dismiss();

            }
        });

        //creating alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Click Methods
    void getNearestGlacier(View v){
        String hello =latitude;
        String Bellow = longitude;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);
        Toast.makeText(this,""+countryName,Toast.LENGTH_LONG);
        Log.e("TAGGER", ""+countryName);
        latitude = String.valueOf(lonarray[smallestIndexGlacier]);
        longitude = String.valueOf(latarray[smallestIndexGlacier]);

//        switch (countryName){
//            case "India":{
//                latitude = String.valueOf(lonarray[9]);
//                longitude = String.valueOf(latarray[9]);
//            }
//                break;
//        }
        if(Objects.equals(countryName, "India")){
            latitude = String.valueOf(lonarray[9]);
            longitude = String.valueOf(latarray[9]);
        }
        else if(Objects.equals(countryName, "USA")){
            latitude = String.valueOf(lonarray[2]);
            longitude = String.valueOf(latarray[2]);
        }
        else if(Objects.equals(countryName, "Russia")){
            latitude = String.valueOf(lonarray[4]);
            longitude = String.valueOf(latarray[4]);
        }
        else if(Objects.equals(countryName, "Pakistan")){
            latitude = String.valueOf(lonarray[5]);
            longitude = String.valueOf(latarray[5]);
        }



         Log.e("LATITUDE","latitude"+lonarray[smallestIndexGlacier]);
         Toast.makeText(this, ""+lonarray[smallestIndexGlacier],Toast.LENGTH_LONG).show();

        LatLng coordinate = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)); //Store these lat lng values somewhere. These should be constant.
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 4.0f);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.animateCamera(location);

        Geocoder geocoder1 = new Geocoder(this, Locale.getDefault());
        List<Address> addresses1 = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName1 = addresses.get(0).getAddressLine(0);
        String stateName1 = addresses.get(0).getAddressLine(1);
        String countryName1 = addresses.get(0).getAddressLine(2);
        //userplace.setText(cityName1);



//        LatLng latLng = new LatLng(Double.parseDouble(latitude) , Double.parseDouble(longitude));
//
//        MarkerOptions a = new MarkerOptions()
//                .position(latLng);
//        Marker m = mMap.addMarker(a);
//        m.setPosition(latLng);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(12.0f));

//        LatLng latLng = new LatLng(Double.parseDouble(latitude) , Double.parseDouble(longitude));
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Location");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        currentLocationmMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(12.0f));

//        if(client != null)
//        {
//            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
//        }



    }

    void getmyfood(View v){
        Log.e("Fetch Hona Location", "Latitude: "+latitude+"Longitude: "+longitude);

        Uri gmmIntentUri = Uri.parse("geo:"+latitude+","+longitude+"?q=restaurants");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }

    }




    private double[] distance(double lat1, double lon1, double latarray[], double lonarray[]) {
         int n = lonarray.length;
        //int m = latarray.length;
        double resarray[];

//        double lonarray[] = {10.0,20.0,30.0,40.0,50.0};
//        double latarray[] = {60.0,70.0,80.0,90.0,100.0};

        //temparr = distance(lat:- 28.0,lon:- 77.0, latarray, lonarray);
        resarray = new double[20];
         for(int i=0; i<=n-1; i++) {
             double theta = lon1 - lonarray[i];
             double dist = Math.sin(deg2rad(lat1))
                     * Math.sin(deg2rad(latarray[i]))
                     + Math.cos(deg2rad(lat1))
                     * Math.cos(deg2rad(latarray[i]))
                     * Math.cos(deg2rad(theta));
             dist = Math.acos(dist);
             dist = rad2deg(dist);
             dist = dist * 60 * 1.1515;
             resarray[i] = dist;
         }

        return resarray;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static double getSmallestelementinarray(double[] a, int total){
        double temp;
        for (int i = 0; i < total; i++)
        {
            for (int j = i + 1; j < total; j++)
            {
                if (a[i] > a[j])
                {
                    temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
        return a[0];
    }

    public static int getSmallestindex(double[] a, double small, int size){
        int i;
        int gotit=0;
        for(i=0; i<=size-1; i++){
        if(a[i]==small){
            gotit =i;
            //Log.e("LOGGERGOTTHESMALL", "Gotit:-"+gotit+"i:-"+i);
        }
    }

        return gotit;
    }



}
