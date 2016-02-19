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

package com.matthewmichelotti.collider.internal;

import com.matthewmichelotti.collider.geom.PlacedShape;
import com.matthewmichelotti.collider.geom.Shape;
import com.matthewmichelotti.collider.geom.Vec2d;
import com.matthewmichelotti.collider.internal.CardDir;

public final class ShapeUtil {
	private ShapeUtil() {}

	public static PlacedShape getBoundingBox(PlacedShape a, PlacedShape b) {
		double right = Math.max(a.getRight(), b.getRight());
		double top = Math.max(a.getTop(), b.getTop());
		double left = Math.min(a.getLeft(), b.getLeft());
		double bottom = Math.min(a.getBottom(), b.getBottom());
		Shape resultShape = Shape.newRect(right - left, top - bottom);
		return new PlacedShape(new Vec2d(left + .5*resultShape.getWidth(), bottom + .5*resultShape.getHeight()), resultShape);
	}

	public static PlacedShape getBoundingBox(PlacedShape shape) {
		if(shape.getShape().isRect()) return shape;
		else return new PlacedShape(shape.getPos(), Shape.newRect(shape.getShape().getWidth(), shape.getShape().getHeight()));
	}

	public static boolean isZero(Vec2d pos, Shape shape) {
		return pos.getX() == 0.0 && pos.getY() == 0.0
				&& shape.getWidth() == 0.0 && shape.getHeight() == 0.0;
	}

	public static double getEdge(PlacedShape shape, CardDir dir) {
		switch(dir) {
			case EAST: return shape.getRight();
			case NORTH: return shape.getTop();
			case WEST: return -shape.getLeft();
			case SOUTH: return -shape.getBottom();
			default: throw new RuntimeException();
		}
	}

	public static double getPos(Vec2d pos, CardDir dir) {
		switch(dir) {
			case EAST: return pos.getX();
			case NORTH: return pos.getY();
//			case WEST: return -pos.getX();
//			case SOUTH: return -pos.getY();
			default: throw new RuntimeException();
		}
	}

	public static double getRectOverlap(PlacedShape a, PlacedShape b, CardDir dir) {
		return getEdge(a, dir) + getEdge(b, dir.reverse());
	}

	public static RectSector getRectSector(PlacedShape shape, Vec2d point) {
		int x = getIntervalSector(shape.getLeft(), shape.getRight(), point.getX());
		int y = getIntervalSector(shape.getBottom(), shape.getTop(), point.getY());
		return new RectSector(x, y);
	}

	public static Vec2d getCorner(PlacedShape shape, RectSector sector) {
		if(!sector.isCorner()) throw new IllegalArgumentException();
		double x = (sector.x == 1) ? shape.getRight() : shape.getLeft();
		double y = (sector.y == 1) ? shape.getTop() : shape.getBottom();
		return new Vec2d(x, y);
	}

	private static int getIntervalSector(double left, double right, double value) {
		if(value < left) return -1;
		else if(value > right) return 1;
		else return 0;
	}

	public static class RectSector {
		private final int x, y;

		private RectSector(int x, int y) {
			if(x < -1 || x > 1) throw new IllegalArgumentException();
			if(y < -1 || y > 1) throw new IllegalArgumentException();
			this.x = x;
			this.y = y;
		}

		public boolean isCorner() {
			return x != 0 && y != 0;
		}
	}
}
