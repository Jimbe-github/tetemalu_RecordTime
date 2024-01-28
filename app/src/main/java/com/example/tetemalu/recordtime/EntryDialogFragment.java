package com.example.tetemalu.recordtime;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;

public class EntryDialogFragment extends DialogFragment {
  static final String RESULT_TITLE = "title";
  static final String RESULT_ID = "id";

  private static final String ARGS_DATE = "date";
  private static final String ARGS_TITLE = "title";
  private static final String ARGS_ID = "id";

  /**
   * EntryDialogFragment を生成する際はこのメソッドを使用すること
   * @param date 日付
   * @param title タイトル初期値
   * @param id Entry の id. 新規の場合は 0 以下を指定
   * @return EntryDialogFragment オブジェクト
   */
  static EntryDialogFragment getInstance(@NonNull LocalDate date, String title, int id) {
    EntryDialogFragment fragment = new EntryDialogFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARGS_DATE, date);
    args.putString(ARGS_TITLE, title);
    if(id > 0) args.putInt(ARGS_ID, id);
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    Bundle args = getArguments();
    LocalDate date = args == null ? null : (LocalDate)args.getSerializable(ARGS_DATE);
    String title = args == null ? null : args.getString(ARGS_TITLE, null);
    int id = args == null ? Entry.INVALID_ID : args.getInt(ARGS_ID, Entry.INVALID_ID);

    EditText titleEdit = new EditText(getContext());
    titleEdit.setHint("title");
    if(title != null) {
      titleEdit.setText(title);
      titleEdit.setSelection(0, titleEdit.getText().length());
    }

    return new AlertDialog.Builder(getContext())
            .setTitle(date.toString())
            .setView(titleEdit)
            .setPositiveButton("登録", (d,w) -> {
              String newTitle = titleEdit.getText().toString();
              Bundle result = new Bundle();
              result.putString(RESULT_TITLE, newTitle);
              result.putInt(RESULT_ID, id);
              getParentFragmentManager().setFragmentResult("Entry", result);
            })
            .setNegativeButton("キャンセル", null)
            .create();
  }
}
