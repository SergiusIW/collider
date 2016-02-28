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

import com.matthewmichelotti.collider.geom.Shape;
import com.matthewmichelotti.collider.geom.Vec2d;

/**
 * Description of position, shape, and velocities of a hitbox
 * used for testing collision with other HitBoxes.
 * Methods for creating HitBoxes can be found in the {@link Collider} class.
 * Based on the velocities, the position and shape of a Hitbox will
 * automatically reflect changes in time in the Collider.
 * The word "hitbox" is used in a general sense, as a Hitbox is
 * not necessarily a rectangle.
 * <p>
 * When a Hitbox's state is modified, it will need to check with the Collider
 * for potential interactions.
 * Will wait for all consecutive modifier method calls on a Hitbox
 * before performing this check.
 * <p>
 * For the sake of avoiding numerical instability, the dimensions
 * of a Hitbox should never be zero nor extremely small.
 * Something on the order of 1/10 of a "pixel" should still be fine,
 * but, for example, 1e-11 "pixels" is too small.
 * 
 * @author Matthew Michelotti
 */
//TODO fix javadocs
public final class Hitbox {
	private final static HitboxState DEFAULT_STATE;

	static {
		DEFAULT_STATE = new HitboxState(new Vec2d(0.0, 0.0), Shape.newCircle(1.0));
		DEFAULT_STATE.setGroup(-1);
	}

	private Collider collider;
	TightSet<Hitbox> overlapSet = new TightSet<>();
	TightSet<FunctionEvent> events = new TightSet<>();
	private double startTime = 0.0;
	private HitboxState stateAtStartTime = DEFAULT_STATE;
	private double internalRemainingTime = Double.POSITIVE_INFINITY;

	private int testId = -1;
	private Object owner;
	
	Hitbox(Collider collider, HitboxState intialState, Object owner) {
		this.collider = collider;
		this.owner = owner;
		changeState(intialState);
	}

	/**
	 * TODO javadoc
	 * @return
	 * @throws IllegalStateException if called after Hitbox was deleted,
	 *         or if remainingTime in HitboxState is negative
	 */
	public HitboxState getState() {
		checkNotDeleted();
		checkRemainingTime();
		return stateAtStartTime.advance(startTime, collider.getTime());
	}

	HitboxState getInternalStateAtStartTime() {
		HitboxState state = stateAtStartTime.clone();
		state.setRemainingTime(internalRemainingTime);
		return state;
	}

	HitboxState getInternalState() {
		return getInternalStateAtStartTime().advance(startTime, collider.getTime());
	}

	void setState(HitboxState state, double internalRemainingTime) {
		this.startTime = collider.getTime();
		this.stateAtStartTime = state.clone();
        this.stateAtStartTime.clearInteractivityChange();
		this.internalRemainingTime = internalRemainingTime;
	}

	/**
	 * Call when done using this Hitbox.
	 * No more events will be generated involving this Hitbox.
	 * @throws IllegalStateException if remainingTime in HitboxState is negative
	 */
	public void delete() {
		if(collider == null) return;
		checkRemainingTime();
		if(stateAtStartTime.getGroup() >= 0) {
			HitboxState newState = getState();
			newState.setGroup(-1);
			changeState(newState);
		}
		collider = null;
		overlapSet = null;
		events = null;
		stateAtStartTime = null;
		owner = null;
	}

	private void checkNotDeleted() {
		if(collider == null) throw new IllegalStateException("Hitbox was deleted");
	}

	private void checkRemainingTime() {
		double endTime = startTime + stateAtStartTime.getRemainingTime();
		if(endTime < getTime()) throw new IllegalStateException("remaining time for HitboxState is negative");
	}

	/**
	 * Updates the state of this Hitbox.  This will trigger
	 * the computation of new collision times with other HitBoxes.
	 * @param state new state
	 * @throws IllegalStateException if called after Hitbox was deleted,
	 *         or if remainingTime in original HitboxState is negative
	 */
	public void changeState(HitboxState state) {
		checkNotDeleted();
		checkRemainingTime();
		if(!getState().isSame(state)) collider.updateHitbox(this, state);
		if(stateAtStartTime.getGroup() < 0 && !events.isEmpty()) throw new RuntimeException();
	}

	boolean testMark(int testId) {
		if(testId == this.testId) return false;
		this.testId = testId;
		return true;
	}
	
	/**
	 * Get the owner Object associated with this Hitbox.
	 * @return object
	 * @throws IllegalStateException if called after Hitbox was deleted
	 */
	public final Object getOwner() {
		checkNotDeleted();
		return owner;
	}
	
	/**
	 * Returns the current time of the simulation.
	 * Same as calling {@link Collider#getTime()}.
	 * @return The current time of the simulation.
	 * @throws IllegalStateException if called after Hitbox was deleted
	 */
	public final double getTime() {
		checkNotDeleted();
		return collider.getTime();
	}
}
