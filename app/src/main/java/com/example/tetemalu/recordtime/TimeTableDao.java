package com.example.tetemalu.recordtime;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface TimeTableDao {
  @Insert
  long[] insertAll(TimeTableEntity... timeTableEntitys);

  @Query("SELECT * FROM TimeTable")
  LiveData<List<TimeTableEntity>> getAll();
}
