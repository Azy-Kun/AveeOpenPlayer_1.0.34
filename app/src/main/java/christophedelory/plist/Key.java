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
 * A dictionary key.
 * @version $Revision: 90 $
 * @author Christophe Delory
 * @castor.class xml="key"
 */
public class Key extends PlistText
{
    /**
     * Builds a new and empty key.
     */
    public Key()
    {
        super();
    }

    /**
     * Builds a new key with the specified value.
     * @param value the key value. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     */
    public Key(final java.lang.String value)
    {
        super();

        setValue(value); // Throws NullPointerException if value is null.
    }

    @Override
    public int hashCode()
    {
        int ret = 0;
        final java.lang.String value = getValue();

        if (value != null)
        {
            ret = value.hashCode();
        }

        return ret;
    }

    @Override
    public boolean equals(final Object obj)
    {
        boolean ret = false;

        if ((obj != null) && (obj instanceof Key))
        {
            final Key key = (Key) obj;
            final java.lang.String value = getValue();

            if (value == null)
            {
                ret = (key.getValue() == null);
            }
            else
            {
                ret = value.equals(key.getValue());
            }
        }

        return ret;
    }
}
