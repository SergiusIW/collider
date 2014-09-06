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

//TODO rename "setEndTime" to "finalize", and require finalize to be called after all other changes to the HitBox (still requires endTime)

/**
 * Description of position, shape, and velocities of a hitbox
 * used for testing collision with other HitBoxes.
 * Methods for creating HitBoxes can be found in the {@link Collider} class.
 * Based on the velocities, the position and shape of a HitBox will
 * automatically reflect changes in time in the Collider.
 * The word "hitbox" is used in a general sense, as a HitBox is
 * not necessarily a rectangle.
 * <p>
 * When a HitBox's state is modified, it will need to check with the Collider
 * for potential interactions.
 * Will wait for all consecutive modifier method calls on a HitBox
 * before performing this check.
 * Whenever you modify the state of a HitBox, you must also call
 * {@link #finalize(double)}.
 * <p>
 * For the sake of avoiding numerical instability, the dimensions
 * of a HitBox should never be zero nor extremely small.
 * Something on the order of 1/10 of a "pixel" should still be fine,
 * but, for example, 1e-11 "pixels" is too small.
 * 
 * @author Matthew Michelotti
 */
public abstract class HitBox {
	/**
	 * Number of legal HitBox groups.
	 * Current value is 256, but it most cases you won't need more than 3 groups.
	 * Legal groups are 0-255 inclusive.
	 */
	public final static int NUM_GROUPS = 256;
	
	double startTime, endTime;
	final Collider collider;
	Object overlapSet;
	
	private int group = -2;
	private int changeId = 0;
	private int testId = -1;
	private Object owner;
	
	HitBox(Collider collider) {
		this.collider = collider;
	}
	
	void init() {
		this.startTime = collider.getTime();
		this.endTime = this.startTime;
		
		this.group = -1;
		setGroup(0);
	}
	
	void markTransitionStart() {
		startTime = collider.getTime();
		if(endTime < startTime) {
			throw new RuntimeException("updating HitBox late");
		}
		changeId++;
	}
	
	/**
	 * Call when done using this HitBox.
	 * No more events will be generated involving this HitBox.
	 * This object will be placed uninitialized in a pool
	 * for future use.
	 */
	public void free() {
		//NOTE: overridden free method should place the HitBox in the appropriate pool
		owner = null;
		group = -2;
	}
	
	boolean isInitialized() {
		return group != -2;
	}
	
	final boolean testMark(int testId) {
		if(testId == this.testId) return false;
		this.testId = testId;
		return true;
	}
	
	final int getChangeId() {return changeId;}
	
	/**
	 * Call this method if there is a change in the return values
	 * of {@link InteractTester#canInteract(HitBox, HitBox)} involving
	 * this HitBox.
	 * This will prompt searching for potential collisions between
	 * previously uninteractable HitBoxes.
	 * Separate events will not be generated for HitBoxes that overlap and used
	 * to interact with each other but no longer do because of this call.
	 */
	public final void interactivityChange() {collider.altering(this, true);}
	
	/**
	 * Set the group that this HitBox belongs to.
	 * Default group is 0.
	 * The value -1 denotes that this HitBox does not belong to any group
	 * and thus is never tested for collisions.
	 * This method will also invoke the functionality of
	 * {@link #interactivityChange()}.
	 * <p>
	 * Collision testing will only be performed on HitBoxes of the groups
	 * specified by the {@link InteractTester#getInteractGroups(HitBox)}
	 * method.
	 * This reduces the number of HitBoxes to iterate over for collision checks.
	 * It is a good idea to use only a small number of groups for a game, perhaps 1 to 3.
	 * As an example, if you are implementing a
	 * <a href="http://en.wikipedia.org/wiki/Shoot_'em_up#Bullet_hell_and_niche_appeal">danmaku</a>
	 * game, you might use one group for bullets and one group for everything else,
	 * and make it so bullets do not check for collisions within the bullet group.
	 * @param group Group that this HitBox should belong to.  Must be between
	 *   -1 and {@link #NUM_GROUPS}-1 inclusive. The value -1 denotes not belonging to any group.
	 */
	public final void setGroup(int group) {
		if(group < -1 || group >= NUM_GROUPS) {
			throw new IllegalArgumentException("invalid group:" + group);
		}
		collider.altering(this, true);
		this.group = group;
	}
	
	/**
	 * This should be called after you modify the HitBox by changing
	 * its position, velocity, interactivity, etc. (exception: this does
	 * not need to be called after calling {@link #setOwner(Object)}).
	 * Call this method only once after you have made all of the other
	 * changes to this HitBox.  You must specify an endTime, which
	 * is the expected time of the next change to the HitBox state.
	 * You must call finalize again when this endTime is reached, if not sooner.
	 * Although you are allowed to change the HitBox state and call finalize prior
	 * to the specified endTime, doing so will result in more collisions
	 * that need to be tested.
	 * @param endTime Expected time of next change to HitBox state.
	 * Positive infinity is allowed.
	 */
	public final void finalize(double endTime) {
		double time = collider.getTime();
		if(endTime < time) throw new IllegalArgumentException("endTime already passed");
		collider.altering(this);
		this.endTime = endTime;
	}
	
	/**
	 * Set an object to be associated with this HitBox.
	 * This is provided as an alternative to looking up
	 * a related object in a HashMap with HitBoxes as keys.
	 * @param obj Object to be associated with this HitBox.
	 */
	public final void setOwner(Object obj) {this.owner = obj;}
	
	/**
	 * Returns the group that this HitBox belongs to.
	 * @return The group that this HitBox belongs to.
	 * @see #setGroup(int)
	 */
	public final int getGroup() {return group;}
	
	/**
	 * Returns the Object associated with this HitBox.
	 * @return The Object associated with this HitBox.
	 */
	public final Object getOwner() {return owner;}
	
	/**
	 * Returns the current time of the simulation.
	 * Same as calling {@link Collider#getTime()}.
	 * @return The current time of the simulation.
	 */
	public final double getTime() {return collider.getTime();}
	
	/**
	 * Returns a normal vector between the two HitBoxes.
	 * Vector will be pointed away from this HitBox and towards the dest HitBox.
	 * @param dest Other HitBox that normal vector will be pointed towards.
	 * @return Normal vector between the two HitBoxes.
	 *   This object will be re-used each time getNormal is called
	 *   on any HitBox generated from the same Collider.
	 */
	public final Normal getNormal(HitBox dest) {
		return collider.getNormal(this, dest);
	}
	
	/**
	 * Returns the amount that the two HitBoxes overlap.
	 * This is the same as the overlap in the return
	 * value of {@link #getNormal(HitBox)}.
	 * Due to rounding error, this value may be positive
	 * even if a collision event was not generated
	 * for the two HitBoxes, and vice versa.
	 * @param other Other HitBox to compute overlap of.
	 * @return The amount that the two HitBoxes overlap.
	 */
	public final double getOverlap(HitBox other) {
		return collider.getNormal(this, other).overlap;
	}

	
	/**
	 * Returns true if the two HitBoxes overlap.
	 * This is the same as checking if {@link #getOverlap(HitBox)}
	 * is positive.
	 * Due to rounding error, this value may be true
	 * even if a collision event was not generated
	 * for the two HitBoxes, and vice versa.
	 * @param other Other HitBox to test overlap with.
	 * @return True if the two HitBoxes overlap.
	 */
	public final boolean overlaps(HitBox other) {
		return collider.getNormal(this, other).overlap > 0.0;
	}
	
	abstract boolean isMoving();
	
	abstract double getBoundEdgeComp(int edge, double startTime, double endTime);
	
	abstract double getMaxBoundEdgeVel();
	
	final double getBoundEdgeComp(int edge) {
		return getBoundEdgeComp(edge, startTime, endTime);
	}
}
