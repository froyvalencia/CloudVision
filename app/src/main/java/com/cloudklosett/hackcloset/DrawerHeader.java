package com.cloudklosett.hackcloset;

import android.*;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.util.LinkedList;
import java.util.List;

    @NonReusable
    @Layout(R.layout.drawer_header)
    public class DrawerHeader {

        String username;
        String email;

        DrawerHeader(String e, String name){
            username = name;
            email = e;
        }

        @View(R.id.profileImageView)
        private ImageView profileImage;

        @View(R.id.nameTxt)
        private TextView nameTxt;

        @View(R.id.emailTxt)
        private TextView emailTxt;

        @Resolve
        private void onResolved() {

            nameTxt.setText(username);
            emailTxt.setText(email);
        }


    }



