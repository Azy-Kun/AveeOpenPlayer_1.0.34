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

package com.aveeopen.comp.Playlists.Files;

import android.content.Context;

import com.aveeopen.Common.tlog;
import com.aveeopen.PlayerCore;
import com.aveeopen.comp.playback.Song.PlaylistSong;

import org.myapache.commons.logging.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import christophedelory.playlist.Media;
import christophedelory.playlist.Playlist;
import christophedelory.playlist.Sequence;
import christophedelory.playlist.SpecificPlaylist;
import christophedelory.playlist.SpecificPlaylistProvider;
import christophedelory.playlist.m3u.M3U;
import mychristophedelory.content.Content;
import mychristophedelory.content.type.ContentType;
import mychristophedelory.logging.LogFactory;

public class PlaylistFilesUtils {

    private static PlaylistFilesUtils instance = null;
    private final Log logger;
    private Iterable<SpecificPlaylistProvider> serviceLoader;

    public PlaylistFilesUtils() {
        logger = LogFactory.getLog(getClass()); // May throw LogConfigurationException.

//        # First one, as it is a binary format that can easily be recognized.
//        christophedelory.playlist.pla.PLAProvider
//        christophedelory.playlist.asx.AsxProvider
//        christophedelory.playlist.b4s.B4sProvider
//        # BEFORE SMIL (same root element).
//        christophedelory.playlist.wpl.WplProvider
//        christophedelory.playlist.smil.SmilProvider
//        christophedelory.playlist.rss.RSSProvider
//        christophedelory.playlist.atom.AtomProvider
//        # Before XSPF, because this format is very close to XSPF,
//        # but its XML format is strictly checked (and XSPF's format is not)
//        christophedelory.playlist.hypetape.HypetapeProvider
//        christophedelory.playlist.xspf.XspfProvider
//        christophedelory.playlist.rmp.RmpProvider
//        christophedelory.playlist.plist.PlistProvider
//        christophedelory.playlist.kpl.KplProvider
//        christophedelory.playlist.pls.PLSProvider
//        christophedelory.playlist.mpcpl.MPCPLProvider
//        christophedelory.playlist.plp.PLPProvider
//        # Shall be last, at the M3U format can match almost everything.
//            christophedelory.playlist.m3u.M3UProvider

        List<SpecificPlaylistProvider> serviceLoader = new ArrayList<>();

        serviceLoader.add(new christophedelory.playlist.pla.PLAProvider());
        //serviceLoader.add(new christophedelory.playlist.asx.AsxProvider());
        //serviceLoader.add(new christophedelory.playlist.b4s.B4sProvider());

        //serviceLoader.add(new christophedelory.playlist.wpl.WplProvider());
        //serviceLoader.add(new christophedelory.playlist.smil.SmilProvider());
        //serviceLoader.add(new christophedelory.playlist.rss.RSSProvider());
        //serviceLoader.add(new christophedelory.playlist.atom.AtomProvider());

        //serviceLoader.add(new christophedelory.playlist.hypetape.HypetapeProvider());
        //serviceLoader.add(new christophedelory.playlist.xspf.XspfProvider());
        //serviceLoader.add(new christophedelory.playlist.rmp.RmpProvider());
        //serviceLoader.add(new christophedelory.playlist.plist.PlistProvider());

        //TODO: org.w3c.dom.DOMException: 0, on saving playlist, can't use numbers as tags
        //serviceLoader.add(new christophedelory.playlist.kpl.KplProvider());

        serviceLoader.add(new christophedelory.playlist.pls.PLSProvider());
        serviceLoader.add(new christophedelory.playlist.mpcpl.MPCPLProvider());
        serviceLoader.add(new christophedelory.playlist.plp.PLPProvider());

        serviceLoader.add(new christophedelory.playlist.m3u.M3UProvider());


        this.serviceLoader = serviceLoader;
    }

    public static PlaylistFilesUtils s() {
        if (instance == null)
            instance = new PlaylistFilesUtils();
        return instance;
    }

    public static String makePlaylistPath(String destPath, String name, PlaylistFilesType playlistType) {
        if (destPath != null && destPath.length() > 0) {
            if (destPath.charAt(destPath.length() - 1) != '/')
                destPath += "/";
        } else {
            destPath = "//";
        }

        return destPath + name + "." + playlistType.fileExtension;
    }

    //taken from SpecificPlaylistFactory.readFrom
    private SpecificPlaylist myReadFrom(final URL url) throws IOException {
        SpecificPlaylist ret = null;

        for (SpecificPlaylistProvider service : serviceLoader) {
            final URLConnection urlConnection = url.openConnection(); //  Throws NullPointerException if url is null. May throw IOException.
            urlConnection.setAllowUserInteraction(false); // Shall not throw IllegalStateException.
            urlConnection.setConnectTimeout(10000); // Shall not throw IllegalArgumentException.
            urlConnection.setDoInput(true); // Shall not throw IllegalStateException.
            urlConnection.setDoOutput(false); // Shall not throw IllegalStateException.
            urlConnection.setReadTimeout(60000); // Shall not throw IllegalArgumentException.
            urlConnection.setUseCaches(true); // Shall not throw IllegalStateException.

            urlConnection.connect(); // May throw SocketTimeoutException, IOException.

            final String contentEncoding = urlConnection.getContentEncoding(); // May be null.

            final InputStream in = urlConnection.getInputStream(); // May throw IOException, UnknownServiceException.

            try {
                ret = service.readFrom(in, contentEncoding, logger); // May throw Exception. Shall not throw NullPointerException because of in.
                // Returns it even if null.
                break;
            } catch (Exception e) {
                // Ignore it.
                if (logger.isTraceEnabled()) {
                    logger.trace("Playlist provider " + service.getId() + " cannot unmarshal <" + url + ">", e);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Playlist provider " + service.getId() + " cannot unmarshal <" + url + ">: " + e);
                }
            } finally {
                in.close(); // May throw IOException.
            }
        }

        return ret;
    }

    //taken from SpecificPlaylistFactory.findProviderByExtension
    private SpecificPlaylistProvider findProviderByExtension(final String filename) {
        SpecificPlaylistProvider ret = null;
        final String name = filename.toLowerCase(Locale.ENGLISH); // Throws NullPointerException if filename is null.

        for (SpecificPlaylistProvider service : serviceLoader) {
            final ContentType[] types = service.getContentTypes();

            for (ContentType type : types) {
                if (type.matchExtension(name)) {
                    ret = service;
                    break;
                }
            }

            if (ret != null) {
                break;
            }
        }

        return ret;
    }

    public List<PlaylistSong> getSongsFromPlaylistFile(String filePath) {

        URL url;

        try {
            url = new URL("file://" + filePath);
        } catch (MalformedURLException e) {
            return null;
        }

        URL _url = url;
        File inputFile = new File(filePath);

        Context context = PlayerCore.s().getAppContext();
        if (context == null)
            return null;

        SpecificPlaylist specificPlaylist = null;

        try {
            specificPlaylist = myReadFrom(_url);
        } catch (IOException e) {
            tlog.w(e.getMessage());
        }

        if (specificPlaylist == null) {
            return null;
        }

        List<PlaylistSong> resultList = new ArrayList<>();

        if (inputFile.exists()) {

            PlaylistFilesRUtils.ReadParameters parameters = new PlaylistFilesRUtils.ReadParameters();
            try {
                parameters.playlistPath = inputFile.getCanonicalPath();
            } catch (Exception e) {
                parameters.playlistPath = inputFile.getAbsolutePath();
            }

            PlaylistFilesRUtils.readFromSpecificPlaylist(specificPlaylist, parameters, resultList);
        }

        return resultList;
    }


    public int createPlaylist(String filePath, PlaylistFilesType playlistType, boolean writeRelativePaths) {
        return createPlaylist(filePath, playlistType, null, writeRelativePaths);
    }

    public int createPlaylist(String filePath, PlaylistFilesType playlistType, List<String> dataSources, boolean writeRelativePaths) {
        return addDataSourceToPlaylistFile(filePath, dataSources, true, writeRelativePaths);
    }

    public int addDataSourceToPlaylistFile(String filePath, List<String> dataSources, boolean overwriteCurrentContent, boolean writeRelativePaths) {
        return addToPlaylistFile(filePath, PlaylistSong.makeSongListFromDataSourceList(dataSources), overwriteCurrentContent, writeRelativePaths);
    }

    //_dataSources maybe null
    public int addToPlaylistFile(String filePath, List<PlaylistSong> dataSources, boolean overwriteCurrentContent, boolean writeRelativePaths) {

        URL url;

        try {
            if (filePath.startsWith("file:"))
                url = new URL(filePath);
            else
                url = new URL("file://" + filePath);
        } catch (MalformedURLException e) {
            return 0;
        }

        final boolean _extM3U = true;

        SpecificPlaylistProvider specificPlaylistProvider = null;
        SpecificPlaylist specificPlaylist = null;

        File outputFile = new File(filePath);

        if (outputFile.exists()) {

            try {
                specificPlaylist = myReadFrom(url);
                specificPlaylistProvider = specificPlaylist.getProvider();

                if (specificPlaylist instanceof M3U) {
                    ((M3U) specificPlaylist).setExtensionM3U(_extM3U);
                }

                if (overwriteCurrentContent) {
                    try {
                        specificPlaylist = specificPlaylistProvider.toSpecificPlaylist(new Playlist());
                    } catch (Exception e) {
                        tlog.w(e.getMessage());
                    }
                }
            } catch (IOException e) {
                tlog.w(e.getMessage());
            }

            if (specificPlaylist == null || specificPlaylistProvider == null) {
                if (!overwriteCurrentContent) {
                    //not allowed to overwrite file
                    return 0;
                }

                specificPlaylistProvider = findProviderByExtension(filePath);
                try {
                    specificPlaylist = specificPlaylistProvider.toSpecificPlaylist(new Playlist());
                } catch (Exception e) {
                    tlog.w(e.getMessage());
                }
            }

        } else {
            //file doesn't exist
            overwriteCurrentContent = true;
            specificPlaylistProvider = findProviderByExtension(filePath);

            try {
                specificPlaylist = specificPlaylistProvider.toSpecificPlaylist(new Playlist());
            } catch (Exception e) {
                tlog.w(e.getMessage());
            }
        }

        if (specificPlaylist == null) {
            tlog.w("error specificPlaylist is null");
            return 0;
        }

        SpecificPlaylist outputSpecificPlaylist = specificPlaylist;

        PlaylistFilesWUtils.AppendParameters appendParameters = new PlaylistFilesWUtils.AppendParameters();
        appendParameters.writeRelativePaths = writeRelativePaths;
        try {
            appendParameters.playlistPath = outputFile.getCanonicalPath();
        } catch (Exception e) {
            appendParameters.playlistPath = outputFile.getAbsolutePath();
        }

        int addedCount = PlaylistFilesWUtils.appendToSpecificPlaylist(outputSpecificPlaylist, appendParameters, dataSources);
        OutputStream out;

        try {
            //
            File dir = outputFile.getParentFile();
            if (dir != null && !dir.exists())
                dir.mkdirs();

            out = new FileOutputStream(outputFile); // May throw FileNotFoundException, SecurityException.
        } catch (Exception e) {
            return 0;
        }

        try {
            outputSpecificPlaylist.writeTo(out, null); // May throw Exception.
            out.flush(); // May throw IOException.
            out.close(); // May throw IOException.
        } catch (Exception e) {
            tlog.w("outputSpecificPlaylist.writeTo: " + e.getMessage());
            e.printStackTrace();
        }

        return addedCount;
    }

    /**
     * Adds the specified file or directory, and optionally its sub-directories, to the input sequence.
     *
     * @param sequence     the playlist sequence to add to. Shall not be <code>null</code>.
     * @param file         a file or directory. Shall not be <code>null</code>.
     * @param recurse      specifies if the sub-directories of this directory shall be recursively scanned or not.
     * @param playlistFile an optional file to exclude from the sequence. May be <code>null</code>.
     * @throws NullPointerException if <code>sequence</code> is <code>null</code>.
     * @throws NullPointerException if <code>file</code> is <code>null</code>.
     * @throws SecurityException    if a security manager exists and its {@link SecurityManager#checkRead(String)} method denies read access to a file.
     * @throws IOException          if an I/O error occurs.
     */
    private void lizzyAddToPlaylistAsFile(final Sequence sequence, final File file, final boolean recurse, final File playlistFile, final boolean recursive) throws IOException {
        if (file.isDirectory()) // Throws NullPointerException if file is null. May throw SecurityException.
        {
            if (recurse) {
                final File[] files = file.listFiles(); // May throw SecurityException.

                if (files != null) {
                    for (File child : files) {
                        lizzyAddToPlaylistAsFile(sequence, child, recursive, playlistFile, recursive); // Throws NullPointerException if sequence is null. May throw SecurityException, IOException.
                    }
                }
            }
        } else if (file.isFile()) // May throw SecurityException.
        {
            boolean include = true;
            String filePath = file.getPath();

            if (playlistFile != null) {
                final File canonicalFile = file.getCanonicalFile(); // May throw IOException, SecurityException.

                if (canonicalFile.equals(playlistFile)) {
                    include = false;
                } else {
                    // Try to make the playlist entry file name RELATIVE to the playlist file.
                    File parentFile = canonicalFile.getParentFile(); // Shall not be null, it is a file, not a directory.
                    final File playlistParentFile = playlistFile.getParentFile(); // Shall not be null, it is a file, not a directory.

                    if (parentFile.equals(playlistParentFile)) {
                        filePath = file.getName();
                    } else {
                        final StringBuilder sb = new StringBuilder(file.getName());
                        File previousFile = parentFile;
                        parentFile = previousFile.getParentFile();

                        while (parentFile != null) {
                            sb.insert(0, '/'); // Shall not throw StringIndexOutOfBoundsException.
                            final String previousFileName = previousFile.getName();

                            if (!"/".equals(previousFileName) && !"\\".equals(previousFileName)) {
                                sb.insert(0, previousFileName); // Shall not throw StringIndexOutOfBoundsException.
                            }

                            if (parentFile.equals(playlistParentFile)) {
                                filePath = sb.toString();
                                break;
                            }

                            previousFile = parentFile;
                            parentFile = previousFile.getParentFile();
                        }
                    }
                }
            }

            if (include) {
                final Media media = new Media();
                final Content content = new Content(filePath);
                media.setSource(content);

                sequence.addComponent(media); // Throws NullPointerException if sequence is null.
            }
        }
    }

}