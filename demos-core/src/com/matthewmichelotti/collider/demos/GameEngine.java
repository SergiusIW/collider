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

package com.matthewmichelotti.collider.demos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;
import com.matthewmichelotti.collider.Collider;
import com.matthewmichelotti.collider.ColliderEvent;
import com.matthewmichelotti.collider.ColliderOpts;
import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.HBRect;
import com.matthewmichelotti.collider.HitBox;
import com.matthewmichelotti.collider.InteractTester;
import com.matthewmichelotti.collider.demos.comps.CBounds;
import com.matthewmichelotti.collider.util.ColliderListener;
import com.matthewmichelotti.collider.util.ColliderProcess;
import com.matthewmichelotti.collider.util.ContProcesses;
import com.matthewmichelotti.collider.util.ContProcess;

/**
 * Manages FunctionEvents, ColliderEvents,
 * creation of new HitBoxes, and rendering.
 * @author Matthew Michelotti
 */
public class GameEngine {
	public final static int SCREEN_WIDTH = 1280; //960
	public final static int SCREEN_HEIGHT = 720;
	
	public final static int GROUP_NORMAL = 0;
	public final static int GROUP_BULLET = 1;

	private final static int[] ALL_GROUPS_ARR = new int[] {GROUP_NORMAL, GROUP_BULLET};
	private final static int[] NORMAL_GROUP_ARR = new int[] {GROUP_NORMAL};

	private ContProcesses processes;
	private Collider collider;
	private HashSet<Component> comps = new HashSet<Component>();
	private PriorityQueue<FunctionEvent> events = new PriorityQueue<FunctionEvent>();
	private MousePosListener mouseListener;
	private CBounds bounds;
	
	private ShapeRenderer shapeR = new ShapeRenderer();
	private SpriteBatch spriteB = new SpriteBatch();
	private BitmapFont font = new BitmapFont();
	private Color bgColor = Color.BLACK;
	private OrthographicCamera camera;
	
	private long renderWork = 0;
	private long otherWork = 0;

	public GameEngine() {
		clear();
		camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.translate(.5f*SCREEN_WIDTH, .5f*SCREEN_HEIGHT, 0);
		camera.update();
	}
	
	public void clear() {
		processes = new ContProcesses();
		
		ColliderOpts opts = new ColliderOpts();
		opts.cellWidth = 22.0;
		opts.separateBuffer = .1;
		opts.maxForesightTime = 2.0;
		opts.interactTester = new CompInteractTester();
		collider = new Collider(opts);
		
		comps.clear();
		events.clear();
		mouseListener = null;
		bounds = null;
		
		bgColor = Color.BLACK;

		processes.addProcess(new ColliderProcess(collider, new MyColliderListener()));
		processes.addProcess(new EventProcess());
		events.add(new LogEvent(0.0));
	}
	
	public HBRect makeRect() {return collider.makeRect();}
	public HBCircle makeCircle() {return collider.makeCircle();}

	public void addEvent(FunctionEvent event) {
		events.add(event);
	}
	
	public void addComp(Component comp) {
		boolean success = comps.add(comp);
		if(!success) throw new RuntimeException();
		if(comp instanceof MousePosListener) {
			if(mouseListener != null) throw new RuntimeException();
			mouseListener = (MousePosListener)comp;
		}
		if(comp instanceof CBounds) {
			if(bounds != null) throw new RuntimeException();
			bounds = (CBounds)comp;
		}
	}
	
	public void removeComp(Component comp) {
		boolean success = comps.remove(comp);
		if(!success) throw new RuntimeException();
		if(mouseListener == comp) mouseListener = null;
		if(bounds == comp) bounds = null;
	}
	
	public boolean isInBounds(HitBox hitBox) {
		return hitBox.getOverlap(bounds.hitBox()) >= .1;
	}
	
	public void stepToTime(double time) {
		if(time < getTime()) return;
		long startNanoTime = TimeUtils.nanoTime();
		if(time > getTime() && mouseListener != null) {
			double x = SCREEN_WIDTH*Gdx.input.getX()/(double)Gdx.graphics.getWidth();
			double y = SCREEN_HEIGHT*(1.0 - Gdx.input.getY()/(double)Gdx.graphics.getHeight());
			x = Math.max(0, Math.min(SCREEN_WIDTH, x));
			y = Math.max(0, Math.min(SCREEN_HEIGHT, y));
			mouseListener.updateMousePos(time, x, y);
		}
		processes.stepToTime(time);
		otherWork += (TimeUtils.nanoTime() - startNanoTime);
	}
	
	public double getTime() {
		return processes.getTime();
	}
	
	public void setBG(Color color) {
		this.bgColor = color;
	}
	
	public void render(boolean drawFPS) {
		long startNanoTime = TimeUtils.nanoTime();
		
		spriteB.setProjectionMatrix(camera.combined);
		shapeR.setProjectionMatrix(camera.combined);

		Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		ArrayList<Component> orderedComps = new ArrayList<Component>(comps);
		ArrayList<Component> messageComps = new ArrayList<Component>();
		Collections.sort(orderedComps);

		shapeR.begin(ShapeType.Filled);
		for(Component c : orderedComps) {
			if(c.getMessage() != null) messageComps.add(c);
			Color color = c.getColor();
			if(color == null) continue;
			shapeR.setColor(color);
			if(c.isRect()) {
				HBRect rect = c.rect();
				double l = rect.getX() - .5*rect.getWidth();
				double b = rect.getY() - .5*rect.getHeight();
				shapeR.rect((float)l, (float)b, (float)rect.getWidth(), (float)rect.getHeight());
			}
			else {
				HBCircle circle = c.circ();
				shapeR.circle((float)circle.getX(), (float)circle.getY(), .5f*(float)circle.getDiam());
			}
		}
		shapeR.end();

		spriteB.begin();
		
		font.setColor(Color.WHITE);
		for(Component c : messageComps) {
			String message = c.getMessage();
			BitmapFont.TextBounds tb = font.getBounds(message);
			float x = (float)c.hitBox().getX() - .5f*tb.width;
			float y = (float)c.hitBox().getY() + .5f*tb.height;
			font.draw(spriteB, message, x, y);
		}
		
		if(drawFPS) {
			font.draw(spriteB, "FPS: "+ Gdx.graphics.getFramesPerSecond(), 0, SCREEN_HEIGHT);
		}
		
		spriteB.end();
		
		renderWork += (TimeUtils.nanoTime() - startNanoTime);
	}
	
	public void dispose() {
		shapeR.dispose();
		spriteB.dispose();
		font.dispose();
	}
	
	private class LogEvent extends FunctionEvent {
		private LogEvent(double time) {super(time);}
		
		@Override public void resolve() {
			collider.log();
			System.out.println("Free Memory: " + Runtime.getRuntime().freeMemory());
			System.out.println("Total Memory: " + Runtime.getRuntime().totalMemory());
			System.out.println("Rendering Work: " + renderWork*1e-9);
			System.out.println("Other Work: " + otherWork*1e-9);
			System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
			renderWork = 0;
			otherWork = 0;
			setTime(getTime() + 3.0);
			addEvent(this);
		}
	}

	private static class MyColliderListener implements ColliderListener {
		@Override public void collision(ColliderEvent evt) {
			Component compA = (Component)evt.getFirst().getOwner();
			Component compB = (Component)evt.getSecond().getOwner();
			compA.onCollide(compB);
			if(!compA.isDeleted() && !compB.isDeleted()) compB.onCollide(compA);
		}
		@Override public void separation(ColliderEvent evt) {
			Component compA = (Component)evt.getFirst().getOwner();
			Component compB = (Component)evt.getSecond().getOwner();
			compA.onSeparate(compB);
			if(!compA.isDeleted() && !compB.isDeleted()) compB.onSeparate(compA);
		}
	}
	
	private class EventProcess implements ContProcess {
		@Override public double peekNextEventTime() {
			FunctionEvent event = events.peek();
			if(event == null) return Double.POSITIVE_INFINITY;
			return event.getTime();
		}
		@Override public void stepToTime(double time) {}
		@Override public void resolveEvent() {
			FunctionEvent event = events.poll();
			if(event.getTime() == processes.getTime()) event.resolve();
		}
	}
	
	private static class CompInteractTester implements InteractTester {
		@Override public boolean canInteract(HitBox a, HitBox b) {
			Component compA = (Component)a.getOwner();
			Component compB = (Component)b.getOwner();
			return compA.canInteract(compB) || compB.canInteract(compA);
		}

		@Override public int[] getInteractGroups(HitBox hitBox) {
			if(((Component)hitBox.getOwner()).interactsWithBullets()) return ALL_GROUPS_ARR;
			return NORMAL_GROUP_ARR;
		}
	}
}
