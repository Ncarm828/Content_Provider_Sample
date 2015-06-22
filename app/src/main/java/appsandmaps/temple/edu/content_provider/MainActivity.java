package appsandmaps.temple.edu.content_provider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;

import com.samsung.android.sdk.remotesensor.SrsRemoteSensorManager;
import com.samsung.android.sdk.remotesensor.SrsRemoteSensorManager.EventListener;
import com.samsung.android.sdk.remotesensor.SrsRemoteSensorEvent;
import com.samsung.android.sdk.remotesensor.SrsRemoteSensor;

import com.samsung.android.sdk.remotesensor.Srs;


import java.util.List;
import java.util.Locale;

import android.content.pm.PackageInfo;



//public class MainActivity extends ActionBarActivity {

    public class MainActivity extends Activity implements EventListener {

        //Global Methods for MainActvity
        static SrsRemoteSensorManager mServiceManager = null;
        List<SrsRemoteSensor> pedoSensorList;
        Srs remoteSensor = null;
        SrsRemoteSensor pedometerSensor = null;
        static String Steps = "0";


        private static final String GEAR_PACKAGE_NAME = "com.samsung.accessory";
        private static final String GEAR_FIT_PACKAGE_NAME = "com.samsung.android.wms";
        private static final String REMOTESENSOR_PACKAGE_NAME = "com.samsung.android.sdk.remotesensor";
        private boolean mBroadcastState = false;


        private final static String TAG = "CustomContentProvider";
        AlarmReceiver alarm = new AlarmReceiver();
        TextView textViews;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //Samsung SDK library for getting a remotesensor
            remoteSensor = new Srs();

            //checks the order of the permission and if everything is okay creates a connection with remote sensor
            if (checkPermission()) {
                initializeeSRS();
            }

            //used to display information and progress bar
            getStepInformation();

            //sets the alarm and passes the context of main activity
            alarm.setAlarm(this);

            //the manager class is passed in the remoteSensor,then you are able to control the the sensor
            mServiceManager = new SrsRemoteSensorManager(remoteSensor);

            //press the button once the connections are good then it startes displaying the current steps
            //!* reports steps every five minute by default and cannot change this yet *!
            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getPedometerSensorInfo();
                    getPedometerEvent();


                }
            });


        }

        @Override
        protected void onResume() {
            super.onResume();
            mServiceManager.registerListener(this, pedometerSensor,
                    SrsRemoteSensorManager.SENSOR_DELAY_NORMAL, 0);
        }


        @Override
        protected void onPause() {
            super.onPause();
            mServiceManager.unregisterListener(this, pedometerSensor);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            unregisterBroadcastReceiver();
          //  stopPedometerEvent();

        }




        public void getPedometerSensorInfo() {
                pedoSensorList =
                        mServiceManager.getSensorList(SrsRemoteSensor.TYPE_PEDOMETER);
            if (pedoSensorList != null){
                SrsRemoteSensor sensor;
                sensor = pedoSensorList.get(0);
                makeToast(sensor.toString());
                // pedoSensorText.setText(sensor.toString());
            }else{
                makeToast("Sensor is NULL Please Wait....");
            }
        }

        public void getPedometerEvent() {
                if (pedoSensorList != null){

                pedometerSensor = pedoSensorList.get(0);
            mServiceManager.registerListener(this, pedometerSensor, SrsRemoteSensorManager.SENSOR_DELAY_NORMAL, 0);
            }else{
                makeToast("Sensor is NULL Please Wait....");
            }
        }

        public void stopPedometerEvent() {
            SrsRemoteSensor sensor;
            sensor = pedoSensorList.get(0);
            mServiceManager.unregisterListener(this, sensor);
        }


        @Override
        public void onAccuracyChanged(SrsRemoteSensor srsRemoteSensor, int i) {

        }

        @Override
        public void onSensorValueChanged(final SrsRemoteSensorEvent srsRemoteSensorEvent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (srsRemoteSensorEvent.sensor.getType() == SrsRemoteSensor.TYPE_PEDOMETER) {
//                        pedoValueText.setText("Step Count : (" +
//                                Float.toString(srsRemoteSensorEvent.values[0]) + ")");
                       // makeToast(Float.toString(srsRemoteSensorEvent.values[0]));

                        //May need to delete
                        Steps = Float.toString(srsRemoteSensorEvent.values[0]);

                        updateInformation("1");
                        textViews = (TextView) findViewById(R.id.textView);
                        textViews.setText(Steps);

                    }
                }
            });
        }

        @Override
        public void onSensorDisabled(SrsRemoteSensor srsRemoteSensor) {

        }


        void getStepInformation() {
            Cursor cur = getContentResolver().query(ContractClass.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String Id = cur.getString(cur.getColumnIndex(ContractClass.FitNessTable.ID));
                    String title = cur.getString(cur.getColumnIndex(ContractClass.FitNessTable.STEPS));
                    String Steps = cur.getString(cur.getColumnIndex(ContractClass.FitNessTable.EXPERIENCE));
                    System.out.println("Id = " + Id + ", Steps : " + title + ", Experience :" + Steps);
                    textViews = (TextView) findViewById(R.id.textView);
                    textViews.setText(title);
                    circlebar(Steps);

                }


            } else {
                makeToast("Nothing added");
            }


        }

        private void makeToast(String text) {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }


        void circlebar(final String steps) {

            final TextView tv;
            final ProgressBar pBar;
            final int[] pStatus = {0};
            final Handler handler = new Handler();

            tv = (TextView) findViewById(R.id.textView1);
            pBar = (ProgressBar) findViewById(R.id.progressBar1);

            final int percent = ((Integer.parseInt(steps) * 100) / 50000);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (pStatus[0] <= percent) {

                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                pBar.setProgress(pStatus[0]);
                                pBar.setSecondaryProgress(pStatus[0] + 3);
                                tv.setText(steps + "/" + 50000);
                            }
                        });
                        try {
                            // Sleep for 200 milliseconds.
                            // Just to display the progress slowly
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pStatus[0]++;
                    }
                }
            }).start();
        }


        void updateInformation(String str_id) {
            try {
                int id = Integer.parseInt(str_id);
                ContentValues values = new ContentValues();
                values.put(ContractClass.FitNessTable.STEPS, Steps);
                //  values.put(ContractClass.FitNessTable.EXPERIENCE, content.getText().toString());
                getContentResolver().update(ContractClass.CONTENT_URI, values,
                        ContractClass.FitNessTable.ID + " = " + id, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean initializeeSRS() {
            boolean srsInitState = false;

            try {
                /**
                 * initialize() initialize Remote Sensor package. This needs to be called first.
                 * If the device does not support Remote Sensor, SsdkUnsupportedException is thrown.
                 */
                remoteSensor.initialize(this.getApplicationContext());

                srsInitState = true;

            } catch (SsdkUnsupportedException e) {
                srsInitState = false;

                switch (e.getType()) {
                    case SsdkUnsupportedException.LIBRARY_NOT_INSTALLED:
                        registerBroadcastReceiver();

                        try {
                            if ((remoteSensor.isFeatureEnabled(Srs.TYPE_GEAR_MANAGER) == false) && (remoteSensor.isFeatureEnabled(Srs.TYPE_GEAR_FIT_MANAGER) == false)) {
                                Toast.makeText(this, "Install Gear Manager or Gear Fit Manager package", Toast.LENGTH_SHORT).show();
                                invokeInstallOption(R.string.manager_msg_str, null);
                                break;
                            }

                            if (remoteSensor.isFeatureEnabled(Srs.TYPE_REMOTE_SENSOR_SERVICE) == false) {
                                Toast.makeText(this, "Install Remote Sensor Service package", Toast.LENGTH_SHORT).show();
                                invokeInstallOption(R.string.rss_msg_str, null);
                            }

                        } catch (RuntimeException eRun) {
                            Toast.makeText(this, "RuntimeException = " + eRun.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED:
                        Toast.makeText(this, "Package update is required", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(this, "SsdkUnsupportedException = " + e.getType(), Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                invokeInstallOption(-1, e.getMessage());
            }

            return srsInitState;
        }

        private void registerBroadcastReceiver() {
            mBroadcastState = true;

            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addDataScheme("package");

            this.registerReceiver(btReceiver, filter);
        }

        private void unregisterBroadcastReceiver() {

            if ((btReceiver != null) && (mBroadcastState)) {
                this.unregisterReceiver(btReceiver);
            }

            mBroadcastState = false;
        }

        BroadcastReceiver btReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent == null) {
                    return;
                }

                String action = intent.getAction();

                if (action == null) {
                    return;
                }

                if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                    String appName = intent.getDataString();

                    if (appName != null) {
                        if (appName.toLowerCase(Locale.ENGLISH).contains(GEAR_FIT_PACKAGE_NAME.toLowerCase(Locale.ENGLISH))) {
                            invokeInstallOption(R.string.rss_msg_str, null);
                        } else if (appName.toLowerCase(Locale.ENGLISH).contains(REMOTESENSOR_PACKAGE_NAME.toLowerCase(Locale.ENGLISH))) {
                            if (checkPermission()) {
                                initializeeSRS();
                            }
                        }
                    }
                }
            }
        };

        private void invokeInstallOption(final int msgID, String msg) {

            DialogInterface.OnClickListener msgClick = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selButton) {
                    switch (selButton) {
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = null;

                            if ((msgID == R.string.rss_msg_str) || (msgID == R.string.rss_permission_msg_str)) {
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("samsungapps://ProductDetail/" + "com.samsung.android.sdk.remotesensor"));
                            } else if (msgID == R.string.manager_msg_str) {
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("samsungapps://ProductDetail/" + "com.samsung.android.wms"));
                            } else if (msgID == R.string.permission_msg_str) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());

                                intent = new Intent(Intent.ACTION_DELETE, packageURI);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            } else if (msgID == -1) {
                                finish();
                            }

                            if (intent != null) {
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException eRun) {
                                }
                            }

                            break;

                        default:
                            break;
                    }
                }
            };

            AlertDialog.Builder message = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

            if (msgID != -1) {
                message.setMessage(msgID);
            }

            if (msg != null) {
                message.setMessage(msg);
            }

            message.setPositiveButton(R.string.ok_str, msgClick);
            message.setCancelable(false);

            message.show();

        }

        private boolean checkPermission() {
            PackageManager packageManager = this.getPackageManager();
            boolean mIsSapInstalled = false;
            boolean mIsWingtipInstalled = false;
            boolean mIsSapPermissionGranted = false;
            boolean mIsWingtipPermissionGranted = false;

            if (packageManager == null) {
                return false;
            }

		/* If the Remote Sensor service is not installed, return */
            if ((checkPackage(packageManager, REMOTESENSOR_PACKAGE_NAME) == false) ||
                    ((checkPackage(packageManager, GEAR_PACKAGE_NAME) == false) &&
                            (checkPackage(packageManager, GEAR_FIT_PACKAGE_NAME) == false))) {
                return true;
            }

		/* If the Remote Sensor service is not having permission to access Gear Manger, launch the Samsung App Store to download Remote Sensor Service*/
            if (checkPackage(packageManager, GEAR_PACKAGE_NAME) == true) {
                mIsSapInstalled = true;

                if (packageManager.checkPermission(
                        "com.samsung.accessory.permission.ACCESSORY_FRAMEWORK",
                        "com.samsung.android.sdk.remotesensor") == PackageManager.PERMISSION_GRANTED) {

                    mIsSapPermissionGranted = true;
                }
            }

		/* If the Remote Sensor service is not having permission to access Gear Fit Manger, launch the Samsung App Store to download Remote Sensor Service*/
            if (checkPackage(packageManager, GEAR_FIT_PACKAGE_NAME) == true) {
                mIsWingtipInstalled = true;

                if (packageManager.checkPermission(
                        "com.samsung.android.sdk.permission.SESSION_MANAGER_SERVICE",
                        "com.samsung.android.sdk.remotesensor") == PackageManager.PERMISSION_GRANTED) {

                    mIsWingtipPermissionGranted = true;
                }
            }

            if (((mIsWingtipInstalled == true) && (mIsWingtipPermissionGranted == false))
                    || ((mIsSapInstalled == true) && (mIsSapPermissionGranted == false))) {

                invokeInstallOption(R.string.rss_permission_msg_str, null);

                return false;
            }

		/* If the Remote Sensor application is not having permission to access Remote Sensor Service, launch the Samsung App Store to download Remote Sensor Application*/
            if (packageManager.checkPermission(
                    "com.samsung.android.sdk.permission.REMOTE_SENSOR_SERVICE",
                    "appsandmaps.temple.edu.content_provider") != PackageManager.PERMISSION_GRANTED) {

                invokeInstallOption(R.string.permission_msg_str, null);

                return false;
            }

            return true;
        }


        private boolean checkPackage(PackageManager packageManager, String szPackageName) {
            PackageInfo packageInfo = null;
            boolean bReturn = false;

            try {
                packageInfo = packageManager.getPackageInfo(szPackageName, 0);

                if (packageInfo != null) {
                    return true;
                }

                bReturn = false;

            } catch (PackageManager.NameNotFoundException e1) {
                bReturn = false;
            }

            return bReturn;
        }



    }






