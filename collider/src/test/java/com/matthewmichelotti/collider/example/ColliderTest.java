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

package com.matthewmichelotti.collider.example;

import static org.junit.Assert.*;

import com.matthewmichelotti.collider.*;
import com.matthewmichelotti.collider.geom.Shape;
import com.matthewmichelotti.collider.geom.Vec2d;
import org.junit.Test;

public class ColliderTest {

	@Test
	public void collisionAndSeparationTest() {
		InteractTester interactTester = new InteractTester() {
			@Override public boolean canInteract(Hitbox a, Hitbox b) { return true; }
			@Override public GroupSet getInteractGroups(Hitbox hitBox) { return new GroupSet(0); }
		};
		Collider collider = new Collider(interactTester, 2.0, .1);

		HitboxState state = new HitboxState(new Vec2d(10.0, 0.0), Shape.newRect(2.0, 2.0));
		state.setVel(new Vec2d(-1.0, 0.0));
		Hitbox rect = collider.newHitbox(state, null);

		state = new HitboxState(new Vec2d(0.0, 0.0), Shape.newCircle(2.0));
		state.setVel(new Vec2d(1.0, 0.0));
		Hitbox circle = collider.newHitbox(state, null);

		ColliderEvent event = collider.advance(100.0);
		if(event.getFirstHitbox() != rect) event = event.swap();
		assertEquals("unexpected event time", 4.0, event.getTime(), .0001);
		assertTrue("event should have rect hitbox", event.getFirstHitbox() == rect);
		assertTrue("event should have circle hitbox", event.getSecondHitbox() == circle);
		assertTrue("event should be a collision event", event.isCollision());

		event = collider.advance(100.0);
		if(event.getFirstHitbox() != rect) event = event.swap();
		assertEquals("unexpected event time", 6.05, event.getTime(), .0001);
		assertTrue("event should have rect hitbox", event.getFirstHitbox() == rect);
		assertTrue("event should have circle hitbox", event.getSecondHitbox() == circle);
		assertTrue("event should be a separation event", event.isSeparation());
	}
}
