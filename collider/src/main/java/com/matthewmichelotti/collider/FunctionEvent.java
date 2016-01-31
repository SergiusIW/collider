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
	double time;
	int id;
	
	FunctionEvent() {}
	
	abstract void resolve(Collider collider);
	
	@Override public final int compareTo(FunctionEvent o) {
		if(time > o.time) return 1;
		if(time == o.time) return id - o.id;
		return -1;
	}
}
