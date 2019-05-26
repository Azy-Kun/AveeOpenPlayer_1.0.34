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

/**
 * Human-readable text, usually in small quantities.
 * <br>
 * <u>Note</u>: when present, the value of the "type" attribute must be one of "text", "html", or "xhtml".
 * If the "type" attribute is not provided, Atom processors must behave as though it were present with a value of "text".
 * Unlike the content element, MIME media types must NOT be used as values for the "type" attribute here.
 * <br>
 * <u>Caution</u>: XHTML elements are not handled.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="textcontainer" ns-uri="http://www.w3.org/2005/Atom"
 */
public class TextContainer extends Type
{
    /**
     * The associated text.
     */
    private String _text = null;

    /**
     * Returns the associated text.
     * No default value.
     * @return a text. May be <code>null</code> if not yet initialized.
     * @see #setText
     * @castor.field
     *  get-method="getText"
     *  set-method="setText"
     *  required="true"
     * @castor.field-xml
     *  node="text"
     */
    public String getText()
    {
        return _text;
    }

    /**
     * Initializes the associated text.
     * @param text a text. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>text</code> is <code>null</code>.
     * @see #getText
     */
    public void setText(final String text)
    {
        _text = text.trim(); // Throws NullPointerException if text is null.
    }
}
