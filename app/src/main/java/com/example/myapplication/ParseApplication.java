package com.example.myapplication;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

       // ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("5RcFANaUpahISqmHvg2omL9Al9d6g3XyM2obKcgz")
                .clientKey("4y6VlEbhf8OOWSHZFP3UQPQfGiY240T8ezowEgfH")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
