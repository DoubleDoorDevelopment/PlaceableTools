/*
 * Copyright (c) 2014, DoubleDoorDevelopment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.doubledoordev.placeableTools.util.renderShapes;

/**
 * Point used for rendering stuff.
 * Used U, V and W to avoid confusion with the block coords.
 *
 * @author Dries007
 */
public class Point3D
{
    double U, V, W;

    public Point3D(double U, double V, double W)
    {
        this.U = U;
        this.V = V;
        this.W = W;
    }

    public double getU()
    {
        return U;
    }

    public void setU(double u)
    {
        this.U = u;
    }

    public double getV()
    {
        return V;
    }

    public void setV(double v)
    {
        this.V = v;
    }

    public double getW()
    {
        return W;
    }

    public void setW(double w)
    {
        W = w;
    }

    public Point3D copy()
    {
        return new Point3D(U, V, W);
    }

    public Point3D move(double U, double V, double W)
    {
        this.U += U;
        this.V += V;
        this.W += W;

        return this;
    }

    public Point3D moveNew(double U, double V, double W)
    {
        return copy().move(U, V, W);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point3D point3D = (Point3D) o;

        return Double.compare(point3D.U, U) == 0 && Double.compare(point3D.V, V) == 0 && Double.compare(point3D.W, W) == 0;

    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = Double.doubleToLongBits(U);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(V);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(W);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "Point3D[" + U + ';' + V + ';' + W + ']';
    }
}
