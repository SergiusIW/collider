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

final class TileKey {
	private final int x;
	private final int y;
	private final int group;

	public TileKey(int x, int y, int group) {
		this.x = x;
		this.y = y;
		this.group = group;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TileKey tileKey = (TileKey) o;

		if (x != tileKey.x) return false;
		if (y != tileKey.y) return false;
		return group == tileKey.group;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 1299227 * result + y;
		result = 1299227 * result + group;
		return result;
	}
}
