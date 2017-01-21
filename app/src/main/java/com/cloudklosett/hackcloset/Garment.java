package com.cloudklosett.hackcloset;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by William on 1/21/2017.
 */

public class Garment implements Serializable {
    private String name;
    private UUID id;
    private ArrayList<String> tags;


    public String getName() {
        return name;
    }

    public String getId() {
        return id.toString();
    }

    Garment(List<EntityAnnotation> labels) {
        id = UUID.randomUUID();
        tags = new ArrayList<String>(labels.size());

        this.name = labels.get(0).getDescription() + " " + labels.get(1).getDescription();
        for (EntityAnnotation label: labels) {
            if (label.getScore() > 0.5) {
                tags.add(label.getDescription());
            }
        }
    }
}


