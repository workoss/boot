package com.workoss.boot.util.concurrent.promise;

import java.util.concurrent.Future;

public interface FutureListener<V> {
    /**
     * Invoked when the operation associated with the {@link Future} has been completed.
     *
     * @param future the source {@link Future} which called this callback
     */
    void operationComplete(Future<V> future) throws Exception;
}
