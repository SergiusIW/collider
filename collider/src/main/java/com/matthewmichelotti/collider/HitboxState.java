/*
 * Copyright 2013-2016 Matthew D. Michelotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.matthewmichelotti.collider;

import com.matthewmichelotti.collider.geom.PlacedShape;
import com.matthewmichelotti.collider.geom.Shape;
import com.matthewmichelotti.collider.geom.Vec2d;

//TODO javadoc
public final class HitboxState {
	private Vec2d pos;
	private Vec2d vel;
	private Shape shape;
	private Shape shapeVel;
	private double endTime;

	public HitboxState(PlacedShape shape) {
		this(shape.getPos(), shape.getShape());
	}

	public HitboxState(Vec2d pos, Shape shape) {
		if(pos == null || shape == null) throw new NullPointerException();
		this.pos = pos;
		this.vel = Vec2d.ZERO;
		this.shape = shape;
		this.shapeVel = shape.isCircle() ? Shape.ZERO_CIRCLE : Shape.ZERO_RECT;
		this.endTime = Double.POSITIVE_INFINITY;
	}

	public Vec2d getPos() {
		return pos;
	}

	public Vec2d getVel() {
		return vel;
	}

	public Shape getShape() {
		return shape;
	}

	public Shape getShapeVel() {
		return shapeVel;
	}

	public double getEndTime() {
		return endTime;
	}

	public PlacedShape getPlacedShape() {
		return new PlacedShape(pos, shape);
	}

	public void setPos(Vec2d pos) {
		if(pos == null) throw new NullPointerException();
		this.pos = pos;
	}

	public void setVel(Vec2d vel) {
		if(vel == null) throw new NullPointerException();
		this.vel = vel;
	}

	public void setShape(Shape shape) {
		if(shape.isCircle() != this.shape.isCircle()) throw new IllegalArgumentException("incorrect shape type");
		this.shape = shape;
	}

	public void setShapeVel(Shape shapeVel) {
		if(shapeVel.isCircle() != this.shapeVel.isCircle()) throw new IllegalArgumentException("incorrect shape type");
		this.shapeVel = shapeVel;
	}

	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
}
