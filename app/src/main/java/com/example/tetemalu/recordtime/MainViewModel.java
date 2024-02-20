package com.example.tetemalu.recordtime;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.*;

import java.time.*;
import java.util.*;

public class MainViewModel extends ViewModel {
  private static class Factory implements ViewModelProvider.Factory {
    private Context context;
    Factory(Context context) {
      this.context = context;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      if(modelClass.isAssignableFrom(MainViewModel.class)) {
        return (T)new MainViewModel(AppDatabase.getInstance(context));
      }
      return ViewModelProvider.Factory.super.create(modelClass);
    }
  }

  static MainViewModel getInstance(FragmentActivity activity) {
    return new ViewModelProvider(activity, new MainViewModel.Factory(activity)).get(MainViewModel.class);
  }

  private final AppDatabase appDatabase;
  MainViewModel(@NonNull AppDatabase appDatabase) {
    this.appDatabase = appDatabase;

    addCloseable(appDatabase::close); //ViewModel が close する時に データベースも close (必要かは不明)
  }

  private final MutableLiveData<YearMonth> currentLiveData = new MutableLiveData<>(YearMonth.now());
  LiveData<YearMonth> getCurrent() {
    return currentLiveData;
  }
  void next() {
    currentLiveData.setValue(currentLiveData.getValue().plusMonths(1));
  }
  void prev() {
    currentLiveData.setValue(currentLiveData.getValue().minusMonths(1));
  }

  private final MutableLiveData<LocalDate> selectedDateLiveData = new MutableLiveData<>(null);
  LiveData<LocalDate> getSelectedDate() {
    return selectedDateLiveData;
  }
  void setSelectedDate(LocalDate date) {
    selectedDateLiveData.setValue(date);
  }

  //selectedDate が設定されたら DB から該当データを取得して自身を更新する.
  private LiveData<List<TimeTableEntity>> timeTableLiveData;
  LiveData<List<TimeTableEntity>> getTimeTable() {
    if(timeTableLiveData == null) {
      timeTableLiveData =
              Transformations.switchMap(selectedDateLiveData, date -> {
                if(date == null) return new MutableLiveData<>(Collections.emptyList());
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime endExclusive = start.plusDays(1);
                TimeTableDao timeTableDao = appDatabase.getTimeTableDao();
                return timeTableDao.getAllWithinRange(start, endExclusive);
              });
    }
    return timeTableLiveData;
  }
}
