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

abstract class FunctionEvent implements Comparable<FunctionEvent> {
	private final double time;
	private final int id;
	private HitBox first;
	private HitBox second;
	
	FunctionEvent(double time, int id, HitBox first, HitBox second) {
		this.time = time;
		this.id = id;
	}

	double getTime() {
		return time;
	}
	
	abstract ColliderEvent resolve(Collider collider);
	
	@Override public final int compareTo(FunctionEvent o) {
		if(time != o.time) return Double.compare(time, o.time);
		return Integer.compare(id, o.id);
	}

	boolean isPairEvent() {
		return second != null;
	}

	HitBox getFirst() {
		return first;
	}

	HitBox getSecond() {
		return second;
	}

	HitBox getOther(HitBox hitbox) {
		if(hitbox == first) return second;
		if(hitbox == second) return first;
		throw new IllegalArgumentException();
	}
}
