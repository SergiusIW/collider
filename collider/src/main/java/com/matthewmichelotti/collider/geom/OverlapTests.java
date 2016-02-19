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

	static DirVec2d normalFrom(PlacedShape a, PlacedShape b) {
		if(a.getShape().isRect()) {
			if(b.getShape().isRect()) return rectRectNormal(a, b);
			else throw new RuntimeException("not implemented");
		} else {
			throw new RuntimeException("not implemented");
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
}
