package com.creator.rxjavademo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
        test2();
        test3();
        test4();
    }

    public void test() {
        Observable<String> myObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello from observable");
                subscriber.onCompleted();
            }
        });

        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: got " + s);
            }
        };

        myObservable.subscribe(mySubscriber);
    }

    public void test2() {
        Observable.just("Hello world from test2")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "test2: got " + s);
                    }
                });
    }

    public void test3() {
        Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "integer=" + integer);
                    }
                });
    }

    public void test4() {
        final String url = "http://www.baidu.com";
        Observable.create(
                new Observable.OnSubscribe<String>() {
                    OkHttpClient client = new OkHttpClient();

                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            Response response = client.newCall(new Request.Builder().url(url).build()).execute();
                            subscriber.onNext(response.body().string());
                            if (!response.isSuccessful()) {
                                subscriber.onError(new Exception("error: " + response.message()));
                            } else {
                                subscriber.onCompleted();
                            }
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: msg=" + e.getMessage());
                    }

                    @Override
                    public void onNext(@NonNull String content) {
                        Log.d(TAG, "onNext: content.size=" + content.length());
                    }
                });
    }
}
