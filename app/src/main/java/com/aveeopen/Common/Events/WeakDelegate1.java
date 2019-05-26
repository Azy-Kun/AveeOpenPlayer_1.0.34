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

import java.lang.ref.WeakReference;
import java.util.List;

public class WeakDelegate1<T1> {

    WeakReference<Handler<T1>> listenerWeak = new WeakReference<>(null);

    public void clear() {
        listenerWeak = new WeakReference<>(null);
    }

    public WeakDelegate1<T1> subscribeWeak(Handler<T1> listener, List<Object> listenerRefHolder) {
        listenerRefHolder.add(listener);
        listenerWeak = new WeakReference<>(listener);
        return this;
    }

    public Handler subscribeHoldWeak(Handler<T1> listener) {
        listenerWeak = new WeakReference<>(listener);
        return listener;
    }

    public void invoke(T1 arg1) {
        Handler<T1> listener = listenerWeak.get();
        if (listener != null)
            listener.invoke(arg1);
    }

    public interface Handler<T1> {
        void invoke(T1 t1);
    }

}

