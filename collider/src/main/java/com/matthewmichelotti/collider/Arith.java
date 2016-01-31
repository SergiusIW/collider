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

final class Arith {
	static double quadRootAscending(double a, double b, double c) {
		double det = b*b - 4*a*c;
		if(det < 0.0) return Double.NaN;
		if(b >= 0) return (2*c)/(-b - Math.sqrt(det));
		else return (-b + Math.sqrt(det))/(2*a);
	}
	
	static int floor(double value) {
		int result = (int)value;
		if(value < 0.0 && value != result) return result - 1;
		else return result;
	}
	
	static int ceil(double value) {
		int result = (int)value;
		if(value > 0.0 && value != result) return result + 1;
		else return result;
	}
	
	static double min(double a, double b) {
		return (a < b) ? a : b;
	}
	
	static double max(double a, double b) {
		return (a > b) ? a : b;
	}
	
	static int min(int a, int b) {
		return (a < b) ? a : b;
	}
	
	static int max(int a, int b) {
		return (a > b) ? a : b;
	}
	
	static double abs(double value) {
		if(value < 0.0) return -value;
		else return value;
	}
}
