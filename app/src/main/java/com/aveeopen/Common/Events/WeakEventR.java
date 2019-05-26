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

public class WeakEventR<TResult> {

    nallar.collections.ConcurrentWeakHashMap listeners = new nallar.collections.ConcurrentWeakHashMap();

    public void subscribeWeak(Handler<TResult> listener, List<Object> listenerRefHolder) {
        listenerRefHolder.add(listener);
        listeners.put(listener, this);
    }

    public Handler<TResult> subscribeHoldWeak(Handler<TResult> listener) {
        listeners.put(listener, this);
        return listener;
    }

    public TResult invoke(TResult defaultValue) {
        TResult result = defaultValue;

        for (Handler<TResult> listener : (Iterable<Handler<TResult>>) listeners.keySet()) {
            if (listener != null)
                result = listener.invoke();
        }

        return result;
    }

    public interface Handler<TResult> {
        TResult invoke();
    }

}
