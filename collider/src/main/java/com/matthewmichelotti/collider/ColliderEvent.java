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
 * A collision or seperation event between two HitBoxes.
 *
 * @author Matthew Michelotti
 */
public final class ColliderEvent {
	private Hitbox a, b;
	private boolean collided;

	public static ColliderEvent newCollide(Hitbox a, Hitbox b) {
		return new ColliderEvent(a, b, true);
	}

	public static ColliderEvent newSeparation(Hitbox a, Hitbox b) {
		return new ColliderEvent(a, b, false);
	}
	
	ColliderEvent(Hitbox a, Hitbox b, boolean collided) {
		if(a == null || b == null) throw new RuntimeException();
		this.a = a;
		this.b = b;
		this.collided = collided;
	}

	public ColliderEvent swap() {
		return new ColliderEvent(b, a, collided);
	}
	
	/**
	 * Returns the time of the event.
	 * This is the same as calling {@link Collider#getTime()}.
	 * @return The time of the event.
	 */
	public double getTime() {return a.getTime();}
	
	/**
	 * Returns true if this is a collision event.
	 * @return True if this is a collision event.
	 */
	public boolean isCollision() {return collided;}
	
	/**
	 * Returns true if this is a separation event.
	 * @return True if this is a separation event.
	 */
	public boolean isSeparation() {return !collided;}
	
	/**
	 * Returns the first Hitbox involved in this event.
	 * @return The first Hitbox involved in this event.
	 */
	public Hitbox getFirstHitbox() {return a;}
	
	/**
	 * Returns the second Hitbox involved in this event.
	 * @return The second Hitbox involved in this event.
	 */
	public Hitbox getSecondHitbox() {return b;}

	public Object getFirst() {return a.getOwner();}
	public Object getSecond() {return b.getOwner();}
	
	/**
	 * Given one of the HitBoxes involved in this event, this is a convenience method
	 * for obtaining the other involved Hitbox.
	 * Like using <code>(hitbox == getFirst()) ? getSecond() : getFirst()</code>.
	 * @param hitbox One of the HitBoxes involved in this event.
	 * @return The other Hitbox involved in this event.
	 */
	public Hitbox getOther(Hitbox hitbox) {
		if(a == hitbox) return b;
		else if(b == hitbox) return a;
		else throw new IllegalArgumentException();
	}
}
