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

import java.util.HashSet;
import java.util.Iterator;

class TightSet<T> implements Iterable<T> {
	private final static int MIN_SIZE = 4;

	private HashSet<T> set = new HashSet<T>(MIN_SIZE);
	private int highestSize = 0;

	TightSet() {
	}

	boolean add(T value) {
		boolean result = set.add(value);
		highestSize = Math.max(highestSize, set.size());
		return result;
	}

	boolean remove(T value) {
		boolean result = set.remove(value);
		if(highestSize > MIN_SIZE && set.size()*2 < highestSize) {
			HashSet<T> newSet = new HashSet<>(Math.max(set.size(), MIN_SIZE));
			newSet.addAll(set);
			set = newSet;
			highestSize = set.size();
		}
		return result;
	}

	@Override
	public Iterator<T> iterator() {
		return set.iterator();
	}

	boolean isEmpty() {
		return set.isEmpty();
	}

	void clear() {
		if(highestSize > MIN_SIZE) set = new HashSet<>(MIN_SIZE);
		else set.clear();
	}
}
