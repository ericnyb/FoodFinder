package com.ericbandiero.foodfindo;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by ${"Eric Bandiero"} on 3/2/2016.
 */
public class AppUtility {


    public static boolean isMapProviderInstalled;

    //TODO Move as many of these as we can to our aar UtilityShared class
    private static String className=AppUtility.class.getSimpleName();
    private static Set<String> lastKnownZipCode;

    public static Set getLastKnownZipCode() {
        return lastKnownZipCode;
    }

    public static void setLastKnownZipCode(Set<String> setLastKnownZipCodes) {
        lastKnownZipCode = setLastKnownZipCodes;
    }
    public static void getZipCodesFromAddressList(List<Address> listOfAddresses, Set<String> zips) {
        for (Address address : listOfAddresses) {
            if (address.getPostalCode() != null) {
                zips.add(address.getPostalCode());
            }
        }
    }
    public static List<Address> getAddressesFromGeoCoder(Context context,double latitude,double longitude,int addressesToReturn) {
        List<Address> listOfAddresses = null;
        Geocoder gc=new Geocoder(context);
        try {
            listOfAddresses = gc.getFromLocation(latitude, longitude, addressesToReturn);

            //This is a test of more than one zip code - will give us 10023 & 10024
            //listOfAddresses = gc.getFromLocation(40.782891, -73.983085, addressesToReturn);
        } catch (IOException e) {
            Log.e(new Object() { }.getClass().getEnclosingClass()+">",e.getMessage());
        }
        catch (IllegalArgumentException e) {
            Log.e(new Object() { }.getClass().getEnclosingClass()+">",e.getMessage());
        }
        //Could be empty or null - we don't want to return null
        if (listOfAddresses==null){
            listOfAddresses=new ArrayList<>();
        }
        return listOfAddresses;
    }
}
