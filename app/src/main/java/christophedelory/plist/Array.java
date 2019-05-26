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

import java.util.ArrayList;
import java.util.List;

/**
 * A plist array collection.
 * @version $Revision: 92 $
 * @author Christophe Delory
 * @castor.class xml="array"
 */
public class Array extends PlistObject
{
    /**
     * The list of plist objects.
     */
    private final List<PlistObject> _objects = new ArrayList<PlistObject>();

    /**
     * Returns the list of plist objects.
     * @return a list of plist objects. May be empty but not <code>null</code>.
     * @see #addPlistObject
     * @castor.field
     *  get-method="getPlistObjects"
     *  set-method="addPlistObject"
     *  type="christophedelory.plist.PlistObject"
     *  collection="arraylist"
     * @castor.field-xml
     *  auto-naming="deriveByClass"
     *  node="element"
     */
    public List<PlistObject> getPlistObjects()
    {
        return _objects;
    }

    /**
     * Adds the specified plist object.
     * @param object a plist object. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>object</code> is <code>null</code>.
     * @see #getPlistObjects
     */
    public void addPlistObject(final PlistObject object)
    {
        if (object instanceof Key) // Throws NullPointerException if object is null.
        {
            throw new IllegalArgumentException("No dictionary key allowed in an array");
        }

        object.setParent(this);
        _objects.add(object);
    }
}
