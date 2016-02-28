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

import com.matthewmichelotti.collider.processes.FlowProcess;

import java.util.TreeSet;

/**
 * Class for managing
 * <a href="http://en.wikipedia.org/wiki/Collision_detection#A_posteriori_.28discrete.29_versus_a_priori_.28continuous.29">continuous collision detection</a>
 * of HitBoxes.
 * Contains functionality to efficiently determine the next collision that will occur
 * and precisely what time it will occur.
 * Also tracks when two overlapping HitBoxes separate.
 * 
 * @see ColliderOpts
 * @see Hitbox
 * @author Matthew Michelotti
 */
//TODO fix javadocs
public final class Collider {
	private final Field field;
	private double time = 0.0;
	private final InteractTester interactTester;
	private final TreeSet<FunctionEvent> queue = new TreeSet<>();
	private final double separateBuffer;

	private int nextEventId = 0;
	private int testId = 0;

	//TODO track hitBoxesInUse and numOverlaps...
	private int hitBoxesInUse = 0;
	private int numOverlaps = 0;

	/**
	 * Constructor.
	 *
	 * @param interactTester Used to determine which pairs of HitBoxes should be tested for collisions.
	 * @param cellWidth An efficiency parameter representing the width and height of a
	 * cell in the Collider grid.
	 * The Collider references HitBoxes in a conceptually infinite grid
	 * in order to reduce the number of collisions that need to be tested.
	 * If your game uses a grid layout, it would be a good choice
	 * to use the same cell width (or a power of two times that cell width).
	 * Otherwise, a good guideline is that most of the HitBoxes should
	 * have width and height less than cellWidth.
	 * @param separateBuffer Roughly the distance that two collided HitBoxes must be from each other before
	 * a separated event is generated.  This must be non-zero due to numerical stability
	 * issues.  A good choice for most games is around 1/10 of a "pixel",
	 * since that won't be noticeable.
	 */
	public Collider(InteractTester interactTester, double cellWidth, double separateBuffer) {
		if(interactTester == null) throw new NullPointerException("interactTester must be non-null");
		if(cellWidth <= 0.0) throw new IllegalArgumentException("cellWidth must be positive");
		if(separateBuffer <= 0.0) throw new IllegalArgumentException("separateBuffer must be positive");
		this.interactTester = interactTester;
		this.separateBuffer = separateBuffer;
		field = new Field(cellWidth);
	}

	/**
	 * Constructs a new Hitbox to be used with this Collider.
	 * @param state initial state of the hitbox
	 * @param owner the owner object to be associated with the hitbox
	 * @return a new hitbox
	 */
	public Hitbox newHitbox(HitboxState state, Object owner) {
		return new Hitbox(this, state, owner);
	}
	
	/**
	 * Same as calling {@link Collider#stepToTime(double, boolean)}
	 * with inclusive set to true.
	 * @param newTime Advances simulation to this time
	 *   (unless a collision/separation occurs earlier).
	 * @return A description of the collision/separation, or null if no collision/separation occurred.
	 *   This object will be reused whenever advance is called.
	 */
	public ColliderEvent advance(double maxTime) {
		return advance(maxTime, true);
	}

	private FunctionEvent peekQueue() {
		if(queue.isEmpty()) return null;
		return queue.first();
	}
	
	/**
	 * Advance the simulation to the specified time.
	 * Moving HitBoxes will reflect this change in their
	 * positions and dimensions.
	 * If a collision/separation occurs before newTime, it will only
	 * update the time to the point of the collision/separation.
	 * @param newTime Advances simulation to this time
	 *   (unless a collision/separation occurs earlier).
	 * @param inclusive If true, this method may return an event that occurs precisely at newTime.
	 * @return A description of the collision/separation, or null if no collision/separation occurred.
	 *   This object will be reused whenever advance is called.
	 */
	public ColliderEvent advance(double maxTime, boolean inclusive) {
		if(maxTime < time) throw new IllegalArgumentException();
		for(FunctionEvent evt = peekQueue();
				evt != null && (inclusive ? evt.getTime() <= maxTime : evt.getTime() < maxTime);
				evt = peekQueue())
		{
			queue.pollFirst();
			time = evt.getTime();
			evt.getFirst().events.remove(evt);
			if(evt.isPairEvent()) evt.getSecond().events.remove(evt);
			ColliderEvent colliderEvent = evt.resolve(this);
			if(colliderEvent != null) return colliderEvent;
		}
		time = maxTime;
		return null;
	}
	
	/**
	 * Returns current time of the simulation.
	 * @return Current time of the simulation.
	 */
	public double getTime() {
		return time;
	}
	
	/**
	 * This method reveals the time of the next event in the priority queue
	 * that is used to keep track of potential collisions/separations and internal
	 * events for this Collider.
	 * This is useful
	 * for coupling the Collider with other continuous-time processes.
	 * Calling {@link #stepToTime(double)} with <code>peekNextTime()</code>
	 * as the newTime will not necessarily return an
	 * event, because it may just process an internal event.
	 * @return Time of next event in priority queue, or positive infinity if
	 *   there are no events.
	 * @see FlowProcess
	 */
	public double peekNextEventTime() {
		FunctionEvent evt = peekQueue();
		if(evt == null) return Double.POSITIVE_INFINITY;
		else return evt.getTime();
	}
	
	/**
	 * Prints some debug information for the Collider to standard output.
	 * Specifically:
	 * <ul>
	 * <li> Number of HitBoxes in use.
	 * <li> Number of entries in the grid.
	 * <li> Number of events in the priority queue.
	 * <li> Number of tracked overlapping HitBoxes.
	 * </ul>
	 */
	public void log() {
		System.out.println("------- Collider Info -------");
		System.out.println(" hit boxes: " + hitBoxesInUse);
		System.out.println(" grid entries: " + field.getNumEntries());
		System.out.println(" queue size: " + queue.size());
		System.out.println(" overlaps: " + numOverlaps);
		System.out.println("-----------------------------");
	}

	void updateHitbox(final Hitbox hitbox, HitboxState newStatePublic) {
		clearRelatedEvents(hitbox);
		testId++;

		HitboxState oldState = hitbox.getInternalStateAtStartTime();
		IntBox oldBounds = oldState.getGroup() >= 0 ? field.getIndexBounds(oldState) : null;

		HitboxState newState = newStatePublic.clone();
		if(newState.getGroup() >= 0) newState.setRemainingTime(Math.min(newState.getRemainingTime(), field.getGridPeriod(newState)));
		IntBox newBounds = newState.getGroup() >= 0 ? field.getIndexBounds(newState) : null;

		if(oldState.getGroup() == newState.getGroup()) {
			field.remove(hitbox, oldState.getGroup(), oldBounds, newBounds);
			field.add(hitbox, newState.getGroup(), oldBounds, newBounds);
		} else {
			field.remove(hitbox, oldState.getGroup(), oldBounds, null);
			field.add(hitbox, newState.getGroup(), null, newBounds);
		}

		hitbox.setState(newStatePublic, newState.getRemainingTime());

		if(newState.getGroup() >= 0) {
			for(Hitbox otherHitbox : field.iterator(newBounds, interactTester.getInteractGroups(hitbox).array, testId)) {
				if(hitbox == otherHitbox) continue;
				if(hitbox.overlapSet.contains(otherHitbox)) continue;
				if(!interactTester.canInteract(hitbox, otherHitbox)) continue;
				collisionCheck(hitbox, otherHitbox);
			}

			for(Hitbox otherHitbox : newStatePublic.interactivityChange ? hitbox.overlapSet.valuesToList() : hitbox.overlapSet) {
				if(newStatePublic.interactivityChange && !interactTester.canInteract(hitbox, otherHitbox)) {
					hitbox.overlapSet.remove(otherHitbox);
					otherHitbox.overlapSet.remove(hitbox);
				} else {
					separationCheck(hitbox, otherHitbox);
				}
			}

			if(newState.getRemainingTime() != newStatePublic.getRemainingTime()) {
				queueFunctionEvent(new FunctionEvent(getTime() + newState.getRemainingTime(), nextEventId++, hitbox, null) {
					@Override ColliderEvent resolve(Collider collider) {
						updateHitbox(hitbox, hitbox.getState());
						return null;
					}
				});
			}
		} else {
			for(Hitbox otherHitbox : hitbox.overlapSet) {
				otherHitbox.overlapSet.remove(hitbox);
			}
			hitbox.overlapSet.clear();
		}
	}

	private void collisionCheck(final Hitbox a, final Hitbox b) {
		if(a == b) throw new IllegalStateException();
		double collideTime = CollisionTests.collideTime(a.getInternalState(), b.getInternalState());
		if(collideTime == Double.POSITIVE_INFINITY) return;
		queueFunctionEvent(new FunctionEvent(getTime() + collideTime, nextEventId++, a, b) {
			@Override ColliderEvent resolve(Collider collider) {
				boolean success = a.overlapSet.add(b);
				success &= b.overlapSet.add(a);
				if(!success) throw new IllegalStateException();
				separationCheck(a, b);
				return ColliderEvent.newCollide(a, b);
			}
		});
	}

	private void separationCheck(final Hitbox a, final Hitbox b) {
		if(a == b) throw new IllegalStateException();
		double separateTime = CollisionTests.separateTime(a.getInternalState(), b.getInternalState(), separateBuffer);
		if(separateTime == Double.POSITIVE_INFINITY) return;
		queueFunctionEvent(new FunctionEvent(getTime() + separateTime, nextEventId++, a, b) {
			@Override ColliderEvent resolve(Collider collider) {
				boolean success = a.overlapSet.remove(b);
				success &= b.overlapSet.remove(a);
				if(!success) throw new IllegalStateException();
				collisionCheck(a, b);
				return ColliderEvent.newSeparation(a, b);
			}
		});
	}

	private void queueFunctionEvent(FunctionEvent event) {
		queue.add(event);
		event.getFirst().events.add(event);
		if(event.isPairEvent()) event.getSecond().events.add(event);
	}

	private void clearRelatedEvents(Hitbox hitbox) {
		for(FunctionEvent event : hitbox.events) {
			queue.remove(event);
			if(event.isPairEvent()) event.getOther(hitbox).events.remove(event);
		}
		hitbox.events.clear();
	}
}
