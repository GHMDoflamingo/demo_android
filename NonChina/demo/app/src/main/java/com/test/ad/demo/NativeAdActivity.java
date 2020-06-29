package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventListener;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.NativeAd;
import com.anythink.network.mintegral.MintegralATConst;

import java.util.HashMap;
import java.util.Map;

public class NativeAdActivity extends Activity {

    private static String TAG = "NativeAdActivity";

    String unitIds[] = new String[]{
            DemoApplicaion.mPlacementId_native_all
            , DemoApplicaion.mPlacementId_native_facebook
            , DemoApplicaion.mPlacementId_native_banner_facebook
            , DemoApplicaion.mPlacementId_native_admob
            , DemoApplicaion.mPlacementId_native_inmobi
            , DemoApplicaion.mPlacementId_native_flurry
            , DemoApplicaion.mPlacementId_native_applovin
            , DemoApplicaion.mPlacementId_native_mintegral
            , DemoApplicaion.mPLacementId_native_automatic_rending_mintegral
            , DemoApplicaion.mPlacementId_native_mopub
            , DemoApplicaion.mPlacementId_native_appnext
            , DemoApplicaion.mPlacementId_native_nend

    };

    String unitGroupName[] = new String[]{
            "All network",
            "facebook",
            "faceboon native banner",
            "admob",
            "inmobi",
            "flurry",
            "applovin",
            "mintegral",
            "mintegral auto-rending",
            "mopub",
            "appnext",
            "nend"
    };

    ATNative atNatives[] = new ATNative[unitIds.length];
    ATNativeAdView anyThinkNativeAdView;
    NativeAd mNativeAd;

    RadioGroup mRadioGroup;

    int mCurrentSelectIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_native);

        mRadioGroup = (RadioGroup) findViewById(R.id.placement_select_group);

        for (int i = 0; i < unitIds.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(20, 20, 20, 20);
            radioButton.setText(unitGroupName[i]);
            radioButton.setId(i + 1000);
            mRadioGroup.addView(radioButton);
        }

        mRadioGroup.check(0);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mCurrentSelectIndex = i - 1000;
            }
        });


        int padding = dip2px(10);
        int adViewHeight = dip2px(340) - 2 * padding;

        final NativeDemoRender anyThinkRender = new NativeDemoRender(this);

        for (int i = 0; i < unitIds.length; i++) {
            atNatives[i] = new ATNative(this, unitIds[i], new ATNativeNetworkListener() {
                @Override
                public void onNativeAdLoaded() {
                    Log.i(TAG, "onNativeAdLoaded");
                    Toast.makeText(NativeAdActivity.this, "load success..."
                            , Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNativeAdLoadFail(AdError adError) {
                    Log.i(TAG, "onNativeAdLoadFail, " + adError.printStackTrace());
                    Toast.makeText(NativeAdActivity.this, "load fail...：" + adError.printStackTrace(), Toast.LENGTH_LONG).show();

                }
            });

            Map<String, Object> localMap = new HashMap<>();

            // since v5.6.2
            localMap.put(ATNative.KEY_WIDTH, getResources().getDisplayMetrics().widthPixels - 2 * padding);
            localMap.put(ATNative.KEY_HEIGHT, adViewHeight);

            // before v5.6.2
            //Mintegral
            localMap.put(MintegralATConst.AUTO_RENDER_NATIVE_WIDTH, getResources().getDisplayMetrics().widthPixels - 2 * padding);
            localMap.put(MintegralATConst.AUTO_RENDER_NATIVE_HEIGHT, adViewHeight);
            // before v5.6.2

            atNatives[i].setLocalExtra(localMap);

            if (anyThinkNativeAdView == null) {
                anyThinkNativeAdView = new ATNativeAdView(this);
            }
        }


        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atNatives[mCurrentSelectIndex].makeAdRequest();
            }
        });

        findViewById(R.id.loadcache_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NativeAd nativeAd = atNatives[mCurrentSelectIndex].getNativeAd();
                if (nativeAd != null) {
                    mNativeAd = nativeAd;
                    mNativeAd.setNativeEventListener(new ATNativeEventListener() {
                        @Override
                        public void onAdImpressed(ATNativeAdView view, ATAdInfo entity) {
                            Log.i(TAG, "native ad onAdImpressed:\n" + entity.toString());
                        }

                        @Override
                        public void onAdClicked(ATNativeAdView view, ATAdInfo entity) {
                            Log.i(TAG, "native ad onAdClicked:\n" + entity.toString());
                        }

                        @Override
                        public void onAdVideoStart(ATNativeAdView view) {
                            Log.i(TAG, "native ad onAdVideoStart");
                        }

                        @Override
                        public void onAdVideoEnd(ATNativeAdView view) {
                            Log.i(TAG, "native ad onAdVideoEnd");
                        }

                        @Override
                        public void onAdVideoProgress(ATNativeAdView view, int progress) {
                            Log.i(TAG, "native ad onAdVideoProgress:" + progress);
                        }
                    });
                    mNativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
                        @Override
                        public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                            Log.i(TAG, "native ad onAdCloseButtonClick:");
                            if (view.getParent() != null) {
                                ((ViewGroup) view.getParent()).removeView(view);
                            }
                        }
                    });
                    try {
                        mNativeAd.renderAdView(anyThinkNativeAdView, anyThinkRender);
                    } catch (Exception e) {

                    }

                    anyThinkNativeAdView.setVisibility(View.VISIBLE);
                    mNativeAd.prepare(anyThinkNativeAdView, anyThinkRender.getClickView(), null);

                } else {
                    Toast.makeText(NativeAdActivity.this, "this placement no cache!", Toast.LENGTH_LONG).show();

                }

            }
        });

        anyThinkNativeAdView.setPadding(padding, padding, padding, padding);
        anyThinkNativeAdView.setVisibility(View.GONE);
        ((FrameLayout) findViewById(R.id.ad_container)).addView(anyThinkNativeAdView, new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, adViewHeight));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNativeAd != null) {
            mNativeAd.destory();
        }
    }

    @Override
    protected void onPause() {
        if (mNativeAd != null) {
            mNativeAd.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mNativeAd != null) {
            mNativeAd.onResume();
        }
        super.onResume();
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
