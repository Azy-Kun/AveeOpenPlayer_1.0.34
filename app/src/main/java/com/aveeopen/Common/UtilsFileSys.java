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

import android.os.Environment;

import java.io.File;
import java.util.regex.Pattern;

public class UtilsFileSys {

    static final char WINDOWS_SEPARATOR = '\\';
    static final char UNIX_SEPARATOR = '/';

    public static String extractFilenameExt(File file) {
        return extractFilenameExt(file.getName());
    }

    /**
     * Returns file extension, without dot(.); lower case
     * @param path file path
     * @return file extension
     */
    public static String extractFilenameExt(String path) {
        String ext = path;

        if (ext != null) {
            int index = ext.lastIndexOf(".");
            try {
                ext = ext.substring(index + 1);
                ext = ext.toLowerCase();
            } catch (Exception e) {
                ext = "";
            }

        } else {
            ext = "";
        }

        return ext;
    }

    /**
     * Returns file extension, with dot(.); lower case
     * @param path file path
     * @return file extension
     */
    public static String extractFilenameExtWithDot(String path) {
        String ext = path;

        if (ext != null) {
            int index = ext.lastIndexOf(".");
            try {
                ext = ext.substring(index);
            } catch (Exception e) {
                ext = "";
            }

            ext = ext.toLowerCase();
        } else {
            ext = "";
        }

        return ext;
    }

    public static String extractFilename(String path) {

        int index = path.lastIndexOf("/");
        String pathEnd = path;
        try {
            pathEnd = path.substring(index + 1);
        } catch (Exception ignored) {
        }

        return pathEnd;
    }

    public static String extractFilenameWithoutExt(String path) {

        //filename
        int index = path.lastIndexOf("/");
        String pathEnd = path;
        try {
            pathEnd = path.substring(index + 1);
        } catch (Exception ignored) {
        }

        int index2 = pathEnd.lastIndexOf(".");
        if (index2 > 0) try {
            return pathEnd.substring(0, index2);
        } catch (Exception ignored) {
        }

        return pathEnd;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Get the relative path from one file to another, specifying the directory separator.
     * If one of the provided resources does not exist, it is assumed to be a file unless it ends with '/' or
     * '\'.
     *
     * @param targetPath    targetPath is calculated to this file
     * @param basePath      basePath is calculated from this file
     * @param pathSeparator directory separator. The platform default is not assumed so that we can test Unix behaviour when running on Windows (for example)
     * @return
     */
    public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {

        // Normalize the paths
        // String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
        // String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

        String normalizedTargetPath = targetPath;
        String normalizedBasePath = basePath;

        // Undo the changes to the separators made by normalization
        if (pathSeparator.equals("/")) {
            normalizedTargetPath = separatorsToUnix(normalizedTargetPath);
            normalizedBasePath = separatorsToUnix(normalizedBasePath);

        } else if (pathSeparator.equals("\\")) {
            normalizedTargetPath = separatorsToWindows(normalizedTargetPath);
            normalizedBasePath = separatorsToWindows(normalizedBasePath);

        } else {
            throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
        }

        String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
        String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        StringBuffer common = new StringBuffer();

        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length
                && target[commonIndex].equals(base[commonIndex])) {
            common.append(target[commonIndex] + pathSeparator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // No single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized.
            throw new PathResolutionException("No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath
                    + "'");
        }

        // The number of directories we have to backtrack depends on whether the base is a file or a dir
        // For example, the relative path from
        //
        // /foo/bar/baz/gg/ff to /foo/bar/baz
        //
        // ".." if ff is a file
        // "../.." if ff is a directory
        //
        // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
        // the resource referred to by this path may not actually exist, but it's the best I can do
        boolean baseIsFile = true;

        File baseResource = new File(normalizedBasePath);

        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();

        } else if (basePath.endsWith(pathSeparator)) {
            baseIsFile = false;
        }

        StringBuilder relative = new StringBuilder();

        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                relative.append("..");
                relative.append(pathSeparator);
            }
        }
        relative.append(normalizedTargetPath.substring(common.length()));
        return relative.toString();
    }

    public static String separatorsToUnix(String path) {
        if (path == null || path.indexOf(WINDOWS_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
    }

    public static String separatorsToWindows(String path) {
        if (path == null || path.indexOf(UNIX_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(UNIX_SEPARATOR, WINDOWS_SEPARATOR);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    static class PathResolutionException extends RuntimeException {
        PathResolutionException(String msg) {
            super(msg);
        }
    }
}
