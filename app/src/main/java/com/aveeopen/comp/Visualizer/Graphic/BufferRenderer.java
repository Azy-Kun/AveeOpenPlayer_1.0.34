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

package com.aveeopen.comp.Visualizer.Graphic;

import android.opengl.GLES20;

import com.aveeopen.Common.Vec2f;
import com.aveeopen.Common.tlog;

import junit.framework.Assert;

import mdesl.graphics.ITexture;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.graphics.glutils.VertexArray;
import mdesl.graphics.glutils.VertexAttrib;

public class BufferRenderer {

    private int updateParticleCount = 0;
    private VVertexBuffer vertices;
    private ITexture currentTexture = null;

    public BufferRenderer(ShaderProgram shader, int particlesMinCount) {

        int vertexCount = particlesMinCount * 3 * 2;

        final VertexAttrib[] attributes = new VertexAttrib[]{
                new VertexAttrib(shader.getAttributeLocation("Position"), "Position", 2),
                new VertexAttrib(shader.getAttributeLocation("TexCoord"),  "TexCoord", 2),
                new VertexAttrib(shader.getAttributeLocation("Color"),  "Color", 4),
        };

        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i].location < 0)
                tlog.w("ERROR attribute not found " + attributes[i].name);
        }

        vertices = new VVertexBuffer(vertexCount, attributes);
    }

    public void intColorToF4Color(float[] out, int argb) {
        out[3] = ((argb >> 8 * 3) & 0xFF) / 255.0f;
        out[0] = ((argb >> 8 * 2) & 0xFF) / 255.0f;
        out[1] = ((argb >> 8) & 0xFF) / 255.0f;
        out[2] = ((argb) & 0xFF) / 255.0f;
    }

    public void dispose() {
        vertices.dispose();
    }

    protected boolean checkFlush(RenderState renderData, IAtlasTexture tex, int trianglesNeeded) {
        if (updateStreamRemainingLength() - (Vertex.Size * 3 * trianglesNeeded) < 0) {
            tlog.w("buffer full");
            flush(renderData);
            currentTexture = tex.getTexture2D();
            return true;
        }

        Assert.assertNotNull(tex);

        if (currentTexture == null && tex.getTexture2D() == null) {
            return true;
        } else if (currentTexture != tex.getTexture2D()) {
            flush(renderData);
            currentTexture = tex.getTexture2D();
            return true;
        }

        return true;
    }

    public void flush(RenderState renderData) {
        if (updateParticleCount > 0) {
            renderData.bindShader(renderData.res.getAtlasBufferShader());
            renderData.res.getAtlasBufferShader().setUniformMatrix("u_projView", false, renderData.getVPMatrix());

            vertices.flip();

            render();

            updateParticleCount = 0;
            vertices.clear();
        }
        currentTexture = null;
    }

    private void render() {
        if (currentTexture != null)
            currentTexture.getTexture().bind();
        else
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        vertices.bind();
        vertices.draw(GLES20.GL_TRIANGLES, 0, updateParticleCount * 3);
        vertices.unbind();
    }

    public void onFrameStart(RenderState renderState) {
        updateParticleCount = 0;
        currentTexture = null;
    }

    public void onFrameEnd(RenderState renderState) {
        flush(renderState);
    }

    public void updateNextParticle(RenderState renderData, Vertex v00, Vertex v10, Vertex v01, Vertex v11, IAtlasTexture tex) {
        if (!checkFlush(renderData, tex, 2)) return;

        updateStreamWrite(v00);
        updateStreamWrite(v01);
        updateStreamWrite(v10);

        updateStreamWrite(v10);
        updateStreamWrite(v01);
        updateStreamWrite(v11);

        updateParticleCount += 2;
    }

    public void drawRectangleRightBottom(RenderState renderData,
                                         float x, float y, float z,
                                         float x2, float y2,
                                         int intcolor,
                                         Vec2f tex0, Vec2f tex1,
                                         IAtlasTexture tex) {
        drawRectangleRightBottomWH(renderData,
                x, y, z,
                x2 - x, y2 - y,
                intcolor,
                tex0, tex1, tex);
    }

    public void drawRectangleRightBottomWH(RenderState renderData,
                                           float x, float y, float z,
                                           float hsizex, float hsizey,
                                           int intcolor,
                                           Vec2f tex0, Vec2f tex1,
                                           IAtlasTexture tex) {
        if (!checkFlush(renderData, tex, 2)) return;

        float[] _color = new float[4];
        intColorToF4Color(_color, intcolor);

        tex0.x = tex.translateU(tex0.x);
        tex0.y = tex.translateV(tex0.y);

        tex1.x = tex.translateU(tex1.x);
        tex1.y = tex.translateV(tex1.y);

        Vertex v1 = new Vertex();
        v1.posW = 1.0f;
        v1.texZ = tex.translateW();

        v1.posX = x;
        v1.posY = y + hsizey;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex0.x;
        v1.texY = tex1.y;
        updateStreamWrite(v1);

        v1.posX = x;
        v1.posY = y;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex0.x;
        v1.texY = tex0.y;
        updateStreamWrite(v1);

        v1.posX = x + hsizex;
        v1.posY = y + hsizey;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex1.x;
        v1.texY = tex1.y;
        updateStreamWrite(v1);

        v1.posX = x + hsizex;
        v1.posY = y + hsizey;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex1.x;
        v1.texY = tex1.y;
        updateStreamWrite(v1);

        v1.posX = x;
        v1.posY = y;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex0.x;
        v1.texY = tex0.y;
        updateStreamWrite(v1);

        v1.posX = x + hsizex;
        v1.posY = y;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex1.x;
        v1.texY = tex0.y;
        updateStreamWrite(v1);

        updateParticleCount += 2;
    }

    //0---1
    //|   |
    //2---3
    public void drawRectangle(RenderState renderData,
                              float x0, float y0,
                              float x1, float y1,
                              float x2, float y2,
                              float x3, float y3,
                              float z,
                              int intcolor,
                              Vec2f tex0, Vec2f tex1,
                              IAtlasTexture tex) {
        if (!checkFlush(renderData, tex, 2)) return;

        float[] _color = new float[4];
        intColorToF4Color(_color, intcolor);

        tex0.x = tex.translateU(tex0.x);
        tex0.y = tex.translateV(tex0.y);

        tex1.x = tex.translateU(tex1.x);
        tex1.y = tex.translateV(tex1.y);

        Vertex v1 = new Vertex();
        v1.posW = 1.0f;
        v1.texZ = tex.translateW();

        v1.posX = x2;
        v1.posY = y2;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex0.x;
        v1.texY = tex1.y;
        updateStreamWrite(v1);

        v1.posX = x0;
        v1.posY = y0;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex0.x;
        v1.texY = tex0.y;
        updateStreamWrite(v1);

        v1.posX = x3;
        v1.posY = y3;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex1.x;
        v1.texY = tex1.y;
        updateStreamWrite(v1);

        v1.posX = x3;
        v1.posY = y3;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex1.x;
        v1.texY = tex1.y;
        updateStreamWrite(v1);

        v1.posX = x0;
        v1.posY = y0;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex0.x;
        v1.texY = tex0.y;
        updateStreamWrite(v1);

        v1.posX = x1;
        v1.posY = y1;
        v1.posZ = z;
        v1.color = _color;
        v1.texX = tex1.x;
        v1.texY = tex0.y;
        updateStreamWrite(v1);

        updateParticleCount += 2;
    }

    //x0, y0 , w, h, - bounds, not center
    public void drawCircleSegmentW(RenderState renderData,
                                   float x, float y, float z,
                                   float hsizex, float hsizey,
                                   int intcolor,
                                   Vec2f tex0, Vec2f tex1,
                                   IAtlasTexture tex,
                                   float segmentW) {
        float drawRadius;
        if (hsizex < hsizey)
            drawRadius = hsizex * 0.5f;
        else
            drawRadius = hsizey * 0.5f;

        float circumference = (float) (2.0 * Math.PI * drawRadius);

        int num = (int) ((circumference / segmentW) + 0.5f);//roundup

        int num_segments = Math.max(num, 18);

        drawCircle(renderData, x, y, z, hsizex, hsizey,
                intcolor,
                tex0, tex1,
                tex, num_segments);
    }

    //x0, y0 , w, h, - bounds, not center
    //num_segments = edges
    public void drawCircle(RenderState renderData,
                           float x, float y, float z,
                           float halfSizeX, float halfSizeY,
                           int intColor,
                           Vec2f tex0, Vec2f tex1,
                           IAtlasTexture tex,
                           int numSegments) {
        if (!checkFlush(renderData, tex, numSegments)) return;

        x += halfSizeX * 0.5f;
        y += halfSizeY * 0.5f;

        float[] _color = new float[4];
        intColorToF4Color(_color, intColor);

        float r = 0.5f;

        double theta = 2.0 * Math.PI / (double) (numSegments);
        double tangential_factor = Math.tan(theta);//calculate the tangential factor

        double radial_factor = Math.cos(theta);//calculate the radial factor

        float cx = 0;//we start at angle = 0
        float cy = -r;

        float lastX = 0.0f;
        float lastY = cy;

        tex0.x = tex.translateU(tex0.x);
        tex0.y = tex.translateV(tex0.y);

        tex1.x = tex.translateU(tex1.x);
        tex1.y = tex.translateV(tex1.y);

        float texMidX = (tex0.x + tex1.x) * 0.5f;
        float texMidY = (tex0.y + tex1.y) * 0.5f;
        float texWhalf = (tex1.x - tex0.x);
        float texHhalf = (tex1.y - tex0.y);

        Vertex v1 = new Vertex();

        for (int ii = 0; ii < numSegments; ii++) {
            //calculate the tangential vector
            //remember, the radial vector is (x, y)
            //to get the tangential vector we flip those coordinates and negate one of them

            float tx = -cy;
            float ty = cx;

            //add the tangential vector
            cx += tx * tangential_factor;
            cy += ty * tangential_factor;

            //correct using the radial factor
            cx *= radial_factor;
            cy *= radial_factor;
            //

            v1.posW = 1.0f;
            v1.texZ = tex.translateW();

            v1.posX = x + (lastX * halfSizeX);
            v1.posY = y + (lastY * halfSizeY);
            v1.posZ = z;
            v1.color = _color;
            v1.texX = texMidX + lastX * texWhalf;
            v1.texY = texMidX + lastY * texHhalf;
            updateStreamWrite(v1);

            v1.posX = x;
            v1.posY = y;
            v1.posZ = z;
            v1.color = _color;
            v1.texX = texMidX;
            v1.texY = texMidY;
            updateStreamWrite(v1);

            v1.posX = x + (cx * halfSizeX);
            v1.posY = y + (cy * halfSizeY);
            v1.posZ = z;
            v1.color = _color;
            v1.texX = texMidX + cx * texWhalf;
            v1.texY = texMidX + cy * texHhalf;
            updateStreamWrite(v1);

            updateParticleCount += 1;
            //
            lastX = cx;
            lastY = cy;
        }
    }

    private int updateStreamRemainingLength() {
        return vertices.remaining();
    }

    private void updateStreamWrite(Vertex v1) {
        v1.writeToStream(vertices);
    }

    public static class Vertex {

        public static final int Size = 8 + 8 + 16;

        public float posX, posY, posZ, posW;
        public float texX, texY, texZ;
        public float[] color;//rgba

        public Vertex() {
        }

        public void writeToStream(VertexArray vertices) {
            vertices.put(posX);
            vertices.put(posY);
            vertices.put(texX);
            vertices.put(texY);
            vertices.put(color[0]);
            vertices.put(color[1]);
            vertices.put(color[2]);
            vertices.put(color[3]);
        }
    }
}
