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


final class IntBox {
	int l, b, r, t;
	
	IntBox() {}
	
//	void restrict(IntBox bound) {
//		if(bound == null) return;
//		if(bound.l > l) l = bound.l;
//		if(bound.r < r) r = bound.r;
//		if(bound.b > b) b = bound.b;
//		if(bound.t < t) t = bound.t;
//	}
	
	static class Iterator implements Int2DIterator {
		private int x, y;
		private int l, r, t = -1;
		
		Iterator() {}
		
		Iterator(IntBox box) {init(box);}
		
		void init(IntBox box) {
			int b;
			if(box == null) {l = 0; r = 0; b = 0; t = -1;}
			else {l = box.l; r = box.r; b = box.b; t = box.t;}
			if(r < l) t = b - 1;
			x = l;
			y = b;
		}

		@Override public boolean isDone() {return y > t;}

		@Override
		public void next() {
			x++;
			if(x > r) {
				x = l;
				y++;
			}
		}

		@Override public int getX() {return x;}
		@Override public int getY() {return y;}
	}
	
	static class DiffIterator implements Int2DIterator {
		private int x, y;
		private int l, r, t = -1;
		private int sl, sb, sr, st;
		
		DiffIterator() {}
		
		DiffIterator(IntBox box, IntBox subBox) {init(box, subBox);}
		
		void init(IntBox box, IntBox subBox) {
			int b;
			if(box == null) {l = 0; r = 0; b = 0; t = -1;}
			else {l = box.l; r = box.r; b = box.b; t = box.t;}
			if(r < l) t = b - 1;
			if(subBox == null) {sl = 0; sr = 0; sb = 0; st = -1;}
			else {sl = subBox.l; sr = subBox.r; sb = subBox.b; st = subBox.t;}
			x = l - 1;
			y = b;
			next();
		}

		@Override public boolean isDone() {return y > t;}

		@Override
		public void next() {
			x++;
			if(x > r) {
				x = l;
				y++;
			}
			if(y < sb || y > st || x < sl || x > sr) return;
			if(sr >= r) {
				if(sl <= l) y = st + 1;
				else {
					x = l;
					y++;
				}
			}
			else {
				x = sr + 1;
			}
		}

		@Override public int getX() {return x;}
		@Override public int getY() {return y;}
	}
}
