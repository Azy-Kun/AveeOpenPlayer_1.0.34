/*
 * Copyright 2019 Avee Player. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aveeopen.Common.Events;

import java.util.List;

public class WeakEventR5<T1, T2, T3, T4, T5, TResult> {

    nallar.collections.ConcurrentWeakHashMap listeners = new nallar.collections.ConcurrentWeakHashMap();

    public void subscribeWeak(Handler<T1, T2, T3, T4, T5, TResult> listener, List<Object> listenerRefHolder) {
        listenerRefHolder.add(listener);
        listeners.put(listener, this);
    }

    public Handler<T1, T2, T3, T4, T5, TResult> subscribeHoldWeak(Handler<T1, T2, T3, T4, T5, TResult> listener) {
        listeners.put(listener, this);
        return listener;
    }

    public TResult invoke(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, TResult defaultValue) {
        TResult result = defaultValue;

        for (Handler<T1, T2, T3, T4, T5, TResult> listener : (Iterable<Handler<T1, T2, T3, T4, T5, TResult>>) listeners.keySet()) {
            if (listener != null)
                result = listener.invoke(arg1, arg2, arg3, arg4, arg5);
        }

        return result;
    }

    public interface Handler<T1, T2, T3, T4, T5, TResult> {
        TResult invoke(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
    }

}
