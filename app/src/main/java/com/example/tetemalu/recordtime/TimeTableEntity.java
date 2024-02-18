package com.example.tetemalu.recordtime;

import androidx.room.*;

import java.time.LocalDateTime;

@Entity(tableName = "TimeTable")
public class TimeTableEntity {

  @PrimaryKey(autoGenerate = true)
  public int id;

  @ColumnInfo(name = "datetime")
  public LocalDateTime datetime;

  @ColumnInfo(name = "title")
  public String title;

  @ColumnInfo(name = "done")
  public boolean done;

  public TimeTableEntity(String title, LocalDateTime datetime, boolean done) {
    this.title = title;
    this.datetime = datetime;
    this.done = done;
  }

  @Override
  public String toString() {
    return new StringBuilder("TimeTableEntity")
            .append("[id=").append(id)
            .append(",title=").append(title)
            .append(",datetime=").append(datetime.toString())
            .append(",done=").append(done)
            .append("]").toString();
  }
}
