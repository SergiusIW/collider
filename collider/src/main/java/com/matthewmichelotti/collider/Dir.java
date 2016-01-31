/*
 * Copyright 2013-2014 Matthew D. Michelotti
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

final class Dir {
	final static int R = 0, U = 1, L = 2, D = 3;
	
	static int opp(int dir) {
		return (dir + 2) & 3;
	}
	
	static int dot(int dirA, int dirB) {
		int relDir = (dirB - dirA) & 3;
		switch(relDir) {
		case 0: return 1;
		case 2: return -1;
		default: return 0;
		}
	}
	
	static int x(int dir) {
		switch(dir) {
		case R: return 1;
		case L: return -1;
		default: return 0;
		}
	}
	
	static int y(int dir) {
		switch(dir) {
		case U: return 1;
		case D: return -1;
		default: return 0;
		}
	}
}
