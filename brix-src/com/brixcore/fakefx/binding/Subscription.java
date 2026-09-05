package com.brixcore.fakefx.binding;

import com.brixcore.fakefx.beans.InvalidationListener;
import com.brixcore.fakefx.beans.Observable;
import com.brixcore.fakefx.beans.value.ChangeListener;
import com.brixcore.fakefx.beans.value.ObservableValue;
import java.util.Objects;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes6.dex */
@FunctionalInterface
public interface Subscription {
    public static final Subscription EMPTY = new Subscription() { // from class: com.brixcore.fakefx.binding.Subscription$$ExternalSyntheticLambda5
        @Override // com.brixcore.fakefx.binding.Subscription
        public final void unsubscribe() {
            Subscription.lambda$static$0();
        }
    };

    void unsubscribe();

    static /* synthetic */ void lambda$static$0() {
    }

    default Subscription and(final Subscription other) {
        Objects.requireNonNull(other);
        return new Subscription() { // from class: com.brixcore.fakefx.binding.Subscription$$ExternalSyntheticLambda0
            @Override // com.brixcore.fakefx.binding.Subscription
            public final void unsubscribe() {
                this.f$0.lambda$and$1(other);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* synthetic */ default void lambda$and$1(Subscription other) {
        unsubscribe();
        other.unsubscribe();
    }

    static <T> Subscription subscribe(final ObservableValue<T> observableValue, final Consumer<? super T> subscriber) {
        Objects.requireNonNull(observableValue);
        Objects.requireNonNull(subscriber);
        final ChangeListener<? super T> changeListener = new ChangeListener() { // from class: com.brixcore.fakefx.binding.Subscription$$ExternalSyntheticLambda3
            @Override // com.brixcore.fakefx.beans.value.ChangeListener
            public final void changed(ObservableValue observableValue2, Object obj, Object obj2) {
                subscriber.accept(obj2);
            }
        };
        subscriber.accept(observableValue.getValue2());
        observableValue.addListener(changeListener);
        return new Subscription() { // from class: com.brixcore.fakefx.binding.Subscription$$ExternalSyntheticLambda4
            @Override // com.brixcore.fakefx.binding.Subscription
            public final void unsubscribe() {
                observableValue.removeListener(changeListener);
            }
        };
    }

    static Subscription subscribeInvalidations(final ObservableValue<?> observableValue, final Runnable runnable) {
        Objects.requireNonNull(observableValue);
        Objects.requireNonNull(runnable);
        final InvalidationListener listener = new InvalidationListener() { // from class: com.brixcore.fakefx.binding.Subscription$$ExternalSyntheticLambda1
            @Override // com.brixcore.fakefx.beans.InvalidationListener
            public final void invalidated(Observable observable) {
                runnable.run();
            }
        };
        observableValue.addListener(listener);
        return new Subscription() { // from class: com.brixcore.fakefx.binding.Subscription$$ExternalSyntheticLambda2
            @Override // com.brixcore.fakefx.binding.Subscription
            public final void unsubscribe() {
                observableValue.removeListener(listener);
            }
        };
    }
}
