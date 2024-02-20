package com.example.tetemalu.recordtime;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MainViewModel model = MainViewModel.getInstance(this);

    SlidingPaneLayout slidingLayout = findViewById(R.id.sliding_layout);
    model.getSelectedDate().observe(this, date -> {
      if(date != null) slidingLayout.open();
    });

    //戻るボタンの処理を登録
    getOnBackPressedDispatcher().addCallback(this, new TwoPaneOnBackPressedCallback(slidingLayout));
  }

  //1 ペイン状態かつ open(DateFragment が表示されている)状態で戻るボタンが押されたら close(MonthFragment が表示される)状態にする
  private static class TwoPaneOnBackPressedCallback extends OnBackPressedCallback implements SlidingPaneLayout.PanelSlideListener {
    private final SlidingPaneLayout slidingPaneLayout;

    TwoPaneOnBackPressedCallback(@NonNull SlidingPaneLayout slidingPaneLayout) {
      super(slidingPaneLayout.isSlideable() && slidingPaneLayout.isOpen());
      this.slidingPaneLayout = slidingPaneLayout;
      slidingPaneLayout.addPanelSlideListener(this);
    }

    @Override
    public void handleOnBackPressed() {
      slidingPaneLayout.closePane();
    }

    @Override
    public void onPanelSlide(@NonNull View panel, float slideOffset) {
      //no process
    }

    @Override
    public void onPanelOpened(@NonNull View panel) {
      setEnabled(true);
    }

    @Override
    public void onPanelClosed(@NonNull View panel) {
      setEnabled(false);
    }
  }
}