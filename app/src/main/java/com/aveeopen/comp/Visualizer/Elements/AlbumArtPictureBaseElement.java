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

package com.aveeopen.comp.Visualizer.Elements;

import android.graphics.Bitmap;

import com.aveeopen.Common.Utils;
import com.aveeopen.comp.AlbumArt.AlbumArtRequest;
import com.aveeopen.comp.AlbumArt.ImageLoadedListener;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

import mdesl.graphics.glutils.FrameBuffer;

public abstract class AlbumArtPictureBaseElement extends Element implements ImageLoadedListener {

    private AlbumArtRequest albumArtRequest = new AlbumArtRequest("", "", "", "");
    private Bitmap bitmap = null;
    private boolean bitmapLoading = false;
    private boolean bitmapLoadedIn = false;
    private Object imageLoadStrongReference;

    public AlbumArtPictureBaseElement() {
    }

    public void setResPicturePath(AlbumArtRequest newAlbumArtRequest) {
        if (newAlbumArtRequest == null)
            newAlbumArtRequest = new AlbumArtRequest("", "", "", "");

        if (Utils.compareNullStrings(albumArtRequest.videoThumbDataSource, newAlbumArtRequest.videoThumbDataSource)) {
            if (Utils.compareNullStrings(albumArtRequest.path0, newAlbumArtRequest.path0)) {
                if (Utils.compareNullStrings(albumArtRequest.path1, newAlbumArtRequest.path1)) {
                    if (Utils.compareNullStrings(albumArtRequest.genStr, newAlbumArtRequest.genStr)) {
                        return;
                    }
                }
            }
        }

        this.albumArtRequest = newAlbumArtRequest;
        this.markNeedReCreateGLResources();
    }

    @Override
    protected void onApplyCustomization(CustomizationData customizationData) {
        super.onApplyCustomization(customizationData);
    }

    @Override
    protected void onReadCustomization(CustomizationData outCustomizationData) {
        super.onReadCustomization(outCustomizationData);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, String dataSource, String url0, String url1) {

        if (Utils.compareNullStrings(dataSource, albumArtRequest.videoThumbDataSource)) {
            if (Utils.compareNullStrings(url0, albumArtRequest.path0)) {
                if (Utils.compareNullStrings(url1, albumArtRequest.path1)) {
                    this.bitmap = bitmap;
                    bitmapLoadedIn = false;
                    super.markNeedReCreateGLResources();
                }
            }
        }

    }

    @Override
    public void setUserObject1(Object obj1) {
        imageLoadStrongReference = obj1;
    }

    @Override
    protected void markNeedReCreateGLResources() {
        bitmap = null;
        bitmapLoading = false;
        bitmapLoadedIn = false;
        super.markNeedReCreateGLResources();
    }

    @Override
    public void onCreateGLResources(RenderState renderData) {
        super.onCreateGLResources(renderData);

        if (!bitmapLoading) {
            bitmapLoading = true;
            final int targetBoundsWidth = renderData.getFullscreenWidth();
            final int targetBoundsHeight = renderData.getFullscreenHeight();

            renderData.res.visualizationData.onRequestAlbumArtPathAndBitmap(
                    AlbumArtPictureBaseElement.this,
                    targetBoundsWidth,
                    targetBoundsHeight,
                    albumArtRequest.makeCopy());
        }

        if (!bitmapLoadedIn) {
            bitmapLoadedIn = true;
            onAlbumArtCreateGLResources(bitmap);
        }

        bitmap = null;
    }


    @Override
    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        super.onRender(renderData, resultFB);
        updateCurrentAlbumArtId(renderData);
        onAlbumArtRender(renderData);
    }


    private void updateCurrentAlbumArtId(RenderState renderData) {
        AlbumArtRequest result = renderData.res.visualizationData.onRequestsAlbumArtPath();

        if (result != null) {
            setResPicturePath(result);
        } else {
            setResPicturePath(null);
        }
    }

    protected abstract void onAlbumArtCreateGLResources(Bitmap bitmap);

    protected abstract void onAlbumArtRender(RenderState renderData);
}
