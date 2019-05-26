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

public class WeakEvent3<T1, T2, T3> {

    nallar.collections.ConcurrentWeakHashMap listeners = new nallar.collections.ConcurrentWeakHashMap();

    public void subscribeWeak(Handler<T1, T2, T3> listener, List<Object> listenerRefHolder) {
        listenerRefHolder.add(listener);
        listeners.put(listener, this);
    }

    public Handler<T1, T2, T3> subscribeHoldWeak(Handler<T1, T2, T3> listener) {
        listeners.put(listener, this);
        return listener;
    }

    public void invoke(T1 arg1, T2 arg2, T3 arg3) {

        for (Handler<T1, T2, T3> listener : (Iterable<Handler<T1, T2, T3>>) listeners.keySet()) {
            if (listener != null)
                listener.invoke(arg1, arg2, arg3);
        }
    }

    public interface Handler<T1, T2, T3> {
        void invoke(T1 t1, T2 t2, T3 t3);
    }

}
