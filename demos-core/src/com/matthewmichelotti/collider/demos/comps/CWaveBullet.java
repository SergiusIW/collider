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
import com.matthewmichelotti.collider.HBPositioned;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.GameEngine;
import com.matthewmichelotti.collider.demos.PosAndVel;
import com.matthewmichelotti.collider.demos.WaveUpdater;

/**
 * A circular/rectangular bullet that moves in a wave-like pattern.
 * @author Matthew Michelotti
 */
public class CWaveBullet extends Component {
	public CWaveBullet(PosAndVel pos, boolean isRect) {
		super(isRect ? Game.engine.makeRect() : Game.engine.makeCircle());
		if(isRect) rect().setDims(40.0, 20.0);
		else circ().setDiam(CMorphBullet.DIAM);
		
		final HBPositioned hitBox = hitBox();
		hitBox.setGroup(GameEngine.GROUP_BULLET);
		
		final double startX = pos.x;
		final double startY = pos.y;
		final double baseVX = pos.vx;
		final double baseVY = pos.vy;
		final double startTime = Game.engine.getTime();
		double velMag = Math.sqrt(baseVX*baseVX + baseVY*baseVY);
		if(velMag <= 0.0) throw new RuntimeException();
		final double perpUX = -baseVY/velMag;
		final double perpUY = baseVX/velMag;
		
		new WaveUpdater(0, 40, 2) {
			@Override protected boolean isValid() {return !isDeleted();}
			@Override protected void update(double value, double vel, double endTime) {
				double timeDiff = Game.engine.getTime() - startTime;
				hitBox.setX(startX + baseVX*timeDiff + value*perpUX);
				hitBox.setY(startY + baseVY*timeDiff + value*perpUY);
				hitBox.setVel(baseVX + vel*perpUX, baseVY + vel*perpUY);
				hitBox.finalize(endTime);
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
