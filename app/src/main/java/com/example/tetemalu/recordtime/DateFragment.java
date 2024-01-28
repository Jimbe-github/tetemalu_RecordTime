package com.example.tetemalu.recordtime;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.*;
import androidx.lifecycle.*;
import androidx.recyclerview.widget.*;

import java.time.LocalDate;
import java.util.*;

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

    TextView dateText = view.findViewById(R.id.date);
    dateText.setText(date.toString());

    MainViewModel model = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

    Adapter adapter = new Adapter();
    model.getEntryList().observe(getViewLifecycleOwner(), adapter::setList);

    RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    recyclerView.setAdapter(adapter);

    FragmentManager fm = getChildFragmentManager();

    fm.setFragmentResultListener("Entry", getViewLifecycleOwner(), (rkey,result) -> {
      int id = result.getInt(EntryDialogFragment.RESULT_ID, Entry.INVALID_ID);
      String title = result.getString(EntryDialogFragment.RESULT_TITLE, null);
      LiveData<Entry> entryLiveData =
              id == Entry.INVALID_ID   ? model.insert(date, title) :
              TextUtils.isEmpty(title) ? model.delete(id) :
                                         model.update(id, title);
      entryLiveData.observe(getViewLifecycleOwner(), entry -> model.requestEntryList(date));
    });

    Button addButton = view.findViewById(R.id.add);
    addButton.setOnClickListener(v -> EntryDialogFragment.getInstance(date, null, Entry.INVALID_ID).show(fm, null));

    model.requestEntryList(date);
  }

  private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final List<Entry> list = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    void setList(List<Entry> newList) {
      list.clear();
      list.addAll(newList); //防御コピー
      notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new Adapter.ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
      holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
      return list.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView textView;

      private ViewHolder(@NonNull ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
        textView = itemView.findViewById(android.R.id.text1);
      }

      void bind(Entry entry) {
        textView.setText(entry.title);
      }
    }
  }
}