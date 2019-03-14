package com.example.mohit.payroll.Extras;

public class Url {
    public static final String Url_IP = "49.50.66.125:7398";

    public static String GetUserLoginApp = "http://" + Url_IP + "/api/login/GetUserLoginApp";
    public static String GetCurrentAttendanceData = "http://" + Url_IP + "/api/attendance/GetCurrentAttendanceData";
    public static String SaveAttendanceData = "http://" + Url_IP + "/api/attendance/SaveAttendanceData";
    public static String getuserprofileapp = "http://" + Url_IP + "/api/users/getuserprofileapp";
    public static String GetNoticeCircularData = "http://" + Url_IP + "/api/NoticeCircular/GetNoticeCircularData";
    public static String getcurrentattendancehistory = "http://" + Url_IP + "/api/attendance/getcurrentattendancehistory";
    public static String GetApplyLeaveBalance = "http://" + Url_IP + "/api/ApplyLeave/GetApplyLeaveBalance";
    public static String SaveApplyLeaveData = "http://" + Url_IP + "/api/ApplyLeave/SaveApplyLeaveData";
    public static String GetApplyLeaveData = "http://" + Url_IP + "/api/ApplyLeave/GetApplyLeaveData";
    public static String DeleteApplyLeaveData = "http://" + Url_IP + "/api/ApplyLeave/DeleteApplyLeaveData";

}
