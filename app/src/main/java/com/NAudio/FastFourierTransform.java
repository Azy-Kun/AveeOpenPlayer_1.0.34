//NAudio is an open source .NET audio library written by Mark Heath (mark.heath@gmail.com)
//        For more information, visit http://naudio.codeplex.com
//        NAudio is now being hosted on GitHub http://github.com/naudio/NAudio

//Microsoft Public License (Ms-PL)
//
//        This license governs use of the accompanying software. If you use the software, you accept this license. If you do not accept the license, do not use the software.
//
//        1. Definitions
//
//        The terms "reproduce," "reproduction," "derivative works," and "distribution" have the same meaning here as under U.S. copyright law.
//
//        A "contribution" is the original software, or any additions or changes to the software.
//
//        A "contributor" is any person that distributes its contribution under this license.
//
//        "Licensed patents" are a contributor's patent claims that read directly on its contribution.
//
//        2. Grant of Rights
//
//        (A) Copyright Grant- Subject to the terms of this license, including the license conditions and limitations in section 3, each contributor grants you a non-exclusive, worldwide, royalty-free copyright license to reproduce its contribution, prepare derivative works of its contribution, and distribute its contribution or any derivative works that you create.
//
//        (B) Patent Grant- Subject to the terms of this license, including the license conditions and limitations in section 3, each contributor grants you a non-exclusive, worldwide, royalty-free license under its licensed patents to make, have made, use, sell, offer for sale, import, and/or otherwise dispose of its contribution in the software or derivative works of the contribution in the software.
//
//        3. Conditions and Limitations
//
//        (A) No Trademark License- This license does not grant you rights to use any contributors' name, logo, or trademarks.
//
//        (B) If you bring a patent claim against any contributor over patents that you claim are infringed by the software, your patent license from such contributor to the software ends automatically.
//
//        (C) If you distribute any portion of the software, you must retain all copyright, patent, trademark, and attribution notices that are present in the software.
//
//        (D) If you distribute any portion of the software in source code form, you may do so only under this license by including a complete copy of this license with your distribution. If you distribute any portion of the software in compiled or object code form, you may only do so under a license that complies with this license.
//
//        (E) The software is licensed "as-is." You bear the risk of using it. The contributors give no express warranties, guarantees or conditions. You may have additional consumer rights under your local laws which this license cannot change. To the extent permitted under your local laws, the contributors exclude the implied warranties of merchantability, fitness for a particular purpose and non-infringement.

package com.NAudio;

public class FastFourierTransform {


    public static class Complex {
        float X;//r
        float Y;//m

        public Complex(float x, float y) {
            X = x;
            Y = y;
        }

        public float re() {
            return X;
        }


        public float im() {
            return Y;
        }
    }

    static int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    /// <summary>
    /// This computes an in-place complex-to-complex FFT
    /// x and y are the real and imaginary arrays of 2^m points.
    /// </summary>
    public static void FFT(boolean forward, Complex[] data) {
        FFT(forward, log(data.length, 2), data);
    }

    public static void FFT(boolean forward, int m, Complex[] data) {
        int n, i, i1, j, k, i2, l, l1, l2;
        float c1, c2, tx, ty, t1, t2, u1, u2, z;

        // Calculate the number of points
        n = 1;
        for (i = 0; i < m; i++)
            n *= 2;

        // Do the bit reversal
        i2 = n >> 1;
        j = 0;
        for (i = 0; i < n - 1; i++) {
            if (i < j) {
                tx = data[i].X;
                ty = data[i].Y;
                data[i].X = data[j].X;
                data[i].Y = data[j].Y;
                data[j].X = tx;
                data[j].Y = ty;
            }
            k = i2;

            while (k <= j) {
                j -= k;
                k >>= 1;
            }
            j += k;
        }

        // Compute the FFT
        c1 = -1.0f;
        c2 = 0.0f;
        l2 = 1;
        for (l = 0; l < m; l++) {
            l1 = l2;
            l2 <<= 1;
            u1 = 1.0f;
            u2 = 0.0f;
            for (j = 0; j < l1; j++) {
                for (i = j; i < n; i += l2) {
                    i1 = i + l1;
                    t1 = u1 * data[i1].X - u2 * data[i1].Y;
                    t2 = u1 * data[i1].Y + u2 * data[i1].X;
                    data[i1].X = data[i].X - t1;
                    data[i1].Y = data[i].Y - t2;
                    data[i].X += t1;
                    data[i].Y += t2;
                }
                z = u1 * c1 - u2 * c2;
                u2 = u1 * c2 + u2 * c1;
                u1 = z;
            }
            c2 = (float) Math.sqrt((1.0f - c1) / 2.0f);
            if (forward)
                c2 = -c2;
            c1 = (float) Math.sqrt((1.0f + c1) / 2.0f);
        }

        // Scaling for forward transform
        if (forward) {
            for (i = 0; i < n; i++) {
                data[i].X /= n;
                data[i].Y /= n;
            }
        }
    }
}
