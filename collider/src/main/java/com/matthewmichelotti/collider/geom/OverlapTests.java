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

import com.matthewmichelotti.collider.internal.CardDir;
import com.matthewmichelotti.collider.internal.ShapeUtil;

final class OverlapTests {
	private OverlapTests() {}

	//FIXME implement normalFrom for circle-circle and circle-rect
	static DirVec2d normalFrom(PlacedShape a, PlacedShape b) {
		if(a.getShape().isRect()) {
			if(b.getShape().isRect()) return rectRectNormal(a, b);
			else return rectCircleNormal(a, b);
		} else {
			if(b.getShape().isRect()) return rectCircleNormal(b, a).flip();
			else return circleCircleNormal(a, b);
		}
	}

	static boolean overlaps(PlacedShape a, PlacedShape b) {
		return normalFrom(a, b).getLength() >= 0.0;
	}

	private static DirVec2d rectRectNormal(PlacedShape dst, PlacedShape src) {
		CardDir minDir = null;
		double overlap = Double.POSITIVE_INFINITY;
		for(CardDir dir : CardDir.values()) {
			double testOverlap = ShapeUtil.getEdge(src, dir)
					+ ShapeUtil.getEdge(dst, dir.reverse());
			if(testOverlap < overlap) {
				overlap = testOverlap;
				minDir = dir;
			}
		}
		return new DirVec2d(minDir.asVec(), overlap);
	}

	private static DirVec2d circleCircleNormal(PlacedShape dst, PlacedShape src) {
		Vec2d dir = dst.getPos().sub(src.getPos());
		double dist = dir.getLength();
		if(dist == 0.0) dir = new Vec2d(1.0, 0.0);
		return new DirVec2d(dir, .5*(dst.getShape().getWidth() + src.getShape().getWidth()) - dist);
	}

	private static DirVec2d rectCircleNormal(PlacedShape dst, PlacedShape src) {
		ShapeUtil.RectSector sector = ShapeUtil.getRectSector(dst, src.getPos());
		if(sector.isCorner()) {
			PlacedShape corner = new PlacedShape(ShapeUtil.getCorner(dst, sector), Shape.ZERO_CIRCLE);
			return circleCircleNormal(corner, src);
		} else {
			return rectRectNormal(dst, src);
		}
	}
}
