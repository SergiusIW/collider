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

package com.matthewmichelotti.collider.demos;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.HBPositioned;
import com.matthewmichelotti.collider.HBRect;

/**
 * A single entity in the game.
 * Each Component is associated with a single
 * {@link com.matthewmichelotti.collider.HitBox HitBox}.
 * Displays that HitBox in a given color, and
 * may also display a message.
 * Subclasses implement functionality for handling
 * collisions and separations with other Components.
 * @author Matthew Michelotti
 */
public abstract class Component implements Comparable<Component> {
	private static int NEXT_ID = 0;
	private final int id;
	private HBPositioned hitBox;

	protected Component(HBPositioned hitBox) {
		if(hitBox == null) throw new IllegalArgumentException();
		this.hitBox = hitBox;
		hitBox.setBelongingObject(this);
		id = NEXT_ID++;
		Game.engine.addComp(this);
	}

	protected final void delete() {
		if(hitBox == null) return;
		Game.engine.removeComp(this);
		hitBox.free();
		hitBox = null;
	}
	
	protected final boolean isInBounds() {return Game.engine.isInBounds(hitBox);}
	public final boolean isDeleted() {return hitBox == null;}
	public final int getId() {return id;}
	
	public final HBPositioned hitBox() {return hitBox;}
	public final HBCircle circ() {return (HBCircle)hitBox;}
	public final HBRect rect() {return (HBRect)hitBox;}
	public final boolean isRect() {return hitBox instanceof HBRect;}
	public final boolean isCirc() {return hitBox instanceof HBCircle;}
	
	public abstract void onCollide(Component other);
	public abstract void onSeparate(Component other);
	public abstract boolean canInteract(Component other);
	public abstract boolean interactsWithBullets();
	public abstract Color getColor();
	public String getMessage() {return null;}
	
	@Override public final int compareTo(Component o) {
		return id - o.id;
	}
}
