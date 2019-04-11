package com.example.mohit.payroll;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohit.payroll.DB_Handler.AttendanceSaveHandler;
import com.example.mohit.payroll.Extras.GenFunction;
import com.example.mohit.payroll.Extras.Url;
import com.example.mohit.payroll.models.AttendanceSaveModel;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AttendanceActivity extends AppCompatActivity implements LocationListener {

    LinearLayout ll_out_button;
    LinearLayout ll_in_button;
    LinearLayout ll_in_button_gray;
    LinearLayout ll_out_button_gray;
    RelativeLayout ll_photo_upload;
    LinearLayout ll_buttons;
    ImageView image_attendance;
    Button button_image_attendance;
    Button button_attendance_submit;
    int CAMERA_PIC_REQUEST = 1;
    private ProgressDialog progressDialog;
    String inDate = "", inTime = "", outDate = "", outTime = "";
    public Bitmap image = null;
    String id;
    String punchStatus = "";
    TextView out_date_gray, out_date, in_date_gray, in_date;
    String encodedImage;

    AttendanceSaveHandler attendanceSaveHandler;
    SQLiteDatabase sqLiteDatabase;

    //////Geo Location
    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        progressDialog = new ProgressDialog(AttendanceActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        attendanceSaveHandler = new AttendanceSaveHandler(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        id = prefs.getString("id", null);

        //////Geo Location
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }
            // get location
            getLocation();
        }
        ////////////

        ll_buttons = findViewById(R.id.ll_buttons);
        ll_out_button = findViewById(R.id.ll_out_button);
        ll_in_button = findViewById(R.id.ll_in_button);
        ll_in_button_gray = findViewById(R.id.ll_in_button_gray);
        ll_out_button_gray = findViewById(R.id.ll_out_button_gray);
        ll_photo_upload = findViewById(R.id.ll_photo_upload);
        ll_in_button.setVisibility(View.GONE);
        ll_out_button.setVisibility(View.GONE);
        ll_in_button_gray.setVisibility(View.GONE);
        ll_out_button_gray.setVisibility(View.GONE);
        ll_photo_upload.setVisibility(View.GONE);
        out_date_gray = findViewById(R.id.out_date_gray);
        out_date = findViewById(R.id.out_date);
        in_date = findViewById(R.id.in_date);
        in_date_gray = findViewById(R.id.in_date_gray);
        out_date.setText("");
        out_date_gray.setText("");
        in_date.setText("");
        in_date_gray.setText("");

        ll_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ll_in_button.setVisibility(View.GONE);
//                ll_out_button.setVisibility(View.VISIBLE);
//                ll_out_button_gray.setVisibility(View.GONE);
//                ll_in_button_gray.setVisibility(View.VISIBLE);
//                ll_photo_upload.setVisibility(View.VISIBLE);
//                ll_buttons.setVisibility(View.GONE);
                ll_photo_upload.setVisibility(View.VISIBLE);
                ll_buttons.setVisibility(View.GONE);
                punchStatus = "In";
            }
        });

        ll_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                punchStatus = "Out";
//                ll_in_button.setVisibility(View.GONE);
//                ll_out_button.setVisibility(View.VISIBLE);
//                ll_out_button_gray.setVisibility(View.GONE);
//                ll_in_button_gray.setVisibility(View.VISIBLE);
                ll_photo_upload.setVisibility(View.VISIBLE);
                ll_buttons.setVisibility(View.GONE);
            }
        });

        button_image_attendance = findViewById(R.id.button_image_attendance);
        image_attendance = findViewById(R.id.image_attendance);
        button_image_attendance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        button_attendance_submit = findViewById(R.id.button_attendance_submit);
        button_attendance_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image != null) {
//                    if (GenFunction.isNetworkAvailable(AttendanceActivity.this)) {
//                        new SaveAttendanceData().execute();
//                    } else {
//                        if (checkDataAvailable()) {
//                            Toast.makeText(AttendanceActivity.this, "First Sync Previous Data", Toast.LENGTH_SHORT).show();
//                        } else {
                            saveAttendanceWhenNoInternet();
//                        }
//                    }
                } else
                    Toast.makeText(AttendanceActivity.this, "Please Click a photo", Toast.LENGTH_SHORT).show();
            }
        });
        new GetCurrentAttendanceData().execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                image = (Bitmap) data.getExtras().get("data");
                image_attendance.setImageBitmap(image);
                try {
//                    final Uri imageUri = data.getData();
//                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    encodedImage = encodeImage(image);
                } catch (Exception e) {
                    Log.e("Exception Image", e.getMessage());
                }
            }
        }
    }

    //encode image to base64 string
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    class GetCurrentAttendanceData extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject;
        JSONArray returnData;
        JSONObject returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.GetCurrentAttendanceData);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("employeeId", Integer.parseInt(id));
                json.put("month", month + 1);
//                json.put("month", 1);
                json.put("year", year);
                Log.i("json req getattendance", json.toString());
                httppost.setEntity(new ByteArrayEntity(json.toString().replaceAll("\\\\", "").replaceAll("\"\"", "\"").getBytes("UTF8")));
                String responseBody = httpclient.execute(httppost, responseHandler);
                jsonobject = new JSONObject(responseBody);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } finally {
                progressDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jsonobject != null) {
                    isExceptionOccured = jsonobject.getBoolean("isExceptionOccured");
                    status = jsonobject.getBoolean("status");
                    if (status && !isExceptionOccured) {
                        returnData = new JSONArray(jsonobject.getString("returnValue"));
                        returnValue = returnData.getJSONObject(0);
                        if (!returnValue.getString("inDate").equals("null")) {
                            if (!returnValue.getString("outDate").equals("null")) {
                                ll_in_button_gray.setVisibility(View.VISIBLE);
                                ll_out_button_gray.setVisibility(View.VISIBLE);
                                ll_in_button.setVisibility(View.GONE);
                                ll_out_button.setVisibility(View.GONE);
                                ll_photo_upload.setVisibility(View.GONE);
                                String[] inDate = returnValue.getString("inDate").split("T");
                                String[] inTime = returnValue.getString("inTime").split("T");
                                String[] outDate = returnValue.getString("outDate").split("T");
                                String[] outTime = returnValue.getString("outTime").split("T");
//                                in_date_gray.setText(inDate[0] + "\n" + inTime[1]);
                                in_date_gray.setText(inDate[0] + "\n" + checkOutDateTime(inTime[1]));
//                                out_date_gray.setText(outDate[0] + "\n" + outTime[1]);
                                out_date_gray.setText(outDate[0] + "\n" + checkOutDateTime(outTime[1]));
                            } else {
                                ll_in_button_gray.setVisibility(View.VISIBLE);
                                ll_out_button_gray.setVisibility(View.GONE);
                                ll_in_button.setVisibility(View.GONE);
                                ll_out_button.setVisibility(View.VISIBLE);
                                ll_photo_upload.setVisibility(View.GONE);
                                String[] inDate = returnValue.getString("inDate").split("T");
                                String[] inTime = returnValue.getString("inTime").split("T");
                                in_date_gray.setText(inDate[0] + "\n" + checkOutDateTime(inTime[1]));
                            }
                        } else {
                            ll_in_button_gray.setVisibility(View.GONE);
                            ll_out_button_gray.setVisibility(View.VISIBLE);
                            ll_in_button.setVisibility(View.VISIBLE);
                            ll_out_button.setVisibility(View.GONE);
                            ll_photo_upload.setVisibility(View.GONE);
                        }

                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        ll_photo_upload.setVisibility(View.GONE);
                        ll_in_button_gray.setVisibility(View.VISIBLE);
                        ll_out_button_gray.setVisibility(View.VISIBLE);
                        ll_in_button.setVisibility(View.GONE);
                        ll_out_button.setVisibility(View.GONE);

                        /////
//                        ll_in_button_gray.setVisibility(View.GONE);
//                        ll_out_button_gray.setVisibility(View.VISIBLE);
//                        ll_in_button.setVisibility(View.VISIBLE);
//                        ll_out_button.setVisibility(View.GONE);
//                        ll_photo_upload.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AttendanceActivity.this, "Please try again later!", Toast.LENGTH_LONG).show();
                    ll_photo_upload.setVisibility(View.GONE);
                    ll_in_button_gray.setVisibility(View.VISIBLE);
                    ll_out_button_gray.setVisibility(View.VISIBLE);
                    ll_in_button.setVisibility(View.GONE);
                    ll_out_button.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }

    class SaveAttendanceData extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject, returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String currentDate = sdfDate.format(new Date());
        String currentTime = sdfTime.format(new Date());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.SaveAttendanceData);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("employeeId", Integer.parseInt(id));
                json.put("month", month + 1);
                json.put("year", year);
                json.put("punchDate", currentDate);
                json.put("punchTime", currentTime);
                if (loc == null) {
                    json.put("gpslocation", "");
                } else {
                    json.put("gpslocation", loc.getLatitude() + "," + loc.getLongitude());
                }
                json.put("punchStatus", punchStatus);
//                json.put("punchImage", "");
                json.put("punchImage", encodedImage);
                Log.i("json req saveattendance", json.toString());
                httppost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
                String responseBody = httpclient.execute(httppost, responseHandler);
                jsonobject = new JSONObject(responseBody);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } finally {
                progressDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jsonobject != null) {
                    isExceptionOccured = jsonobject.getBoolean("isExceptionOccured");
                    status = jsonobject.getBoolean("status");
                    if (status && !isExceptionOccured) {
//                        ll_in_button.setVisibility(View.GONE);
//                        ll_in_button_gray.setVisibility(View.VISIBLE);
//                        ll_photo_upload.setVisibility(View.GONE);
//                        if (punchStatus.equals("In")) {
//                            ll_out_button.setVisibility(View.VISIBLE);
//                            ll_out_button_gray.setVisibility(View.GONE);
//                        } else {
//                            ll_out_button.setVisibility(View.GONE);
//                            ll_out_button_gray.setVisibility(View.VISIBLE);
//                        }
                        String message = jsonobject.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AttendanceActivity.this, Dashboard_activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AttendanceActivity.this, "Attendance not saved, Please try again later!!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }

    private boolean checkDataAvailable() {
        sqLiteDatabase = attendanceSaveHandler.getWritableDatabase();
        Cursor cursor = attendanceSaveHandler.getinformation(sqLiteDatabase);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    private void getAttendanceWhenNoInternet(){
        sqLiteDatabase = attendanceSaveHandler.getWritableDatabase();
        Cursor cursor = attendanceSaveHandler.getAttendance(sqLiteDatabase);
        if (cursor.moveToFirst()){
            do{
                String punchStatus = cursor.getString(cursor.getColumnIndex("punchStatus"));
                if(punchStatus.equals("In")){

                }
            }while(cursor.moveToNext());
        }
        cursor.close();
    }

    private void saveAttendanceWhenNoInternet() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String currentDate = sdfDate.format(new Date());
        String currentTime = sdfTime.format(new Date());

        sqLiteDatabase = attendanceSaveHandler.getWritableDatabase();
        AttendanceSaveModel ultm = new AttendanceSaveModel();
        ultm.employeeId = Integer.parseInt(id);
        ultm.month = month + 1;
        ultm.year = year;
        ultm.punchDate = currentDate;
        ultm.punchTime = currentTime;
        if (loc == null) {
            ultm.gpslocation = "";
        } else {
            ultm.gpslocation = loc.getLatitude() + "," + loc.getLongitude();
        }
        ultm.punchStatus = punchStatus;
        ultm.punchImage = encodedImage;
        ultm.isSync = "N";
        attendanceSaveHandler.addinnformation(ultm, sqLiteDatabase);
        attendanceSaveHandler.close();

        String message = "Attendance Saved Successfully";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(AttendanceActivity.this, Dashboard_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    ////Geo Location ////////
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application.Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public String checkOutDateTime(String bigTime) {
//        final String time = bigTime;
//
//        try {
//            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
//            final Date dateObj = sdf.parse(time);
//            return (new SimpleDateFormat("K:mm").format(dateObj));
//        } catch (final ParseException e) {
//            e.printStackTrace();
//        }
//        return "";

        String time[] = bigTime.split(":");
        if (Integer.parseInt(time[0]) > 11) {
            String convertedTime = "" + (Integer.parseInt(time[0]) - 12);
            if ((Integer.parseInt(time[0]) - 12) > 9)
                return "" + convertedTime + ":" + time[1] + " PM";
            else
                return "0" + convertedTime + ":" + time[1] + " PM";
        } else
            return time[0] + ":" + time[1] + " AM";
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AttendanceActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
//        tvLatitude.setText(Double.toString(loc.getLatitude()));
//        tvLongitude.setText(Double.toString(loc.getLongitude()));
//        tvTime.setText(DateFormat.getTimeInstance().format(loc.getTime()));
//        Toast.makeText(this, DateFormat.getTimeInstance().format(loc.getTime()) + "", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, loc.getLatitude() + " latitude", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, loc.getLongitude() + " Longitude", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}
