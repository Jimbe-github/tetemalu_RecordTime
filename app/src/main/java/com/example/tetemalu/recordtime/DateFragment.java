package com.example.tetemalu.recordtime;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.*;
import androidx.fragment.app.*;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import java.util.*;

public class DateFragment extends Fragment {
  public DateFragment() {
    super(R.layout.date_fragment);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

    TextView year_text = view.findViewById(R.id.selected_year);
    TextView month_text = view.findViewById(R.id.selected_month);
    TextView date_text = view.findViewById(R.id.selected_date);

    model.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
      year_text.setText(date.getYear() + " 年");
      month_text.setText(date.getMonthValue() + " 月");
      date_text.setText(date.getDayOfMonth() + " 日");
    });

    Adapter adapter = new Adapter();

    RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);

    TimeTableDao timeTableDao = AppDatabase.getInstance(getContext()).getTimeTableDao();
    timeTableDao.getAll().observe(getViewLifecycleOwner(), adapter::setList);
  }

  private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private static class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView textView;

      ViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
        textView = itemView.findViewById(android.R.id.text1);
      }

      void bind(TimeTableEntity entity) {
        textView.setText(entity.title + " / " + entity.datetime.toString());
        textView.setBackgroundColor(entity.done ? Color.rgb(124,252,0) : Color.rgb(249,247,57)); // done=赤, !done=黄色
      }
    }

    private List<TimeTableEntity> list = Collections.emptyList();

    @SuppressLint("NotifyDataSetChanged")
    void setList(@NonNull List<TimeTableEntity> list) {
      this.list = list;
      notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
      return list.size();
    }
  }
}