package com.example.mohit.payroll;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
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
import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    Button button_login;
    private ProgressDialog progressDialog;
    AppCompatEditText login_id, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        String restoredText = prefs.getString("isLogin", null);
        if(restoredText != null){
            Intent intent = new Intent(LoginActivity.this, Dashboard_activity.class);
            startActivity(intent);
            finish();
        }
//        Intent intent = new Intent(LoginActivity.this, Dashboard_activity.class);
//        startActivity(intent);
//        finish();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");

        login_id = findViewById(R.id.login_id);
        password = findViewById(R.id.password);
        button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_id.getText().toString().equals("") || password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter login credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    if(GenFunction.isNetworkAvailable(LoginActivity.this)){
                        new loginUser().execute();
                    } else {
                        Toast.makeText(LoginActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

//    public void login(View view) {
//        Intent intent = new Intent(LoginActivity.this, Dashboard_activity.class);
//        startActivity(intent);
//    }

    class loginUser extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject, returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.GetUserLoginApp);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("userName", login_id.getText().toString());
                json.put("password", password.getText().toString());
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
                        SharedPreferences.Editor editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
//                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("employeeId", returnValue.getString("employeeId"));
                        editor.putString("isAdmin", returnValue.getString("isAdmin"));
                        editor.putString("isRH", returnValue.getString("isRH"));
                        editor.putString("isGH", returnValue.getString("isGH"));
                        editor.putString("userName", returnValue.getString("userName"));
                        editor.putString("password", returnValue.getString("password"));
                        editor.putString("mode", returnValue.getString("mode"));
                        editor.putString("id", returnValue.getString("id"));
                        editor.putString("empType", returnValue.getString("empType"));
                        editor.putString("isLogin", "yes");
                        editor.commit();
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, Dashboard_activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please try again later!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }
}
