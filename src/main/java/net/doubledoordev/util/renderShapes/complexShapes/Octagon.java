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

import net.doubledoordev.util.renderShapes.IShape;
import net.doubledoordev.util.renderShapes.Point3D;
import net.doubledoordev.util.renderShapes.Quad;

/**
 * Flat regular octagon in the horizontal plane.
 *
 * @author Dries007
 */
public class Octagon extends ComplexShape
{
    final IShape[] shapes = new IShape[3];

    public Octagon(Point3D center, final double rad)
    {
        shapes[0] = new Quad(center.moveNew(-rad, 0, -rad / 2), center.moveNew(-rad, 0, rad / 2), center.moveNew(-rad / 2, 0, rad), center.moveNew(-rad / 2, 0, -rad));
        shapes[1] = new Quad(center.moveNew(-rad / 2, 0, -rad), center.moveNew(-rad / 2, 0, rad), center.moveNew(rad / 2, 0, rad), center.moveNew(rad / 2, 0, -rad));
        shapes[2] = new Quad(center.moveNew(rad / 2, 0, -rad), center.moveNew(rad / 2, 0, rad), center.moveNew(rad, 0, rad / 2), center.moveNew(rad, 0, -rad / 2));
    }

    @Override
    public IShape[] getSubShapes()
    {
        return shapes;
    }
}
