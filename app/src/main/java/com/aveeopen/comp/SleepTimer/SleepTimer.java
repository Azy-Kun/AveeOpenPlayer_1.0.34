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

package com.aveeopen.comp.SleepTimer;

import android.os.CountDownTimer;

import com.aveeopen.Common.Events.WeakEvent;
import com.aveeopen.Common.Events.WeakEvent3;

import java.lang.ref.WeakReference;

public class SleepTimer {

    public static WeakEvent3<Boolean /*enabled*/, Integer /*minutes*/, Boolean /*playLastSongToEnd*/> onSleepTimerConfigChanged = new WeakEvent3<>();
    public static WeakEvent onSleepTimerFire = new WeakEvent();

    private static final Object createInstanceLock = new Object();
    private static volatile WeakReference<SleepTimer> instanceWeak = new WeakReference<>(null);
    private boolean enabled = false;
    private int minutes = 0;
    private boolean playLastSongToEnd = false;
    private int remainingSeconds = 0;
    private MyCountDownTimer countDownTimer = null;

    public SleepTimer() {
        onSleepTimerConfigChanged.invoke(enabled, minutes, playLastSongToEnd);
    }

    public static SleepTimer createOrGetInstance() {

        SleepTimer inst0 = instanceWeak.get();
        if (inst0 != null) return inst0;

        synchronized (createInstanceLock) {
            SleepTimer inst = instanceWeak.get();
            if (inst == null) {
                inst = new SleepTimer();
                instanceWeak = new WeakReference<>(inst);
            }

            return inst;
        }
    }

    public void configure(boolean enabled, int minutes, boolean playLastSongToEnd) {

        if (minutes <= 0) enabled = false;

        this.enabled = enabled;
        this.minutes = minutes;
        this.playLastSongToEnd = playLastSongToEnd;
        this.remainingSeconds = this.minutes * 60;

        //TODO: don't recreate every time MyCountDownTimer, we very often call this method from ui
        if (this.enabled) {
            if (countDownTimer != null)
                countDownTimer.cancel();
            countDownTimer = new MyCountDownTimer(this.minutes * 60 * 1000);
            countDownTimer.start();
        } else {
            if (countDownTimer != null)
                countDownTimer.cancel();
            countDownTimer = null;
        }

        onSleepTimerConfigChanged.invoke(enabled, minutes, playLastSongToEnd);
    }

    public SleepTimerConfig getConfig() {

        SleepTimerConfig config = new SleepTimerConfig();
        config.enabled = enabled;
        config.minutes = minutes;
        config.playLastSongToEnd = playLastSongToEnd;

        return config;
    }

    public int getRemainingSeconds() {
        return this.remainingSeconds;
    }

    private void fire() {
        configure(false, minutes, playLastSongToEnd);
        onSleepTimerFire.invoke();
    }

    class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture) {
            super(millisInFuture, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            remainingSeconds = (int) (millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            remainingSeconds = 0;
            SleepTimer.this.fire();
        }
    }
}
