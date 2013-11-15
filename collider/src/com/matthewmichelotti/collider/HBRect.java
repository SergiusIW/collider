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
 * An axis-aligned rectangular HitBox.
 * @author Matthew Michelotti
 * @see Collider#makeRect()
 */
public final class HBRect extends HBPositioned {
	double startHW, startHH;
	double velHW, velHH;
	
	HBRect(Collider collider) {
		super(collider);
	}

	@Override
	void init() {
		super.init();
		this.startHW = 0.0;
		this.startHH = 0.0;
		this.velHW = 0.0;
		this.velHH = 0.0;
	}

	@Override
	void markTransitionStart() {
		double time = collider.getTime();
		startHW = getHW(time);
		startHH = getHH(time);
		super.markTransitionStart();
	}

	@Override
	public void free() {
		collider.free(this);
		super.free();
	}
	
	/**
	 * Set the width.
	 * @param width Width.
	 */
	public void setWidth(double width) {collider.altering(this); this.startHW = .5*width;}
	
	/**
	 * Set the height.
	 * @param height Height.
	 */
	public void setHeight(double height) {collider.altering(this); this.startHH = .5*height;}
	
	/**
	 * Set the velocity of the width.
	 * @param velWidth Velocity of the width.
	 */
	public void setVelWidth(double velWidth) {
		double velHW = .5*velWidth;
		if(this.velHW == velHW) return;
		collider.altering(this);
		this.velHW = velHW;
	}
	
	/**
	 * Set the velocity of the height.
	 * @param velHeight Velocity of the height.
	 */
	public void setVelHeight(double velHeight) {
		double velHH = .5*velHeight;
		if(this.velHH == velHH) return;
		collider.altering(this);
		this.velHH = velHH;
	}

	
	/**
	 * Set the width and height to the same value.
	 * @param dim Width and height.
	 */
	public void setDims(double dim) {
		collider.altering(this);
		this.startHW = .5*dim;
		this.startHH = startHW;
	}

	/**
	 * Set the width and height.
	 * @param width Width.
	 * @param height Height.
	 */
	public void setDims(double width, double height) {
		collider.altering(this);
		this.startHW = .5*width;
		this.startHH = .5*height;
	}
	
	/**
	 * Set the velocities of the width and height to the same value.
	 * @param velDim Velocity of the width/height.
	 */
	public void setVelDims(double velDim) {
		double velHDim = .5*velDim;
		if(this.velHW == velHDim && this.velHH == velHDim) return;
		collider.altering(this);
		this.velHW = velHDim;
		this.velHH = velHDim;
	}
	
	/**
	 * Set the velocities of the width and height;
	 * @param velWidth Velocity of the width.
	 * @param velHeight Velocity of the height.
	 */
	public void setVelDims(double velWidth, double velHeight) {
		double velHW = .5*velWidth;
		double velHH = .5*velHeight;
		if(this.velHW == velHW && this.velHH == velHH) return;
		collider.altering(this);
		this.velHW = velHW;
		this.velHH = velHH;
	}
	
	double getHW(double time) {return startHW + (time - startTime)*velHW;}
	double getHH(double time) {return startHH + (time - startTime)*velHH;}
	
	/**
	 * Get the width.
	 * @return Width.
	 */
	public double getWidth() {return 2*getHW(collider.getTime());}
	
	/**
	 * Get the height.
	 * @return Height.
	 */
	public double getHeight() {return 2*getHH(collider.getTime());}
	
	/**
	 * Get the velocity of the width.
	 * @return Velocity of the width.
	 */
	public double getVelWidth() {return 2*velHW;}
	
	/**
	 * Get the velocity of the height.
	 * @return Velocity of the height.
	 */
	public double getVelHeight() {return 2*velHH;}
	
	double getStartHDim(int dir) {
		switch(dir) {
		case Dir.R: case Dir.L: return startHW;
		case Dir.U: case Dir.D: return startHH;
		default: throw new IllegalArgumentException();
		}
	}
	
	double getVelHDim(int dir) {
		switch(dir) {
		case Dir.R: case Dir.L: return velHW;
		case Dir.U: case Dir.D: return velHH;
		default: throw new IllegalArgumentException();
		}
	}
	
	double getStartEdgeComp(int edge) {
		return getStartPosComp(edge) + getStartHDim(edge);
	}
	
	double getVelEdgeComp(int edge) {
		return getVelComp(edge) + getVelHDim(edge);
	}
	
	double getEdgeComp(int edge, double time) {
		return getStartEdgeComp(edge) + (time - startTime)*getVelEdgeComp(edge);
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
		return velX != 0.0 || velY != 0.0 || velHW != 0.0 || velHH != 0.0;
	}

	@Override
	double getMaxBoundEdgeVel() {
		double vel = 0.0;
		for(int dir = 0; dir < 2; dir++) {
			vel = Math.max(vel, Arith.abs(getVelComp(dir)) + Arith.abs(getVelHDim(dir)));
		}
		return vel;
	}
	
	void dummyMimicCircle(HBCircle c) {
		startTime = c.startTime;
		startX = c.startX;
		startY = c.startY;
		startHW = c.startRad;
		startHH = c.startRad;
		velX = c.velX;
		velY = c.velY;
		velHW = c.velRad;
		velHH = c.velRad;
	}
}
