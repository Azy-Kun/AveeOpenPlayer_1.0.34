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

package com.aveeopen.comp.AlbumArt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.widget.ImageView;

import com.AOSP.MyThumbnailUtils;
import com.aveeopen.PlayerCore;
import com.aveeopen.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;
import com.squareup.picasso.Target;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class AlbumArtCore {

    private static final Object createInstanceLock = new Object();
    private static volatile WeakReference<AlbumArtCore> instanceWeak = new WeakReference<>(null);
    private Picasso myPicasso = null;

    public static AlbumArtCore createInstance() {

        AlbumArtCore inst0 = instanceWeak.get();
        if (inst0 != null) return inst0;

        synchronized (createInstanceLock) {
            AlbumArtCore inst = instanceWeak.get();
            if (inst == null) {
                inst = new AlbumArtCore();
                instanceWeak = new WeakReference<>(inst);
            }

            return inst;
        }
    }

    public static AlbumArtCore getInstance() {
        return instanceWeak.get();
    }

    Picasso getMyPicasso(Context context) {
        if (myPicasso == null) {
            myPicasso = new Picasso.Builder(context)
                    .addRequestHandler(new MyPicassoRequestHandlerAlbumArt1())
                    .addRequestHandler(new MyPicassoRequestHandlerAlbumArt2())
                    .build();
        }
        return myPicasso;
    }

    public void cancelRequest(final ImageView imageView) {
        Context context = PlayerCore.s().getAppContext();
        if (context == null) {
            return;
        }
        getMyPicasso(context).cancelRequest(imageView);
    }

    public void loadAlbumArt(String url, final ImageView imageView) {
        Context context = PlayerCore.s().getAppContext();
        if (context == null) {
            return;
        }

        //imageLoader doesn't accept "/storage/...."
        if (url != null && url.length() > 0) {
            if (url.charAt(0) == '/')
                url = "file://" + url;
        }

        getMyPicasso(context)
                .load(Uri.parse(url))
                .placeholder(R.drawable.placeholderart4)
                .error(R.drawable.placeholderart4)
                .fit()
                .into(imageView);
    }

    public void loadAlbumArt(String dataSource,
                             String url0,
                             String url1,
                             String generateText,
                             final ImageView imageView,
                             boolean fitCenterInside,
                             boolean preferLarge) {
        Context context = PlayerCore.s().getAppContext();
        if (context == null) {
            return;
        }

        //imageLoader doesn't accept "/storage/...."
        if (url0 != null && url0.length() > 0) {
            if (url0.charAt(0) == '/')
                url0 = "file://" + url0;
        }

        if (url1 != null && url1.length() > 0) {
            if (url1.charAt(0) == '/')
                url1 = "file://" + url1;
        }

        Uri uri = new Uri.Builder()
                .scheme(MyPicassoRequestHandlerAlbumArt2.MY_SCHEME)
                .appendQueryParameter("src", dataSource)
                .appendQueryParameter("0", url0)
                .appendQueryParameter("1", url1)
                .appendQueryParameter("large", preferLarge ? "1" : "0")
                .appendQueryParameter("gentext", generateText)
                .build();


        if (fitCenterInside) {
            getMyPicasso(context)
                    .load(uri)
                    .placeholder(R.drawable.placeholderart4)
                    .error(R.drawable.placeholderart4)
                    .fit()
                    .centerInside()
                    .into(imageView);
        } else {
            getMyPicasso(context)
                    .load(uri)
                    .placeholder(R.drawable.placeholderart4)
                    .error(R.drawable.placeholderart4)
                    .into(imageView);
        }
    }

    public void loadAlbumArtLarge(final String dataSource,
                                  final String url0,
                                  final String url1,
                                  final String generateText,
                                  final ImageLoadedListener loadedListener,
                                  int targetBoundsWidth,
                                  int targetBoundsHeight) {
        boolean mainThread = Looper.myLooper() == Looper.getMainLooper();
        if (mainThread)
            loadASyncAlbumArtLarge(dataSource, url0, url1, generateText, loadedListener, targetBoundsWidth, targetBoundsHeight);
        else
            loadSyncAlbumArtLarge(dataSource, url0, url1, generateText, loadedListener, targetBoundsWidth, targetBoundsHeight);
    }

    private void loadSyncAlbumArtLarge(final String dataSource,
                                       final String url0,
                                       final String url1,
                                       final String generateText,
                                       final ImageLoadedListener loadedListener,
                                       int targetBoundsWidth,
                                       int targetBoundsHeight) {
        Context context = PlayerCore.s().getAppContext();
        if (context == null) {
            return;
        }

        String url0Fixed = url0;
        String url1Fixed = url1;

        //imageLoader dont accept "/storage/...."
        if (url0Fixed != null && url0Fixed.length() > 0) {
            if (url0Fixed.charAt(0) == '/')
                url0Fixed = "file://" + url0;
        }

        if (url1Fixed != null && url1Fixed.length() > 0) {
            if (url1Fixed.charAt(0) == '/')
                url1Fixed = "file://" + url1;
        }

        Uri uri = new Uri.Builder()
                .scheme(MyPicassoRequestHandlerAlbumArt2.MY_SCHEME)
                .appendQueryParameter("src", dataSource)
                .appendQueryParameter("0", url0Fixed)
                .appendQueryParameter("1", url1Fixed)
                .appendQueryParameter("large", "1")
                .appendQueryParameter("gentext", generateText)
                .build();

        Bitmap bitmap = null;

        try {
            bitmap = getMyPicasso(context)
                    .load(uri)
                    .placeholder(R.drawable.placeholderart4)
                    .error(R.drawable.placeholderart4)
                    .resize(targetBoundsWidth, targetBoundsHeight)
                    .onlyScaleDown()
                    .centerInside()
                    .get();
        } catch (IOException ignored) {
        }

        loadedListener.onBitmapLoaded(bitmap, dataSource, url0, url1);
    }

    private void loadASyncAlbumArtLarge(final String dataSource,
                                        final String url0,
                                        final String url1,
                                        final String generateText,
                                        final ImageLoadedListener loadedListener,
                                        int targetBoundsWidth,
                                        int targetBoundsHeight) {
        Context context = PlayerCore.s().getAppContext();
        if (context == null) {
            return;
        }

        String url0Fixed = url0;
        String url1Fixed = url1;

        //imageLoader doesn't accept "/storage/...."
        if (url0Fixed != null && url0Fixed.length() > 0) {
            if (url0Fixed.charAt(0) == '/')
                url0Fixed = "file://" + url0;
        }

        if (url1Fixed != null && url1Fixed.length() > 0) {
            if (url1Fixed.charAt(0) == '/')
                url1Fixed = "file://" + url1;
        }

        Uri uri = new Uri.Builder()
                .scheme(MyPicassoRequestHandlerAlbumArt2.MY_SCHEME)
                .appendQueryParameter("src", dataSource)
                .appendQueryParameter("0", url0Fixed)
                .appendQueryParameter("1", url1Fixed)
                .appendQueryParameter("large", "1")
                .appendQueryParameter("gentext", generateText)
                .build();

        com.squareup.picasso.Target imageLoad = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                loadedListener.onBitmapLoaded(bitmap, dataSource, url0, url1);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                loadedListener.onBitmapLoaded(null, dataSource, url0, url1);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                //loadedListener.onBitmapLoaded(null, dataSource, url0, url1);
            }
        };

        loadedListener.setUserObject1(imageLoad);//keep reference

        getMyPicasso(context)
                .load(uri)
                .placeholder(R.drawable.placeholderart4)
                .error(R.drawable.placeholderart4)
                .resize(targetBoundsWidth, targetBoundsHeight)
                .onlyScaleDown()
                .centerInside()
                .into(imageLoad);//its weak referenced
    }

    private class MyPicassoRequestHandlerAlbumArt1 extends RequestHandler {

        private static final String MY_SCHEME = "mycontent";

        private InputStream createInputStreamFromPath(Uri uri) {
            Context context = PlayerCore.s().getAppContext();
            if (context == null) return null;

            InputStream is = null;

            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            } catch (Exception ignored) {
            }
            if (pfd != null) {
                try {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    is = new FileInputStream(fd);
                } catch (Exception ignored) {
                }
            }

            return is;
        }

        @Override
        public boolean canHandleRequest(Request request) {
            return MY_SCHEME.equals(request.uri.getScheme());
        }

        @Override
        public Result load(Request request, int i) throws IOException {

            Uri uri = request.uri.buildUpon().scheme("content").build();

            InputStream is = createInputStreamFromPath(uri);
            if (is == null) {
                return null;
            }
            return new Result(is, Picasso.LoadedFrom.DISK);
        }
    }

    private class MyPicassoRequestHandlerAlbumArt2 extends RequestHandler {

        private static final String MY_SCHEME = "mycontent2";

        private Bitmap createBitmapFromPath(Uri uri) {

            if(uri == null) return null;

            Context context = PlayerCore.s().getAppContext();
            if (context == null) return null;

            Bitmap bm = null;

            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            } catch (Exception ignored) {
            }
            if (pfd != null) {
                try {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                } catch (Exception ignored) {
                }
            }

            return bm;
        }

        @Override
        public boolean canHandleRequest(Request request) {
            return MY_SCHEME.equals(request.uri.getScheme());
        }

        @Override
        public Result load(Request request, int i) throws IOException {
            Uri uriDataSource = Uri.parse(request.uri.getQueryParameter("src"));
            Uri uri0 = Uri.parse(request.uri.getQueryParameter("0"));
            Uri uri1 = Uri.parse(request.uri.getQueryParameter("1"));
            String isLargeStr = request.uri.getQueryParameter("large");
            String generateText = request.uri.getQueryParameter("gentext");
            boolean isLarge = "1".equals(isLargeStr);

            {
                Bitmap bm;
                bm = createBitmapFromPath(uri0);
                if (bm != null) return new Result(bm, Picasso.LoadedFrom.DISK);

                bm = createBitmapFromPath(uri1);
                if (bm != null) return new Result(bm, Picasso.LoadedFrom.DISK);
            }


            if (uriDataSource != null) {
                Bitmap videoBitmap = MyThumbnailUtils.createVideoThumbnail(uriDataSource.getPath(),
                        isLarge ? MediaStore.Video.Thumbnails.MINI_KIND : MediaStore.Video.Thumbnails.MICRO_KIND);
                if (videoBitmap != null) {
                    return new Result(videoBitmap, Picasso.LoadedFrom.DISK);
                }
            }

            Drawable drawableBg = null;
            Context context = PlayerCore.s().getAppContext();
            if (context != null) {
                drawableBg = ContextCompat.getDrawable(context, R.drawable.placeholderart4);
            }

            if (generateText != null && generateText.length() > 0) {
                char ch = generateText.charAt(0);
                float hue = SimpleTextAlbumArtCreator.valueInAlphabet(ch) * 360.0f;
                float[] textHsl = new float[]{hue + 0.0f, 0.2f, 1.0f};
                float[] bgHsl = new float[]{hue, 0.8f, 0.5f};
                float[] bgHsl2 = new float[]{hue + 10.0f, 0.9f, 0.2f};
                Bitmap bm = SimpleTextAlbumArtCreator.textAsBitmap(request.targetWidth,
                        request.targetHeight,
                        generateText,
                        ColorUtils.HSLToColor(textHsl),
                        ColorUtils.HSLToColor(bgHsl),
                        ColorUtils.HSLToColor(bgHsl2),
                        drawableBg);

                return new Result(bm, Picasso.LoadedFrom.DISK);
            }

            return null;
        }
    }

}
