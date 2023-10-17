package com.duckattack.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class DuckAttack extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture wormImg;
	private Texture duckImg;
	private Sprite wormSprite;
	private Texture appleImg;
	private Texture bg;

	private Sound duckVoice;
	private Sound wormEat;
	private Sound gameOver;
	private Rectangle worm;
	private Array<Rectangle> ducks;
	private Array<Rectangle> apples;


	private float duckSpawnTime;
	private int health;

	private float appleSpawnTime;
	private int applesCollected;

	private BitmapFont font;
	private static final float WORM_SPEED = 300;
	private static final float APPLE_SPEED = 150;
	private static final float APPLE_HEALTH_REGEN = 10;
	private static final float APPLE_SPAWN_TIME = 3;
	private static final float DUCK_SPEED = 200;
	private static final float DUCK_DAMAGE = 25;
	private static final float DUCK_SPAWN_TIME = 2;

	private boolean isMovingLeft = false;
	private boolean gameOverSoundPlayed = false;



	@Override
	public void create () {
		batch = new SpriteBatch();

		bg = new Texture("background.png");
		wormImg = new Texture("worm80.png");
		wormSprite = new Sprite(wormImg);

		duckImg = new Texture("duck96.png");
		appleImg = new Texture("apple72.png");

		duckVoice = Gdx.audio.newSound(Gdx.files.internal("quack1.mp3"));
		wormEat = Gdx.audio.newSound(Gdx.files.internal("eating.mp3"));
		gameOver = Gdx.audio.newSound(Gdx.files.internal("gameOver.mp3"));

		font = new BitmapFont();
		worm = new Rectangle();

		worm.x = Gdx.graphics.getWidth() / 2f - wormImg.getWidth() / 2f;
		worm.y = 20;
		worm.width = wormImg.getWidth();
		worm.height = wormImg.getHeight();

		ducks = new Array<>();
		apples = new Array<>();
		health = 100;
		applesCollected = 0;
		spawnDuck();
		spawnApple();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);

		if(health > 0){
			handleInput();
			update(Gdx.graphics.getDeltaTime());
		}

		batch.begin();

		draw();

		batch.end();
	}

	private void handleInput() {
		boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isTouched() && Gdx.input.getX() < Gdx.graphics.getWidth() / 2);
		boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || (Gdx.input.isTouched() && Gdx.input.getX() >= Gdx.graphics.getWidth() / 2);

		// Determine the movement direction
		if (isLeftPressed) {
			isMovingLeft = true;
			moveLeft(Gdx.graphics.getDeltaTime());
		} else if (isRightPressed) {
			isMovingLeft = false;
			moveRight(Gdx.graphics.getDeltaTime());
		}

		if (isMovingLeft && !wormSprite.isFlipX()) {
			wormSprite.setFlip(true, false);
		} else if (!isMovingLeft && wormSprite.isFlipX()) {
			wormSprite.setFlip(false, false);
		}
	}

	private void update(float delta){
		float elapsedTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
		if(elapsedTime - duckSpawnTime > DUCK_SPAWN_TIME) spawnDuck();
		if(elapsedTime - appleSpawnTime > APPLE_SPAWN_TIME) spawnApple();

		for (Iterator<Rectangle> it = ducks.iterator(); it.hasNext(); ) {
			Rectangle duck = it.next();
			duck.y -= DUCK_SPEED * delta;
			if (duck.y + duckImg.getHeight() < 0) {
				it.remove();
			}
			if (duck.overlaps(worm)) {
				health -= DUCK_DAMAGE;
				duckVoice.play();
				it.remove();
			}
		}

		for (Iterator<Rectangle> it = apples.iterator(); it.hasNext(); ) {
			Rectangle apple = it.next();
			apple.y -= APPLE_SPEED * delta;
			if (apple.y + appleImg.getHeight() < 0) {
				it.remove();
			}
			if (apple.overlaps(worm)) {
				applesCollected++;
				health += APPLE_HEALTH_REGEN;
				if(health + APPLE_HEALTH_REGEN > 100) health = 100;
				wormEat.play();
				it.remove();
			}
		}
	}

	private void draw(){
		batch.draw(bg, 0,-290);
		if(health <= 0 && !gameOverSoundPlayed){
			gameOver.play();
			gameOverSoundPlayed = true;
			return;
		}
		if(gameOverSoundPlayed) {
			font.setColor(Color.RED);
			String gameOverString = "GAME OVER";
			font.getData().setScale(2); // Set font size to 2 times the default size
			font.draw(batch, gameOverString, (Gdx.graphics.getWidth() - gameOverString.length()*0.9f) / 2f, Gdx.graphics.getHeight()/ 2f);
			return;
		}

		// DRAW DUCKS
		for(Rectangle duck : ducks){
			batch.draw(duckImg, duck.x, duck.y);
		}

		// DRAW APPLES
		for(Rectangle apple : apples){
			batch.draw(appleImg, apple.x, apple.y);
		}

		// DRAW WORM
		batch.draw(wormSprite, worm.x, worm.y);


		font.setColor(Color.BLACK);
		font.draw(batch, "HEALTH: " + health, 20f, Gdx.graphics.getHeight() -40f);

		font.setColor(Color.GREEN);
		font.draw(batch,
				"SCORE: " + applesCollected,
				20f, Gdx.graphics.getHeight() - 60f
		);
	}

	private void moveLeft(float delta) {
		worm.x -= WORM_SPEED * delta;
		if (worm.x < 0) worm.x = 0;
	}

	private void moveRight(float delta) {
		worm.x += WORM_SPEED * delta;
		if (worm.x > Gdx.graphics.getWidth() - wormImg.getWidth())
			worm.x = Gdx.graphics.getWidth() - wormImg.getWidth();
	}

	private void spawnDuck(){
		Rectangle duck = new Rectangle();
		duck.x = MathUtils.random(0, Gdx.graphics.getWidth() - duckImg.getWidth());
		duck.y = Gdx.graphics.getWidth();
		duck.width = duckImg.getWidth();
		duck.height = duckImg.getHeight();
		ducks.add(duck);
		duckSpawnTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
	}

	private void spawnApple(){
		Rectangle apple = new Rectangle();
		apple.x = MathUtils.random(0, Gdx.graphics.getWidth() - appleImg.getWidth());
		apple.y = Gdx.graphics.getWidth();
		apple.width = appleImg.getWidth();
		apple.height = appleImg.getHeight();
		apples.add(apple);
		appleSpawnTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
	}

	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		wormImg.dispose();
		duckImg.dispose();
		duckVoice.dispose();
		appleImg.dispose();
		wormEat.dispose();
		gameOver.dispose();
		bg.dispose();
	}
}
