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
package christophedelory.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringWriter;

/**
 * General IO manipulation.
 * @version $Revision: 55 $
 * @author Christophe Delory
 */
public final class IOUtils
{
    /**
     * Copies the contents of the specified input stream to a string, using the specified character encoding.
     * @param in an input stream. Shall not be <code>null</code>.
     * @param encoding the encoding to use. May be <code>null</code> (in this case, the default platform one will be used).
     * @return a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>in</code> is <code>null</code>.
     * @throws IOException if an I/O error occurs.
     * @throws java.io.UnsupportedEncodingException if the named charset is not supported.
     */
    public static String toString(final InputStream in, final String encoding) throws IOException
    {
        final InputStreamReader reader;

        if (encoding == null)
        {
            reader = new InputStreamReader(in); // Throws NullPointerException if in is null.
        }
        else
        {
            reader = new InputStreamReader(in, encoding); // May throw UnsupportedEncodingException. Throws NullPointerException if in is null.
        }

        final StringWriter writer = new StringWriter();
        final char[] buffer = new char[512];
        int nb = 0;

        while (-1 != (nb = reader.read(buffer))) // May throw IOException.
        {
            writer.write(buffer, 0, nb);
        }

        return writer.toString();
    }

    /**
     * The default no-arg constructor shall not be accessible.
     */
    private IOUtils()
    {
    }
}
