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

package com.matthewmichelotti.collider.processes;

import java.util.concurrent.atomic.AtomicLong;

public abstract class TimedFunction implements Comparable<TimedFunction> {
	private final static AtomicLong NEXT_ID = new AtomicLong();

	private final double time;
	private final long id = NEXT_ID.incrementAndGet();

	protected TimedFunction(double time) {
		this.time = time;
	}

	public final double getTime() {
		return time;
	}

	public abstract void invoke();

	@Override
	public final int compareTo(TimedFunction other) {
		int result = Double.compare(time, other.time);
		if(result == 0) result = Long.compare(id, other.id);
		return result;
	}
}
