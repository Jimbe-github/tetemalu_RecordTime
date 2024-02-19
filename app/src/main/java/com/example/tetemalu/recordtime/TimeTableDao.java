package com.example.tetemalu.recordtime;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.time.*;
import java.util.List;

@Dao
public interface TimeTableDao {
  @Insert
  long[] insertAll(TimeTableEntity... timeTableEntitys);

  @Query("SELECT * FROM TimeTable")
  LiveData<List<TimeTableEntity>> getAll();

  @Query("SELECT * FROM TimeTable WHERE :start <= datetime AND datetime < :endExclusive")
  LiveData<List<TimeTableEntity>> getAllWithinRange(LocalDateTime start, LocalDateTime endExclusive);
}
