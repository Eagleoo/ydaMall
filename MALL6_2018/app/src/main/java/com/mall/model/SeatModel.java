package com.mall.model;

public class SeatModel {
	private String RowNo = "";
	private String ColumnNo = "";
	private String SeatType = "";
	private String SeatStatus = "";
	private String LocNo="";
	

	public String getLocNo() {
		return LocNo;
	}

	public void setLocNo(String locNo) {
		LocNo = locNo;
	}

	public String getRowNo() {
		return RowNo;
	}

	public void setRowNo(String rowNo) {
		RowNo = rowNo;
	}

	public String getColumnNo() {
		return ColumnNo;
	}

	public void setColumnNo(String columnNo) {
		ColumnNo = columnNo;
	}

	public String getSeatType() {
		return SeatType;
	}

	public void setSeatType(String seatType) {
		SeatType = seatType;
	}

	public String getSeatStatus() {
		return SeatStatus;
	}

	public void setSeatStatus(String seatStatus) {
		SeatStatus = seatStatus;
	}

}
