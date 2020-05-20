package com.workoss.boot.util.event;

public interface Subscriber<E extends Event> {

    void onEvent(E event);

    void onError(Throwable throwable);
}
