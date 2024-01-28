package com.example.tetemalu.recordtime;

import android.app.Application;
import android.content.Context;
import android.util.*;

import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class MainViewModel extends AndroidViewModel {
  private static final String LOG_TAG = "MainViewModel";

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  public MainViewModel(@NonNull Application application) {
    super(application);
  }

  @Override
  protected void onCleared() {
    try {
      executor.shutdown();
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch(Exception e) {
      Log.w(LOG_TAG, "termination time-out. shutdown now.");
      executor.shutdownNow();
    } finally {
      super.onCleared();
    }
  }

  private final MutableLiveData<List<Entry>> entryListLiveData = new MutableLiveData<>(Collections.emptyList());
  LiveData<List<Entry>> getEntryList() { return entryListLiveData; }

  void requestEntryList(LocalDate where) {
    executor.execute(() -> {
      try {
        Entries entries = Entries.load(getApplication());
        entryListLiveData.postValue(entries.getEntryListByDate(where));
      } catch(Exception e) {
        e.printStackTrace();
      }
    });
  }
  LiveData<Entry> insert(LocalDate date, String title) {
    return execute(entries -> entries.insert(date, title));
  }
  LiveData<Entry> update(int id, String title) {
    return execute(entries -> entries.update(id, title));
  }
  LiveData<Entry> delete(int id) {
    return execute(entries -> entries.delete(id));
  }
  private LiveData<Entry> execute(Function<Entries,Entry> process) {
    MutableLiveData<Entry> entryLiveData = new MutableLiveData<>(null);
    executor.execute(() -> {
      Context context = getApplication();
      try {
        Entries entries = Entries.load(context);
        Entry entry = process.apply(entries);
        entries.save(context);
        entryLiveData.postValue(entry);
      } catch(Exception e) {
        e.printStackTrace();
      }
    });
    return entryLiveData;
  }
}

class Entries {
  private static final String FILENAME = "entries.json";

  private static final class LocalDateAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(JsonWriter out, LocalDate src) throws IOException {
      if(src == null) out.nullValue(); //null-safe
      else out.value(src.toString());
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
      if(in.peek() == JsonToken.NULL) { //null-safe
        in.nextNull();
        return null;
      }
      return LocalDate.parse(in.nextString());
    }
  }

  private static Gson getGson() {
    return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting() //debug時用
            .create();
  }

  static Entries load(Context context) {
    File file = new File(context.getFilesDir(), FILENAME);
    if(file.exists()) {
      try(InputStream is = Files.newInputStream(file.toPath());
          BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
        Entries entries = getGson().fromJson(r, Entries.class);
        if(entries != null) return entries;
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    return new Entries();
  }

  private int nextId = 1;
  private List<Entry> entryList = new ArrayList<>();

  void save(Context context) {
    File file = new File(context.getFilesDir(), FILENAME);
    try(OutputStream os = Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(os))) {
      getGson().toJson(this, w);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  List<Entry> getEntryListByDate(@NonNull LocalDate date) {
    List<Entry> result = new ArrayList<>();
    for(Entry entry : entryList) {
      if(entry.date.equals(date)) result.add(entry);
    }
    return result;
  }

  Entry insert(@NonNull LocalDate date, @NonNull String title) {
    Entry entry = new Entry(nextId++, date, title);
    entryList.add(entry);
    return entry;
  }

  Entry update(int id, @NonNull String title) {
    for(int i = 0; i < entryList.size(); i++) {
      if(entryList.get(i).id == id) {
        Entry entry = new Entry(id, entryList.get(i).date, title);
        entryList.set(i, entry);
        return entry;
      }
    }
    return null;
  }

  Entry delete(int id) {
    for(int i = 0; i < entryList.size(); i++) {
      if(entryList.get(i).id == id) return entryList.remove(i);
    }
    return null;
  }
}

class Entry {
  static int INVALID_ID = -1;

  final int id; // 0 は使わず, 1 以上ということにしておく
  @NonNull
  final LocalDate date;
  @NonNull
  final String title;

  Entry(int id, @NonNull LocalDate date, @NonNull String title) {
    this.id = id;
    this.date = date;
    this.title = title;
  }
}