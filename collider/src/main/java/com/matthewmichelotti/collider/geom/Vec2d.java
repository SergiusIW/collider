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

package com.matthewmichelotti.collider.geom;

//TODO javadoc
public final class Vec2d {
	public final static Vec2d ZERO = new Vec2d(0, 0);

	private final double x, y;

	public Vec2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getLength() {
		return Math.sqrt(x*x + y*y);
	}

	public Vec2d normalize() {
		double length = getLength();
		if(length == 0.0) throw new IllegalStateException("can't normalize vector of length 0");
		return new Vec2d(x/length, y/length);
	}

	public double distanceSq(Vec2d other) {
		double dx = x - other.x;
		double dy = y - other.y;
		return dx*dx + dy*dy;
	}

	public double distance(Vec2d other) {
		return Math.sqrt(this.distanceSq(other));
	}

	public Vec2d add(Vec2d delta) {
		return new Vec2d(x + delta.x, y + delta.y);
	}

	public Vec2d scale(double scalar) {
		return new Vec2d(x*scalar, y*scalar);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Vec2d vec2d = (Vec2d) o;

		if (Double.compare(vec2d.x, x) != 0) return false;
		return Double.compare(vec2d.y, y) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 1299227 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
