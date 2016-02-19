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

import com.matthewmichelotti.collider.geom.Vec2d;

public enum CardDir {
	EAST(new Vec2d(1.0, 0.0)), NORTH(new Vec2d(0.0, 1.0)), SOUTH(new Vec2d(0.0, -1.0)), WEST(new Vec2d(-1.0, 0.0));

	private final Vec2d vec;

	CardDir(Vec2d vec) {
		this.vec = vec;
	}

	public CardDir reverse() {
		switch(this) {
			case EAST: return WEST;
			case WEST: return EAST;
			case NORTH: return SOUTH;
			case SOUTH: return NORTH;
			default: throw new RuntimeException();
		}
	}

	public Vec2d asVec() {
		return vec;
	}
}
