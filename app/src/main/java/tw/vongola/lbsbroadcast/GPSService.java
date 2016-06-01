package tw.vongola.lbsbroadcast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class GPSService extends Service implements LocationListener {
    private Context mContext;
    private LocationManager lms;

    public GPSService(){

    }

    public GPSService(Context mContext) {
        this.mContext = mContext;
//        this.lms = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        //取得系統定位服務
        this.lms = (LocationManager) (mContext.getSystemService(Context.LOCATION_SERVICE));
        if (this.lms.isProviderEnabled(LocationManager.GPS_PROVIDER) || this.lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lms = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        } else {
            Toast.makeText(mContext, "請開啟定位服務", Toast.LENGTH_LONG).show();
            mContext.startService(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public double[] getLocation() {	//將定位資訊顯示在畫面中
        double[] coordinates = new double[2];
        if(lms != null && lms.isProviderEnabled(LocationManager.GPS_PROVIDER) || this.lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            List<String> providers = lms.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = lms.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            coordinates[0] = bestLocation.getLongitude();	//取得經度
            coordinates[1] = bestLocation.getLatitude();	//取得緯度
        }
        else {
            Log.d("GPS", "Can not get Location!");
            Toast.makeText(mContext, "無法定位座標", Toast.LENGTH_LONG).show();
        }
        return coordinates;
    }
}
