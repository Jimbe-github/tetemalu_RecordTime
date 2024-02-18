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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    super.onViewCreated(view, savedInstanceState);

    MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

    TextView monthText = view.findViewById(R.id.month);

    Adapter adapter = new Adapter(yearMonth -> {
      monthText.setText(yearMonth.getYear() + "年" + yearMonth.getMonthValue() + "月");
    });

    adapter.setDateClickListener(date -> {
      //日付がクリックされたら日付を設定(これによって MainActivity のオブザーバが動く)
      model.setSelectedDate(date);
    });

    Button next = view.findViewById(R.id.next);
    next.setOnClickListener(v -> adapter.next());

    Button prev = view.findViewById(R.id.prev);
    prev.setOnClickListener(v -> adapter.prev());

    RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
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
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_day, parent, false));
        dateBox = itemView.findViewById(R.id.day);
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