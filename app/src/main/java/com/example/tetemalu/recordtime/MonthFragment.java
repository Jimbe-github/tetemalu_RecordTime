package com.example.tetemalu.recordtime;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.*;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.*;
import java.util.*;
import java.util.function.Consumer;

public class MonthFragment extends Fragment {
  public MonthFragment() {
    super(R.layout.month_fragment);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    MainViewModel model = MainViewModel.getInstance(requireActivity());

    TextView monthText = view.findViewById(R.id.month);
    model.getCurrent().observe(getViewLifecycleOwner(), yearMonth -> {
      //年月の表示を更新
      long millies = yearMonth.atDay(1).getLong(ChronoField.EPOCH_DAY) * 24 * 60 * 60 * 1000; //epoch[ms]
      monthText.setText(DateUtils.formatDateTime(requireContext(), millies, DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY));
    });

    Adapter adapter = new Adapter();
    adapter.setDayClickListener(model::setSelectedDate);
    model.getCurrent().observe(getViewLifecycleOwner(), adapter::setYearMonth);

    Button prevButton = view.findViewById(R.id.prev);
    prevButton.setOnClickListener(v -> model.prev());

    Button nextButton = view.findViewById(R.id.next);
    nextButton.setOnClickListener(v -> model.next());

    RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7));
    recyclerView.setAdapter(adapter);
  }

  private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek(); //週の始まり

    private Consumer<LocalDate> dayClickListener;

    private YearMonth current;
    private LocalDate start;

    private void setYearMonth(YearMonth yearMonth) {
      this.current = yearMonth;
      start = current.atDay(1).with(TemporalAdjusters.previousOrSame(firstDayOfWeek)); //月初直前の週始め
      notifyItemRangeChanged(7, 6*7); //日付部分
    }

    void setDayClickListener(Consumer<LocalDate> dayClickListener) {
      this.dayClickListener = dayClickListener;
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

    @Override
    public int getItemCount() {
      return 7 + 6*7;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
      private final int HEADER_BGCOLOR = Color.rgb(240,240,240);
      private final TextView day;
      private LocalDate date;
      ViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_day, parent, false));
        day = itemView.findViewById(R.id.day);
        day.setClickable(true);
        day.setOnClickListener(v -> {
          if(date != null && dayClickListener != null) dayClickListener.accept(date);
        });
      }
      void bind(int position) {
        if(position < 7) { //曜日名
          date = null;
          DayOfWeek dow = firstDayOfWeek.plus(position);
          day.setTextColor(getTextColor(dow));
          day.setBackgroundColor(HEADER_BGCOLOR);
          day.setText(dow.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()));
        } else {
          date = start.plusDays(position - 7);
          day.setTextColor(getTextColor(date));
          day.setBackgroundColor(getBackgroundColor(date));
          day.setText(String.valueOf(date.getDayOfMonth()));
        }
      }
      private int getTextColor(LocalDate date) {
        return date.getMonth() != current.getMonth() ? Color.LTGRAY : //前月・次月
               getTextColor(date.getDayOfWeek());
      }
      private int getTextColor(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SUNDAY   ? Color.RED  : //日曜
               dayOfWeek == DayOfWeek.SATURDAY ? Color.BLUE : //土曜
               Color.BLACK;
      }
      private int getBackgroundColor(LocalDate date) {
        return date.isEqual(LocalDate.now()) ? Color.CYAN : //今日
               Color.TRANSPARENT;
      }
    }
  }
}