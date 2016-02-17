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

import com.matthewmichelotti.collider.geom.PlacedShape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class Field {
	private HashMap<TileKey, TightSet<HitBox>> data;
	private double cellWidth;
	private int numEntries = 0;
	
	Field(double cellWidth) {
		if(cellWidth <= 0.0) throw new IllegalArgumentException("cellWidth must be positive");
		this.cellWidth = cellWidth;
		data = new HashMap<>();
	}
	
	int getNumEntries() {return numEntries;}
	
	void remove(HitBox hitBox, int group, IntBox oldBox, IntBox newBox) {
		if(group < 0) return;
		for(Int2DIterator iter = oldBox.diffIterator(newBox); !iter.isDone(); iter.next()) {
			removeFromCell(hitBox, iter.getX(), iter.getY(), group);
		}
	}

	void add(HitBox hitBox, int group, IntBox oldBox, IntBox newBox) {
		if(group < 0) return;
		for(Int2DIterator iter = newBox.diffIterator(oldBox); !iter.isDone(); iter.next()) {
			addToCell(hitBox, iter.getX(), iter.getY(), group);
		}
	}

	//NOTE: should iterate to completion
	Iterable<HitBox> iterator(IntBox region, int[] groups, int testId) {
		return new HitBoxIter(region, groups, testId);
	}

	IntBox getIndexBounds(HitboxState hitbox) {
		PlacedShape box = hitbox.getBoundingBox();
		IntBox result = new IntBox();
		result.l = (int)Math.floor(box.getLeft()/cellWidth);
		result.b = (int)Math.floor(box.getBottom()/cellWidth);
		result.r = Math.max(result.l, (int)Math.ceil(box.getRight()/cellWidth) - 1);
		result.t = Math.max(result.b, (int)Math.ceil(box.getTop()/cellWidth) - 1);
		return result;
	}
	
	double getGridPeriod(HitboxState hitbox) {
		double speed = hitbox.getMaxEdgeVel();
		if(speed <= 0.0) return Double.POSITIVE_INFINITY;
		else return cellWidth/speed;
	}
	
	private void addToCell(HitBox hitBox, int x, int y, int group) {
		TileKey key = new TileKey(x, y, group);
		TightSet<HitBox> set = data.get(key);
		if(set == null) {
			set = new TightSet<>();
			data.put(key, set);
		}
		boolean success = set.add(hitBox);
		if(!success) throw new RuntimeException();
		numEntries++;
	}

	private void removeFromCell(HitBox hitBox, int x, int y, int group) {
		TileKey key = new TileKey(x, y, group);
		TightSet<HitBox> set = data.get(key);
		boolean success = set.remove(hitBox);
		if(!success) throw new RuntimeException();
		if(set.isEmpty()) data.remove(key);
		numEntries--;
	}
	
	private class HitBoxIter implements Iterator<HitBox>, Iterable<HitBox> {
		private Int2DIterator boxIter;
		private Iterator<HitBox> cellIter;
		private int[] groups;
		private int groupIndex;
		private HitBox next;
		private int testId = 0;
		
		private HitBoxIter(IntBox region, int[] groups, int testId) {
			if(groups.length == 0) return;
			boxIter = region.iterator();
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
			TileKey key = new TileKey(boxIter.getX(), boxIter.getY(), groups[groupIndex]);
			TightSet<HitBox> set = data.get(key);
			if(set == null) cellIter = Collections.<HitBox>emptyList().iterator();
			else cellIter = set.iterator();
		}
		
		private void clear() {
			cellIter = null;
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
