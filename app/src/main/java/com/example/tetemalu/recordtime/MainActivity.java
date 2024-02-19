package com.example.tetemalu.recordtime;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MainViewModel model = new ViewModelProvider(this).get(MainViewModel.class);
    model.setDatabase(AppDatabase.getInstance(this));

    SlidingPaneLayout slidingLayout = findViewById(R.id.sliding_layout);
    model.getSelectedDate().observe(this, date -> {
      if(date != null) slidingLayout.open();
    });

    getOnBackPressedDispatcher().addCallback(this, new TwoPaneOnBackPressedCallback(slidingLayout));
  }

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