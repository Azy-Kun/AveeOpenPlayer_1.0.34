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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * The date primitive type.
 * Contents should conform to a subset of ISO 8601
 * (in particular, YYYY '-' MM '-' DD 'T' HH ':' MM ':' SS 'Z'. Smaller units may be omitted with a loss of precision).
 * @version $Revision: 92 $
 * @author Christophe Delory
 * @castor.class xml="date"
 */
public class Date extends PlistObject
{
    /**
     * The internal date and time format.
     */
    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US); // Should not throw NullPointerException, IllegalArgumentException.

    /**
     * The date.
     */
    private java.util.Date _value = null;

    /**
     * Builds a new and empty date.
     */
    public Date()
    {
        super();
    }

    /**
     * Builds a new date with the specified value.
     * @param value the date value. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     */
    public Date(final java.util.Date value)
    {
        super();

        if (value == null)
        {
            throw new NullPointerException("no date");
        }

        _value = value;
    }

    /**
     * Returns the date.
     * @return a date. Shall not be <code>null</code>.
     * @throws NullPointerException if no date has been defined in this instance.
     * @see #setValueString
     * @see #getValue
     * @castor.field
     *  get-method="getValueString"
     *  set-method="setValueString"
     *  required="true"
     * @castor.field-xml
     *  node="text"
     */
    public java.lang.String getValueString()
    {
        synchronized(DATETIME_FORMAT)
        {
            return DATETIME_FORMAT.format(_value); // Throws NullPointerException if _value is null.
        }
    }

    /**
     * Initializes the date.
     * @param value a date. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @throws ParseException if the beginning of the specified string cannot be parsed.
     * @see #getValueString
     * @see #setValue
     */
    public void setValueString(final java.lang.String value) throws ParseException
    {
        synchronized(DATETIME_FORMAT)
        {
            _value = DATETIME_FORMAT.parse(value); // May throw ParseException. Throws NullPointerException if value is null.
        }
    }

    /**
     * Returns the date.
     * @return a date. May be <code>null</code> if not yet initialized.
     * @see #setValue
     * @see #getValueString
     */
    public java.util.Date getValue()
    {
        return _value;
    }

    /**
     * Initializes the date.
     * @param value a date. Shall not be <code>null</code>.
     * @throws NullPointerException if <code>value</code> is <code>null</code>.
     * @see #getValue
     * @see #setValueString
     */
    public void setValue(final java.util.Date value)
    {
        if (value == null)
        {
            throw new NullPointerException("no date");
        }

        _value = value;
    }
}
