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

package com.aveeopen.Common;

import android.util.Log;

public class tlog {

    static final boolean LOG = false;//Set to false in PUBLISH VERSION

    private static String getLogTagWithMethod(String prefix) {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        return prefix + trace[2].getFileName() + "." + trace[2].getMethodName() + ":" + trace[2].getLineNumber();
    }

    public static void w(String msg) {
        if(LOG) Log.w(getLogTagWithMethod("###"), ":" + msg);
    }

    public static void d(String msg) {
        if(LOG) Log.d(getLogTagWithMethod("###"), ":" + msg);
    }

    public static void e(String msg) {
        if(LOG) Log.e(getLogTagWithMethod("###"), ":" + msg);
    }

    public static void notice(String msg) {
        if(LOG) Log.w(getLogTagWithMethod("###"), ":" + msg);
    }

}


