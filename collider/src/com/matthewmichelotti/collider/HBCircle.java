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
 * A circular HitBox.
 * @author Matthew Michelotti
 * @see Collider#makeCircle()
 */
public final class HBCircle extends HBPositioned {
	double startRad;
	double velRad;
	
	HBCircle(Collider collider) {
		super(collider);
	}

	@Override
	void init() {
		super.init();
		this.startRad = 0.0;
		this.velRad = 0.0;
	}

	@Override
	void markTransitionStart() {
		double time = collider.getTime();
		startRad = getRad(time);
		super.markTransitionStart();
	}

	@Override
	public void free() {
		collider.free(this);
		super.free();
	}
	
	/**
	 * Set the diameter.
	 * @param diam Diameter.
	 */
	public void setDiam(double diam) {collider.altering(this); this.startRad = .5*diam;}
	
	/**
	 * Set the velocity of the diameter.
	 * @param velDiam Velocity of the diameter.
	 */
	public void setVelDiam(double velDiam) {
		double velRad = .5*velDiam;
		if(this.velRad == velRad) return;
		collider.altering(this);
		this.velRad = velRad;
	}
	
	double getRad(double time) {return startRad + (time - startTime)*velRad;}
	
	/**
	 * Get the diameter.
	 * @return Diameter.
	 */
	public double getDiam() {return 2*getRad(collider.getTime());}
	
	/**
	 * Get the velocity of the diameter.
	 * @return Velocity of the diameter.
	 */
	public double getVelDiam() {return 2*velRad;}
	
	double getStartEdgeComp(int edge) {
		return getStartPosComp(edge) + startRad;
	}
	
	double getVelEdgeComp(int edge) {
		return getVelComp(edge) + velRad;
	}

	@Override
	double getBoundEdgeComp(int edge, double startTime, double endTime) {
		double base = getStartEdgeComp(edge);
		double vel = getVelEdgeComp(edge);
		double evalTime = (vel > 0.0) ? endTime : startTime;
		return base + vel*(evalTime - this.startTime);
	}

	@Override
	boolean isMoving() {
		return velX != 0.0 || velY != 0.0 || velRad != 0.0;
	}

	@Override
	double getMaxBoundEdgeVel() {
		double vel = 0.0;
		double absVelRad = Arith.abs(velRad);
		for(int dir = 0; dir < 2; dir++) {
			vel = Math.max(vel, Arith.abs(getVelComp(dir)) + absVelRad);
		}
		return vel;
	}
}
