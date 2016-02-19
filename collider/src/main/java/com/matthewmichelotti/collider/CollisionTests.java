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

import com.matthewmichelotti.collider.geom.Shape;
import com.matthewmichelotti.collider.internal.CardDir;
import com.matthewmichelotti.collider.internal.ShapeUtil;

final class CollisionTests {
	private final static double HIGH_TIME = 1e50;

	private CollisionTests() {
	}

	static double collideTime(HitboxState a, HitboxState b) {
		double remainingTime = Math.min(a.getRemainingTime(), b.getRemainingTime());
		a = cloneHitboxWithTime(a, remainingTime);
		b = cloneHitboxWithTime(b, remainingTime);

		if(!boundingBoxTest(a, b)) return Double.POSITIVE_INFINITY;
		return timeUnpadded(a, b, true);
	}

	static double separateTime(HitboxState a, HitboxState b, double padding) {
		double remainingTime = Math.min(a.getRemainingTime(), b.getRemainingTime());
		a = cloneHitboxWithTime(a, remainingTime);
		b = cloneHitboxWithTime(b, remainingTime);

		if(a.getShape().isRect() && b.getShape().isRect()) {
			a.setShape(Shape.newRect(a.getShape().getWidth() + padding*2, b.getShape().getHeight() + padding*2));
		} else {
			HitboxState circle = a.getShape().isCircle() ? a : b;
			circle.setShape(Shape.newCircle(circle.getShape().getWidth() + padding*2));
		}

		return timeUnpadded(a, b, false);
	}

	private static double getRemainingTime(HitboxState a, HitboxState b) {
		return Math.min(HIGH_TIME, Math.min(a.getRemainingTime(), b.getRemainingTime()));
	}

	private static HitboxState cloneHitboxWithTime(HitboxState hitbox, double remainingTime) {
		hitbox = hitbox.clone();
		hitbox.setRemainingTime(remainingTime);
		return hitbox;
	}

	private static double timeUnpadded(HitboxState a, HitboxState b, boolean forCollide) {
		double result;
		if(a.getShape().isRect()) {
			if(b.getShape().isRect()) {
				result = rectRectTime(a, b, forCollide);
			} else {
				result = rectCircleTime(a, b, forCollide);
			}
		} else {
			if(b.getShape().isRect()) {
				result = rectCircleTime(b, a, forCollide);
			} else {
				result = circleCircleTime(a, b, forCollide);
			}
		}
		if(result >= a.getRemainingTime() || result >= b.getRemainingTime()) return Double.POSITIVE_INFINITY;
		else return result;
	}

	private static boolean boundingBoxTest(HitboxState a, HitboxState b) {
		return a.getBoundingBox().overlaps(b.getBoundingBox());
	}

	private static double rectRectTime(HitboxState a, HitboxState b, boolean forCollide) {
		double overlapStart = 0.0;
		double overlapEnd = Double.POSITIVE_INFINITY;
		for (CardDir dir : CardDir.values()) {
			double overlap = ShapeUtil.getRectOverlap(a.getPlacedShape(), b.getPlacedShape(), dir);
			double overlapVel = ShapeUtil.getRectOverlap(a.getPlacedShapeVel(), b.getPlacedShapeVel(), dir);
			if (overlap < 0.0) {
				if (!forCollide) return 0.0;
				if (overlapVel <= 0.0) return Double.POSITIVE_INFINITY;
				overlapStart = Math.max(overlapStart, -overlap / overlapVel);
			} else if (overlapVel < 0.0) {
				overlapEnd = Math.min(overlapEnd, -overlap / overlapVel);
			}
			if (overlapStart >= overlapEnd) return forCollide ? Double.POSITIVE_INFINITY : 0.0;
		}

		return forCollide ? overlapStart : overlapEnd;
	}

	private static double circleCircleTime(HitboxState a, HitboxState b, boolean forCollide) {
		double sign = forCollide ? 1.0 : -1.0;

		double netRad = .5 * (a.getShape().getWidth() + b.getShape().getWidth());
		double distX = a.getPos().getX() - b.getPos().getX();
		double distY = a.getPos().getY() - b.getPos().getY();

		double coeffC = sign * (netRad * netRad - distX * distX - distY * distY);
		if (coeffC > 0.0) return 0.0;

		double netRadVel = .5 * (a.getShapeVel().getWidth() + b.getShapeVel().getWidth());
		double distXVel = a.getVel().getX() - b.getVel().getX();
		double distYVel = a.getVel().getY() - b.getVel().getY();

		double coeffA = sign * (netRadVel * netRadVel - distXVel * distXVel - distYVel * distYVel);
		double coeffB = sign * 2.0 * (netRad * netRadVel - distX * distXVel - distY * distYVel);

		double result = MathUtil.quadRootAscending(coeffA, coeffB, coeffC);
		if (!Double.isNaN(result) && result >= 0.0) return result;
		else return Double.POSITIVE_INFINITY;
	}

	private static double rectCircleTime(HitboxState rect, HitboxState circle, boolean forCollide) {
		if(forCollide) return rectCircleCollideTime(rect, circle);
		else return rectCircleSeparateTime(rect, circle);
	}

	private static double rectCircleCollideTime(HitboxState rect, HitboxState circle) {
		double baseTime = rectRectTime(rect, circle, true);
		if(baseTime >= rect.getRemainingTime()) return Double.POSITIVE_INFINITY;
		return baseTime + rebasedRectCircleCollideTime(rect.advance(0.0, baseTime), circle.advance(0.0, baseTime));
	}

	private static double rebasedRectCircleCollideTime(HitboxState rect, HitboxState circle) {
		ShapeUtil.RectSector sector = ShapeUtil.getRectSector(rect.getPlacedShape(), circle.getPos());
		if (!sector.isCorner()) return 0.0;

		HitboxState corner = new HitboxState(ShapeUtil.getCorner(rect.getPlacedShape(), sector), Shape.ZERO_CIRCLE);
		corner.setVel(ShapeUtil.getCorner(rect.getPlacedShapeVel(), sector));
		return circleCircleTime(corner, circle, true);
	}

	private static double rectCircleSeparateTime(HitboxState rect, HitboxState circle)
	{
		double baseTime = rectRectTime(rect, circle, false);
		if(baseTime == 0.0) return 0.0;
		if(baseTime >= HIGH_TIME) return Double.POSITIVE_INFINITY;
		rect = rect.advance(0.0, baseTime, true);
		circle = circle.advance(0.0, baseTime, true);
		rect.reverseVels();
		circle.reverseVels();
		double result = baseTime - rebasedRectCircleCollideTime(rect, circle);
		return Math.max(result, 0.0);
	}
}
