package com.example.rajat.app;

/**
 * Created by komorebi on 12/29/16.
 */
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BeaconInter extends Application implements BeaconConsumer {

    private static BeaconInter instance = null;
    private BeaconManager beaconManager;
    private static final Identifier nameSpaceId = Identifier.parse("0x5dc33487f02e477d4058");

    public CopyOnWriteArrayList<String> regionNameList;
    public CopyOnWriteArrayList<Region> regionList;
    public HashMap<String,Region> ssnRegionMap;
    public OnListRefreshListener onListRefreshListener;
    public MainActivity context;

    public interface OnListRefreshListener {
        void onListRefresh();
    }

    public static BeaconInter getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setUpBeacon();
    }

    public void setUpBeacon(){
        ssnRegionMap = new HashMap<>();
        regionList = new CopyOnWriteArrayList<>();
        regionNameList = new CopyOnWriteArrayList<>();

        ssnRegionMap.put("0x0117c59825E9",new Region("Test Room",nameSpaceId, Identifier.parse("0x0117c59825E9"),null));
        ssnRegionMap.put("0x0117c55be3a8",new Region("Git Room",nameSpaceId,Identifier.parse("0x0117c55be3a8"),null));
        ssnRegionMap.put("0x0117c552c493",new Region("Android Room",nameSpaceId,Identifier.parse("0x0117c552c493"),null));
        ssnRegionMap.put("0x0117c55fc452",new Region("iOS Room",nameSpaceId,Identifier.parse("0x0117c55fc452"),null));
        ssnRegionMap.put("0x0117c555c65f",new Region("Python Room",nameSpaceId,Identifier.parse("0x0117c555c65f"),null));
        ssnRegionMap.put("0x0117c55d6660",new Region("Office",nameSpaceId,Identifier.parse("0x0117c55d6660"),null));
        ssnRegionMap.put("0x0117c55ec086",new Region("Ruby Room",nameSpaceId,Identifier.parse("0x0117c55ec086"),null));

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        new BackgroundPowerSaver(this);
        beaconManager.bind(this);

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {

            }

            @Override
            public void didExitRegion(Region region) {

            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                String regionName = region.getUniqueId();
                String beaconSSN = region.getId2().toHexString();
                Toast.makeText(getApplicationContext(), "" + i, Toast.LENGTH_SHORT).show();
                switch (i){
                    case 0:
                        Toast.makeText(getApplicationContext(), "Enter " + regionName , Toast.LENGTH_SHORT).show();
                        regionNameList.add(regionName);
                        regionList.add(region);
                        BeaconInter.notifyListChange();
                        Toast.makeText(getApplicationContext(),"Found beacon", Toast.LENGTH_SHORT).show();
                        BeaconInter.showNotification("Found beacon");
                        enterRegion(beaconSSN);
                        break;
                    default:
                        Log.i("TAG","Outside " + regionName);
                        Toast.makeText(getApplicationContext(), "Outside" + regionName , Toast.LENGTH_SHORT).show();
                        if(regionNameList.contains(regionName)){
                            regionNameList.remove(regionName);
                        }
                        if(regionList.contains(region)) {
                            regionList.remove(region);
                            BeaconInter.notifyListChange();
                        }
                        exitRegion(beaconSSN);
                        BeaconInter.showNotification("Exit beacon");
                        Toast.makeText(getApplicationContext(),"Exit beacon",Toast.LENGTH_SHORT).show();
                        break;
                }

                ArrayList<String> list_beaconSSN = new ArrayList<String>();
                for(Region r: regionList){
                    list_beaconSSN.add(r.getId2().toHexString());
                }
                updateUserInRegions(list_beaconSSN);


            }
        });


        try {
            for(String key:ssnRegionMap.keySet()) {
                Region region = ssnRegionMap.get(key);
                beaconManager.startMonitoringBeaconsInRegion(region);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static void showNotification(String message){
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(instance)
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("Status!") // title for notification
                .setContentText(message) // message for notification
                .setAutoCancel(true); // clear notification after click
        NotificationManager mNotificationManager =
                (NotificationManager) instance.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }

    private void enterRegion(final String beaconSSN){
        //on Enter Region
    }

    private void exitRegion(final String beaconSSN){
       //on Exit Region
    }

    private void updateUserInRegions(final ArrayList<String> list_beaconSSN){
        //whatever
    }

    private static void notifyListChange(){
        if (instance.context != null && instance.onListRefreshListener != null) {
            instance.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BeaconInter.instance.onListRefreshListener.onListRefresh();
                }
            });
        }
    }


}

