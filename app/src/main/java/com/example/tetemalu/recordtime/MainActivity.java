package com.example.tetemalu.recordtime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.*;

import android.os.Bundle;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
  private static final String REQUESTKEY_MONTH_FRAGMENT = "month_fragment";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FragmentManager fm = getSupportFragmentManager();

    //月フラグメントからの通知を受けて画面を変更
    fm.setFragmentResultListener(REQUESTKEY_MONTH_FRAGMENT, this, (rkey, result) -> {
      LocalDate date = (LocalDate) result.getSerializable(MonthFragment.RESULT_DATE);

      // 日付画面フラグメントを表示
      fm.beginTransaction()
              .replace(R.id.month_fragment_container, DateFragment.getInstance(date))
              .addToBackStack(null)
              .commit();
    });

    // 月画面フラグメントを表示
    if(savedInstanceState == null) {
      fm.beginTransaction()
              .replace(R.id.month_fragment_container, MonthFragment.getInstance(REQUESTKEY_MONTH_FRAGMENT))
              .commit();
    }
  }
}