package com.example.tetemalu.recordtime;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converters {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //SQLite datetime 風?

  @TypeConverter
  public static LocalDateTime toLocalDateTime(String val) {
    return LocalDateTime.parse(val, formatter);
  }

  @TypeConverter
  public static String fromLocalDateTime(LocalDateTime val) {
    return formatter.format(val);
  }
}
