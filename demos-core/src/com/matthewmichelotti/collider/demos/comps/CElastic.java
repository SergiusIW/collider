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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.Normal;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.Geom;

/**
 * A circular object that undergoes perfectly elastic collisions.
 * @author Matthew Michelotti
 */
public class CElastic extends Component {
	private final static Color[] COLORS = new Color[] {
		new Color(1.0f, 1.0f, 0.0f, 1.0f),
		new Color(0.0f, 0.0f, 1.0f, 1.0f),
		new Color(1.0f, 0.0f, 0.0f, 1.0f),
		new Color(0.5f, 0.0f, 0.8f, 1.0f),
		new Color(1.0f, 0.5f, 0.0f, 1.0f),
		new Color(0.0f, 0.35f, 0.0f, 1.0f),
		new Color(0.6f, 0.0f, 0.0f, 1.0f),
		new Color(0.0f, 0.0f, 0.0f, 1.0f)
	};
	
	public final static int NUM_COLORS = 2*COLORS.length - 1;
	
	private Color color;
	private Array<Component> overlaps = new Array<Component>();

	public CElastic(double x, double y, double diam, double maxVel, int colorI) {
		super(Game.engine.makeCircle());
		this.color = COLORS[colorI % COLORS.length];
		HBCircle circ = circ();
		circ.setPos(x, y);
		circ.setDiam(diam);
		circ.setVel(2*maxVel*(.5 - Math.random()), 2*maxVel*(.5 - Math.random()));
		circ.setEndTime(Double.POSITIVE_INFINITY);
	}
	
	@Override public boolean canInteract(Component other) {
		return other instanceof CElastic || other instanceof CTarget;
	}
	@Override public boolean interactsWithBullets() {return false;}

	@Override
	public void onCollide(Component other) {
		CElastic otherCE = null;
		if(other instanceof CElastic) otherCE = (CElastic)other;
		if(otherCE != null && getId() > otherCE.getId()) return; 
		boolean success = elasticCollision(other);
		overlaps.add(other);
		if(otherCE != null) otherCE.overlaps.add(this);
		if(!success) return;
		if(overlaps.size == 1 && (otherCE == null || otherCE.overlaps.size == 1)) return;
		ObjectSet<Component> visitedSet = new ObjectSet<Component>();
		for(int i = 0; i < 100; i++) {
			if(!collideIteration(visitedSet)) {
				if(i >= 15) {
					System.out.println("WARNING: chained elastic collision took "
							+ (i + 1) + " iterations");
				}
				return;
			}
			visitedSet.clear();
		}
		throw new RuntimeException("chained elastic collision not converging");
	}

	@Override
	public void onSeparate(Component other) {
		overlaps.removeValue(other, true);
	}

	@Override public Color getColor() {return color;}
	
	private boolean elasticCollision(Component other) {
		if(other instanceof CTarget) {
			Normal normal = other.hitBox().getNormal(hitBox());
			boolean success = elasticCollision(normal.getUnitX(), normal.getUnitY(), 0, 0,
					Double.POSITIVE_INFINITY);
			if(success) ((CTarget)other).hit();
			return success;
		}
		else if(other instanceof CElastic) {
			HBCircle circA = circ();
			HBCircle circB = other.circ();
			Normal n = circB.getNormal(circA);
			double v1x = circA.getVelX();
			double v1y = circA.getVelY();
			double v2x = circB.getVelX();
			double v2y = circB.getVelY();
			boolean result = elasticCollision(
					n.getUnitX(), n.getUnitY(), v2x, v2y, Geom.area(circB));
			result |= ((CElastic)other).elasticCollision(
					-n.getUnitX(), -n.getUnitY(), v1x, v1y, Geom.area(circA));
			
//			new CCircFade(circB.getX() + .5*n.getUnitX()*circB.getDiam(),
//					circB.getY() + .5*n.getUnitY()*circB.getDiam(),
//					5, Color.WHITE, .2, null);
			
			return result;
		}
		throw new RuntimeException();
	}
	
	private boolean elasticCollision(double nx, double ny, double v2x, double v2y, double m2) {
		HBCircle circ = circ();
		double m1 = Geom.area(circ);
		double v1x = circ.getVelX();
		double v1y = circ.getVelY();

		double normalRelVelComp = nx*(v2x - v1x) + ny*(v2y - v1y);
		if(normalRelVelComp <= 0.00001) return false;
		double massRatio;
		if(m2 == Double.POSITIVE_INFINITY) massRatio = 1.0;
		else massRatio = m2/(m1 + m2);
		double term = 2*massRatio*normalRelVelComp;
		circ.setVel(v1x + term*nx, v1y + term*ny);
		circ.setEndTime(Double.POSITIVE_INFINITY);
		return true;
	}
	
	private boolean collideIteration(ObjectSet<Component> visitedSet) {
		visitedSet.add(this);
		boolean changed = false;
		for(Component c : overlaps) {
			if(visitedSet.contains(c)) continue;
			changed |= elasticCollision(c);
		}
		for(Component c : overlaps) {
			if(c instanceof CElastic) {
				if(visitedSet.contains(c)) continue;
				changed |= ((CElastic)c).collideIteration(visitedSet);
			}
		}
		return changed;
	}
}
