package com.example.tetemalu.recordtime;

import androidx.lifecycle.*;

import java.time.*;

public class MainViewModel extends ViewModel {
  private final MutableLiveData<LocalDate> selectedDateLiveData = new MutableLiveData<>(LocalDate.now());
  LiveData<LocalDate> getSelectedDate() {
    return selectedDateLiveData;
  }
  void setSelectedDate(LocalDate date) {
    selectedDateLiveData.setValue(date);
  }
}
