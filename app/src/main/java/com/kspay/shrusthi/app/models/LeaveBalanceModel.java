package com.kspay.shrusthi.app.models;

public class LeaveBalanceModel {
    private String leaveId;
    private String erpId;
    private String leaveType;
    private String leaveName;
    private String balance;
    private String leaveFrom;
    private String isHalfDay;
    private String isFirstHalf;
    private String isSecondHalf;
    private String leaveTo;
    private String isHalfDayTo;
    private String noofLeave;
    private String applyDate;
    private String leaveStatus;
    private String leaveRemark;
    private String leaveActionBy;
    private String entryBy;
    private String pageSize;
    private String pageNumber;
    private String totalCount;
    private String empRH;

    public LeaveBalanceModel(String leaveId, String erpId, String leaveType, String leaveName, String balance,
                             String leaveFrom, String isHalfDay, String isFirstHalf, String isSecondHalf, String leaveTo,
                             String isHalfDayTo, String noofLeave, String applyDate, String leaveStatus,
                             String leaveRemark, String leaveActionBy, String entryBy,
                             String pageSize, String pageNumber, String totalCount, String empRH) {
        this.leaveId = leaveId;
        this.erpId = erpId;
        this.leaveType = leaveType;
        this.leaveName = leaveName;
        this.balance = balance;
        this.leaveFrom = leaveFrom;
        this.isHalfDay = isHalfDay;
        this.isFirstHalf = isFirstHalf;
        this.isSecondHalf = isSecondHalf;
        this.leaveTo = leaveTo;
        this.isHalfDayTo = isHalfDayTo;
        this.noofLeave = noofLeave;
        this.applyDate = applyDate;
        this.leaveStatus = leaveStatus;
        this.leaveRemark = leaveRemark;
        this.leaveActionBy = leaveActionBy;
        this.entryBy = entryBy;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.totalCount = totalCount;
        this.empRH = empRH;

    }

    public String getLeaveId() {

        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public String getErpId() {
        return erpId;
    }

    public void setErpId(String erpId) {
        this.erpId = erpId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveName() {
        return leaveName;
    }

    public void setLeaveName(String leaveName) {
        this.leaveName = leaveName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLeaveFrom() {
        return leaveFrom;
    }

    public void setLeaveFrom(String leaveFrom) {
        this.leaveFrom = leaveFrom;
    }

    public String getIsHalfDay() {
        return isHalfDay;
    }

    public void setIsHalfDay(String isHalfDay) {
        this.isHalfDay = isHalfDay;
    }

    public String getIsFirstHalf() {
        return isFirstHalf;
    }

    public void setIsFirstHalf(String isFirstHalf) {
        this.isFirstHalf = isFirstHalf;
    }

    public String getIsSecondHalf() {
        return isSecondHalf;
    }

    public void setIsSecondHalf(String isSecondHalf) {
        this.isSecondHalf = isSecondHalf;
    }

    public String getLeaveTo() {
        return leaveTo;
    }

    public void setLeaveTo(String leaveTo) {
        this.leaveTo = leaveTo;
    }

    public String getIsHalfDayTo() {
        return isHalfDayTo;
    }

    public void setIsHalfDayTo(String isHalfDayTo) {
        this.isHalfDayTo = isHalfDayTo;
    }

    public String getNoofLeave() {
        return noofLeave;
    }

    public void setNoofLeave(String noofLeave) {
        this.noofLeave = noofLeave;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(String leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public String getLeaveRemark() {
        return leaveRemark;
    }

    public void setLeaveRemark(String leaveRemark) {
        this.leaveRemark = leaveRemark;
    }

    public String getLeaveActionBy() {
        return leaveActionBy;
    }

    public void setLeaveActionBy(String leaveActionBy) {
        this.leaveActionBy = leaveActionBy;
    }

    public String getEntryBy() {
        return entryBy;
    }

    public void setEntryBy(String entryBy) {
        this.entryBy = entryBy;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getEmpRH() {
        return empRH;
    }

    public void setEmpRH(String totalCount) {
        this.empRH = empRH;
    }


}
