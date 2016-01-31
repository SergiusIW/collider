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
 * User-implemented interface to determine which HitBoxes are allowed to interact
 * with each other.  This reduces the number of collisions that need to be tested.
 * 
 * @author Matthew Michelotti
 */
public interface InteractTester {
	/**
	 * Determines whether collision/separation events should be generated for a given
	 * pair of HitBoxes.  The order of the two HitBoxes should not matter.
	 * If the same HitBoxes are input multiple times,
	 * the return value must be the same.
	 * As an exception to this rule, the return value may change if this method
	 * is called after
	 * <ul>
	 * <li>{@link HitBox#interactivityChange()}
	 *     is called on one of the involved HitBoxes,
	 * <li>The group of one of the involved HitBoxes is changed by calling
	 *     {@link HitBox#setGroup(int)},
	 * <li>A collision/separation event occurs for this pair of HitBoxes.
	 * </ul>
	 * @param a First HitBox.
	 * @param b Second HitBox.
	 * @return True if collision/separation events should be generated
	 * for this pair of HitBoxes.
	 */
	public boolean canInteract(HitBox a, HitBox b);
	
	/**
	 * Determines which groups a given HitBox is allowed to interact with.
	 * Do not need to list groups in order, but there should be no duplicates.
	 * If this method does not list a certain group as interactable with
	 * a certain HitBox, then {@link #canInteract(HitBox,
	 * HitBox)} must return false
	 * when comparing that HitBox with any HitBox of the given group.
	 * @param hitBox HitBox to test interactivity of.
	 * @return an array of groups that can be interacted with.  For efficiency, this
	 * array should not be constructed each time the method is called.
	 * This array will not be modified.
	 * Returning null will be treated the same as returning an empty array
	 * @see HitBox#setGroup(int)
	 */
	public int[] getInteractGroups(HitBox hitBox);
}
