package com.example.tetemalu.recordtime;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;

public class DateFragment extends Fragment {
  private static final String ARGS_DATE = "date";

  /**
   * DateFragment を生成する際はこのメソッドを使用すること
   * @param date 日付
   * @return DateFragment オブジェクト
   */
  static DateFragment getInstance(@NonNull LocalDate date) {
    DateFragment fragment = new DateFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARGS_DATE, date);
    fragment.setArguments(args);
    return fragment;
  }

  DateFragment() {
    super(R.layout.date_fragment);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Bundle args = getArguments();
    LocalDate date = args == null ? null : (LocalDate)args.getSerializable(ARGS_DATE);
    if(date == null) throw new IllegalArgumentException("Parameter 'date' is missing");

    // 〇月をセット
    TextView month_textview = view.findViewById(R.id.selected_month);
    month_textview.setText(String.valueOf(date.getMonthValue()));

    // 〇日をセット
    TextView date_textview = view.findViewById(R.id.selected_date);
    date_textview.setText(String.valueOf(date.getDayOfMonth()));

    // 日付カレンダーフラグメントを設定(フラグメント自身のフラグメントマネージャは getChildFragmentManager で得る)
    getChildFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainerView, new DailyCalendarFragment())
            .addToBackStack(null)
            .commit();
  }
}