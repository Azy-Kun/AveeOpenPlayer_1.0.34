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
package christophedelory.rss;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Specifies a text input box that can be displayed with the channel.
 * The purpose of this element is something of a mystery.
 * You can use it to specify a search engine box.
 * Or to allow a reader to provide feedback.
 * Most aggregators ignore it.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="textInput"
 */
public class TextInput
{
    /**
     * The label of the Submit button in the text input area.
     */
    private String _title = null;

    /**
     * Explains the text input area.
     */
    private String _description = null;

    /**
     * The name of the text object in the text input area.
     */
    private String _name = null;

    /**
     * The URL of the CGI script that processes text input requests.
     */
    private URI _link = null;

    /**
     * Initializes the label of the Submit button in the text input area.
     * @param title a button label. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>title</code> is <code>null</code>.
     * @see #getTitle
     */
    public void setTitle(final String title)
    {
        _title = title.trim(); // Throws NullPointerException if title is null.
    }

    /**
     * Returns the label of the Submit button in the text input area.
     * No default value.
     * @return a button label. May be <code>null</code> if not yet initialized.
     * @see #setTitle
     * @castor.field
     *  get-method="getTitle"
     *  set-method="setTitle"
     *  required="true"
     * @castor.field-xml
     *  name="title"
     *  node="element"
     */
    public String getTitle()
    {
        return _title;
    }

    /**
     * Returns the explanation for the text input area.
     * @return the text input description. May be <code>null</code> if not yet initialized.
     * @see #setDescription
     * @castor.field
     *  get-method="getDescription"
     *  set-method="setDescription"
     *  required="true"
     * @castor.field-xml
     *  name="description"
     *  node="element"
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Initializes the explanation for the text input area.
     * @param description the text input description. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>description</code> is <code>null</code>.
     * @see #getDescription
     */
    public void setDescription(final String description)
    {
        _description = description.trim(); // Throws NullPointerException if description is null.
    }

    /**
     * Initializes the name of the text object in the text input area.
     * @param name a name. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @see #getName
     */
    public void setName(final String name)
    {
        _name = name.trim(); // Throws NullPointerException if name is null.
    }

    /**
     * Returns the name of the text object in the text input area.
     * No default value.
     * @return a name. May be <code>null</code> if not yet initialized.
     * @see #setName
     * @castor.field
     *  get-method="getName"
     *  set-method="setName"
     *  required="true"
     * @castor.field-xml
     *  name="name"
     *  node="element"
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Returns the URL of the CGI script that processes text input requests.
     * @return an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if no link has been defined in this text input description.
     * @see #setLinkString
     * @see #getLink
     * @castor.field
     *  get-method="getLinkString"
     *  set-method="setLinkString"
     *  required="true"
     * @castor.field-xml
     *  name="link"
     *  node="element"
     */
    public String getLinkString()
    {
        return _link.toString(); // Throws NullPointerException if _link is null.
    }

    /**
     * Initializes the URL of the CGI script that processes text input requests.
     * @param link an URL as a string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>link</code> is <code>null</code>.
     * @throws URISyntaxException if the given string violates RFC 2396, as augmented by the {@link URI} deviations.
     * @see #getLinkString
     * @see #setLink
     */
    public void setLinkString(final String link) throws URISyntaxException
    {
        _link = new URI(link); // May throw URISyntaxException. Throws NullPointerException if link is null.
    }

    /**
     * Initializes the URL of the CGI script that processes text input requests.
     * @param link an URL. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>link</code> is <code>null</code>.
     * @see #getLink
     * @see #setLinkString
     */
    public void setLink(final URI link)
    {
        if (link == null)
        {
            throw new NullPointerException("No link");
        }

        _link = link;
    }

    /**
     * Returns the URL of the CGI script that processes text input requests.
     * No default value.
     * @return an URL. May be <code>null</code> if not yet initialized.
     * @see #setLink
     * @see #getLinkString
     */
    public URI getLink()
    {
        return _link;
    }
}
