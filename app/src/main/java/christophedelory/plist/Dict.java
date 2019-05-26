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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * A plist dictionary collection.
 * @version $Revision: 92 $
 * @author Christophe Delory
 * @castor.class xml="dict"
 */
public class Dict extends PlistObject
{
    /**
     * The list of plist objects.
     */
    private final Hashtable<Key,PlistObject> _objects = new Hashtable<Key,PlistObject>();

    /**
     * A temporary key value, yet to link with a plist object.
     */
    private transient Key _tmpKey = null;

    /**
     * Returns the dictionary of plist keys and their associated objects.
     * @return a map of keys to plist objects. May be empty but not <code>null</code>.
     * @see #put
     */
    public Hashtable<Key,PlistObject> getDictionary()
    {
        return _objects;
    }

    /**
     * Maps a key to the specified plist object.
     * @param key a key. Shall not be <code>null</code>.
     * @param object a plist object. Shall not be <code>null</code>.
     * @return the previous value of the specified key in this dictionary, or <code>null</code> if it did not have one.
     * @throws NullPointerException if <code>key</code> is <code>null</code>.
     * @throws NullPointerException if <code>object</code> is <code>null</code>.
     * @see #getDictionary
     */
    public PlistObject put(final Key key, final PlistObject object)
    {
        return _objects.put(key, object); // Throws NullPointerException if key or object is null.
    }

    /**
     * Maps a key string to the specified plist object.
     * This is a convenience method, equivalent to "<code>put(new Key(key), object)</code>".
     * @param key a key string. Shall not be <code>null</code>.
     * @param object a plist object. Shall not be <code>null</code>.
     * @return the previous value of the specified key in this dictionary, or <code>null</code> if it did not have one.
     * @throws NullPointerException if <code>key</code> is <code>null</code>.
     * @throws NullPointerException if <code>object</code> is <code>null</code>.
     * @see #put(Key,PlistObject)
     */
    public PlistObject put(final java.lang.String key, final PlistObject object)
    {
        final Key k = new Key(key); // Throws NullPointerException if key is null.

        return _objects.put(k, object); // Throws NullPointerException if object is null.
    }

    /**
     * Returns all plist objects (including keys) in this dictionary as a list.
     * This method allows the Castor framework to marshal the dictionary to XML.
     * The developer should rather use the {@link #getDictionary} method.
     * That's why it is tagged as deprecated.
     * @return a list of plist objects. May be empty but not <code>null</code>.
     * @deprecated for Castor's usage. Use {@link #getDictionary} instead.
     * @see #addKeyOrObject
     * @see #getDictionary
     * @castor.field
     *  get-method="getKeysAndObjects"
     *  set-method="addKeyOrObject"
     *  type="christophedelory.plist.PlistObject"
     *  collection="arraylist"
     * @castor.field-xml
     *  auto-naming="deriveByClass"
     *  node="element"
     */
    @Deprecated
    public List<PlistObject> getKeysAndObjects()
    {
        final List<PlistObject> ret = new ArrayList<PlistObject>(_objects.size());
        final Enumeration<Key> iter = _objects.keys();

        while (iter.hasMoreElements())
        {
            final Key key = iter.nextElement();
            ret.add(key);
            ret.add(_objects.get(key));
        }

        return ret;
    }

    /**
     * Adds the specified plist object.
     * This method allows the Castor framework to unmarshal the dictionary from XML.
     * The developer should rather use the {@link #put} method.
     * That's why it is tagged as deprecated.
     * @param object a plist object. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>object</code> is <code>null</code>.
     * @throws IllegalArgumentException if this object is unexpected at this position.
     * @deprecated for Castor's usage. Use {@link #put} instead.
     * @see #getKeysAndObjects
     * @see #put
     */
    @Deprecated
    public void addKeyOrObject(final PlistObject object)
    {
        object.setParent(this); // Throws NullPointerException if object is null.

        if (_tmpKey == null)
        {
            if (!(object instanceof Key))
            {
                throw new IllegalArgumentException("A key is expected here");
            }

            _tmpKey = (Key) object;
        }
        else
        {
            if (object instanceof Key)
            {
                throw new IllegalArgumentException("A key is unexpected here");
            }

            put(_tmpKey, object);
            _tmpKey = null;
        }
    }

    /**
     * Searches for the object associated with the specified key in <u>this</u> dictionary.
     * @param keyString the key string to search for. Shall not be <code>null</code>.
     * @return a plist object, or <code>null</code> if none was found.
     * @throws NullPointerException if <code>keyString</code> is <code>null</code>.
     */
    public PlistObject findObjectByKey(final java.lang.String keyString)
    {
        final Key key = new Key(keyString);

        return _objects.get(key);
    }
}
