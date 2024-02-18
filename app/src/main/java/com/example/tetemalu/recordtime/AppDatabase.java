package com.example.tetemalu.recordtime;

import android.content.Context;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {TimeTableEntity.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
  private static AppDatabase INSTANCE = null;

  synchronized static AppDatabase getInstance(Context context) {
    if(INSTANCE == null) {
      INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "TimeTable.db")
              .addCallback(new Callback() {
                @Override
                public void onCreate(SupportSQLiteDatabase db) {
                  super.onCreate(db);

                  //テスト用初期データ
                  db.execSQL("INSERT INTO TimeTable (title, datetime, done) VALUES " +
                          "('テスト1', '2023-02-18 10:20:30', 1)," +
                          "('テスト2', '2023-02-19 11:21:31', 0)," +
                          "('テスト3', '2023-02-19 22:32:42', 0)"
                  );
                  /* DAO を使って出来るという海外の記事を見たが例外が発生してしまった
                  TimeTableDao dao = INSTANCE.getTimeTableDao();
                  dao.insertAll(
                          new TimeTableEntity("テスト1", LocalDateTime.of(2023,2,18,10,20,30), true),
                          new TimeTableEntity("テスト2", LocalDateTime.of(2023,2,19,11,21,31), false),
                          new TimeTableEntity("テスト3", LocalDateTime.of(2023,2,19,22,32,42), false)
                  );
                  */
                }
              })
              .build();
    }
    return INSTANCE;
  }

  public abstract TimeTableDao getTimeTableDao();
}
