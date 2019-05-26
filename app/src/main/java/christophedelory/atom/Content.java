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
package christophedelory.atom;

import christophedelory.lang.StringUtils;

/**
 * Contains or links to the content of an entry.
 * <br>
 * <u>Note</u>: the value of the "type" attribute may be one of "text", "html", or "xhtml".
 * Failing that, it must conform to the syntax of a MIME media type, but must not be a composite type (see section 4.2.6 of <a href="http://www.ietf.org/rfc/rfc4288.txt">RFC4288</a>).
 * If neither the type attribute nor the src attribute is provided, Atom processors must behave as though the type attribute were present with a value of "text".
 * <br>
 * <u>Caution</u>: XHTML elements are not handled.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="content" ns-uri="http://www.w3.org/2005/Atom"
 */
public class Content extends Type
{
    /**
     * The associated text.
     */
    private String _text = null;

    /**
     * The associated URI reference.
     */
    private String _src = null;

    /**
     * Returns the associated text.
     * No default value.
     * @return a text. May be <code>null</code>.
     * @see #setText
     * @castor.field
     *  get-method="getText"
     *  set-method="setText"
     * @castor.field-xml
     *  node="text"
     */
    public String getText()
    {
        return _text;
    }

    /**
     * Initializes the associated text.
     * @param text a text. May be <code>null</code>.
     * @see #getText
     */
    public void setText(final String text)
    {
        _text = StringUtils.normalize(text);
    }

    /**
     * Returns the associated URI reference.
     * If the "src" attribute is present, the content must be empty (i.e. no text).
     * Atom Processors may use the URI to retrieve the content and may choose to ignore remote content or to present it in a different manner than local content.
     * If the "src" attribute is present, the "type" attribute should be provided and must be a MIME media type, rather than "text", "html", or "xhtml".
     * The value is advisory; that is to say, when the corresponding URI is dereferenced, if the server providing that content also provides a media type, the server-provided media type is authoritative.
     * @return an URI as a string. May be <code>null</code>.
     * @see #setSrc
     * @castor.field
     *  get-method="getSrc"
     *  set-method="setSrc"
     * @castor.field-xml
     *  name="src"
     *  node="attribute"
     */
    public String getSrc()
    {
        return _src;
    }

    /**
     * Initializes the associated URI reference.
     * @param src an URI as a string. May be <code>null</code>.
     * @see #getSrc
     */
    public void setSrc(final String src)
    {
        _src = StringUtils.normalize(src);
    }
}
