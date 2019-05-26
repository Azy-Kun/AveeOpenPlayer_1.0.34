/*
 * Copyright (c) 2008, Christophe Delory
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY CHRISTOPHE DELORY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CHRISTOPHE DELORY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package mychristophedelory.content.type;

import java.io.File;
import java.util.Locale;

//import javax.swing.filechooser.FileFilter;

import christophedelory.player.PlayerSupport;

/**
 * Defines a mapping between a group of one or more file extensions, and a group of one or more MIME type.
 * MIME stands for Multipurpose Internet Mail Extension, as defined in RFC 2045 and 2046.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public class ContentType implements Cloneable // extends FileFilter
{
    /**
     * A list of file extensions, for example: <code>.txt</code>.
     */
    private final String[] _extensions;

    /**
     * A list of MIME types.
     * Examples: <code>text/plain</code>, "application/octet-stream".
     */
    private final String[] _mimeTypes;

    /**
     * The list of players supporting this type of content.
     */
    private final PlayerSupport[] _playerSupports;

    /**
     * An optional description of the content type.
     */
    private String _description;

    /**
     * Builds a new content type from the input parameters.
     * The extensions shall include the dot ('.') character, if needed.
     * All input strings are converted to lower case using the default locale.
     * @param extensions an array of file extensions. Shall not be <code>null</code>.
     * @param mimeTypes an array of MIME types. Shall not be <code>null</code>.
     * @param playerSupports an array of player support information. May be <code>null</code>.
     * @param description a (friendly) description of the new content type. May be <code>null</code>.
     * @throws NullPointerException if <code>extensions</code> or one of its component is <code>null</code>.
     * @throws NullPointerException if <code>mimeTypes</code> or one of its component is <code>null</code>.
     * @throws IllegalArgumentException if <code>extensions</code> has no items (empty array).
     * @throws IllegalArgumentException if <code>mimeTypes</code> has no items (empty array).
     * @see String#toLowerCase(Locale)
     */
    public ContentType(final String[] extensions, final String[] mimeTypes, final PlayerSupport[] playerSupports, final String description)
    {
        super();

        if (extensions.length <= 0) // Throws NullPointerException if extensions is null.
        {
            throw new IllegalArgumentException("Empty extension array");
        }

        if (mimeTypes.length <= 0) // Throws NullPointerException if mimeTypes is null.
        {
            throw new IllegalArgumentException("Empty MIME type array");
        }

        _extensions = new String[extensions.length];

        for (int i = 0; i < extensions.length; i++)
        {
            _extensions[i] = extensions[i].toLowerCase(Locale.ENGLISH); // Throws NullPointerException if extensions[i] is null.
        }

        _mimeTypes = new String[mimeTypes.length];

        for (int i = 0; i < mimeTypes.length; i++)
        {
            _mimeTypes[i] = mimeTypes[i].toLowerCase(Locale.ENGLISH); // Throws NullPointerException if mimeTypes[i] is null.
        }

        _description = description;
        _playerSupports = (playerSupports == null) ? new PlayerSupport[0] : playerSupports.clone(); // Should not throw CloneNotSupportedException.
    }

    /**
     * Returns the list of file extensions.
     * The extensions may include the dot ('.') character, if it is part of the file extension.
     * @return an array of extensions. Shall not be <code>null</code> nor empty.
     */
    public String[] getExtensions()
    {
        return _extensions.clone();
    }

    /**
     * Returns the list of MIME types.
     * @return an array of MIME types. Shall not be <code>null</code> nor empty.
     */
    public String[] getMimeTypes()
    {
        return _mimeTypes.clone();
    }

    /**
     * Returns the list of players supporting this type of content.
     * @return an array of playlist support information. May be empty but not <code>null</code>.
     * @since 0.2.0
     */
    public PlayerSupport[] getPlayerSupports()
    {
        return _playerSupports.clone();
    }

    /**
     * Returns the friendly description of the content type.
     * @return a content type description. May be <code>null</code>.
     * @see #setDescription
     * @see //FileFilter#getDescription
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Initializes the friendly description of the content type.
     * @param description a content type description. May be <code>null</code>.
     * @see #getDescription
     */
    public void setDescription(final String description)
    {
        _description = description;
    }

    /**
     * Tests whether this content type matches the specified file extension pattern.
     * The input pattern is first converted to lower case, using the default locale.
     * @param pattern a file name, or more generally a pattern including a file extension. Shall not be <code>null</code>.
     * @return <code>true</code> if <code>pattern</code> matches this content type.
     * @throws NullPointerException if <code>pattern</code> is <code>null</code>.
     * @see #getExtensions
     * @see String#toLowerCase(Locale)
     * @see String#endsWith
     */
    public boolean matchExtension(final String pattern)
    {
        final String p = pattern.toLowerCase(Locale.ENGLISH); // Throws NullPointerException if pattern is null.
        boolean ret = false;

        for (String extension : _extensions)
        {
            ret = ret || p.endsWith(extension);
        }

        return ret;
    }

    /**
     * Tests whether the name of the given file matches the specified file extension pattern.
     * <br>
     * <u>CAUTION</u>: this method accepts also all directories, in order to be properly used with a {@link //javax.swing.JFileChooser}.
     * @param f the file to test. Shall not be <code>null</code>.
     * @return <code>true</code> if the file extension matches this content type.
     * @throws NullPointerException if <code>f</code> is <code>null</code>.
     * @see #matchExtension
     */
    //@Override
    public boolean accept(final File f)
    {
        return (f.isDirectory()) ? true : matchExtension(f.getName()); // Throws NullPointerException if f is null.
    }

    /**
     * Creates and returns a "shallow" copy of this object.
     * @return a clone of this instance.
     * @throws CloneNotSupportedException shall not be thrown, because this class is cloneable.
     * @since 1.0.0
     * @see Object#clone
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone(); // Should not throw CloneNotSupportedException.
    }
}
