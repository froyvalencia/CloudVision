package com.cloudklosett.hackcloset;

/**
 * Created by William on 1/21/2017.
 */

public enum GarmentTypes {
    UNCLASSIFIED("Unclassified"),
    SHIRT("Shirt"),
    PANTS("Pants"),
    JACKET("Jacket"),
    COAT("Coat"),
    SHORTS("Shorts"),
    SWETTER("Swetter"),
    UNDERWEAR("Underwear"),
    SOCKS("Socks");

    private String friendlyName;

    private GarmentTypes(String friendlyName){
        this.friendlyName = friendlyName;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
