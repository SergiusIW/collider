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

/**
 * A normal vector between two HitBoxes.
 * 
 * @author Matthew Michelotti
 * @see HitBox#getNormal(HitBox)
 */
public final class Normal {
	double x, y, overlap;
	
	Normal() {}
	
	/**
	 * Returns the x-coordinate of the unit normal vector.
	 * @return The x-coordinate of the unit normal vector.
	 */
	public double getUnitX() {return x;}
	
	/**
	 * Returns the y-coordinate of the unit normal vector.
	 * @return The y-coordinate of the unit normal vector.
	 */
	public double getUnitY() {return y;}
	
	/**
	 * Returns the magnitude of the normal vector.
	 * This is the amount that the two HitBoxes overlap.
	 * A negative value represents the distance between
	 * two non-overlapping HitBoxes.
	 * @return The magnitude of the normal vector.
	 */
	public double getOverlap() {return overlap;}
}
