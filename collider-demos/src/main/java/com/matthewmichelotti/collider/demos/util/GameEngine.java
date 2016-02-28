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

package com.matthewmichelotti.collider.demos.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.matthewmichelotti.collider.*;
import com.matthewmichelotti.collider.geom.PlacedShape;
import com.matthewmichelotti.collider.geom.Shape;
import com.matthewmichelotti.collider.geom.Vec2d;
import com.matthewmichelotti.collider.processes.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GameEngine {
	public final static int SCREEN_WIDTH = 1280;
	public final static int SCREEN_HEIGHT = 720;

	private final static AtomicBoolean CREATED = new AtomicBoolean(false);

	private final static double BOUNDS_PADDING = 80;
	public final static PlacedShape BOUNDS = new PlacedShape(
			new Vec2d(.5*SCREEN_WIDTH, .5*SCREEN_HEIGHT),
			Shape.newRect(SCREEN_WIDTH + 2*BOUNDS_PADDING, SCREEN_HEIGHT + 2*BOUNDS_PADDING));

	private final FlowProcesses processes;
	private final Collider collider;
	private final GenericFlowProcess events;
	private final HashSet<GameElem> elems = new HashSet<>();

	private MousePosListener mouseListener = null;

	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private final Color bgColor;
	private OrthographicCamera camera;

	private long renderWork = 0;
	private long otherWork = 0;

	private boolean running = false;

	public GameEngine(Color bgColor) {
		if(CREATED.getAndSet(true)) throw new IllegalStateException("can only construct one GameEngine");

		this.bgColor = bgColor;

		collider = new Collider(new GameElemInteractTester(), 22.0, 0.1);

		ColliderProcess colliderProcess = new ColliderProcess(collider, new GameElemColliderListener());
		events = new GenericFlowProcess();
		processes = new FlowProcesses(colliderProcess, events);

		addEvent(new TimedFunction(getTime() + 3.0) {
			@Override public void invoke() { log(); }
		});
	}

	public void setMouseListener(MousePosListener listener) {
		this.mouseListener = listener;
	}

	public void run() {
		if(running) return;
		running = true;
		Game.run(this);
	}

	void initGdx() {
		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();

		camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.translate(.5f*SCREEN_WIDTH, .5f*SCREEN_HEIGHT, 0);
		camera.update();
	}

	void addElem(GameElem elem) {
		elems.add(elem);
	}

	void deleteElem(GameElem elem) {
		elems.remove(elem);
	}

	void addEvent(TimedFunction event) {
		events.add(event);
	}

	void removeEvent(TimedFunction event) {
		events.remove(event);
	}

	private void log() {
		collider.log();
		System.out.println("Free Memory: " + Runtime.getRuntime().freeMemory());
		System.out.println("Total Memory: " + Runtime.getRuntime().totalMemory());
		System.out.println("Rendering Work: " + renderWork*1e-9);
		System.out.println("Other Work: " + otherWork*1e-9);
		System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
		renderWork = 0;
		otherWork = 0;

		addEvent(new TimedFunction(getTime() + 3.0) {
			@Override public void invoke() { log(); }
		});
	}

	Hitbox newHitbox(HitboxState state, GameElem owner) {
		return collider.newHitbox(state, owner);
	}

	double getTime() {
		return collider.getTime();
	}

	void render(boolean drawFPS) {
		long startNanoTime = TimeUtils.nanoTime();

		spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);

		Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		ArrayList<GameElem> orderedElems = new ArrayList<GameElem>(elems);
		Collections.sort(orderedElems);
		ArrayList<GameElem> messageElems = new ArrayList<GameElem>();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for(GameElem elem : orderedElems) {
			if(elem.getMessage() != null) messageElems.add(elem);
			Color color = elem.getColor();
			if(color == null) continue;
			shapeRenderer.setColor(color);
			PlacedShape hitbox = elem.getHitbox().getState().getPlacedShape();
			if(hitbox.getShape().isRect()) {
				shapeRenderer.rect((float)hitbox.getLeft(), (float)hitbox.getBottom(),
						(float)hitbox.getShape().getWidth(), (float)hitbox.getShape().getHeight());
			}
			else {
				shapeRenderer.circle((float)hitbox.getPos().getX(), (float)hitbox.getPos().getY(),
						.5f*(float)hitbox.getShape().getWidth());
			}
		}
		shapeRenderer.end();

		spriteBatch.begin();

		font.setColor(Color.WHITE);
		for(GameElem elem : messageElems) {
			String message = elem.getMessage();
			BitmapFont.TextBounds tb = font.getBounds(message);
			PlacedShape hitbox = elem.getHitbox().getState().getPlacedShape();
			float x = (float)hitbox.getPos().getX() - .5f*tb.width;
			float y = (float)hitbox.getPos().getY() + .5f*tb.height;
			font.draw(spriteBatch, message, x, y);
		}

		if(drawFPS) {
			font.draw(spriteBatch, "FPS: "+ Gdx.graphics.getFramesPerSecond(), 0, SCREEN_HEIGHT);
		}

		spriteBatch.end();

		renderWork += (TimeUtils.nanoTime() - startNanoTime);
	}

	void dispose() {
		shapeRenderer.dispose();
		spriteBatch.dispose();
		font.dispose();
	}

	public void advance(double time) {
		if(time < getTime()) return;
		long startNanoTime = TimeUtils.nanoTime();
		if(time > getTime() && mouseListener != null) {
			double x = SCREEN_WIDTH*Gdx.input.getX()/(double)Gdx.graphics.getWidth();
			double y = SCREEN_HEIGHT*(1.0 - Gdx.input.getY()/(double)Gdx.graphics.getHeight());
			x = Math.max(0, Math.min(SCREEN_WIDTH, x));
			y = Math.max(0, Math.min(SCREEN_HEIGHT, y));
			mouseListener.updateMousePos(new Vec2d(x, y), time);
		}
		processes.advance(time);
		otherWork += (TimeUtils.nanoTime() - startNanoTime);
	}

	private static class GameElemColliderListener implements ColliderListener {
		@Override public void collision(ColliderEvent evt) {
			GameElem elemA = (GameElem)evt.getFirst();
			GameElem elemB = (GameElem)evt.getSecond();
			elemA.onCollide(elemB);
			if(!elemA.isDeleted() && !elemB.isDeleted()) elemB.onCollide(elemA);
		}

		@Override public void separation(ColliderEvent evt) {
			GameElem elemA = (GameElem)evt.getFirst();
			GameElem elemB = (GameElem)evt.getSecond();
			elemA.onSeparate(elemB);
			if(!elemA.isDeleted() && !elemB.isDeleted()) elemB.onSeparate(elemA);
		}
	}

	private static class GameElemInteractTester implements InteractTester {
		@Override public boolean canInteract(Hitbox a, Hitbox b) {
			GameElem elemA = (GameElem)a.getOwner();
			GameElem elemB = (GameElem)b.getOwner();
			return elemA.canInteract(elemB) || elemB.canInteract(elemA);
		}

		@Override public GroupSet getInteractGroups(Hitbox hitbox) {
			return ((GameElem) hitbox.getOwner()).getInteractGroups();
		}
	}
}
