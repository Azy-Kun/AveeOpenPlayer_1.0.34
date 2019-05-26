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

import android.graphics.PointF;
import android.graphics.RectF;

import com.aveeopen.Common.Vec2f;
import com.aveeopen.Common.Vec2i;
import com.aveeopen.Common.tlog;
import com.aveeopen.comp.Visualizer.Graphic.RenderState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mdesl.graphics.glutils.FrameBuffer;

public abstract class Element {

    protected ElementGroup parent;
    private boolean glResourcesCreated = false;
    private int blendMode = 0;

    protected float posX = 0.5f, posY = 0.5f;
    protected float localPosX = 0.5f, localPosY = 0.5f;
    private boolean posXIsUniform, posYIsUniform = false;
    private boolean localPosXIsUniform, localPosYIsUniform = false;
    private float scaleX = 0.5f, scaleY = 0.5f;
    private String scaleMeasure = null;
    private float scaleMeasureMul = 1.0f;
    //set to 0.0 for auto uniform scale
    private boolean scaleXIsUniform = false, scaleYIsUniform = false;

    public Element() {
        glResourcesCreated = false;
    }

    public void setBlendMode(int mode) {
        blendMode = mode;
    }

    public void setPosition(float x, float y) {
        posX = x;
        posY = y;
    }

    public void setLocalPosition(float x, float y) {
        localPosX = x;
        localPosY = y;
    }

    public void setPositionUniform(boolean x, boolean y) {
        posXIsUniform = x;
        posYIsUniform = y;
    }

    public void setLocalPositionUniform(boolean x, boolean y) {
        localPosXIsUniform = x;
        localPosYIsUniform = y;
    }

    public void setScale(float x, float y) {
        scaleX = x;
        scaleY = y;
    }

//    public float getScaleX() {
//        return scaleX;
//    }
//
//    public float getScaleY() {
//        return scaleY;
//    }

    public void setScaleMeasure(String measure, float mul) {
        scaleMeasure = measure;
        scaleMeasureMul = mul;
    }

    private float scaleXMeasured(Meter meter) {
        return scaleX + (meter.measureVec2f(scaleMeasure).x * scaleMeasureMul);
    }

    private float scaleYMeasured(Meter meter) {
        return scaleY + (meter.measureVec2f(scaleMeasure).y * scaleMeasureMul);
    }

    public void setScaleUniform(boolean x, boolean y) {
        scaleXIsUniform = x;
        scaleYIsUniform = y;
    }

    RectF measureDrawRect(Meter meter) {
        float elementX = meter.measureScreenSpaceX(posX, posXIsUniform);
        float elementY = meter.measureScreenSpaceY(posY, posYIsUniform);

        float elementW = meter.measureScreenScaleX(scaleXMeasured(meter), scaleXIsUniform);
        float elementH = meter.measureScreenScaleY(scaleYMeasured(meter), scaleYIsUniform);

        elementX -= meter.measureLocalSpaceX(localPosX, localPosXIsUniform, elementW, elementH);
        elementY -= meter.measureLocalSpaceY(localPosY, localPosYIsUniform, elementW, elementH);

        return new RectF(elementX, elementY, elementX + elementW, elementY + elementH);
    }

    RectF measureDrawRect(Meter meter, Vec2i elementDim) {
        float elementX = meter.measureScreenSpaceX(posX, posXIsUniform);
        float elementY = meter.measureScreenSpaceY(posY, posYIsUniform);

        float elementW = elementDim.x;
        float elementH = elementDim.y;

        elementX -= meter.measureLocalSpaceX(localPosX, localPosXIsUniform, elementW, elementH);
        elementY -= meter.measureLocalSpaceY(localPosY, localPosYIsUniform, elementW, elementH);

        return new RectF(elementX, elementY, elementX + elementW, elementY + elementH);
    }

    PointF measureDrawScaleRect(Meter meter) {

        float elementW = meter.measureScaleX(scaleXMeasured(meter), scaleXIsUniform);
        float elementH = meter.measureScaleY(scaleYMeasured(meter), scaleYIsUniform);

        return new PointF(elementW, elementH);
    }

    public void removeFromParent() {
        if (parent != null)
            parent.removeChild(this);
    }

    protected void markNeedReCreateGLResources() {
        glResourcesCreated = false;
    }

    public void reCreateGLResources(RenderState renderData) {
        glResourcesCreated = false;
    }

    protected void onRenderCheckResources(RenderState renderData) {
        if (!glResourcesCreated)
            onCreateGLResources(renderData);
        glResourcesCreated = true;
    }

    public void onEarlyUpdate(RenderState renderData, FrameBuffer resultFB) {

    }

    public void onRender(RenderState renderData, FrameBuffer resultFB) {
        onRenderCheckResources(renderData);

        renderData.bindFrameBuffer(resultFB);
        renderData.setBlendMode(blendMode);
    }

    public void updateRenderStates(RenderState renderData, FrameBuffer resultFB) {
        renderData.bindFrameBuffer(resultFB);
        renderData.setBlendMode(blendMode);
    }

    protected void onCreateGLResources(RenderState renderData) {
    }

    public boolean getCustomization(Element.CustomizationList customization, int customizationIndex) {

//        Element.CustomizationData customizationData = new CustomizationData(customizationIndex, "", 0xffff00ff);
//        onReadCustomization(customizationData);
//        customization.addData(customizationData);

        Element.CustomizationData customizationData = new CustomizationData(customization.getNewDataJSONObject());
        onReadCustomization(customizationData);
        //customization.addData(customizationData);


        return true;
    }

    //return: false - failed
    public boolean setCustomization(Element.CustomizationList customization, Integer[] dataCounter) {

        Element.CustomizationData customizationData = customization.getData(dataCounter[0]);
        dataCounter[0]++;
        if (customizationData == null) return false;//failed

        //apply data
        onApplyCustomization(customizationData);

        return true;
    }


    protected void onApplyCustomization(Element.CustomizationData customizationData) {
    }

    protected void onReadCustomization(Element.CustomizationData outCustomizationData) {
    }

    public static class CustomizationData {

        JSONObject jsonObj;
        boolean badFormat = false;

        public static String[] getPropertyTypeParts(String type) {

            String splitChar = " ";
            String[] parts = type.split(splitChar);
            if(parts.length < 1) {
                parts = new String[1];
                parts[0] = type;
            }
            return parts;
        }

        public CustomizationData(JSONObject jsonObj) {
            this.jsonObj = jsonObj;
        }

        public void setCustomizationName(String value) {
            try {
                jsonObj.put("_name", value);
            } catch (JSONException ignored) {
            }
        }

        public String getCustomizationName() {
            try {
                return jsonObj.getString("_name");
            } catch (JSONException e) {
                badFormat = true;
            }
            return "";
        }

        public String getChildTypeValue() {

            try {
                return jsonObj.getString("v");
            } catch (JSONException e) {
                return "";
            }
        }

        public void putChildTypeValue(String typeValue) {

            try {
                jsonObj.put("v", typeValue);
            } catch (JSONException e) {
            }
        }

//        public int getInt(String name) {
//            try {
//                return jsonObj.getInt(name);
//            } catch (JSONException e) {
//                badFormat = true;
//            }
//            return 0;
//        }
//
//        public float getFloat(String name) {
//            try {
//                return (float)jsonObj.getDouble(name);
//            } catch (JSONException e) {
//                badFormat = true;
//            }
//            return 0.0f;
//        }
//
//        public String getString(String name)
//        {
//            try {
//                return jsonObj.getString(name);
//            } catch (JSONException e) {
//                badFormat = true;
//            }
//            return "";
//        }
//
//        public void putInt(String name, int value) {
//            try {
//                jsonObj.put(name, value);
//            } catch (JSONException e) {
//            }
//        }
//
//        public void putFloat(String name, float value) {
//            try {
//                jsonObj.put(name, value);
//            } catch (JSONException e) {
//            }
//        }
//
//        public void putString(String name, String value) {
//            try {
//                jsonObj.put(name, value);
//            } catch (JSONException e) {
//            }
//        }

        public CustomizationData putChild(String name, String childType , String[] validTypes)
        {
            JSONObject jsonObjChild = new JSONObject();
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("_child");
                for(String type : validTypes) {
                    sb.append(" ");
                    sb.append(type);
                }

                jsonObjChild.put("v", childType);//value
                jsonObjChild.put("t", sb.toString());//type
                jsonObj.put(name, jsonObjChild);
            } catch (JSONException e) {
            }
            return new CustomizationData(jsonObjChild);
        }

        public CustomizationData getChild(String name) {
            try {
                JSONObject jsonObjChild = jsonObj.getJSONObject(name);
                //jsonObjChild.getString("t");
                //jsonObjChild.getString("v");
                return new CustomizationData(jsonObjChild);
            } catch (JSONException e) {
                return new CustomizationData(new JSONObject());//empty
            }
        }

        public void putPropertyBool(String name, boolean value)
        {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value ? 1 : 0);//value
                //jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public void putPropertyBool(String name, boolean value, String type) {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value ? 1 : 0);//value
                jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public void putPropertyInt(String name, int value)
        {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value);//value
                //jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public void putPropertyInt(String name, int value, String type) {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value);//value
                jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }


        public void putPropertyFloat(String name, float value) {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value);//value
                //jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public void putPropertyFloat(String name, float value, String type) {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value);//value
                jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public void putPropertyVec2f(String name, Vec2f value) {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value.toString());//value
                //jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public void putPropertyVec2f(String name, Vec2f value, String type) {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value.toString());//value
                jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public void putPropertyString(String name, String value) {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value);//value
                //jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public void putPropertyString(String name, String value, String type) {
            try {
                JSONObject jsonObjProp = new JSONObject();
                jsonObjProp.put("v", value);//value
                jsonObjProp.put("t", type);//type
                jsonObj.put(name, jsonObjProp);
            } catch (JSONException e) {
            }
        }

        public boolean getPropertyBool(String name, boolean defaultValue) {
            try {
                return jsonObj.getJSONObject(name).getInt("v") != 0;
            } catch (JSONException e) {
                return defaultValue;
            }
        }

        public int getPropertyInt(String name, int defaultValue) {
            try {
                return jsonObj.getJSONObject(name).getInt("v");
            } catch (JSONException e) {
                return defaultValue;
            }
        }

        public float getPropertyFloat(String name, float defaultValue) {
            try {
                return (float)jsonObj.getJSONObject(name).getDouble("v");
            } catch (JSONException e) {
                return defaultValue;
            }
        }

        public String getPropertyString(String name, String defaultValue) {
            try {
                return  jsonObj.getJSONObject(name).getString("v");
            } catch (JSONException e) {
                return defaultValue;
            }
        }

        public Vec2f getPropertyVec2f(String name, Vec2f defaultValue) {
            try {
                return Vec2f.FromString(jsonObj.getJSONObject(name).getString("v"), defaultValue);
            } catch (JSONException e) {
                return defaultValue;
            }
        }

        public String getPropertyType(String name) {

            try {
                return  jsonObj.getJSONObject(name).getString("t");
            } catch (JSONException e) {
                return "";
            }
        }


//        public boolean hasProperties() {
//            return jsonObj.length()>0;
//        }



        public Iterator<String> GetAllPropertiesSorted()
        {
            Iterator<String> it = jsonObj.keys();
            List<String> list = new ArrayList<String>();
            while (it.hasNext()) {
                list.add(it.next());
            }

            Collections.sort(list);
            return list.iterator();
        }

        public Iterator<String> GetAllProperties()
        {
            return jsonObj.keys();

//            Iterator<String> keys = jsonObj.keys();
//            try {
//                while( keys.hasNext() ) {
//                    String key = keys.next();
//                    JSONObject obj = jsonObj.getJSONObject(key);
//
//
//                }
//            } catch (JSONException e) {
//                return
//            }
        }
    }

    public static class CustomizationList {

        //private List<CustomizationData> list;
        private JSONObject jsonRoot;
        private JSONArray jsonArray;

//        public CustomizationList(List<CustomizationData> list) {
//            this.list = list;
//            jsonRoot = new JSONObject(serialized);
//        }

        public CustomizationList(String serialized) {

            try {
                jsonRoot = new JSONObject(serialized);
                jsonArray = jsonRoot.getJSONArray("list");

                //list = new ArrayList<>(jsonArray.length());
                //for (int i = 0; i < jsonArray.length(); ++i) {
                //    list.add(new CustomizationData(jsonArray.getJSONObject(i)));
                //}

            } catch (JSONException e) {
                tlog.w("Failed to create from saved string: "+e.getMessage());
                createEmpty();
            }
        }

        public CustomizationList() {
            //list = new ArrayList<>();
            createEmpty();
        }

        private void createEmpty()
        {
            try {
                jsonRoot = new JSONObject();
                jsonArray = new JSONArray();
                jsonRoot.put("list", jsonArray);
            } catch (JSONException e) {
                //critical error
                tlog.w(e.getMessage());
                jsonRoot = null;
                jsonArray = null;
            }
        }

        public static CustomizationList deserialize(String serialized) {
            if (serialized == null) return null;

            return new CustomizationList(serialized);
//            List<String> strParts = UtilsSerialize.deserializeIterableAsList(";", serialized);
//            List<CustomizationData> list = new ArrayList<>(strParts.size());
//            for (String s : strParts)
//                list.add(CustomizationData.FromString(":", s));

            //return new CustomizationList(list);
        }

        public String serialize() {
            if(jsonRoot == null) return "";
            return jsonRoot.toString();
            //return UtilsSerialize.serializeIterable(";", list);
        }

//        public int getColor(int index) {
//            if (index >= 0 && index < list.size())
//                return list.get(index).color1;
//            return 0xffffffff;
//        }

//        public void addData(CustomizationData customizationData) {
//            //JSONObject jsonObj = new JSONObject();
//            jsonArray.put(customizationData.getJsonObj());
//            //list.add(customizationData);
//        }

        public JSONObject getNewDataJSONObject() {
            JSONObject jsonObj = new JSONObject();
            jsonArray.put(jsonObj);
            return jsonObj;
        }

        public int dataCount() {
            return jsonArray.length();
        }

        public CustomizationData getData(int customizationIndex) {

            if(customizationIndex < 0 || customizationIndex >= jsonArray.length())
                return null;

            try {
                JSONObject obj = jsonArray.getJSONObject(customizationIndex);
                return new CustomizationData(obj);
            }catch (JSONException e) {
                tlog.w(e.getMessage());
                return null;
            }

//            if (customizationIndex >= 0 && customizationIndex < list.size())
//                return list.get(customizationIndex);
//            return null;
        }

        public CustomizationList createClone() {
            return new CustomizationList(jsonRoot != null ? jsonRoot.toString() : "");
//            List<CustomizationData> listClone = new ArrayList<>(list.size());
//
//            for (CustomizationData d : list) {
//                listClone.add(d.createclone());
//            }
//
//            return new CustomizationList(listClone);
        }
    }
}
