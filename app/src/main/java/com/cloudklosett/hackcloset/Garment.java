package com.cloudklosett.hackcloset;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by William on 1/21/2017.
 */

public class Garment implements Serializable {
    final  static String[] decorators = {"Amazing", "Extraordinary", "Super", "Neat", "Cool", "Fun", "Classy", "Fabulous", "Stylish"};
    final  static String[] descriptors = {"blue", "red", "green", "gray", "black", "white", "yellow"};
    Random r = new Random();

    private String name;
    private UUID id;
    private ArrayList<String> tags;
    private GarmentTypes type;

    public  void setName(String name) {
        this.name = name;
    }

    public void setType (GarmentTypes type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public String getId() {
        return id.toString();
    }

    public GarmentTypes getType() { return type;};

    Garment(List<EntityAnnotation> labels) {
        id = UUID.randomUUID();
        tags = new ArrayList<String>(labels.size());

        String noun = "";
        GarmentTypes finalType = GarmentTypes.UNCLASSIFIED;
        String descriptor = "";
        for (EntityAnnotation label: labels) {
            for (GarmentTypes type: GarmentTypes.values()) {
                Log.d("Name detct", label.getDescription() + " " + type.toString() + " " + type.isContained(label.getDescription()));
                if (finalType == GarmentTypes.UNCLASSIFIED && type.isContained(label.getDescription())) {
                    finalType = type;
                    noun = label.getDescription();
                }
            }
            for (int i = 0; i < descriptors.length; i++) {
                if (descriptor == "" && label.getDescription().toLowerCase().contains(descriptors[i])) {
                    descriptor = descriptors[i];
                }
            }
        }
        this.type = finalType;

        String decorator = decorators[r.nextInt(decorators.length - 1)];

        if (type != GarmentTypes.UNCLASSIFIED) {
            name = decorator + " " + descriptor + " " + noun;
        } else {
            name = decorator + " mystery object";
        }

        Log.d("Name final", name);



    }
}


