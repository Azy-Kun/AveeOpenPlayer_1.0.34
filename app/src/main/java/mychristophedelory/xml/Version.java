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
package mychristophedelory.xml;

import java.io.Serializable;

/**
 * A generic version information, composed of a version number, a revision number and a step number.
 * @version $Revision: 92 $
 * @author Christophe Delory
 * @castor.class xml="version" verify-constructable="false"
 */
public class Version implements Cloneable, Serializable
{
    /**
     * The serialization runtime associates with each serializable class a version number, called a serialVersionUID,
     * which is used during deserialization to verify that the sender and receiver of a serialized object have loaded classes for that object
     * that are compatible with respect to serialization.
     */
    private static final long serialVersionUID = 0L;

    /**
     * The current version of this project.
     * Defaults to "0.0.0".
     */
    public static Version CURRENT = new Version();

    /**
     * Builds a version information from the specified string.
     * @param name the version string.
     * @see #toString
     * @throws NullPointerException if the version string is <code>null</code>.
     * @throws IllegalArgumentException if the version string is malformed.
     * @throws IndexOutOfBoundsException if one of the numbers is strictly negative.
     * @throws NumberFormatException if the version string contains a non-parsable integer.
     */
    public static Version valueOf(final String name)
    {
        final int k = name.indexOf('.'); // May throw NullPointerException.

        if (k < 0)
        {
            throw new IllegalArgumentException("The format of a version string is <version.revision.step>");
        }

        if (k == 0)
        {
            throw new IllegalArgumentException("No version part in version string '" + name + '\'');
        }

        if ((k + 1) >= name.length())
        {
            throw new IllegalArgumentException("No revision/step part in version string '" + name + '\'');
        }

        final int l = name.indexOf('.', k + 1);

        if (l < 0)
        {
            throw new IllegalArgumentException("The format of a version string is <version.revision.step>");
        }

        if (l == (k + 1))
        {
            throw new IllegalArgumentException("No revision part in version string '" + name + '\'');
        }

        if ((l + 1) >= name.length())
        {
            throw new IllegalArgumentException("No step part in version string '" + name + '\'');
        }

        final String versionString = name.substring(0, k); // Should not throw IndexOutOfBoundsException.
        final String revisionString = name.substring(k + 1, l); // Should not throw IndexOutOfBoundsException.
        final String stepString = name.substring(l + 1); // Should not throw IndexOutOfBoundsException.

        final int version = Integer.parseInt(versionString); // May throw NumberFormatException.
        final int revision = Integer.parseInt(revisionString); // May throw NumberFormatException.
        final int step = Integer.parseInt(stepString); // May throw NumberFormatException.

        return new Version(version, revision, step); // May throw IndexOutOfBoundsException.
    }

    /**
     * The version number.
     */
    private int _version;

    /**
     * The revision number.
     */
    private int _revision;

    /**
     * The step number.
     */
    private int _step;

    /**
     * Builds a default version information.
     * All fields are set to '0'.
     */
    private Version()
    {
        _version = 0;
        _revision = 0;
        _step = 0;
    }

    /**
     * Builds a new <code>Version</code> instance which should keep a specific version, revision and step numbers.
     * @param version the version number.
     * @param revision the revision number.
     * @param step the step number.
     * @throws IndexOutOfBoundsException if one of the numbers is strictly negative.
     */
    public Version(final int version, final int revision, final int step)
    {
        setVersion(version); // May throw IndexOutOfBoundsException.
        setRevision(revision); // May throw IndexOutOfBoundsException.
        setStep(step); // May throw IndexOutOfBoundsException.
    }

    /**
     * Initializes the version number.
     * @param version the version number.
     * @throws IndexOutOfBoundsException if the number is strictly negative.
     * @see #getVersion
     */
    private void setVersion(final int version)
    {
        if (version < 0)
        {
            throw new IndexOutOfBoundsException("Version number is negative");
        }

        _version = version;
    }

    /**
     * Returns the version number kept by this instance.
     * @return a version number.
     */
    public int getVersion()
    {
        return _version;
    }

    /**
     * Initializes the revision number.
     * @param revision the revision number.
     * @throws IndexOutOfBoundsException if the number is strictly negative.
     * @see #getRevision
     */
    private void setRevision(final int revision)
    {
        if (revision < 0)
        {
            throw new IndexOutOfBoundsException("Revision number is negative");
        }

        _revision = revision;
    }

    /**
     * Returns the revision number kept by this instance.
     * @return a revision number.
     */
    public int getRevision()
    {
        return _revision;
    }

    /**
     * Initializes the step number.
     * @param step the step number.
     * @throws IndexOutOfBoundsException if the number is strictly negative.
     * @see #getStep
     */
    private void setStep(final int step)
    {
        if (step < 0)
        {
            throw new IndexOutOfBoundsException("Step number is negative");
        }

        _step = step;
    }

    /**
     * Returns the step number kept by this instance.
     * @return a step number.
     */
    public int getStep()
    {
        return _step;
    }

    public int compareTo(final Object o)
    {
        return hashCode() - ((Version) o).hashCode(); // May throw NullPointerException, ClassCastException.
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Two version information are the same if their version and revision and step numbers are the same.
     * @param obj the reference object with which to compare. A <code>Version</code> instance is expected.
     * @return <code>true</code> if this object is the same as the <code>obj</code> argument; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(final Object obj)
    {
        boolean ret = false;

        if ((obj != null) && (obj instanceof Version))
        {
            ret = (hashCode() == obj.hashCode());
        }

        return ret;
    }

    @Override
    public int hashCode()
    {
        return ((_version & 0x000003ff) << 20) | ((_revision & 0x000003ff) << 10) | (_step & 0x000003ff);
    }

    /**
     * Creates and returns a "shallow" copy of this object.
     * @return a clone of this instance.
     * @throws CloneNotSupportedException shall not be thrown, because this class is cloneable.
     * @see Object#clone
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone(); // Should not throw CloneNotSupportedException.
    }

    /**
     * Returns a string representation of this version information.
     * It takes the following form: "<code>&lt;version&gt;.&lt;revision&gt;.&lt;step&gt;</code>".
     * @return a string representing this version information.
     */
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(_version);
        sb.append('.');
        sb.append(_revision);
        sb.append('.');
        sb.append(_step);

        return sb.toString();
    }
}
