package com.pasabuy.pasabuy;

import android.util.Log;

public class ProductObject {
    private String mName="";
    private String mDescription="";
    private String mTag="";
    private String mPrice="";
    private String mImageUrl="";
    private String mProductId="";
    private String mRequest = "";
    private String mLocation = "";
    private boolean mLiked = false;

    private String mSellerName="";
    private String mSellerImageUrl="";

    public ProductObject(String name, String tag, String imageUrl, String price, String pId){
        mName = name;
        mTag = tag;
        mImageUrl = imageUrl;
        mPrice = price;
        mProductId = pId;
    }

    public void setSeller(String seller) {
        mSellerName = seller;
    }
    public void setSellerImage (String url) {
        mSellerImageUrl = url;
    }
    public void setLocation(String city, String country){
        if(null != city && !city.isEmpty()) {
            mLocation = (city + ", " + country).trim();
        } else {
            mLocation = country.trim();
        }
    }
    public void setDescription (String description) {
        mDescription = description;
    }
    public void setRequest(String request){
            mRequest = request;
    }
    public void setLiked(boolean liked)
    {
        mLiked = liked;
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
    public void setPrice(String price){
        mPrice = price;
    }
    public void setProductId(String pId){
        mProductId = pId;
    }

    public String getSellerName(){
        return mSellerName;
    }
    public String getSellerImageUrl() { return mSellerImageUrl; }

    public String getName(){
        return mName;
    }
    public String getDescription() { return mDescription; }
    public String getLocation() { return mLocation; }
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
    public String getRequest() {return mRequest;}
    public boolean getRequestBool() {
        return mRequest.equalsIgnoreCase("REQUEST");
    }
    public boolean getLiked(){return mLiked;}

}
