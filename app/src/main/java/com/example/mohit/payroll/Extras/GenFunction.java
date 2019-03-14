package com.example.mohit.payroll.Extras;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenFunction {

    public static String MonthMMM[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public static boolean isNetworkAvailable(Context context) {
        boolean outcome = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
            for (NetworkInfo tempNetworkInfo : networkInfos) {
                if (tempNetworkInfo.isConnected()) {
                    outcome = true;
                    break;
                }
            }
        }
        return outcome;
    }

    public static String convertDDMMYYYYTODDMMMYYYY(String convertingDate){
        String convertedDate[] = convertingDate.split("-");
//        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
//        Date date;
//        try {
//            date = format1.parse(convertingDate);
//            convertedDate = format2.format(date);
//        } catch (Exception e){
//           convertedDate = convertingDate;
//        }
        return convertedDate[2]+"-"+MonthMMM[Integer.parseInt(convertedDate[1])-1]+"-"+convertedDate[0];
    }

}
