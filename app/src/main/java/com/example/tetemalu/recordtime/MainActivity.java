package com.example.tetemalu.recordtime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.*;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MainViewModel model = new ViewModelProvider(this).get(MainViewModel.class);
    FragmentManager fm = getSupportFragmentManager();

    //日付が設定されたら画面を変更
    model.getSelectedDate().observe(this, date -> {
      // 日付画面フラグメントを表示
      fm.beginTransaction()
              .replace(R.id.fragment_container, new DateFragment())
              .addToBackStack(null)
              .commit();
    });

    // 月画面フラグメントを表示
    if(savedInstanceState == null) {
      fm.beginTransaction()
              .replace(R.id.fragment_container, new MonthFragment())
              .commit();
    }
  }
}