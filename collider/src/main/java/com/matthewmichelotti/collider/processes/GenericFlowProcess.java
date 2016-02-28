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

import java.util.TreeSet;

public final class GenericFlowProcess implements FlowProcess {
	private final TreeSet<TimedFunction> queue = new TreeSet<>();
	private double time = 0.0;

	public GenericFlowProcess() {}

	public boolean add(TimedFunction function) {
		return queue.add(function);
	}

	public boolean remove(TimedFunction function) {
		return queue.remove(function);
	}

	public double getTime() {
		return time;
	}

	@Override
	public double peekNextTime() {
		if(queue.isEmpty()) return Double.POSITIVE_INFINITY;
		else return Math.max(time, queue.first().getTime());
	}

	@Override
	public void advance(double newTime) {
		if(newTime < time) throw new IllegalStateException();
		if(newTime > peekNextTime()) throw new IllegalStateException();
		time = newTime;
	}

	@Override
	public void resolveNext() {
		if(queue.first().getTime() != time) throw new IllegalStateException();
		queue.pollFirst().invoke();
	}
}
