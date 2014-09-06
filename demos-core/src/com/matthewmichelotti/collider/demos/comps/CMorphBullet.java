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

package com.matthewmichelotti.collider.demos.comps;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.GameEngine;
import com.matthewmichelotti.collider.demos.PosAndVel;
import com.matthewmichelotti.collider.demos.WaveUpdater;

/**
 * A bullet that resizes its diameter in a wave-like fashion.
 * @author Matthew Michelotti
 */
public class CMorphBullet extends Component {
	public final static double DIAM = 40;
	
	public CMorphBullet(PosAndVel pos) {
		super(Game.engine.makeCircle());
		final HBCircle circ = circ();
		circ.setPos(pos.x, pos.y);
		circ.setVel(pos.vx, pos.vy);
		circ.setGroup(GameEngine.GROUP_BULLET);
		new WaveUpdater(DIAM, -35, 2) {
			@Override protected boolean isValid() {return !isDeleted();}
			@Override protected void update(double value, double vel, double endTime) {
				circ.setDiam(value);
				circ.setVelDiam(vel);
				circ.finalize(endTime);
			}
		};
		if(!isInBounds()) throw new RuntimeException();
	}

	@Override public boolean canInteract(Component o) {
		if(o instanceof CBounds) return true;
		if(o instanceof CTarget) return !((CTarget)o).isEnemy();
		return false;
	}
	@Override public boolean interactsWithBullets() {return false;}

	@Override
	public void onCollide(Component other) {
		if(!(other instanceof CTarget)) return;
		new CCircFade(circ(), CBullet.COLOR, .15, hitBox().getNormal(other.hitBox()));
		delete();
		((CTarget)other).hit();
	}

	@Override public void onSeparate(Component other) {
		if(other instanceof CBounds) delete();
	}

	@Override public Color getColor() {return CBullet.COLOR;}
}
