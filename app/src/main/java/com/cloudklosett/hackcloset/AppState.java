package com.cloudklosett.hackcloset;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by William on 1/21/2017.
 */
public class AppState implements Serializable {
    public final  String GARMENT_SAVE_LOC = "com.cloudklosett.garments";
    public final  String OUTFIT_SAVE_LOC = "com.cloudklosett.image.keys";
    public final  String IMAGE_PREIX = "com.cloudklosett.image.";

    public final static String OUTFIT_EDITOR_MESSAGE = "com.cloudklosett.outfit.id";
    public final static String GARMENT_ID_MESSAGE = "com.cloudklosett.GARMENT_ID_MESSAGE";

    public String edittingDate;

    public void setEdittingDate(String edittingDate) {
        this.edittingDate = edittingDate;
    }

    public String getEdittingDate() {
        return this.edittingDate;
    }

    private String newGarmentEvent;

    public void setNewGarmentEvent(String newGarmentEvent) {
        this.newGarmentEvent = newGarmentEvent;
    }

    public String getNewGarmentEvent() {
        if (this.newGarmentEvent == null)
            return null;
        String value = this.newGarmentEvent;
        this.newGarmentEvent = null;
        return value;
    }

    private static AppState ourInstance = new AppState();

    public static AppState getInstance() {
        return ourInstance;
    }

    private HashMap<String, Garment> garments;
    public HashMap<String, Outfit> outfits;
    private HashMap<String, Bitmap> images;

    private AppState() {
        garments = new HashMap<String, Garment>();
        outfits = new HashMap<String, Outfit>();
        images = new HashMap<String, Bitmap>();
        newGarmentEvent = null;
        edittingDate = null;
    }

    public void saveAll(Context context)  {
        File dir = context.getFilesDir();

        long startTime = System.nanoTime();
        try {
            saveToFile( new File(dir, GARMENT_SAVE_LOC), garments);
            saveToFile( new File(dir, OUTFIT_SAVE_LOC), outfits);

        } catch (Exception e) {
            Log.e("Save Error", Log.getStackTraceString(e), e);
        }

        startTime = System.nanoTime();
    }

    private void saveImages(Context context) {
        for (String key: images.keySet()) {
            saveImage(key, context);
        }
    }

    public void saveImage(String key, Context context) {
        FileOutputStream out = null;
        try {
            out =  context.openFileOutput(IMAGE_PREIX + key, context.MODE_PRIVATE);
            images.get(key).compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFile(File file, Object toSave) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(file);
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(toSave);
        os.close();
        outputStream.close();
    }

    public void loadAll(Context context) {
        File dir = context.getFilesDir();
        try {
            garments = (HashMap<String, Garment>) loadFromFile( new File(dir, GARMENT_SAVE_LOC));

            for (Garment garment: getAllGarments()) {
                loadImage(garment.getId(), context);
            }

            outfits = (HashMap<String, Outfit>) loadFromFile( new File(dir, OUTFIT_SAVE_LOC));
        } catch ( Exception e ) {
            Log.e("Load Error", Log.getStackTraceString(e), e);
        }
    }

    private Object loadFromFile(File file) throws  FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream is = new ObjectInputStream(fis);
        Object obj =  is.readObject();
        is.close();
        fis.close();
        return obj;
    }

    private void loadImage(String key, Context context) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        File imgFile = new File( context.getFilesDir(), IMAGE_PREIX + key);
        try {
            images.put(key, BitmapFactory.decodeFile( imgFile.getAbsolutePath(), bmOptions));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void listFiles(Context c) {
        File dir  = c.getFilesDir();
        Log.d("FILES: ", TextUtils.join(", ", dir.list()));
    }

    public ArrayList<Garment> getAllGarments() {
        return new ArrayList<Garment>(garments.values());
    }

    public Garment getGarment(String id) {
        return garments.get(id);
    }

    public  Outfit getOutfit(String id) {
        if (outfits.containsKey(id)) {
            return  outfits.get(id);
        }
        return new Outfit();
    }

    public void addGarment(Garment garment, Bitmap image) {
        garments.put(garment.getId(), garment);
        images.put(garment.getId(),image);
    }

    public Bitmap getImage(Garment garment) {
        return images.get(garment.getId());
    }

}


