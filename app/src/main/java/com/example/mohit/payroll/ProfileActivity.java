package com.example.mohit.payroll;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohit.payroll.Extras.GenFunction;
import com.example.mohit.payroll.Extras.Url;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    String id;
    TextView tv_contact_number, tv_joining_date, tv_designation, tv_department_name, tv_father_name, tv_emp_id, tv_emp_name;
    ImageView image_profile;
    String imageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        id = prefs.getString("id", null);

        tv_contact_number = findViewById(R.id.tv_contact_number);
        tv_joining_date = findViewById(R.id.tv_joining_date);
        tv_designation = findViewById(R.id.tv_designation);
        tv_department_name = findViewById(R.id.tv_department_name);
        tv_father_name = findViewById(R.id.tv_father_name);
        tv_emp_id = findViewById(R.id.tv_emp_id);
        tv_emp_name = findViewById(R.id.tv_emp_name);
        image_profile = findViewById(R.id.image_profile);

        if(GenFunction.isNetworkAvailable(ProfileActivity.this)) {
            new getuserprofileapp().execute();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    class getuserprofileapp extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject;
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
            HttpPost httppost = new HttpPost(Url.getuserprofileapp);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("employeeId", Integer.parseInt(id));
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
                        returnValue = new JSONObject(jsonobject.getString("returnValue"));
                        if (returnValue != null) {
                            String []date;
                            date = returnValue.getString("dateOfJoining").split("T");
                            String dateSplit[] = date[0].split("-");
                            tv_joining_date.setText(dateSplit[2]+"-"+GenFunction.MonthMMM[Integer.parseInt(dateSplit[1])-1]+"-"+dateSplit[0]);
                            tv_contact_number.setText(returnValue.getString("mobileNumber"));
                            tv_designation.setText(returnValue.getString("designationName"));
                            tv_department_name.setText(returnValue.getString("departmentName"));
                            tv_father_name.setText(returnValue.getString("fatherName"));
                            tv_emp_name.setText(returnValue.getString("employeeName"));
                            tv_emp_id.setText(returnValue.getString("employeeId"));
                            imageString = returnValue.getString("employeeImagePath");
//                            convertingStringToImage();
                            new DownloadImageTask(image_profile)
                                    .execute(imageString);
                        } else {
                            Toast.makeText(getApplicationContext(), "No Record Available", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Please try again later!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }

    //decode base64 string to image
    public void convertingStringToImage() {
        Bitmap imageBitmap = StringToBitMap(imageString);
//        String imageBytes = Base64.decode(imageString, Base64.DEFAULT);
//        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        image_profile.setImageBitmap(imageBitmap);
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
