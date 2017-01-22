package com.cloudklosett.hackcloset;

import android.util.Log;

import java.util.HashSet;

/**
 * Created by William on 1/21/2017.
 */

// denim
public enum GarmentTypes {
    UNCLASSIFIED("Unclassified"),
    SHIRT("Shirt", "Polo"),
    PANTS("Pants", "Trousers", "Jeans", "tights"),
    JACKET("Jacket"),
    HAT("Hat", "Fedora"),
    DRESS("Dress"),
    COAT("Coat"),
    SHORTS("Shorts"),
    SWETTER("Swetter"),
    SWIMWEAR("Swimming Suit, Swimwear"),
    UNDERWEAR("Underwear", "Undergarment", "Lingerie"),
    FORMAL("Formal Wear"),
    SUIT("Suit", "Tuxedo"),
    SOCKS("Socks"),
    SHOES("Shoes", "sneakers", "footwear"),
    ACESSORY("Accessory");


    private String friendlyName;
    private String[] synons;

    private GarmentTypes(String friendlyName, String... synons){
        this.friendlyName = friendlyName;
        this.synons = synons;

    }

    public boolean isContained(String query) {
        String target = query.toLowerCase();
        if (target.contains(friendlyName.toLowerCase()))
            return true;
        for (int i = 0; i < this.synons.length; i++) {
            if (target.contains(this.synons[i].toLowerCase()))
                return true;
        }
        return false;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
