package com.example.tetemalu.recordtime;

import androidx.lifecycle.*;

import java.time.*;
import java.util.*;

public class MainViewModel extends ViewModel {
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
  private final LiveData<List<TimeTableEntity>> timeTableLiveData =
          Transformations.switchMap(selectedDateLiveData, date -> {
            if(date == null) return new MutableLiveData<>(Collections.emptyList());
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime endExclusive = start.plusDays(1);
            TimeTableDao timeTableDao = AppDatabase.getInstance().getTimeTableDao();
            return timeTableDao.getAllWithinRange(start, endExclusive);
          });
  LiveData<List<TimeTableEntity>> getTimeTable() {
    return timeTableLiveData;
  }
}
