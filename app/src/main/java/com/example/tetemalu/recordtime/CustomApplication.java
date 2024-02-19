package com.example.tetemalu.recordtime;

import android.app.Application;

public class CustomApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    AppDatabase.getInstance(this);
  }
}
