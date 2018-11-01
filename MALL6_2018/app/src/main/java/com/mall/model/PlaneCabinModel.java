package com.mall.model;

import java.io.Serializable;

public class PlaneCabinModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private String Cabin = "";
	private String CabName = "";
	/*
	 * 0���ò� 1 �����ؼ۲� 2 ���ؼ۲� 3 �����ؼ۲� 4 �������ؼ۲� 5 ���������ؼ۲� 6 ͷ�Ȳ� 7 ����� 8 �������ؼ۲� 9
	 * �����۲�λ
	 */
	private String CabType = "";
	private String IsPubTar = "";// �Ƿ񹫲��˼ۣ�0�ǹ����˼ۣ�1�����˼ۡ�
	private String SeatNum = "";// ��λ��λ����ݸ�ʽΪ��0��9���Լ�A��A���9����λ���ϣ�
	private String price = "";
	private String ADTPrice = "";//
	private String ADTTax = "";
	private String ADTYq = "";
	private String INFPrice = "";
	private String INFTax = "";
	private String Discount = "";
	private String RewRates = "";// ������ʣ���ʽ��3%
	private String PolicyId = "";// ����ID
	private String Note = "";
	private String TPrice = "";// �ŶӼ۸񣬸�ʽ��650��650Ԫ��
	private String TDiscount = "";//
	private String TLaSeatNum = "";//
	private String TRewRates = "";
	private String TForRemark = "";// �Ŷ�����˵��
	private String TNote = "";//
	private String PPolId = "";// ƥ��ƽ̨���ߣ����߱���
	private String PPolType = "";// ƽ̨����
	private String PPolRewRates = "";// ƽ̨���㣬��ʽ��7.8%
	private String PlatOth = "";
	private String JsPrice = "";

	public String getCabin() {
		return Cabin;
	}

	public void setCabin(String cabin) {
		Cabin = cabin;
	}

	public String getCabName() {
		return CabName;
	}

	public void setCabName(String cabName) {
		CabName = cabName;
	}

	public String getCabType() {
		return CabType;
	}

	public void setCabType(String cabType) {
		CabType = cabType;
	}

	public String getIsPubTar() {
		return IsPubTar;
	}

	public void setIsPubTar(String isPubTar) {
		IsPubTar = isPubTar;
	}

	public String getSeatNum() {
		return SeatNum;
	}

	public void setSeatNum(String seatNum) {
		SeatNum = seatNum;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getADTPrice() {
		return ADTPrice;
	}

	public void setADTPrice(String aDTPrice) {
		ADTPrice = aDTPrice;
	}

	public String getADTTax() {
		return ADTTax;
	}

	public void setADTTax(String aDTTax) {
		ADTTax = aDTTax;
	}

	public String getADTYq() {
		return ADTYq;
	}

	public void setADTYq(String aDTYq) {
		ADTYq = aDTYq;
	}

	public String getINFPrice() {
		return INFPrice;
	}

	public void setINFPrice(String iNFPrice) {
		INFPrice = iNFPrice;
	}

	public String getINFTax() {
		return INFTax;
	}

	public void setINFTax(String iNFTax) {
		INFTax = iNFTax;
	}

	public String getDiscount() {
		return Discount;
	}

	public void setDiscount(String discount) {
		Discount = discount;
	}

	public String getRewRates() {
		return RewRates;
	}

	public void setRewRates(String rewRates) {
		RewRates = rewRates;
	}

	public String getPolicyId() {
		return PolicyId;
	}

	public void setPolicyId(String policyId) {
		PolicyId = policyId;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
	}

	public String getTPrice() {
		return TPrice;
	}

	public void setTPrice(String tPrice) {
		TPrice = tPrice;
	}

	public String getTDiscount() {
		return TDiscount;
	}

	public void setTDiscount(String tDiscount) {
		TDiscount = tDiscount;
	}

	public String getTLaSeatNum() {
		return TLaSeatNum;
	}

	public void setTLaSeatNum(String tLaSeatNum) {
		TLaSeatNum = tLaSeatNum;
	}

	public String getTRewRates() {
		return TRewRates;
	}

	public void setTRewRates(String tRewRates) {
		TRewRates = tRewRates;
	}

	public String getTForRemark() {
		return TForRemark;
	}

	public void setTForRemark(String tForRemark) {
		TForRemark = tForRemark;
	}

	public String getTNote() {
		return TNote;
	}

	public void setTNote(String tNote) {
		TNote = tNote;
	}

	public String getPPolId() {
		return PPolId;
	}

	public void setPPolId(String pPolId) {
		PPolId = pPolId;
	}

	public String getPPolType() {
		return PPolType;
	}

	public void setPPolType(String pPolType) {
		PPolType = pPolType;
	}

	public String getPPolRewRates() {
		return PPolRewRates;
	}

	public void setPPolRewRates(String pPolRewRates) {
		PPolRewRates = pPolRewRates;
	}

	public String getPlatOth() {
		return PlatOth;
	}

	public void setPlatOth(String platOth) {
		PlatOth = platOth;
	}

	public String getJsPrice() {
		return JsPrice;
	}

	public void setJsPrice(String jsPrice) {
		JsPrice = jsPrice;
	}

}
