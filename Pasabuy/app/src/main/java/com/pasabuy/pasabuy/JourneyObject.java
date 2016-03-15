package com.pasabuy.pasabuy;

/**
 * Created by jesus on 3/14/16.
 */
public class JourneyObject {
    private String mCountry="";
    private String mCity="";
    private String mReturnDate="";
    private String mJourneyId="";

    public JourneyObject( String country, String city, String returnDate, String jId){
        mCountry = country;
        mCity = city;
        mReturnDate = returnDate;
        mJourneyId = jId;
    }
    public void setCountry(String country){
        mCountry = country;
    }
    public void setCity(String city){
        mCity = city;
    }
    public void setReturnDate(String returnDate){
        mReturnDate = returnDate;
    }
    public void setJourneyId(String jId){
        mJourneyId = jId;
    }

    public String getCountry(){
        return mCountry;
    }
    public String getCity(){
        return mCity;
    }
    public String getReturnDate(){
        return mReturnDate;
    }
    public String getJourneyId(){
        return mJourneyId;
    }

}
