package com.loprok.twa;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsService;
import androidx.browser.trusted.TrustedWebActivityIntentBuilder;
import androidx.browser.trusted.sharing.ShareData;
import androidx.browser.trusted.sharing.ShareTarget;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.google.androidbrowserhelper.trusted.SharingUtils;
import com.google.androidbrowserhelper.trusted.TwaLauncher;
import com.google.androidbrowserhelper.trusted.splashscreens.SplashScreenStrategy;
import java.util.List;
import org.json.JSONException;

public class LauncherActivity
        extends com.google.androidbrowserhelper.trusted.LauncherActivity {

    private static final String TAG = "LopRokLauncher";
    private static final String CHROME_PACKAGE = "com.android.chrome";

    private boolean mTwaLaunched = false;
    private TwaLauncher mTwaLauncher;

    @Override
    protected boolean shouldLaunchImmediately() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force dark mode at activity level
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Install AndroidX SplashScreen API
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        // Keep the starting splash screen on screen until TWA launches
        splashScreen.setKeepOnScreenCondition(() -> !mTwaLaunched);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = 
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            getWindow().setNavigationBarDividerColor(0xFF000000);
        }

        getWindow().setStatusBarColor(0xFF000000);
        getWindow().setNavigationBarColor(0xFF000000);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
            controller.setAppearanceLightNavigationBars(false);
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        super.onCreate(savedInstanceState);

        launchTwa();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mTwaLaunched) {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mTwaLaunched = false;
        launchTwa();
    }

    @Override
    protected void launchTwa() {
        if (mTwaLaunched) {
            return;
        }
        mTwaLaunched = true;

        String provider = getPreferredProvider();
        Log.d(TAG, "LopRok TWA launching with provider: " + (provider != null ? provider : "default"));

        if (mTwaLauncher != null) {
            mTwaLauncher.destroy();
        }
        mTwaLauncher = new TwaLauncher(this, provider);

        CustomTabColorSchemeParams colorSchemeParams = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(0xFF000000)
                .setSecondaryToolbarColor(0xFF000000)
                .setNavigationBarColor(0xFF000000)
                .setNavigationBarDividerColor(0xFF000000)
                .build();

        TrustedWebActivityIntentBuilder builder = new TrustedWebActivityIntentBuilder(getLaunchingUrl())
                .setColorScheme(CustomTabsIntent.COLOR_SCHEME_DARK)
                .setDefaultColorSchemeParams(colorSchemeParams)
                .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, colorSchemeParams)
                .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, colorSchemeParams);

        addShareDataIfPresent(builder);

        SplashScreenStrategy splashStrategy = new com.google.androidbrowserhelper.trusted.splashscreens.PwaWrapperSplashScreenStrategy(
                this,
                R.drawable.ic_splash_branded,
                0xFF000000,
                android.widget.ImageView.ScaleType.CENTER,
                null,
                0,
                getString(R.string.providerAuthority),
                true
        );

        mTwaLauncher.launch(
                builder,
                getCustomTabsCallback(),
                splashStrategy,
                () -> Log.d(TAG, "TWA launch completed"),
                getFallbackStrategy()
        );
    }

    private void addShareDataIfPresent(TrustedWebActivityIntentBuilder builder) {
        ShareData shareData = SharingUtils.retrieveShareDataFromIntent(getIntent());
        if (shareData == null) {
            return;
        }
        String shareTargetJson = null;
        try {
            shareTargetJson = getString(R.string.shareTarget);
        } catch (Exception e) {
            Log.w(TAG, "Failed to read shareTarget string", e);
        }
        if (shareTargetJson == null || "null".equals(shareTargetJson)) {
            Log.d(TAG, "Failed to share: share target not defined");
            return;
        }
        try {
            ShareTarget shareTarget = SharingUtils.parseShareTargetJson(shareTargetJson);
            builder.setShareParams(shareTarget, shareData);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to parse share target json: " + e);
        }
    }

    private String getPreferredProvider() {
        try {
            PackageManager pm = getPackageManager();
            Intent serviceIntent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION)
                    .setPackage(CHROME_PACKAGE);
            List<ResolveInfo> services = pm.queryIntentServices(serviceIntent, 0);
            if (services != null && !services.isEmpty()) {
                return CHROME_PACKAGE;
            }
        } catch (Exception e) {
            Log.w(TAG, "Error checking Chrome provider availability", e);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        if (mTwaLauncher != null) {
            mTwaLauncher.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected Uri getLaunchingUrl() {

        try {
            Uri url = super.getLaunchingUrl();

            if (url != null) {
                return url;
            }

        } catch (Exception ignored) {
            // Use fallback URL below.
        }

        return Uri.parse("https://loprok.com/home");
    }
}