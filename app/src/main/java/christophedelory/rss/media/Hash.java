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
package christophedelory.rss.media;

/**
 * This is the hash of the binary media file.
 * It can appear multiple times as long as each instance is a different algo.
 * Example:
 * <pre>
 * &lt;media:hash algo="md5"&gt;dfdec888b72151965a34b4b59031290a&lt;/media:hash&gt;
 * </pre>
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="media:hash" ns-uri="http://search.yahoo.com/mrss/" ns-prefix="media"
 */
public class Hash
{
    /**
     * The algorithm used to create the hash.
     */
    private String _algo = null;

    /**
     * The hash value.
     */
    private String _value = null;

    /**
     * Initializes the hash value.
     * @param value a hash value. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     */
    public void setValue(final String value)
    {
        _value = value.trim(); // Throws NullPointerException if value is null.
    }

    /**
     * Returns the hash value.
     * @return a hash value. May be <code>null</code> if not yet initialized.
     * @see #setValue
     * @castor.field
     *  get-method="getValue"
     *  set-method="setValue"
     *  required="true"
     * @castor.field-xml
     *  node="text"
     */
    public String getValue()
    {
        return _value;
    }

    /**
     * Initializes the algorithm used to create the hash.
     * @param algo a hash algorithm. May be <code>null</code>.
     * @see #getAlgo
     */
    public void setAlgo(final String algo)
    {
        _algo = algo;
    }

    /**
     * Returns the algorithm used to create the hash.
     * Possible values are 'md5' and 'sha-1'.
     * Default value is 'md5'.
     * @return a hash algorithm. May be <code>null</code>.
     * @see #setAlgo
     * @castor.field
     *  get-method="getAlgo"
     *  set-method="setAlgo"
     * @castor.field-xml
     *  name="algo"
     *  node="attribute"
     */
    public String getAlgo()
    {
        return _algo;
    }
}
