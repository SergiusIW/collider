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

import com.matthewmichelotti.collider.geom.PlacedShape;
import com.matthewmichelotti.collider.geom.Shape;
import com.matthewmichelotti.collider.geom.Vec2d;

//TODO javadoc
public final class HitboxState implements Cloneable {
	private Vec2d pos;
	private Vec2d vel;
	private Shape shape;
	private Shape shapeVel;
	private int group = 0;
	boolean interactivityChange = true;
	private double remainingTime = Double.POSITIVE_INFINITY;

	public HitboxState(PlacedShape shape) {
		this(shape.getPos(), shape.getShape());
	}

	public HitboxState(Vec2d pos, Shape shape) {
		if(pos == null || shape == null) throw new NullPointerException();
		this.pos = pos;
		this.vel = Vec2d.ZERO;
		this.shape = shape;
		this.shapeVel = shape.isCircle() ? Shape.ZERO_CIRCLE : Shape.ZERO_RECT;
	}

	public Vec2d getPos() {
		return pos;
	}

	public Vec2d getVel() {
		return vel;
	}

	public Shape getShape() {
		return shape;
	}

	public Shape getShapeVel() {
		return shapeVel;
	}

	public int getGroup() {
		return group;
	}

	public PlacedShape getPlacedShape() {
		return new PlacedShape(pos, shape);
	}

	PlacedShape getPlacedShapeVel() {
		return new PlacedShape(vel, shapeVel);
	}

	public double getRemainingTime() {
		return remainingTime;
	}

	public void setPos(Vec2d pos) {
		if(pos == null) throw new NullPointerException();
		this.pos = pos;
	}

	public void setVel(Vec2d vel) {
		if(vel == null) throw new NullPointerException();
		this.vel = vel;
	}

	public void setShape(Shape shape) {
		if(shape.isCircle() != this.shape.isCircle()) throw new IllegalArgumentException("incorrect shape type");
		this.shape = shape;
	}

	public void setShapeVel(Shape shapeVel) {
		if(shapeVel.isCircle() != this.shapeVel.isCircle()) throw new IllegalArgumentException("incorrect shape type");
		this.shapeVel = shapeVel;
	}

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
	 * @param group Group that this HitBox should belong to.
	 */
	public void setGroup(int group) {
		this.group = group;
		interactivityChange();
	}

	/**
	 * Call this method if there is a change in the return values
	 * of {@link InteractTester#canInteract(HitBox, HitBox)} involving
	 * the HitBox.
	 * This will prompt searching for potential collisions between
	 * previously uninteractable HitBoxes.
	 * Separate events will not be generated for HitBoxes that overlap and used
	 * to interact with each other but no longer do because of this call (
	 * TODO consider changing this policy?
	 * )
	 */
	public void interactivityChange() {
		this.interactivityChange = true;
	}

	public void setRemainingTime(double remainingTime) {
		if(remainingTime < 0.0) throw new IllegalArgumentException("remainingTime must be non-negative");
		this.remainingTime = remainingTime;
	}

//	HitboxState advance(double timeDelta) {
//		if(timeDelta == 0.0) return this;
//		HitboxState newState = this.clone();
//		newState.setPos(pos.add(vel.scale(timeDelta)));
//		newState.setShape(shape.add(shapeVel.scale(timeDelta)));
//		return newState;
//	}

	HitboxState advance(double originalTime, double newTime) {
		if(originalTime == newTime) return this;
		if(originalTime > newTime) throw new IllegalArgumentException();
		HitboxState newState = this.clone();
		double delta = newTime - originalTime;
		newState.setPos(pos.add(vel.scale(delta)));
		newState.setShape(shape.add(shapeVel.scale(delta)));
		double endTime = originalTime + remainingTime;
		if(endTime < newTime) throw new IllegalStateException();
		newState.remainingTime = endTime - newTime;
		return newState;
	}

	void reverseVels() {
		setVel(getVel().scale(-1.0));
		setShapeVel(getShapeVel().scale(-1.0));
	}

	@Override
	public HitboxState clone() {
		try {
			return (HitboxState) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	double getMaxEdgeVel() {
		PlacedShape vel = getPlacedShapeVel();
		double result = 0.0;
		result = Math.max(result, Math.abs(vel.getLeft()));
		result = Math.max(result, Math.abs(vel.getBottom()));
		result = Math.max(result, Math.abs(vel.getRight()));
		result = Math.max(result, Math.abs(vel.getTop()));
		return result;
	}

	PlacedShape getBoundingBox() {
		PlacedShape startShape = getPlacedShape();
		PlacedShape endShape = startShape.add(getPlacedShapeVel().scale(getRemainingTime()));
		return ShapeUtil.getBoundingBox(startShape, endShape);
	}

	boolean isSame(HitboxState o) {
		if (group != o.group) return false;
		if (interactivityChange != o.interactivityChange) return false;
		if (Double.compare(o.remainingTime, remainingTime) != 0) return false;
		if (!pos.equals(o.pos)) return false;
		if (!vel.equals(o.vel)) return false;
		if (!shape.equals(o.shape)) return false;
		return shapeVel.equals(o.shapeVel);
	}
}
