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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.utils.TimeUtils;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.Scenarios;

/**
 * Runs all of the demos for the Collider library.
 * @author Matthew Michelotti
 */
public class Demos implements ApplicationListener {
	private long startTime;
	private int scenarioI = 0;
	private boolean clicked = false;
	
	@Override
	public void create() {
		InputProcessor inputProcessor = new InputProcessor() {
			@Override public boolean keyDown(int keycode) {return false;}
			@Override public boolean keyUp(int keycode) {return false;}
			@Override public boolean keyTyped(char character) {return false;}
			@Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				return true;
			}
			@Override public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {
				clicked = true;
				return true;
			}
			@Override public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}
			@Override public boolean mouseMoved(int screenX, int screenY) {return false;}
			@Override public boolean scrolled(int amount) {return false;}
		};
		Gdx.input.setInputProcessor(inputProcessor);
		
		Scenarios.initScenario(0);
		startTime = TimeUtils.nanoTime();
	}

	@Override
	public void dispose() {
		Game.engine.dispose();
	}

	@Override
	public void render() {
		long time = TimeUtils.nanoTime();
		if(clicked) {
			clicked = false;
			scenarioI = (scenarioI + 1) % Scenarios.NUM_SCENARIOS;
			Scenarios.initScenario(scenarioI);
			startTime = time;
		}
		Game.engine.stepToTime((TimeUtils.nanoTime() - startTime)*1e-9);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		Game.engine.render(false);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
