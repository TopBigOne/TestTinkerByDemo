package tinker.sample.mybuglydemo.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;

import java.util.HashMap;
import java.util.Objects;

import tinker.sample.mybuglydemo.BuildConfig;
import tinker.sample.mybuglydemo.R;
import tinker.sample.mybuglydemo.util.Utils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Tinker.MainActivity";
    Button requestPatchButton;
    Button requestConfigButton;
    Button cleanPatchButton;
    Button killSelfButton;
    Button btnLoadPatch;
    Button btnLoadLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate:  -----:package Name : "+getPackageName());
        setContentView(R.layout.activity_main);
        initView();
        Log.e(TAG, "i am on onCreate classloader:" + Objects.requireNonNull(MainActivity.class.getClassLoader()));
        //test resource change
        Log.e(TAG, "i am on onCreate string:" + getResources().getString(R.string.test_resource));
        initEvent();

        askForRequiredPermissions();
    }

    private void initView() {
        btnLoadPatch = (Button) findViewById(R.id.btn_load_patch);
        btnLoadLibrary = (Button) findViewById(R.id.btn_load_library);
        requestPatchButton = (Button) findViewById(R.id.requestPatch);
        requestConfigButton = (Button) findViewById(R.id.requestConfig);
        cleanPatchButton = (Button) findViewById(R.id.cleanPatch);
        killSelfButton = (Button) findViewById(R.id.killSelf);
    }

    private void initEvent() {

        btnLoadPatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:  start load patch.");
                TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
            }
        });

        btnLoadLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               testHotRealEnable();
            }
        });


        //immediately 为 true, 每次强制访问服务器更新
        requestPatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinkerPatch.with().fetchPatchUpdate(true);
            }
        });

        //immediately 为 true, 每次强制访问服务器更新
        requestConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinkerPatch.with().fetchDynamicConfig(new ConfigRequestCallback() {

                    @Override
                    public void onSuccess(HashMap<String, String> configs) {
                        TinkerLog.w(TAG, "request config success, config:" + configs);
                    }

                    @Override
                    public void onFail(Exception e) {
                        TinkerLog.w(TAG, "request config failed, exception:" + e);
                    }
                }, true);
            }
        });


        cleanPatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinkerPatch.with().cleanAll();
            }
        });


        killSelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    private void testHotRealEnable() {
        Log.d(TAG, "testHotRealEnable:  -----1:package Name : "+getPackageName());
        Log.d(TAG, "testHotRealEnable:  -----2");
    }

    private void askForRequiredPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    private boolean hasRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= 16) {
            final int res = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return res == PackageManager.PERMISSION_GRANTED;
        } else {
            // When SDK_INT is below 16, READ_EXTERNAL_STORAGE will also be granted if WRITE_EXTERNAL_STORAGE is granted.
            final int res = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return res == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.setBackground(true);
    }


}