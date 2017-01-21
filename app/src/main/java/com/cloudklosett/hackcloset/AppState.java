package com.cloudklosett.hackcloset;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.HashMap;

/**
 * Created by William on 1/21/2017.
 */
public class AppState implements Serializable {
    public final  String GARMENT_SAVE_LOC = "com.cloudklosett.garments";
    public final  String IMAGE_PREIX = "com.cloudklosett.image.";
    public final  String IMAGE_KEY_LOC = "com.cloudklosett.image.keys";

    private static AppState ourInstance = new AppState();

    public static AppState getInstance() {
        return ourInstance;
    }

    private HashMap<String, Garment> garments;
    public HashMap<String, Outfit> outfits;
    private HashMap<String, Bitmap> images;
    private  ArrayList<String> imageKeys;

    private AppState() {
        garments = new HashMap<String, Garment>();
        outfits = new HashMap<String, Outfit>();
        images = new HashMap<String, Bitmap>();
    }

    public void saveAll(Context context)  {

        Log.d("SAVING", "Start save all");

        File dir = context.getFilesDir();

        long startTime = System.nanoTime();
        try {
            saveToFile( new File(dir, GARMENT_SAVE_LOC), garments);
            saveToFile(new File(dir, IMAGE_KEY_LOC), imageKeys);
        } catch (Exception e) {
            Log.e("Save Error", Log.getStackTraceString(e), e);
        }

        long d1 = (System.nanoTime() - startTime) / 1000000;
        startTime = System.nanoTime();
        saveImages(context);

        long d2 = (System.nanoTime() - startTime) / 1000000;

        Log.d("SAVED", "save done in " + d1 + ", " + d2 + " ms");
    }

    private void saveImages(Context context) {
        for (String key: images.keySet()) {
            saveImage(key, context);
        }
    }

    private void saveImage(String key, Context context) {
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
            imageKeys = (ArrayList<String>) loadFromFile(new File(dir, IMAGE_KEY_LOC));

            for (String key: imageKeys) {
                loadImage(key, context);
            }

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

        FileInputStream in = null
        ;
        try {
            in =  context.openFileInput(IMAGE_PREIX + key);
            images.put(key, BitmapFactory.decodeFile(IMAGE_PREIX + key, bmOptions));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Garment getGarment(String id) {
        return garments.get(id);
    }

    public void addGarment(Garment garment, Bitmap image) {
        garments.put(garment.getId(), garment);
        images.put(garment.getId(),image);
    }

    public Bitmap getImage(Garment garment) {
        return images.get(garment.getId());
    }

}
