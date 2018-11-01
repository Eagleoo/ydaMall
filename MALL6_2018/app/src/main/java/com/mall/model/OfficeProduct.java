package com.mall.model;

public class OfficeProduct {
   private String product_id="";
   private String ProductThumb="";
   private String ProductName="";
   private String PriceMarket="";
   private String Price="";
   private String priceOriginal="";//商币兑换价
   private String  share_id="";
   private String productid="";
   private String content="";
   
   
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getProductid() {
	return productid;
}
public void setProductid(String productid) {
	this.productid = productid;
}
public String getShare_id() {
	return share_id;
}
public void setShare_id(String share_id) {
	this.share_id = share_id;
}
public String getProduct_id() {
	return product_id;
}
public void setProduct_id(String product_id) {
	this.product_id = product_id;
}
public String getProductThumb() {
	return ProductThumb;
}
public void setProductThumb(String productThumb) {
	ProductThumb = productThumb;
}
public String getProductName() {
	return ProductName;
}
public void setProductName(String productName) {
	ProductName = productName;
}
public String getPriceMarket() {
	return PriceMarket;
}
public void setPriceMarket(String priceMarket) {
	PriceMarket = priceMarket;
}
public String getPrice() {
	return Price;
}
public void setPrice(String price) {
	Price = price;
}
public String getPriceOriginal() {
	return priceOriginal;
}
public void setPriceOriginal(String priceOriginal) {
	this.priceOriginal = priceOriginal;
}
   
}
