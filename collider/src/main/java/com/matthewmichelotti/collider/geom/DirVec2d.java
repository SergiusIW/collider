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
public final class DirVec2d {
	private Vec2d direction;
	private double length;

	public DirVec2d(Vec2d dir, double length) {
		this.direction = dir.normalize();
		this.length = length;
	}

	public Vec2d getDirection() {
		return direction;
	}

	public double getLength() {
		return length;
	}

	public Vec2d toVector() {
		return new Vec2d(direction.getX()*length, direction.getY()*length);
	}

	public DirVec2d flip() {
		return new DirVec2d(direction.flip(), length);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DirVec2d dirVec2d = (DirVec2d) o;

		if (Double.compare(dirVec2d.length, length) != 0) return false;
		return direction.equals(dirVec2d.direction);
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = direction.hashCode();
		temp = Double.doubleToLongBits(length);
		result = 1299227 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
