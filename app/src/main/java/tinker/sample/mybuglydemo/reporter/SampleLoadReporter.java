/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tinker.sample.mybuglydemo.reporter;

import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.DisplayMetrics;
import android.util.Log;

import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.UpgradePatchRetry;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.io.File;


/**
 * optional, you can just use DefaultLoadReporter
 * Created by zhangshaowen on 16/4/13.
 */
public class SampleLoadReporter extends DefaultLoadReporter {
    private final static String TAG = "Tinker.SampleLoadReporter";

    public SampleLoadReporter(Context context) {
        super(context);
    }

    @Override
    public void onLoadPatchListenerReceiveFail(final File patchFile, int errorCode) {
        printLoadResult(patchFile, errorCode, -1);
        super.onLoadPatchListenerReceiveFail(patchFile, errorCode);
        SampleTinkerReport.onTryApplyFail(errorCode);
    }

    @Override
    public void onLoadResult(File patchDirectory, int loadCode, long cost) {
        super.onLoadResult(patchDirectory, loadCode, cost);
        TinkerLog.d(TAG, "onLoadResult: loadCode :" + loadCode + ", cost time : " + cost);
        printLoadResult(patchDirectory, loadCode, cost);
        if (loadCode == ShareConstants.ERROR_LOAD_OK) {
            SampleTinkerReport.onLoaded(cost);
        }

        Looper.getMainLooper().myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                if (UpgradePatchRetry.getInstance(context).onPatchRetryLoad()) {
                    SampleTinkerReport.onReportRetryPatch();
                }
                return false;
            }
        });


    }

    private void printLoadResult(File patchDirectory, int loadCode, long cost) {
        if (patchDirectory != null) {
            TinkerLog.i(TAG, "printLoadResult file path: " + patchDirectory.getAbsolutePath());
        }
        TinkerLog.d(TAG, "printLoadResult: loadCode : " + loadCode + ", cost time : " + cost);
        if (loadCode == ShareConstants.ERROR_LOAD_OK) {
            TinkerLog.d(TAG, "printLoadResult: ERROR_LOAD_OK");
            return;
        }
        if (loadCode == ShareConstants.ERROR_LOAD_DISABLE) {
            TinkerLog.e(TAG, "printLoadResult: ERROR_LOAD_DISABLE");
            return;
        }

        if (loadCode == ShareConstants.ERROR_LOAD_PATCH_INFO_NOT_EXIST) {
            TinkerLog.e(TAG, "printLoadResult: ERROR_LOAD_PATCH_INFO_NOT_EXIST");
            return;
        }
        if (loadCode == ShareConstants.ERROR_LOAD_PATCH_DIRECTORY_NOT_EXIST) {
            TinkerLog.e(TAG, "printLoadResult: ERROR_LOAD_PATCH_DIRECTORY_NOT_EXIST");
            return;
        }

        if (loadCode == ShareConstants.ERROR_LOAD_PATCH_VERSION_RESOURCE_MD5_MISMATCH) {
            TinkerLog.e(TAG, "printLoadResult: ERROR_LOAD_PATCH_VERSION_RESOURCE_MD5_MISMATCH");
            return;
        }

    }

    @Override
    public void onLoadException(Throwable e, int errorCode) {
        super.onLoadException(e, errorCode);
        SampleTinkerReport.onLoadException(e, errorCode);
    }

    @Override
    public void onLoadFileMd5Mismatch(File file, int fileType) {
        super.onLoadFileMd5Mismatch(file, fileType);
        SampleTinkerReport.onLoadFileMisMatch(fileType);
    }

    /**
     * try to recover patch oat file
     *
     * @param file
     * @param fileType
     * @param isDirectory
     */
    @Override
    public void onLoadFileNotFound(File file, int fileType, boolean isDirectory) {
        super.onLoadFileNotFound(file, fileType, isDirectory);
        SampleTinkerReport.onLoadFileNotFound(fileType);
    }

    @Override
    public void onLoadPackageCheckFail(File patchFile, int errorCode) {
        super.onLoadPackageCheckFail(patchFile, errorCode);
        SampleTinkerReport.onLoadPackageCheckFail(errorCode);
    }

    @Override
    public void onLoadPatchInfoCorrupted(String oldVersion, String newVersion, File patchInfoFile) {
        super.onLoadPatchInfoCorrupted(oldVersion, newVersion, patchInfoFile);
        SampleTinkerReport.onLoadInfoCorrupted();
    }

    @Override
    public void onLoadInterpret(int type, Throwable e) {
        super.onLoadInterpret(type, e);
        SampleTinkerReport.onLoadInterpretReport(type, e);
    }

    @Override
    public void onLoadPatchVersionChanged(String oldVersion, String newVersion, File patchDirectoryFile, String currentPatchName) {
        super.onLoadPatchVersionChanged(oldVersion, newVersion, patchDirectoryFile, currentPatchName);
    }

}
