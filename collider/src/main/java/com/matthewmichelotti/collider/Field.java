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

import java.util.Iterator;
import java.util.NoSuchElementException;

final class Field {
	private SetPool<HitBox> setPool = new SetPool<HitBox>();
	private LongMap<Object> data;
	private double cellWidth;
	
	private int numEntries = 0;
	
	private IntBox.Iterator boxIter = new IntBox.Iterator();
	private IntBox.DiffIterator diffIter = new IntBox.DiffIterator();
	
	private HitBoxIter iter = new HitBoxIter();
	
	Field(ColliderOpts opts) {
		if(opts.cellWidth <= 0.0) throw new IllegalArgumentException();
		cellWidth = opts.cellWidth;
		data = new LongMap<Object>();
	}
	
	int getNumEntries() {return numEntries;}
	
	void remove(HitBox hitBox, int group, IntBox oldBox, IntBox newBox) {
		if(group < 0) return;
		Int2DIterator iter = iterator(oldBox, newBox);
		for(; !iter.isDone(); iter.next()) {
			removeFromCell(hitBox, iter.getX(), iter.getY(), group);
		}
	}
	
	void add(HitBox hitBox, int group, IntBox oldBox, IntBox newBox) {
		if(group < 0) return;
		Int2DIterator iter = iterator(newBox, oldBox);
		for(; !iter.isDone(); iter.next()) {
			addToCell(hitBox, iter.getX(), iter.getY(), group);
		}
	}
	
	//NOTE: should iterate to completion
	Iterable<HitBox> iterator(IntBox region, int[] groups, int testId) {
		iter.init(region, groups, testId);
		return iter;
	}
	
	void getIndexBounds(HitBox hitBox, IntBox bounds) {
		bounds.l = Arith.floor(-hitBox.getBoundEdgeComp(Dir.L)/cellWidth);
		bounds.b = Arith.floor(-hitBox.getBoundEdgeComp(Dir.D)/cellWidth);
		bounds.r = Arith.max(bounds.l, Arith.ceil(hitBox.getBoundEdgeComp(Dir.R)/cellWidth) - 1);
		bounds.t = Arith.max(bounds.b, Arith.ceil(hitBox.getBoundEdgeComp(Dir.U)/cellWidth) - 1);
	}
	
	double getGridPeriod(HitBox hitBox) {
		double speed = hitBox.getMaxBoundEdgeVel();
		if(speed <= 0.0) return Double.POSITIVE_INFINITY;
		else return cellWidth/speed;
	}
	
	private void addToCell(HitBox hitBox, int x, int y, int group) {
		long key = getKey(x, y, group);
		Object oldSetObj = data.get(key);
		Object newSetObj = setPool.add(oldSetObj, hitBox);
		if(!setPool.wasSuccessful()) throw new RuntimeException();
		if(newSetObj != oldSetObj) data.put(key, newSetObj);
		numEntries++;
	}

	private void removeFromCell(HitBox hitBox, int x, int y, int group) {
		long key = getKey(x, y, group);
		Object oldSetObj = data.get(key);
		Object newSetObj = setPool.remove(oldSetObj, hitBox);
		if(!setPool.wasSuccessful()) throw new RuntimeException();
		if(newSetObj == null) data.remove(key);
		else if(newSetObj != oldSetObj) data.put(key, newSetObj);
		numEntries--;
	}

	private final static long PRIME = 160481219; //NOTE: PRIME*PRIME*SMALL_PRIME < 2^63
	private final static long SMALL_PRIME = 263; //NOTE: SMALL_PRIME > HitBox.NUM_GROUPS
	private final static int MAX_INDEX = (int)(PRIME/2 - 1);
	
	private static long getKey(int x, int y, int group) {
		//this key is used because, at the time of writing, the LongMap in
		//LibGDX just uses the lower bits of the key as the first hash function
		if(x > MAX_INDEX || x < -MAX_INDEX) throw new RuntimeException();
		if(y > MAX_INDEX || y < -MAX_INDEX) throw new RuntimeException();
		if(group >= SMALL_PRIME || group < 0) throw new RuntimeException();
		return group + (SMALL_PRIME*x + PRIME*y);
//		return ((x & 0xFFFF0000L) << 32) | ((y & 0xFFFFFFFFL) << 16) | (x & 0xFFFFL);
	}
	
	private Int2DIterator iterator(IntBox box, IntBox subBox) {
		if(subBox == null) {
			boxIter.init(box);
			return boxIter;
		}
		else {
			diffIter.init(box, subBox);
			return diffIter;
		}
	}
	
	private class HitBoxIter implements Iterator<HitBox>, Iterable<HitBox> {
		private final IntBox.Iterator boxIter = new IntBox.Iterator();
		private final SetPool.SetIterator<HitBox> cellIter = new SetPool.SetIterator<HitBox>();
		private int[] groups;
		private int groupIndex;
		private HitBox next;
		private int testId = 0;
		
		private void init(IntBox region, int[] groups, int testId) {
			clear();
			if(groups.length == 0) return;
			boxIter.init(region);
			if(boxIter.isDone()) return;
			
			this.testId = testId;
			this.groups = groups;
			initCellIter();
			searchNext();
		}
		
		private void searchNext() {
			while(true) {
				while(cellIter.hasNext()) {
					next = cellIter.next();
					if(next.testMark(testId)) return;
				}
				groupIndex++;
				if(groupIndex >= groups.length) {
					groupIndex = 0;
					boxIter.next();
					if(boxIter.isDone()) {
						clear();
						return;
					}
				}
				initCellIter();
			}
		}
		
		private void initCellIter() {
			long key = getKey(boxIter.getX(), boxIter.getY(), groups[groupIndex]);
			cellIter.init(data.get(key));
		}
		
		private void clear() {
			cellIter.clear();
			groups = null;
			groupIndex = 0;
			next = null;
		}
		
		@Override
		public HitBox next() {
			if(next == null) throw new NoSuchElementException();
			HitBox result = next;
			searchNext();
			return result;
		}
		
		@Override public Iterator<HitBox> iterator() {return this;}
		@Override public boolean hasNext() {return next != null;}
		@Override public void remove() {throw new UnsupportedOperationException();}
	}
}
