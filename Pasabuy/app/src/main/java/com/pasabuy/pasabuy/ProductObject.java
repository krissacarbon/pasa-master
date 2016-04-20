package com.pasabuy.pasabuy;

public class ProductObject {
    private String mName="";
    private String mTag="";
    private String mSeller="";
    private String mPrice="";
    private String mImageUrl="";
    private String mProductId="";

    public ProductObject(String name, String tag, String imageUrl, String seller, String price, String pId){
        mName = name;
        mTag = tag;
        mImageUrl = imageUrl;
        mSeller = seller;
        mPrice = price;
        mProductId = pId;
    }
    public void setSeller(String seller){
        mSeller = seller;
    }
    public void setPrice(String price){
        mPrice = price;
    }
    public void setName(String name){
        mName = name;
    }
    public void setTag(String tag){
        mTag = tag;
    }
    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }
    public void setProductId(String pId){
        mProductId = pId;
    }

    public String getName(){
        return mName;
    }
    public String getSeller(){
        return mSeller;
    }
    public String getPrice(){
        return mPrice;
    }
    public String getTag(){
        return mTag;
    }
    public String getImageUrl(){
        return mImageUrl;
    }
    public String getProductId(){
        return mProductId;
    }

}
