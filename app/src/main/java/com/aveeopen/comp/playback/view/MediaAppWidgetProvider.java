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

package com.aveeopen.comp.playback.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.aveeopen.Design.WidgetAndNotificationDesign;
import com.aveeopen.MainActivity;
import com.aveeopen.comp.playback.MediaPlaybackService;
import com.aveeopen.comp.playback.MediaPlaybackServiceDefs;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.R;

public class MediaAppWidgetProvider extends AppWidgetProvider {

    private static MediaAppWidgetProvider sInstance;

    public static synchronized MediaAppWidgetProvider getInstance() {
        if (sInstance == null) {
            sInstance = new MediaAppWidgetProvider();
        }
        return sInstance;
    }

    public MediaAppWidgetProvider()
    {
        WidgetAndNotificationDesign.createInstance();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        defaultAppWidget(context, appWidgetIds, MediaPlaybackService.class);
        // Send broadcast intent to any running MediaPlaybackService so it can
        // wrap around with an immediate update.
        Intent updateIntent = new Intent(MediaPlaybackServiceDefs.APP_WIDGET_UPDATE_ACTION);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        updateIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcast(updateIntent);
    }

    /**
     * Initialize given widgets to default state, where we launch Music on default click
     * and hide actions if service not running.
     */
    private void defaultAppWidget(Context context, int[] appWidgetIds, Class<?> intentCls) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_bar);

        updateNotificationViews(context, views, PlaylistSong.emptyData, false, false, intentCls, 0);

        pushUpdate(context, appWidgetIds, views);
    }


    /**
     * Update all active widget instances by pushing changes
     */
    void performUpdate(Context context,
                       int[] appWidgetIds,
                       PlaylistSong.Data songData,
                       boolean playing,
                       boolean wantsPlaying,
                       Class<?> intentCls) {

        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_bar);

        updateNotificationViews(context, views, songData, playing, wantsPlaying, intentCls, 0);

        pushUpdate(context, appWidgetIds, views);
    }


    public void updateNotificationViews(Context context,
                                        final RemoteViews views,
                                        PlaylistSong.Data songData,
                                        boolean playing,
                                        boolean wantsPlaying,
                                        Class<?> intentCls,
                                        int sessionToken) {
        MediaPlaybackNotification.updateNotificationViews(context, views, songData, playing, wantsPlaying, intentCls, sessionToken);

        views.setViewVisibility(R.id.btnClose, ViewGroup.GONE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent notificationAction = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        views.setOnClickPendingIntent(R.id.artGroup, notificationAction);
    }

    public void notifyChange(Context context,
                             PlaylistSong.Data songData,
                             boolean playing,
                             boolean wantsPlaying,
                             Class<?> intentCls) {
        if (hasInstances(context)) {
            performUpdate(context,
                    null,
                    songData,
                    playing,
                    wantsPlaying,
                    intentCls);
        }
    }

    private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        // Update specific list of appWidgetIds if given, otherwise default to all
        final AppWidgetManager gm = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            gm.updateAppWidget(appWidgetIds, views);
        } else {
            gm.updateAppWidget(new ComponentName(context, this.getClass()), views);
        }
    }

    /**
     * Check against {@link AppWidgetManager} if there are any instances of this widget.
     */
    private boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, this.getClass()));
        return (appWidgetIds.length > 0);
    }
}
