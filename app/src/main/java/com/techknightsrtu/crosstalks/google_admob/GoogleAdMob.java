package com.techknightsrtu.crosstalks.google_admob;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.techknightsrtu.crosstalks.R;

public class GoogleAdMob {

    private Activity activity;
    private FrameLayout ad_view_container;
    private AdView adView;

    public GoogleAdMob(Activity activity, FrameLayout ad_view_container) {
        this.activity = activity;
        this.ad_view_container = ad_view_container;
    }

    public void loadAd() {
        ad_view_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });
    }

    private void loadBanner() {
        // Create an ad request.
        adView = new AdView(activity);
        adView.setAdUnitId(activity.getResources().getString(R.string.AD_UNIT_ID));
        ad_view_container.removeAllViews();
        ad_view_container.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = ad_view_container.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationBannerAdSizeWithWidth(activity, adWidth);
    }

}
