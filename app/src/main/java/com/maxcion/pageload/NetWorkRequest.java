package com.maxcion.pageload;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetWorkRequest {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();

    public static void request(int pageNo, int failCount, Callback callback) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(300);
                    List<String> result = new ArrayList<>();
                    for (int i = 1; i <= 20; i++) {
                        result.add("第" + pageNo + "页的第" + i + "条数据");
                    }
                    if (pageNo == 3 && failCount < 2) {
                        mainThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFail();
                            }
                        });

                    } else {
                        mainThreadExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (pageNo >= 5) {
                                    result.remove(19);
                                }
                                callback.onSuccess(result);
                            }
                        });

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mainThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail();
                        }
                    });
                }
            }
        });

    }

    interface Callback {
        void onSuccess(List<String> result);

        void onFail();
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mHandler.post(command);
        }
    }
}
