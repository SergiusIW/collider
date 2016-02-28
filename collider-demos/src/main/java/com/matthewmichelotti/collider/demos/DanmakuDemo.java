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

package com.matthewmichelotti.collider.demos;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.GroupSet;
import com.matthewmichelotti.collider.HitboxState;
import com.matthewmichelotti.collider.demos.util.GameElem;
import com.matthewmichelotti.collider.demos.util.GameEngine;
import com.matthewmichelotti.collider.demos.util.MousePosListener;
import com.matthewmichelotti.collider.demos.util.VoidFunction;
import com.matthewmichelotti.collider.geom.Shape;
import com.matthewmichelotti.collider.geom.Vec2d;

public class DanmakuDemo {
	enum Group { DEFAULT, BULLET }

	final static GroupSet BULLET_SET = new GroupSet(Group.BULLET.ordinal());
	final static GroupSet DEFAULT_SET = new GroupSet(Group.DEFAULT.ordinal());

	static class Bounds extends GameElem {
		Bounds(GameEngine engine) {
			init(engine, new HitboxState(GameEngine.BOUNDS), BULLET_SET, null);
		}

		@Override public void onCollide(GameElem other) { }
		@Override public void onSeparate(GameElem other) { }
		@Override public boolean canInteract(GameElem other) { return false; }
	}

	static class Bullet extends GameElem {
		final static Color COLOR = new Color(1.0f, 1.0f, 0.1f, 1.0f);

		final boolean fromEnemy;

		Bullet(GameEngine engine, boolean fromEnemy, Vec2d pos, Vec2d vel) {
			this.fromEnemy = fromEnemy;
			init(engine, makeHitbox(pos, vel), DEFAULT_SET, COLOR);
		}

		static HitboxState makeHitbox(Vec2d pos, Vec2d vel) {
			HitboxState hitbox = new HitboxState(pos, Shape.newCircle(10.0));
			hitbox.setVel(vel);
			hitbox.setGroup(Group.BULLET.ordinal());
			return hitbox;
		}

		@Override public boolean canInteract(GameElem o) {
			if(o instanceof Bounds) return true;
			if(o instanceof Target) return ((Target)o).isEnemy() != fromEnemy;
			return false;
		}

		@Override
		public void onCollide(GameElem other) {
			if(other instanceof Target) {
				delete();
				((Target)other).hit();
			}
		}

		@Override public void onSeparate(GameElem other) {
			if(other instanceof Bounds) delete();
		}
	}

	interface Target {
		boolean isEnemy();
		void hit();
	}

	static class EnemyShip extends GameElem implements Target {
		final static Color COLOR = new Color(1.0f, 0.25f, 0.07f, 1.0f);

		private double baseAngle = .1;

		EnemyShip(final GameEngine engine, Vec2d pos) {
			init(engine, new HitboxState(pos, Shape.newCircle(25.0)), BULLET_SET, COLOR);
			makeBullets();
		}

		void makeBullets() {
			final int numPoints = 170;
			Vec2d pos = getPos();
			for(int i = 0; i < numPoints; i++) {
				if(i % 10 < 6) continue;
				double angle = baseAngle + 2*Math.PI*i/(double)numPoints;
				Vec2d dir = new Vec2d(Math.cos(angle), Math.sin(angle));
				new Bullet(getEngine(), true, pos.add(dir.scale(17.5)), dir.scale(100));
			}
			baseAngle = (baseAngle + .24) % (2*Math.PI);
			addEvent(getTime() + .9, new VoidFunction() {
				@Override public void invoke() { makeBullets(); }
			});
		}

		@Override public void onCollide(GameElem other) { }
		@Override public void onSeparate(GameElem other) { }
		@Override public boolean canInteract(GameElem other) { return false; }

		@Override public boolean isEnemy() { return true; }
		@Override public void hit() { flash(); }
	}

	static class PlayerShip extends GameElem implements Target, MousePosListener {
		final static Color COLOR = new Color(0.1f, 0.15f, 1.0f, 1.0f);

		PlayerShip(GameEngine engine, Vec2d pos) {
			init(engine, new HitboxState(pos, Shape.newCircle(10.0)), BULLET_SET, COLOR);
			makeBullets();
		}

		void makeBullets() {
			Vec2d pos = getPos();
			new Bullet(getEngine(), false, pos.add(new Vec2d(10, 10)), new Vec2d(0, 1000));
			new Bullet(getEngine(), false, pos.add(new Vec2d(-10, 10)), new Vec2d(0, 1000));
			addEvent(getTime() + .13, new VoidFunction() {
				@Override public void invoke() { makeBullets(); }
			});
		}

		@Override
		public void updateMousePos(Vec2d mousePos, final double endTime) {
			final double speed = 600;

			if(endTime <= getTime()) return;
			HitboxState state = getHitbox().getState();
			state.setRemainingTime(endTime - getTime());
			Vec2d pos = state.getPos();
			Vec2d delta = mousePos.sub(pos);
			final double arriveTime = getTime() + delta.getLength()/speed;
			if(arriveTime <= getTime()) {
				state.setVel(new Vec2d(0.0, 0.0));
				getHitbox().changeState(state);
				return;
			}
			state.setVel(delta.normalize().scale(speed));
			if(endTime <= arriveTime) {
				getHitbox().changeState(state);
				return;
			}
			state.setRemainingTime(arriveTime - getTime());
			getHitbox().changeState(state);
			addEvent(arriveTime, new VoidFunction() {
				@Override public void invoke() {
					HitboxState state = getHitbox().getState();
					state.setVel(new Vec2d(0.0, 0.0));
					state.setRemainingTime(endTime - arriveTime);
					getHitbox().changeState(state);
				}
			});
		}

		@Override public void onCollide(GameElem other) { }
		@Override public void onSeparate(GameElem other) { }
		@Override public boolean canInteract(GameElem other) { return false; }

		@Override public boolean isEnemy() { return false; }
		@Override public void hit() { flash(); }
	}


	public static void main(String[] args) {
		GameEngine engine = new GameEngine(Color.BLACK);

		new Bounds(engine);

		new EnemyShip(engine, new Vec2d(640, 650));
		new EnemyShip(engine, new Vec2d(340, 500));
		new EnemyShip(engine, new Vec2d(940, 500));

		PlayerShip player = new PlayerShip(engine, GameEngine.BOUNDS.getPos());

		engine.setMouseListener(player);
		engine.run();
	}
}
