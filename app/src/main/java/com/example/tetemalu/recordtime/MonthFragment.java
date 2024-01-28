package com.example.tetemalu.recordtime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.*;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.*;
import java.util.*;
import java.util.function.Consumer;

public class MonthFragment extends Fragment {
  static final String RESULT_DATE = "date";

  private static final String ARGS_REQUESTKEY = "request_key";

  /**
   * MonthFragment を生成する際はこのメソッドを使用すること
   * @param requestKey 日付をクリックした際に親に通知するリクエストキー
   * @return MonthFragment オブジェクト
   */
  static MonthFragment getInstance(@NonNull String requestKey) {
    MonthFragment fragment = new MonthFragment();
    Bundle args = new Bundle();
    args.putString(ARGS_REQUESTKEY, requestKey);
    fragment.setArguments(args);
    return fragment;
  }

  MonthFragment() {
    super(R.layout.month_fragment);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Bundle args = getArguments();
    String requestKey = args == null ? null : args.getString(ARGS_REQUESTKEY, null);
    if(requestKey == null) throw new IllegalArgumentException("Parameter 'requestKey' is missing");

    TextView yearText = view.findViewById(R.id.year);
    TextView monthText = view.findViewById(R.id.month);

    Adapter adapter = new Adapter(yearMonth -> {
      //アダプタの年月を貰って表示する
      yearText.setText(yearMonth.getYear() + "年");
      monthText.setText(yearMonth.getMonthValue() + "月");
    });

    adapter.setDateClickListener(date -> {
      //日付がクリックされたら FragmentManager を通じて親に通知
      Bundle result = new Bundle();
      result.putSerializable(RESULT_DATE, date);
      getParentFragmentManager().setFragmentResult(requestKey, result);
    });

    Button next = view.findViewById(R.id.next);
    next.setOnClickListener(v -> adapter.next());

    Button prev = view.findViewById(R.id.prev);
    prev.setOnClickListener(v -> adapter.prev());

    RecyclerView recyclerView = view.findViewById(R.id.date_recycler_view_container);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
    recyclerView.setAdapter(adapter);
  }

  private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final Locale locale = Locale.getDefault();
    private final DayOfWeek firstDayOfWeek = WeekFields.of(locale).getFirstDayOfWeek(); //週の始まり

    private YearMonth current;
    private LocalDate start;

    private final Consumer<YearMonth> yearMonthObserver;
    private Consumer<LocalDate> dateClickListener;

    Adapter(Consumer<YearMonth> yearMonthObserver) {
      this.yearMonthObserver = yearMonthObserver;

      setYearMonth(YearMonth.now());
    }

    void setDateClickListener(Consumer<LocalDate> dateClickListener) {
      this.dateClickListener = dateClickListener;
    }

    void next() {
      setYearMonth(current.plusMonths(1));
    }

    void prev() {
      setYearMonth(current.minusMonths(1));
    }

    private void setYearMonth(YearMonth yearMonth) {
      this.current = yearMonth;
      start = current.atDay(1).with(TemporalAdjusters.previousOrSame(firstDayOfWeek)); //月初直前の週始め
      notifyItemRangeChanged(0, 6*7);
      if(yearMonthObserver != null) yearMonthObserver.accept(current);
    }

    @Override
    public int getItemCount() {
      return 6*7; //6週間分
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.bind(position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView dateBox;

      private LocalDate date;

      ViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.date, parent, false));
        dateBox = itemView.findViewById(R.id.every_date);
        dateBox.setClickable(true);
        dateBox.setOnClickListener(v -> {
          if(dateClickListener != null && date != null) dateClickListener.accept(date);
        });
      }

      void bind(int position) {
        date = start.plusDays(position);
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, locale);
        dateBox.setText(date.getDayOfMonth() + "(" + dayOfWeek + ")");
      }
    }
  }
}