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
package christophedelory.plist;

/**
 * The definition of a property list.
 * @version $Revision: 92 $
 * @author Christophe Delory
 * @castor.class xml="plist"
 */
public class Plist
{
    /**
     * The version information.
     */
    private java.lang.String _version = "1.0";

    /**
     * The plist object.
     */
    private PlistObject _object = null;

    /**
     * Returns the version information.
     * Defaults to "1.0".
     * @return a version string. Shall not be <code>null</code>.
     * @see #setVersion
     * @castor.field
     *  get-method="getVersion"
     *  set-method="setVersion"
     *  required="true"
     * @castor.field-xml
     *  name="version"
     *  node="attribute"
     */
    public java.lang.String getVersion()
    {
        return _version;
    }

    /**
     * Initializes the version information.
     * @param version a version string. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>version</code> is <code>null</code>.
     * @see #getVersion
     */
    public void setVersion(final java.lang.String version)
    {
        _version = version.trim(); // Throws NullPointerException if version is null.
    }

    /**
     * Returns the plist object.
     * @return a plist object. May be <code>null</code> if not yet initialized.
     * @see #setPlistObject
     * @castor.field
     *  get-method="getPlistObject"
     *  set-method="setPlistObject"
     *  type="christophedelory.plist.PlistObject"
     * @castor.field-xml
     *  auto-naming="deriveByClass"
     *  node="element"
     */
    public PlistObject getPlistObject()
    {
        return _object;
    }

    /**
     * Initializes the plist object.
     * @param object a plist object. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>object</code> is <code>null</code>.
     * @see #getPlistObject
     */
    public void setPlistObject(final PlistObject object)
    {
        if (object instanceof Key) // Throws NullPointerException if object is null.
        {
            throw new IllegalArgumentException("No dictionary key allowed in a plist");
        }

        object.setParent(this);
        _object = object;
    }
}
