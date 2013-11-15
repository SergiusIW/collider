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

package com.matthewmichelotti.collider.demos.comps;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.GameEngine;
import com.matthewmichelotti.collider.demos.PosAndVel;

/**
 * A bullet primarily used in the danmaku scenarios.
 * @author Matthew Michelotti
 */
public class CBullet extends Component {
	public final static double DIAM = 10;
	public final static Color COLOR = new Color(1.0f, 1.0f, 0.1f, 1.0f);
	
	private boolean fromEnemy;
	
	public CBullet(boolean fromEnemy, PosAndVel pos) {
		super(Game.engine.makeCircle());
		this.fromEnemy = fromEnemy;
		HBCircle circ = circ();
		circ.setPos(pos.x, pos.y);
		circ.setVel(pos.vx, pos.vy);
		circ.setDiam(DIAM);
		circ.setGroup(GameEngine.GROUP_BULLET);
		circ.setEndTime(Double.POSITIVE_INFINITY);
		if(!isInBounds()) throw new RuntimeException();
	}

	@Override public boolean canInteract(Component o) {
		if(o instanceof CBounds) return true;
		if(o instanceof CTarget) return ((CTarget)o).isEnemy() != fromEnemy;
		return false;
	}
	@Override public boolean interactsWithBullets() {return false;}

	@Override
	public void onCollide(Component other) {
		if(other instanceof CBounds) return;
		new CCircFade(circ(), COLOR, .15, hitBox().getNormal(other.hitBox()));
		delete();
		((CTarget)other).hit();
	}

	@Override public void onSeparate(Component other) {
		if(other instanceof CBounds) delete();
	}

	@Override public Color getColor() {return COLOR;}
}
