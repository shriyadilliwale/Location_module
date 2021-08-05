package com.example.agri1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    //need to create some variables
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest


    //permission id is just an int that must be unique so we can use any number
    private var PERMISSION_ID = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //now we intiate fused..providerclient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //now lets add the event to our button
        getPos.setOnClickListener{
            Log.d("Debug:",CheckPermission().toString())
            Log.d("Debug:",isLocationEnabled().toString())
            RequestPermission()


            getLastLocation()
        }
    }

    //Create function that will allow us to get last location

    private fun getLastLocation(){
        //first we check permission
        if(CheckPermission()){
            //now we check location service is enabled
            if(isLocationEnabled()){
                //now lets get location
               fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
                    var location:Location? = task.result
                    if(location== null){
                        //if the location is null we will get new user location
                        //so we need to create a new function
                        getNewLocation()
                    }else{
                        //location.latitude will return the latitude coordinators
                        //location.longitude will return longitude coordinators
                        Log.d("Debug:","Your  location:"+location.longitude)
                        Locationtxt.text = "Your Current Location is: \nLat:" + location.latitude +"; Long:" + location.longitude+
                                "\nAddress:"+getCompleteAddressString(location.latitude,location.longitude)

                    }
                }
            }else{
                Toast.makeText(this,"Please Enable your Location service",Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }


    }

    private fun getNewLocation(){
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback,Looper.myLooper()
        )


    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation:Location = locationResult.lastLocation
            Log.d("Debug:","Yout Last Location is:"+lastLocation.longitude.toString())
            //now we will set new location
            Locationtxt.text = "Your Current Coordinators are: \nLat:" + lastLocation.latitude +"; Long:" + lastLocation.longitude+
                    "\nAddress:"+getCompleteAddressString(lastLocation.latitude,lastLocation.longitude)


        }
    }

    //Create function that will check the users permission
    private fun CheckPermission():Boolean{

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    //Creating function that will allow user to get permission

    private fun RequestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID
        )
    }

    //Now we need a function that check if location service of device is enabled

    private fun isLocationEnabled():Boolean{
        var locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    //all adress
    private  fun getCompleteAddressString(
        LATITUDE: Double,
        LONGITUDE: Double
    ): String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("My Current loction address", strReturnedAddress.toString())
            } else {
                Log.w("My Current loction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current loction address", "Canont get Address!")
        }
        return strAdd
    }





    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //this  is biult in function that check the permission result
        //only to debug the code
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You Have the Permission")
            }
        }
    }
}