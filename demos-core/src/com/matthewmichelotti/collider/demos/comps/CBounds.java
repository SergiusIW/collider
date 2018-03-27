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
import com.matthewmichelotti.collider.HBRect;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.GameEngine;

/**
 * A box that extends slightly past the bounds of the screen.
 * @author Matthew Michelotti
 */
public class CBounds extends Component {
	public final static int PAD = 80;

	public CBounds() {
		super(Game.engine.makeRect());
		HBRect rect = rect();
		rect.setPos(.5*GameEngine.SCREEN_WIDTH, .5*GameEngine.SCREEN_HEIGHT);
		rect.setDims(GameEngine.SCREEN_WIDTH + 2*PAD, GameEngine.SCREEN_HEIGHT + 2*PAD);
		rect.commit(Double.POSITIVE_INFINITY);
	}

	@Override public void onCollide(Component other) {}
	@Override public void onSeparate(Component other) {}
	@Override public boolean canInteract(Component other) {return false;}
	@Override public boolean interactsWithBullets() {return true;}
	@Override public Color getColor() {return null;}
}
