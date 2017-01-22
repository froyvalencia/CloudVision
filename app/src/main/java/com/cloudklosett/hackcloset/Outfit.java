package com.cloudklosett.hackcloset;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by William on 1/21/2017.
 */

public class Outfit {
    private UUID id;
    private ArrayList<Garment> garments;
    private String name;

    Outfit() {
        id = UUID.randomUUID();
        garments = new ArrayList<Garment>();
        name = "";
    }

    public String getId() {
        return this.id.toString();
    }

    public String getName() {
        return name;
    }
}
