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

package net.doubledoordev.util.renderShapes.complexShapes;

import net.doubledoordev.util.renderShapes.*;

public class Wireframe extends ComplexShape
{
    final IShape[] shapes = new IShape[12];

    public Wireframe(double size)
    {
        init(new Point3D(0, 0, 0), size);
    }

    public Wireframe()
    {
        init(new Point3D(0, 0, 0), 0.55D);
    }

    public Wireframe(Point3D center, double size)
    {
        init(center, size);
    }

    public Wireframe(Point3D center)
    {
        init(center, 0.55D);
    }

    private void init(Point3D center, double size)
    {
        Point3D b1 = center.moveNew(-size, -size, -size);
        Point3D b2 = center.moveNew(-size, -size, size);
        Point3D b3 = center.moveNew(size, -size, size);
        Point3D b4 = center.moveNew(size, -size, -size);

        Point3D t1 = center.moveNew(-size, size, -size);
        Point3D t2 = center.moveNew(-size, size, size);
        Point3D t3 = center.moveNew(size, size, size);
        Point3D t4 = center.moveNew(size, size, -size);

        shapes[0] = new Line(b1, b2);
        shapes[1] = new Line(b2, b3);
        shapes[2] = new Line(b3, b4);
        shapes[3] = new Line(b4, b1);

        shapes[4] = new Line(t1, t2);
        shapes[5] = new Line(t2, t3);
        shapes[6] = new Line(t3, t4);
        shapes[7] = new Line(t4, t1);

        shapes[8] = new Line(b1, t1);
        shapes[9] = new Line(b2, t2);
        shapes[10] = new Line(b3, t3);
        shapes[11] = new Line(b4, t4);
    }

    @Override
    public IShape[] getSubShapes()
    {
        return shapes;
    }
}
