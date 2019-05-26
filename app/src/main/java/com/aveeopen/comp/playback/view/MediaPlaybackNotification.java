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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.RemoteViews;

import com.aveeopen.Common.Events.WeakEvent4;
import com.aveeopen.MainActivity;
import com.aveeopen.comp.AlbumArt.AlbumArtCore;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AlbumArt.ImageLoadedListener;
import com.aveeopen.comp.playback.MediaPlaybackServiceDefs;
import com.aveeopen.comp.playback.Song.PlaylistSong;
import com.aveeopen.R;

import java.lang.ref.WeakReference;

public class MediaPlaybackNotification {

    public static WeakEvent4<AlbumArtRequest /*artRequest*/, ImageLoadedListener /*listener*/, Integer /*targetW*/, Integer /*targetH*/> onRequestAlbumArtLarge = new WeakEvent4<>();

    private static WeakReference<Notification> currentNotification = new WeakReference<>(null);

    public static Notification getOrCreateNotification(Context context,
                                                       String notificationChannelId,
                                                       PlaylistSong.Data songData,
                                                       boolean playing,
                                                       boolean wantsPlaying,
                                                       Class<?> intentCls,
                                                       int sessionToken) {
        Notification notification = createNotificationInternal(context, notificationChannelId, songData, playing, wantsPlaying, intentCls, sessionToken);
        currentNotification = new WeakReference<>(null);
        return notification;
    }

    public static void updateNotification(int id,
                                          Context context,
                                          String notificationChannelId,
                                          PlaylistSong.Data songData,
                                          boolean playing,
                                          boolean wantsPlaying,
                                          Class<?> intentCls,
                                          int sessionToken) {

        Notification notification = currentNotification.get();
        if (notification == null) {
            notification = getOrCreateNotification(context, notificationChannelId, songData, playing, wantsPlaying, intentCls, sessionToken);
            currentNotification = new WeakReference<>(null);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(id, notification);
        } else {
            updateNotificationViews(context, notification.contentView, songData, playing, wantsPlaying, intentCls, sessionToken);
        }
    }

    public static String createNotificationChannel(final Context context){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = context.getString(R.string.playback_service_notif_channel_name);//MediaPlaybackServiceDefs.NOTIFICATION_CHANNEL_ID;
            String channelName = MediaPlaybackServiceDefs.NOTIFICATION_CHANNEL_NAME;
            NotificationChannel chan = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW);//set below IMPORTANCE_DEFAULT so to make no sound

            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager service = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if(service!=null)
                service.createNotificationChannel(chan);
            return channelId;
        } else {
            return null;
        }
    }

    private static Notification createNotificationInternal(Context context,
                                                           final String notificationChannelId,
                                                           PlaylistSong.Data songData,
                                                           boolean playing,
                                                           boolean wantsPlaying,
                                                           Class<?> intentCls,
                                                           int sessionToken) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_bar);

        updateNotificationViews(context, views, songData, playing, wantsPlaying, intentCls, sessionToken);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent notificationAction = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        final Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context, notificationChannelId);
        } else{
            notificationBuilder = new Notification.Builder(context);
        }


        notificationBuilder.setContent(views);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_mono_xs);

        notificationBuilder.setContentIntent(notificationAction);

        return notificationBuilder.build();
    }

    public static void updateNotificationViews(Context context,
                                               final RemoteViews views,
                                               PlaylistSong.Data songData,
                                               boolean playing,
                                               boolean wantsPlaying,
                                               Class<?> intentCls,
                                               int sessionToken) {

        views.setImageViewResource(R.id.imgArt, R.drawable.placeholderart4);

        AlbumArtCore albumArtCore = AlbumArtCore.getInstance();
        if (albumArtCore != null) {
            ImageLoadedListener imageLoadedListener;
            imageLoadedListener = new ImageLoadedListener() {
                Object object1;

                @Override
                public void onBitmapLoaded(Bitmap bitmap, String url00, String url0, String url1) {
                    if (bitmap != null)
                        views.setImageViewBitmap(R.id.imgArt, bitmap);
                    else
                        views.setImageViewResource(R.id.imgArt, R.drawable.placeholderart4);
                }

                @Override
                public void setUserObject1(Object obj1) {
                    object1 = obj1;
                }
            };

            onRequestAlbumArtLarge.invoke(new AlbumArtRequest(songData.getVideoThumbDataSourceAsStr(),
                    songData.getAlbumArtPath0Str(),
                    songData.getAlbumArtPath1Str(),
                    songData.getAlbumArtGenerateStr()),
                    imageLoadedListener,
                    200, 200);
        }

        String title;
        String artist;
        String album;

        {
            title = songData.trackName;
            artist = songData.artistName;
            album = songData.albumName;

            if (artist == null || artist.equals(MediaStore.UNKNOWN_STRING))
                artist = context.getString(R.string.unknown_artist_name);
            if (album == null || album.equals(MediaStore.UNKNOWN_STRING))
                album = context.getString(R.string.unknown_album_name);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                int playButton = wantsPlaying ? R.drawable.ic_ctrl_pause_s : R.drawable.ic_ctrl_play_s;
                views.setImageViewResource(R.id.btnPlayPause, playButton);

                ComponentName service = new ComponentName(context, intentCls);

                Intent prev = new Intent(MediaPlaybackServiceDefs.PREVIOUS_ACTION);
                prev.setComponent(service);
                views.setOnClickPendingIntent(R.id.btnPrev, PendingIntent.getService(context, 0, prev, 0));


                Intent playPause = new Intent(MediaPlaybackServiceDefs.TOGGLE_PAUSE_ACTION);
                playPause.setComponent(service);
                views.setOnClickPendingIntent(R.id.btnPlayPause, PendingIntent.getService(context, 0, playPause, 0));

                Intent next = new Intent(MediaPlaybackServiceDefs.NEXT_ACTION);
                next.setComponent(service);
                views.setOnClickPendingIntent(R.id.btnNext, PendingIntent.getService(context, 0, next, 0));

                Intent close = new Intent(MediaPlaybackServiceDefs.ACTIVITY_AND_SERVICE_EXIT_ACTION);
                close.setComponent(service);
                views.setOnClickPendingIntent(R.id.btnClose, PendingIntent.getService(context, 0, close, 0));
            }
        }

        views.setTextViewText(R.id.txtSongTitle, title);
        views.setTextViewText(R.id.txtSongArtist, artist);

//        boolean invertNotification = false;//settings
//        if (invertNotification) {
//            views.setTextColor(R.id.txtSongTitle, 0xffffffff);
//            views.setTextColor(R.id.txtSongArtist, 0xffffffff);
//        }
    }
}
