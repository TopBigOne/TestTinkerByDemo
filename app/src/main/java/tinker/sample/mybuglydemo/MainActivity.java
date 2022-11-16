package tinker.sample.mybuglydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Tinker.MainActivity";
    Button requestPatchButton;
    Button requestConfigButton;
    Button cleanPatchButton;
    Button killSelfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Log.e(TAG, "i am on onCreate classloader:" + Objects.requireNonNull(MainActivity.class.getClassLoader()));
        //test resource change
        Log.e(TAG, "i am on onCreate string:" + getResources().getString(R.string.test_resource));
        initEvent();

    }

    private void initEvent() {
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

    private void initView() {
        requestPatchButton = (Button) findViewById(R.id.requestPatch);
        requestConfigButton = (Button) findViewById(R.id.requestConfig);
        cleanPatchButton = (Button) findViewById(R.id.cleanPatch);
        killSelfButton = (Button) findViewById(R.id.killSelf);
    }
}