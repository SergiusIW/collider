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

package com.matthewmichelotti.collider.demos.util;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.GroupSet;
import com.matthewmichelotti.collider.Hitbox;
import com.matthewmichelotti.collider.HitboxState;
import com.matthewmichelotti.collider.geom.Vec2d;
import com.matthewmichelotti.collider.processes.TimedFunction;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GameElem implements Comparable<GameElem> {
	private enum State { UNINITIALIZED, INITIALIZED, DELETED }
	private final static double FLASH_DURATION = .17;
	private final static AtomicInteger NEXT_ID = new AtomicInteger(0);

	private GameEngine engine;
	private Hitbox hitbox;
	private Color color;
	private int id;
	private ArrayList<TimedFunction> events = new ArrayList<>();
	private double flashTime = Double.NEGATIVE_INFINITY;
	private GroupSet interactGroups;
	private State state = State.UNINITIALIZED;

	protected GameElem() { }

	protected void init(GameEngine engine, HitboxState hitbox, GroupSet interactGroups, Color color) {
		if(engine == null || hitbox == null || interactGroups == null) throw new NullPointerException();
		if(state != State.UNINITIALIZED) throw new IllegalStateException();

		this.engine = engine;
		this.interactGroups = interactGroups;
		this.hitbox = engine.newHitbox(hitbox, this);
		this.color = color;
		this.id = NEXT_ID.getAndIncrement();
		engine.addElem(this);

		state = State.INITIALIZED;
	}

	protected final void delete() {
		if(state != State.INITIALIZED) throw new IllegalStateException();
		clearEvents();
		getHitbox().delete();
		engine.deleteElem(this);
		engine = null;
		state = State.DELETED;
	}

	final boolean isDeleted() {
		return engine == null;
	}

	public abstract void onCollide(GameElem other);
	public abstract void onSeparate(GameElem other);
	public abstract boolean canInteract(GameElem other);

	final GroupSet getInteractGroups() {
		return interactGroups;
	}

	final Color getColor() {
		double timeDiff = getTime() - flashTime;
		if(timeDiff >= FLASH_DURATION) return color;
		double alpha = Math.cos(.5*Math.PI*timeDiff/FLASH_DURATION);
		if(alpha <= 0.0) return color;
		Color blendColor = new Color(color);
		return blendColor.lerp(Color.WHITE, (float)alpha);
	}

	protected final Hitbox getHitbox() {
		if(state != State.INITIALIZED) throw new IllegalStateException();
		return hitbox;
	}

	public final Vec2d getPos() {
		if(state != State.INITIALIZED) throw new IllegalStateException();
		return hitbox.getState().getPos();
	}

	protected String getMessage() {
		return null;
	}

	protected final double getTime() {
		if(state != State.INITIALIZED) throw new IllegalStateException();
		return engine.getTime();
	}

	protected GameEngine getEngine() {
		if(state != State.INITIALIZED) throw new IllegalStateException();
		return engine;
	}

	protected final void flash() {
		if(state != State.INITIALIZED) throw new IllegalStateException();
		flashTime = getTime();
	}

	protected final void addEvent(double time, final VoidFunction event) {
		if(state != State.INITIALIZED) throw new IllegalStateException();
		TimedFunction timedFunction = new TimedFunction(time) {
			@Override public void invoke() {
				events.remove(this);
				event.invoke();
			}
		};
		events.add(timedFunction);
		engine.addEvent(timedFunction);
	}

	protected final void clearEvents() {
		if(state != State.INITIALIZED) throw new IllegalStateException();
		for(TimedFunction event : events) {
			engine.removeEvent(event);
		}
		events.clear();
	}

	@Override public final int compareTo(GameElem o) {
		return id - o.id;
	}
}
