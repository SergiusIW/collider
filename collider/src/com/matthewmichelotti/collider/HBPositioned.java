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
 * Superclass of all HitBoxes that make use of a central position.
 * Currently, all concrete HitBox classes extend this class.
 * @author Matthew Michelotti
 */
public abstract class HBPositioned extends HitBox {
	double startX, startY;
	double velX, velY;
	
	HBPositioned(Collider collider) {
		super(collider);
	}

	@Override
	void init() {
		super.init();
		this.startX = 0.0;
		this.startY = 0.0;
		this.velX = 0.0;
		this.velY = 0.0;
	}

	@Override
	void markTransitionStart() {
		double time = collider.getTime();
		startX = getX(time);
		startY = getY(time);
		super.markTransitionStart();
	}
	
	/**
	 * Set the center x-coordinate.
	 * @param x Center x-corrdinate.
	 */
	public final void setX(double x) {collider.altering(this); this.startX = x;}
	
	/**
	 * Set the center y-coordinate.
	 * @param y Center y-coordinate.
	 */
	public final void setY(double y) {collider.altering(this); this.startY = y;}
	
	/**
	 * Set the velocity of the center x-coordinate.
	 * @param velX Velocity of the center x-coordinate.
	 */
	public final void setVelX(double velX) {
		if(this.velX == velX) return;
		collider.altering(this);
		this.velX = velX;
	}
	
	/**
	 * Set the velocity of the center y-coordinate.
	 * @param velY Velocity of the center y-coordinate.
	 */
	public final void setVelY(double velY) {
		if(this.velY == velY) return;
		collider.altering(this);
		this.velY = velY;
	}
	
	/**
	 * Set the center position.
	 * @param x Center x-coordinate.
	 * @param y Center y-coordinate.
	 */
	public final void setPos(double x, double y) {
		collider.altering(this);
		this.startX = x;
		this.startY = y;
	}
	
	/**
	 * Set the velocity of the center position.
	 * @param velX Velocity of the center x-coordinate.
	 * @param velY Velocity of the center y-coordinate.
	 */
	public final void setVel(double velX, double velY) {
		if(this.velX == velX && this.velY == velY) return;
		collider.altering(this);
		this.velX = velX;
		this.velY = velY;
	}
	
	final double getX(double time) {return startX + (time - startTime)*velX;}
	final double getY(double time) {return startY + (time - startTime)*velY;}
	
	/**
	 * Get the center x-coordinate.
	 * @return The center x-coordinate.
	 */
	public final double getX() {return getX(collider.getTime());}
	
	/**
	 * Get the center y-coordinate.
	 * @return The center y-coordinate.
	 */
	public final double getY() {return getY(collider.getTime());}
	
	/**
	 * Get the velocity of the center x-coordinate.
	 * @return The velocity of the center x-coordinate.
	 */
	public final double getVelX() {return velX;}
	
	/**
	 * Get the velocity of the center y-coordinate.
	 * @return The velocity of the center y-coordinate.
	 */
	public final double getVelY() {return velY;}
	
	final double getStartPosComp(int dir) {
		switch(dir) {
		case Dir.R: return startX;
		case Dir.U: return startY;
		case Dir.L: return -startX;
		case Dir.D: return -startY;
		default: throw new IllegalArgumentException();
		}
	}
	
	final double getVelComp(int dir) {
		switch(dir) {
		case Dir.R: return velX;
		case Dir.U: return velY;
		case Dir.L: return -velX;
		case Dir.D: return -velY;
		default: throw new IllegalArgumentException();
		}
	}
	
	final double getPosComp(int dir, double time) {
		return getStartPosComp(dir) + (time - startTime)*getVelComp(dir);
	}
	
	final void dummySetStartCoord(int dir, double value) {
		switch(dir) {
		case Dir.R: startX = value; return;
		case Dir.U: startY = value; return;
		default: throw new IllegalArgumentException();
		}
	}
	
	final void dummySetVelCoord(int dir, double value) {
		switch(dir) {
		case Dir.R: velX = value; return;
		case Dir.U: velY = value; return;
		default: throw new IllegalArgumentException();
		}
	}
}
