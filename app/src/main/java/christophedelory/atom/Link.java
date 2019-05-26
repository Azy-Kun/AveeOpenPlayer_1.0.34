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
 * A reference from an entry or feed to a Web resource.
 * <br>
 * <u>Note</u>: the "type" attribute's value here is an advisory media type:
 * it is a hint about the type of the representation that is expected to be returned when the value of the href attribute is dereferenced.
 * Note that the type attribute does not override the actual media type returned with the representation.
 * The value must conform to the syntax of a <a href="http://www.ietf.org/rfc/rfc4288.txt">MIME media type</a>.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="link" ns-uri="http://www.w3.org/2005/Atom"
 */
public class Link extends Type
{
    /**
     * The link's URI.
     */
    private String _href = null;

    /**
     * The link relation type.
     */
    private String _rel = null;

    /**
     * The language of the resource pointed to by the href attribute.
     */
    private String _hreflang = null;

    /**
     * Human-readable information about the link.
     */
    private String _title = null;

    /**
     * An advisory length of the linked content in octets.
     */
    private Long _length = null;

    /**
     * Returns the link's URI.
     * No default value.
     * @return an URI as a string. May be <code>null</code> if not yet initialized.
     * @see #setHref
     * @castor.field
     *  get-method="getHref"
     *  set-method="setHref"
     *  required="true"
     * @castor.field-xml
     *  name="href"
     *  node="attribute"
     */
    public String getHref()
    {
        return _href;
    }

    /**
     * Initializes the link's URI.
     * @param href an URI as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>href</code> is <code>null</code>.
     * @see #getHref
     */
    public void setHref(final String href)
    {
        _href = href.trim(); // Throws NullPointerException if href is null.
    }

    /**
     * Returns the link relation type.
     * If missing, must be interpreted as if the link relation type is "alternate".
     * <br>
     * Five initial values are defined:
     * <ol>
     * <li>The value "alternate" signifies that the URI in the value of the href attribute identifies an alternate version of the resource described by the containing element.</li>
     * <li>The value "related" signifies that the URI in the value of the href attribute identifies a resource related to the resource described by the containing element.
     * For example, the feed for a site that discusses the performance of the search engine at "http://search.example.com" might contain, as a child of the feed element:
     * <pre>
     * &lt;link rel="related" href="http://search.example.com/"/&gt;
     * </pre>
     * An identical link might appear as a child of any entry elements whose content contains a discussion of that same search engine.</li>
     * <li>The value "self" signifies that the URI in the value of the href attribute identifies a resource equivalent to the containing element.</li>
     * <li>The value "enclosure" signifies that the URI in the value of the href attribute identifies a related resource that is potentially large in size and might require special handling.
     * For link elements with rel="enclosure", the length attribute should be provided.</li>
     * <li>The value "via" signifies that the URI in the value of the href attribute identifies a resource that is the source of the information provided in the containing element.</li>
     * </ol>
     * @return a relation type. May be <code>null</code> if not yet initialized.
     * @see #setRel
     * @castor.field
     *  get-method="getRel"
     *  set-method="setRel"
     * @castor.field-xml
     *  name="rel"
     *  node="attribute"
     */
    public String getRel()
    {
        return _rel;
    }

    /**
     * Initializes the link relation type.
     * @param rel a relation type. May be <code>null</code>.
     * @see #setRel
     */
    public void setRel(final String rel)
    {
        _rel = StringUtils.normalize(rel);
    }

    /**
     * Returns the language of the resource pointed to by the href attribute.
     * When used together with the rel="alternate", it implies a translated version of the entry.
     * @return a language tag. May be <code>null</code>.
     * @see #setHrefLang
     * @castor.field
     *  get-method="getHrefLang"
     *  set-method="setHrefLang"
     * @castor.field-xml
     *  name="hreflang"
     *  node="attribute"
     */
    public String getHrefLang()
    {
        return _hreflang;
    }

    /**
     * Initializes the language of the resource pointed to by the href attribute.
     * @param hreflang a language tag. May be <code>null</code>.
     * @see #setHrefLang
     */
    public void setHrefLang(final String hreflang)
    {
        _hreflang = StringUtils.normalize(hreflang);
    }

    /**
     * Returns human-readable information about the link.
     * @return a title. May be <code>null</code>.
     * @see #setTitle
     * @castor.field
     *  get-method="getTitle"
     *  set-method="setTitle"
     * @castor.field-xml
     *  name="title"
     *  node="attribute"
     */
    public String getTitle()
    {
        return _title;
    }

    /**
     * Initializes human-readable information about the link.
     * @param title a title. May be <code>null</code>.
     * @see #getTitle
     */
    public void setTitle(final String title)
    {
        _title = StringUtils.normalize(title);
    }

    /**
     * Returns an advisory length of the linked content in octets.
     * @return a length. May be <code>null</code>.
     * @see #setLength
     * @castor.field
     *  get-method="getLength"
     *  set-method="setLength"
     * @castor.field-xml
     *  name="length"
     *  node="attribute"
     */
    public Long getLength()
    {
        return _length;
    }

    /**
     * Initializes an advisory length of the linked content in octets.
     * @param length a length. May be <code>null</code>.
     * @see #getLength
     */
    public void setLength(final Long length)
    {
        _length = length;
    }
}
