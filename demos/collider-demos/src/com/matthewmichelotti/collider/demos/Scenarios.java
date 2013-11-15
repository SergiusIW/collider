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

package com.matthewmichelotti.collider.demos;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.HBRect;
import com.matthewmichelotti.collider.demos.comps.CBounds;
import com.matthewmichelotti.collider.demos.comps.CCoagParticle;
import com.matthewmichelotti.collider.demos.comps.CElastic;
import com.matthewmichelotti.collider.demos.comps.CEnemyShip;
import com.matthewmichelotti.collider.demos.comps.CIndicator;
import com.matthewmichelotti.collider.demos.comps.CMorphEnemyShip;
import com.matthewmichelotti.collider.demos.comps.CMorphStickySpinner;
import com.matthewmichelotti.collider.demos.comps.CPlayerShip;
import com.matthewmichelotti.collider.demos.comps.CStickyGun;
import com.matthewmichelotti.collider.demos.comps.CTarget;
import com.matthewmichelotti.collider.demos.comps.CText;
import com.matthewmichelotti.collider.demos.comps.CVarietyGun;

/**
 * Contains static methods for initializing each scenario.
 * @author Matthew Michelotti
 */
public class Scenarios {
	public final static int NUM_SCENARIOS = 11;
	
	private Scenarios() {}

	public static void initScenario(int i) {
		Game.engine.clear();
		switch(i) {
		case 0: initInstructions(); break;
		case 1: initDanmaku1(); break;
		case 2: initDanmaku2(); break;
		case 3: initPool1(); break;
		case 4: initPool2(3); break;
		case 5: initPool2(40); break;
		case 6: initFractal(11); break;
		case 7: initFractal(3); break;
		case 8: initCoagulation(); break;
		case 9: initSpinners(); break;
		case 10: initIndicators(); break;
		default: throw new RuntimeException();
		}
	}
	
	private static void initInstructions() {
		new CText("click screen each time you want to proceed to next demo", 640, 360);
	}
	
	private static void initDanmaku1() {
		Game.engine.setBG(new Color(0.0f, 0.02f, 0.05f, 1.0f));
		new CBounds();
		new CPlayerShip();
		new CEnemyShip(640, 650);
		new CEnemyShip(340, 500);
		new CEnemyShip(940, 500);
	}
	
	private static void initDanmaku2() {
		Game.engine.setBG(new Color(0.0f, 0.02f, 0.05f, 1.0f));
		new CBounds();
		new CPlayerShip();
		new CMorphEnemyShip(640, 650);
		new CMorphEnemyShip(340, 500);
		new CMorphEnemyShip(940, 500);
	}
	
	private static void initCoagulation() {
		Game.engine.setBG(new Color(.95f, 0.98f, 1.0f, 1.0f));
		new CBounds();
		Game.engine.addEvent(new FunctionEvent() {
			@Override public void resolve() {
				for(int i = 0; i < 150; i++) {
					new CCoagParticle();
				}
				setTime(getTime() + .1);
				Game.engine.addEvent(this);
			}
		});
	}
	
	private static void initPool1() {
		makePoolBorder();
		for(int i = 0; i < CElastic.NUM_COLORS; i++) { //40 or 600
			double x = 56 + Math.random()*(1280 - 2*56);
			double y = 56 + Math.random()*(720 - 2*56);
			new CElastic(x, y, 40, 500, i);
		}
	}
	
	private static void initPool2(int numSets) {
		makePoolBorder();
		for(int i = 0; i < numSets*CElastic.NUM_COLORS; i++) { //40 or 600
			double x = 56 + Math.random()*(1280 - 2*56);
			double y = 56 + Math.random()*(720 - 2*56);
			double diam = 1.15*(5 + Math.min(25, -Math.log(Math.random())*9));
			new CElastic(x, y, diam, 250, i % CElastic.NUM_COLORS);
		}
	}
	
	private static void initFractal(double shotDiam) {
		Game.engine.setBG(new Color(0.0f, 0.02f, 0.05f, 1.0f));
		new CBounds();
		HBRect rect = Game.engine.makeRect();
		rect.setPos(640, 360);
		rect.setDims(80);
		rect.setEndTime(Double.POSITIVE_INFINITY);
		new CTarget(rect, CPlayerShip.COLOR);
		for(int i = 0; i < 8; i++) {
			double angle = 2*Math.PI*i/8.0;
			double cos = Math.cos(angle);
			double sin = Math.sin(angle);
			new CStickyGun(640 + cos*350, 360 + sin*350, angle + Math.PI, shotDiam);
		}
	}
	
	private static void initSpinners() {
		Game.engine.setBG(new Color(0.0f, 0.02f, 0.05f, 1.0f));
		new CBounds();
		new CMorphStickySpinner(640 - 200, 360 - 100);
		new CMorphStickySpinner(640 + 200, 360 + 100);
	}
	
	private static void initIndicators() {
		Game.engine.setBG(new Color(0.0f, 0.02f, 0.05f, 1.0f));
		new CBounds();
		new CVarietyGun(50, 50, .25*Math.PI);
		new CVarietyGun(1280 - 50, 50, .75*Math.PI);
		new CVarietyGun(50, 720 - 50, -.25*Math.PI);
		new CVarietyGun(1280 - 50, 720 - 50, -.75*Math.PI);
		new CIndicator(640 - 300, 360, true);
		new CIndicator(640 + 300, 360, false);
	}
	
	private static void makePoolBorder() {
		Game.engine.setBG(new Color(0.0f, 0.7f, 0.2f, 1.0f));
		Color color = new Color(0.4f, 0.25f, 0.1f, 1.0f);
		
		for(int x = 0; x < 1280; x += 80) {
			HBRect rect = Game.engine.makeRect();
			rect.setPos(x + 40, 20);
			rect.setDims(80, 40);
			rect.setEndTime(Double.POSITIVE_INFINITY);
			new CTarget(rect, color);
			rect = Game.engine.makeRect();
			rect.setPos(x + 40, 720 - 20);
			rect.setDims(80, 40);
			rect.setEndTime(Double.POSITIVE_INFINITY);
			new CTarget(rect, color);
		}
		
		for(int y = 40; y < 720 - 40; y += 80) {
			HBRect rect = Game.engine.makeRect();
			rect.setPos(20, y + 40);
			rect.setDims(40, 80);
			rect.setEndTime(Double.POSITIVE_INFINITY);
			new CTarget(rect, color);
			rect = Game.engine.makeRect();
			rect.setPos(1280 - 20, y + 40);
			rect.setDims(40, 80);
			rect.setEndTime(Double.POSITIVE_INFINITY);
			new CTarget(rect, color);
		}
	}
}
