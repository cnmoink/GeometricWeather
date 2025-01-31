package org.breezyweather.common.rxjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class ObserverContainer<T> extends DisposableObserver<T> {

    @NonNull private final CompositeDisposable compositeDisposable;
    @Nullable private final Observer<T> observer;

    public ObserverContainer(@NonNull CompositeDisposable disposable, @Nullable Observer<T> observer) {
        this.compositeDisposable = disposable;
        this.observer = observer;
    }

    @Override
    protected void onStart() {
        compositeDisposable.add(this);
        if (observer != null) {
            observer.onSubscribe(this);
        }
    }

    @Override
    public void onNext(@NonNull T t) {
        if (observer != null) {
            observer.onNext(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (observer != null) {
            observer.onError(e);
        }
        compositeDisposable.remove(this);
    }

    @Override
    public void onComplete() {
        if (observer != null) {
            observer.onComplete();
        }
        compositeDisposable.remove(this);
    }
}