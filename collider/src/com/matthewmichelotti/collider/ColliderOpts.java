/*****************************************************************************
 * Copyright 2013 Matthew D. Michelotti.
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
 ****************************************************************************/

package com.matthewmichelotti.collider;

/**
 * Contains parameters for constructing a {@link Collider}.
 * @author Matthew Michelotti
 */

public final class ColliderOpts {
	/**Used to determine which pairs of HitBoxes should be tested for collisions.*/
	public InteractTester interactTester;
	
	/**
	 * An efficiency parameter representing the width and height of a
	 * cell in the Collider grid.
	 * The Collider references HitBoxes in a conceptually infinite grid
	 * in order to reduce the number of collisions that need to be tested.
	 * If your game uses a grid layout, it would be a good choice
	 * to use the same cell width (or a power of two times that cell width).
	 * Otherwise, a good guideline is that most of the HitBoxes should
	 * have width and height less than cellWidth.
	 */
	public double cellWidth;
	
	/**
	 * An efficiency parameter representing a bound on how far in advance
	 * collisions/separations may be tested for.
	 * This is used to avoid accumulating potential events in a priority
	 * queue that take too long to be resolved, effectively creating a memory leak.
	 * A good choice for most games is around 2 seconds.
	 */
	public double maxForesightTime;
	
	/**
	 * Roughly the distance that two collided HitBoxes must be from each other before
	 * a separated event is generated.  This must be non-zero due to numerical stability
	 * issues.  A good choice for most games is around 1/10 of a "pixel",
	 * since that won't be noticeable.
	 */
	public double separateBuffer;
	
	/**Creates a blank ColliderOpts object.  Fields must be set manually.*/
	public ColliderOpts() {}
}
