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
import com.matthewmichelotti.collider.HBRect;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.GameEngine;
import com.matthewmichelotti.collider.demos.Geom;

/**
 * A circular particle that can merge (aka coagulate) with other particles.
 * @author Matthew Michelotti
 */
public class CCoagParticle extends Component {
	public final static Color COLOR = new Color(0.1f, 0.4f, 1.0f, 1.0f);
	
	public CCoagParticle() {
		super(Game.engine.makeCircle());
		double x = -CBounds.PAD + Math.random()*(GameEngine.SCREEN_WIDTH + 2*CBounds.PAD);
		double y = -CBounds.PAD + Math.random()*(GameEngine.SCREEN_HEIGHT + 2*CBounds.PAD);
		HBCircle circ = circ();
		circ.setPos(x, y);
		circ.setVel(1000*(.5 - Math.random()), 1000*(.5 - Math.random()));
		circ.setDiam(3);
		circ.setEndTime(Double.POSITIVE_INFINITY);
		if(!isInBounds()) throw new RuntimeException();
	}
	@Override public boolean canInteract(Component other) {
		return other instanceof CCoagParticle || other instanceof CBounds;
	}
	@Override public boolean interactsWithBullets() {return false;}

	@Override public void onCollide(Component other) {
		if(isDeleted()) return;
		if(other instanceof CCoagParticle) {
			HBCircle circ = circ();
			HBCircle oCirc = other.circ();
			double area = Geom.area(circ);
			double oArea = Geom.area(oCirc);
			double newArea = area + oArea;
			circ.setDiam(Geom.area2Diam(newArea));
			circ.setVelX(circ.getVelX()*area/newArea + oCirc.getVelX()*oArea/newArea);
			circ.setVelY(circ.getVelY()*area/newArea + oCirc.getVelY()*oArea/newArea);
			circ.setX(circ.getX()*area/newArea + oCirc.getX()*oArea/newArea);
			circ.setY(circ.getY()*area/newArea + oCirc.getY()*oArea/newArea);
			circ.setEndTime(Double.POSITIVE_INFINITY);
			((CCoagParticle)other).delete();
			if(circ.getDiam() > 2*CBounds.PAD) {
				new CCircFade(circ, COLOR, .3, null);
				delete();
			}
		}
	}
	@Override public void onSeparate(Component other) {
		if(other instanceof CBounds) {
			HBCircle circ = circ();
			HBRect rect = other.rect();
			double r = rect.getX() + .5*rect.getWidth();
			double t = rect.getY() + .5*rect.getHeight();
			double l = rect.getX() - .5*rect.getWidth();
			double b = rect.getY() - .5*rect.getHeight();
			if(circ.getX() < l) circ.setX(r);
			else if(circ.getX() > r) circ.setX(l);
			if(circ.getY() < b) circ.setY(t);
			else if(circ.getY() > t) circ.setY(b);
			circ.setEndTime(Double.POSITIVE_INFINITY);
			if(!isInBounds()) throw new RuntimeException();
		}
	}
	@Override public Color getColor() {return COLOR;}
}
