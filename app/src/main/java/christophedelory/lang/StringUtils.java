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
package christophedelory.lang;

/**
 * General strings manipulation.
 * @version $Revision: 92 $
 * @author Christophe Delory
 */
public final class StringUtils
{
    /**
     * Returns a string representation of the integer argument as an integer in base 10.
     * The result is padded if needed with '0' characters up to the specified number of digits.
     * @param i an integer to be converted to a string.
     * @param nbDigits the minimum number of digits to display in the resulting string.
     * @return the string representation of the integer value represented by the argument in decimal (base 10).
     * @see Integer#toString(int)
     */
    public static String toString(final int i, final int nbDigits)
    {
        final StringBuilder sb = new StringBuilder(Integer.toString(i));

        while (sb.length() < nbDigits)
        {
            sb.insert(0, '0'); // Should not throw IndexOutOfBoundsException.
        }

        return sb.toString();
    }

    /**
     * Returns a string representation of the long argument as an integer in base 10.
     * The result is padded if needed with '0' characters up to the specified number of digits.
     * @param i a <code>long</code> to be converted to a string.
     * @param nbDigits the minimum number of digits to display in the resulting string.
     * @return the string representation of the long value represented by the argument in decimal (base 10).
     * @see Long#toString(long)
     */
    public static String toString(final long i, final int nbDigits)
    {
        final StringBuilder sb = new StringBuilder(Long.toString(i));

        while (sb.length() < nbDigits)
        {
            sb.insert(0, '0'); // Should not throw IndexOutOfBoundsException.
        }

        return sb.toString();
    }

    /**
     * Normalizes the input string.
     * That is:
     * <ul>
     * <li>If the string is <code>null</code>, <code>null</code> is returned.</li>
     * <li>The input string is then {@link String#trim trimmed}; if the result is empty, <code>null</code> is returned.</li>
     * <li>Otherwise the trimmed string is returned.</li>
     * </ul>
     * @param str a string. May be <code>null</code>.
     * @see String#trim
     */
    public static String normalize(final String str)
    {
        String ret = null;

        if (str != null)
        {
            final String s = str.trim();

            if (!s.isEmpty())
            {
                ret = s;
            }
        }

        return ret;
    }

    /**
     * The default no-arg constructor shall not be accessible.
     */
    private StringUtils()
    {
    }
}
