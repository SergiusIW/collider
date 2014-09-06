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

import java.util.PriorityQueue;

/**
 * Class for managing
 * <a href="http://en.wikipedia.org/wiki/Collision_detection#A_posteriori_.28discrete.29_versus_a_priori_.28continuous.29">continuous collision detection</a>
 * of HitBoxes.
 * Contains functionality to efficiently determine the next collision that will occur
 * and precisely what time it will occur.
 * Also tracks when two overlapping HitBoxes separate.
 * 
 * @see ColliderOpts
 * @see HitBox
 * @author Matthew Michelotti
 */

public final class Collider {
	private Field field;
	private double time = 0.0;
	private CollisionTester collisionTester;
	private InteractTester interactTester;
	private double maxForesightTime;
	private PriorityQueue<FunctionEvent> queue = new PriorityQueue<FunctionEvent>();
	private ColliderEvent cEvent = new ColliderEvent();
	private boolean processedCollision = true;
	
	private HitBox curHitBox;
	private IntBox oldBounds = new IntBox();
	private IntBox newBounds = new IntBox();
	private int oldGroup;
	private int nextEventId = 0;
	private boolean changeInteractivity = false;
	
	private Array<HitBox> hitBoxRemoveBuffer = new Array<HitBox>();
	
	private int testId = 0;
	
	private int hitBoxesInUse = 0;
	private int numOverlaps = 0;
	
	private HitBoxPool<HBRect> rectPool = new HitBoxPool<HBRect>() {
		@Override protected HBRect newObject() {return new HBRect(Collider.this);}
	};
	private HitBoxPool<HBCircle> circlePool = new HitBoxPool<HBCircle>() {
		@Override protected HBCircle newObject() {return new HBCircle(Collider.this);}
	};
	private Pool<EReiterate> reiteratePool = new Pool<EReiterate>() {
		@Override protected EReiterate newObject() {return new EReiterate();}
	};
	private Pool<ECollide> collidePool = new Pool<ECollide>() {
		@Override protected ECollide newObject() {return new ECollide();}
	};
	
	private SetPool<HitBox> overlapSetPool = new SetPool<HitBox>();
	
	/**
	 * Constructs a new Collider.
	 * @param opts Desired settings for the Collider.
	 */
	public Collider(ColliderOpts opts) {
		if(opts.interactTester == null) throw new IllegalArgumentException();
		if(opts.maxForesightTime <= 0.0) throw new IllegalArgumentException();
		field = new Field(opts);
		collisionTester = new CollisionTester(opts);
		interactTester = opts.interactTester;
		maxForesightTime = opts.maxForesightTime;
	}
	
	/**
	 * Obtains a rectangular HitBox to be used with this Collider.
	 * @return A free HBRect obtained from a pool.
	 */
	public HBRect makeRect() {
		HBRect hitBox = rectPool.obtain();
		hitBox.init();
		return hitBox;
	}
	
	/**
	 * Obtains a circular HitBox to be used with this Collider.
	 * @return A free HBCircle obtained from a pool.
	 */
	public HBCircle makeCircle() {
		HBCircle hitBox = circlePool.obtain();
		hitBox.init();
		return hitBox;
	}
	
	/**
	 * Same as calling {@link Collider#stepToTime(double, boolean)}
	 * with inclusive set to true.
	 * @param newTime Advances simulation to this time
	 *   (unless a collision/separation occurs earlier).
	 * @return A description of the collision/separation, or null if no collision/separation occurred.
	 *   This object will be reused whenever stepToTime is called.
	 */
	public ColliderEvent stepToTime(double newTime) {
		return stepToTime(newTime, true);
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
	 *   This object will be reused whenever stepToTime is called.
	 */
	public ColliderEvent stepToTime(double newTime, boolean inclusive) {
		if(newTime < time) throw new IllegalArgumentException();
		processCurHBAndCollision();
		cEvent.clear();
		for(FunctionEvent evt = queue.peek();
				evt != null && (inclusive ? evt.time <= newTime : evt.time < newTime);
				evt = queue.peek())
		{
			queue.poll();
			time = evt.time;
			evt.resolve(this);
			if(cEvent.isInitialized()) return cEvent;
			processCurHBAndCollision();
		}
		time = newTime;
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
	 * Calling {@link #stepToTime(double)} with <code>peekNextEventTime()</code>
	 * as the newTime will not necessarily return an
	 * event, because it may just process an internal event.
	 * @return Time of next event in priority queue, or positive infinity if
	 *   there are no events.
	 */
	public double peekNextEventTime() {
		processCurHBAndCollision();
		FunctionEvent evt = queue.peek();
		if(evt == null) return Double.POSITIVE_INFINITY;
		else return evt.time;
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
	
	Normal getNormal(HitBox source, HitBox dest) {
		return collisionTester.normal(source, dest, time);
	}
	
	void free(HBRect hitBox) {
		removeHitBoxReferences(hitBox);
		rectPool.free(hitBox);
	}
	
	void free(HBCircle hitBox) {
		removeHitBoxReferences(hitBox);
		circlePool.free(hitBox);
	}
	
	private void removeHitBoxReferences(HitBox hitBox) {
		if(!processedCollision && cEvent.involves(hitBox)) {
			cEvent.clear();
			processedCollision = true;
		}
		altering(hitBox);
		field.remove(hitBox, oldGroup, oldBounds, null);
		for(HitBox b : overlapSetPool.iterator(hitBox.overlapSet)) {
			b.overlapSet = overlapSetPool.remove(b.overlapSet, hitBox);
			if(!overlapSetPool.wasSuccessful()) throw new RuntimeException();
			numOverlaps--;
		}
		hitBox.overlapSet = overlapSetPool.clear(hitBox.overlapSet);
		curHitBox = null;
	}
	
	void altering(HitBox hitBox) {
		altering(hitBox, false);
	}
	
	void altering(HitBox hitBox, boolean changeInteractivity) {
		if(!hitBox.isInitialized()) throw new RuntimeException("cannot alter hitBox after freed");
		curHitBox.endTime = -1;
		if(curHitBox == hitBox) {
			if(changeInteractivity) this.changeInteractivity = true;
			return;
		}
		if(curHitBox != null) processCurHBAndCollision();
		this.curHitBox = hitBox;
		this.changeInteractivity = changeInteractivity;
		field.getIndexBounds(hitBox, oldBounds);
		oldGroup = hitBox.getGroup();
		hitBox.markTransitionStart();
	}

	void queue(FunctionEvent event) {
		event.id = nextEventId;
		nextEventId++;
		queue.add(event);
	}
	void freeEvent(EReiterate evt) {reiteratePool.free(evt);}
	void freeEvent(ECollide evt) {collidePool.free(evt);}

	void processCurHBAndCollision() {
		processCurHBAndCollision(true);
	}
	
	void processCurHBAndCollision(boolean checkReiterate) {
		processCollision();
		if(curHitBox == null) return;
		if(curHitBox.endTime < time) throw new RuntimeException("HitBox was not finalized after being altered");
		testId++;
		if(checkReiterate) checkForReiteration();
		int newGroup = curHitBox.getGroup();
		if(newGroup != oldGroup && !changeInteractivity) throw new RuntimeException();
		for(HitBox b : overlapSetPool.iterator(curHitBox.overlapSet)) {
			if(!b.testMark(testId)) throw new RuntimeException();
			if(newGroup < 0 || (changeInteractivity && !interactTester.canInteract(curHitBox, b))) {
				hitBoxRemoveBuffer.add(b);
			}
			else {
				checkForSeparation(curHitBox, b);
			}
		}
		for(HitBox b : hitBoxRemoveBuffer) {
			curHitBox.overlapSet = overlapSetPool.remove(curHitBox.overlapSet, b);
			if(!overlapSetPool.wasSuccessful()) throw new RuntimeException();
			b.overlapSet = overlapSetPool.remove(b.overlapSet, curHitBox);
			if(!overlapSetPool.wasSuccessful()) throw new RuntimeException();
			numOverlaps--;
		}
		hitBoxRemoveBuffer.clear();
		field.getIndexBounds(curHitBox, newBounds);
		if(oldGroup == newGroup) field.remove(curHitBox, oldGroup, oldBounds, newBounds);
		else field.remove(curHitBox, oldGroup, oldBounds, null);
		int[] groupArr = null;
		if(newGroup >= 0) groupArr = interactTester.getInteractGroups(curHitBox);
		curHitBox.testMark(testId);
		if(groupArr != null && groupArr.length > 0) {
			for (HitBox b : field.iterator(newBounds, groupArr, testId)) {
				if (interactTester.canInteract(curHitBox, b)) {
					checkForCollision(curHitBox, b);
				}
			}
		}
		if(oldGroup == newGroup) field.add(curHitBox, newGroup, oldBounds, newBounds);
		else field.add(curHitBox, newGroup, null, newBounds);
		curHitBox = null;
		changeInteractivity = false;
	}
	
	private void processCollision() {
		if(processedCollision) return;
		processedCollision = true;
		HitBox a = cEvent.getFirst();
		HitBox b = cEvent.getSecond();
		if(cEvent.isCollision()) {
			if(interactTester.canInteract(a, b)) {
				a.overlapSet = overlapSetPool.add(a.overlapSet, b);
				if(!overlapSetPool.wasSuccessful()) throw new RuntimeException();
				b.overlapSet = overlapSetPool.add(b.overlapSet, a);
				if(!overlapSetPool.wasSuccessful()) throw new RuntimeException();
				numOverlaps++;
				if(!cEvent.involves(curHitBox)) checkForSeparation(a, b);
			}
		}
		else {
			a.overlapSet = overlapSetPool.remove(a.overlapSet, b);
			if(!overlapSetPool.wasSuccessful()) throw new RuntimeException();
			b.overlapSet = overlapSetPool.remove(b.overlapSet, a);
			if(!overlapSetPool.wasSuccessful()) throw new RuntimeException();
			numOverlaps--;
			if(!cEvent.involves(curHitBox) && interactTester.canInteract(a, b)) {
				checkForCollision(a, b);
			}
		}
	}

	void setCollision(HitBox a, HitBox b, boolean collided) {
		cEvent.init(a, b, collided);
		processedCollision = false;
	}
	
	private void checkForReiteration() {
		if(!curHitBox.isMoving()) return;
		double period = field.getGridPeriod(curHitBox);
		if(period > maxForesightTime) period = maxForesightTime;
		double firstReiterTime = time + period;
		if(firstReiterTime >= curHitBox.endTime) return;
		EReiterate event = reiteratePool.obtain();
		event.init(curHitBox, firstReiterTime, curHitBox.endTime, period);
		curHitBox.endTime = firstReiterTime;
		queue(event);
	}
	
	private void checkForCollision(HitBox a, HitBox b) {
		double collideTime = collisionTester.collideTime(a, b, time);
		if(collideTime < Double.POSITIVE_INFINITY) {
			ECollide event = collidePool.obtain();
			event.init(a, b, collideTime, true);
			queue(event);
		}
	}
	
	private void checkForSeparation(HitBox a, HitBox b) {
		double collideTime = collisionTester.separateTime(a, b, time);
		if(collideTime < Double.POSITIVE_INFINITY) {
			ECollide event = collidePool.obtain();
			event.init(a, b, collideTime, false);
			queue(event);
		}
	}
	
	private abstract class HitBoxPool <T extends HitBox> extends Pool<T> {
		@Override public T obtain() {
			hitBoxesInUse++;
			return super.obtain();
		}
		@Override public void free(T hitBox) {
			hitBoxesInUse--;
			super.free(hitBox);
		}
	}
}
