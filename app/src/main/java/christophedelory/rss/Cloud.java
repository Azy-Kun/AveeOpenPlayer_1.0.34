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

/**
 * It specifies a web service that supports the rssCloud interface which can be implemented in HTTP-POST, XML-RPC or SOAP 1.1.
 * Its purpose is to allow processes to register with a cloud to be notified of updates to the channel, implementing a lightweight publish-subscribe protocol for RSS feeds.
 * <pre>
 * &lt;cloud domain="rpc.sys.com" port="80" path="/RPC2" registerProcedure="myCloud.rssPleaseNotify" protocol="xml-rpc" /&gt;
 * </pre>
 * In this example, to request notification on the channel it appears in, you would send an XML-RPC message to rpc.sys.com on port 80, with a path of /RPC2.
 * The procedure to call is myCloud.rssPleaseNotify.
 * A full explanation of this element and the rssCloud interface is <a href="http://blogs.law.harvard.edu/tech/soapMeetsRss#rsscloudInterface">here</a>.
 * @author Christophe Delory
 * @version $Revision: 92 $
 * @castor.class xml="cloud"
 */
public class Cloud
{
    /**
     * The cloud's domain.
     */
    private String _domain = null;

    /**
     * A port number.
     */
    private int _port = 0;

    /**
     * The cloud's path.
     */
    private String _path = null;

    /**
     * The cloud's registration procedure.
     */
    private String _registerProcedure = null;

    /**
     * A protocol.
     */
    private String _protocol = null;

    /**
     * Initializes the cloud's domain.
     * @param domain a domain. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>domain</code> is <code>null</code>.
     * @see #getDomain
     */
    public void setDomain(final String domain)
    {
        _domain = domain.trim(); // Throws NullPointerException if domain is null.
    }

    /**
     * Returns the cloud's domain.
     * Example: "rpc.sys.com".
     * No default value.
     * @return the cloud's domain. May be <code>null</code> if not yet initialized.
     * @see #setDomain
     * @castor.field
     *  get-method="getDomain"
     *  set-method="setDomain"
     *  required="true"
     * @castor.field-xml
     *  name="domain"
     *  node="attribute"
     */
    public String getDomain()
    {
        return _domain;
    }

    /**
     * Initializes the port number.
     * @param port a port number.
     * @see #getPort
     */
    public void setPort(final int port)
    {
        _port = port;
    }

    /**
     * Returns the port number.
     * Example: 80.
     * Defaults to 0.
     * @return a port number.
     * @see #setPort
     * @castor.field
     *  get-method="getPort"
     *  set-method="setPort"
     *  required="true"
     * @castor.field-xml
     *  name="port"
     *  node="attribute"
     */
    public int getPort()
    {
        return _port;
    }

    /**
     * Returns the cloud's path.
     * Example: "/RPC2".
     * No default value.
     * @return the cloud's path. May be <code>null</code> if not yet initialized.
     * @see #setPath
     * @castor.field
     *  get-method="getPath"
     *  set-method="setPath"
     *  required="true"
     * @castor.field-xml
     *  name="path"
     *  node="attribute"
     */
    public String getPath()
    {
        return _path;
    }

    /**
     * Initializes the cloud's path.
     * @param path a path. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>path</code> is <code>null</code>.
     * @see #getPath
     */
    public void setPath(final String path)
    {
        _path = path.trim(); // Throws NullPointerException if path is null.
    }

    /**
     * Returns the cloud's registration procedure.
     * Examples: "myCloud.rssPleaseNotify", "pingMe".
     * No default value.
     * @return a registration procedure. May be <code>null</code> if not yet initialized.
     * @see #setRegisterProcedure
     * @castor.field
     *  get-method="getRegisterProcedure"
     *  set-method="setRegisterProcedure"
     *  required="true"
     * @castor.field-xml
     *  name="registerProcedure"
     *  node="attribute"
     */
    public String getRegisterProcedure()
    {
        return _registerProcedure;
    }

    /**
     * Initializes the cloud's register procedure.
     * @param registerProcedure a registration procedure. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>registerProcedure</code> is <code>null</code>.
     * @see #getRegisterProcedure
     */
    public void setRegisterProcedure(final String registerProcedure)
    {
        _registerProcedure = registerProcedure.trim(); // Throws NullPointerException if registerProcedure is null.
    }

    /**
     * Returns the protocol.
     * Examples: "xml-rpc", "soap".
     * No default value.
     * @return a protocol. May be <code>null</code> if not yet initialized.
     * @see #setProtocol
     * @castor.field
     *  get-method="getProtocol"
     *  set-method="setProtocol"
     *  required="true"
     * @castor.field-xml
     *  name="protocol"
     *  node="attribute"
     */
    public String getProtocol()
    {
        return _protocol;
    }

    /**
     * Initializes the protocol.
     * @param protocol a protocol. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>protocol</code> is <code>null</code>.
     * @see #getProtocol
     */
    public void setProtocol(final String protocol)
    {
        _protocol = protocol.trim(); // Throws NullPointerException if protocol is null.
    }
}
