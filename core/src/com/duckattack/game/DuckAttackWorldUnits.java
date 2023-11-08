package com.duckattack.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.duckattack.game.debug.DebugCameraController;

import java.util.Iterator;

import sun.security.x509.OtherName;

public class DuckAttackWorldUnits extends ApplicationAdapter {

    //world units
    private static final float WORLD_WIDTH = 400f;
    private static final float WORLD_HEIGHT = 500f;
    private DebugCameraController debugCameraController;
    private boolean debug = false;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer renderer;
    private SpriteBatch batch;
    private Texture wormImg;
    private Texture duckImg;
    private Sprite wormSprite;
    private Texture appleImg;
    private Texture bulletImg;
    private Texture bg;

    private Sound duckVoice;
    private Sound wormEat;
    private Sound gameOver;
    private Sound bulletSound;
    private Rectangle worm;
    private Rectangle bullet;
    private Array<Rectangle> ducks;
    private Array<Rectangle> apples;


    private float duckSpawnTime;
    private int health;

    private float appleSpawnTime;
    private int applesCollected;

    private BitmapFont font;
    private static final float WORM_SPEED = 300;
    private static final float BULLET_SPEED = 350;
    private static final float APPLE_SPEED = 150;
    private static final float APPLE_HEALTH_REGEN = 10;
    private static final float APPLE_SPAWN_TIME = 3;
    private static final float DUCK_SPEED = 200;
    private static final float DUCK_DAMAGE = 25;
    private static final float DUCK_SPAWN_TIME = 2;
    private boolean isMovingLeft = false;
    private boolean gameOverSoundPlayed = false;
    private boolean isBulletFired = false;
    private int ducksKilled = 0;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        renderer = new ShapeRenderer();

        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);

        batch = new SpriteBatch();

        bg = new Texture("images/background.png");
        wormImg = new Texture("images/worm80.png");
        wormSprite = new Sprite(wormImg);

        duckImg = new Texture("images/duck96.png");
        appleImg = new Texture("images/apple72.png");
        bulletImg = new Texture("images/bullet.png");

        duckVoice = Gdx.audio.newSound(Gdx.files.internal("sounds/quack1.mp3"));
        wormEat = Gdx.audio.newSound(Gdx.files.internal("sounds/eating.mp3"));
        gameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver.mp3"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = generator.generateFont(parameter);
        generator.dispose();

        worm = new Rectangle();
        bullet = new Rectangle();

        worm.x = viewport.getWorldWidth() / 2f - wormImg.getWidth() / 2f;
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
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        if (health > 0) {
            handleInput();
            update(Gdx.graphics.getDeltaTime());
        }

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        draw();

        batch.end();
    }

    private void spawnBullet() {
        isBulletFired = true;
        bullet = new Rectangle();
        bullet.x = (worm.x + worm.width / 2f) - bulletImg.getWidth() / 2f;
        bullet.y = worm.y + worm.height;
        bullet.width = bulletImg.getWidth();
        bullet.height = bulletImg.getHeight();
        bulletSound.play();
    }

    private void handleInput() {
        boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isTouched() && Gdx.input.getX() < WORLD_WIDTH / 2);
        boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || (Gdx.input.isTouched() && Gdx.input.getX() >=WORLD_WIDTH / 2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isBulletFired) spawnBullet();

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

    private void update(float delta) {
        float elapsedTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
        if (elapsedTime - duckSpawnTime > DUCK_SPAWN_TIME) spawnDuck();
        if (elapsedTime - appleSpawnTime > APPLE_SPAWN_TIME) spawnApple();

        for (Iterator<Rectangle> it = ducks.iterator(); it.hasNext(); ) {
            Rectangle duck = it.next();
            duck.y -= DUCK_SPEED * delta;
            if (duck.y + duckImg.getHeight() < 0) {
                it.remove();
            }
            if (duck.overlaps(worm)) {
                health -= DUCK_DAMAGE;
                if (health <= 0) {
                    it.remove();
                    return;
                }
                duckVoice.play();
                it.remove();
            }

            if (isBulletFired) {
                bullet.y += BULLET_SPEED * delta;
                if (bullet.y + bulletImg.getHeight() > WORLD_HEIGHT + bulletImg.getHeight())
                    isBulletFired = false;

                if (bullet.overlaps(duck)) {
                    it.remove();
                    ducksKilled++;
                    isBulletFired = false;
                    duckVoice.play();
                }
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
                if (health + APPLE_HEALTH_REGEN > 100) health = 100;
                wormEat.play();
                it.remove();
            }
        }

    }

    private void draw() {
        batch.draw(bg, WORLD_WIDTH / 2f - bg.getWidth() / 2f, WORLD_HEIGHT - bg.getHeight() / 2f - 290f);
        if (health <= 0 && !gameOverSoundPlayed) {
            gameOver.play();
            gameOverSoundPlayed = true;
            return;
        }
        if (gameOverSoundPlayed) {
            font.setColor(Color.RED);
            String gameOverString = "GAME OVER";
            GlyphLayout layout = new GlyphLayout(font, gameOverString);

            float x = (WORLD_WIDTH - layout.width) / 2f;
            float y = (WORLD_HEIGHT + layout.height) / 2f;

            font.draw(batch, layout, x, y);
            return;
        }

        // DRAW DUCKS
        for (Rectangle duck : ducks) {
            batch.draw(duckImg, duck.x, duck.y);
        }

        // DRAW APPLES
        for (Rectangle apple : apples) {
            batch.draw(appleImg, apple.x, apple.y);
        }

        if (isBulletFired) batch.draw(bulletImg, bullet.x, bullet.y);

        batch.draw(wormSprite, worm.x, worm.y);

        font.setColor(Color.RED);
        font.draw(batch, "HEALTH: " + health, 20f, WORLD_HEIGHT - 20f);
        font.draw(batch, "HEALTH: " + health, 20f - 1, WORLD_HEIGHT - 20f);

        font.setColor(Color.SLATE);
        font.draw(batch,
                "SCORE: " + applesCollected,
                20f, WORLD_HEIGHT - 50f
        );

        font.setColor(Color.DARK_GRAY);
        font.draw(batch,
                "DUCKS KILLED: " + ducksKilled,
                20f, WORLD_HEIGHT - 80f
        );
    }

    private void moveLeft(float delta) {
        worm.x -= WORM_SPEED * delta;
        if (worm.x < 0) worm.x = 0;
    }

    private void moveRight(float delta) {
        worm.x += WORM_SPEED * delta;
        if (worm.x > WORLD_WIDTH - wormImg.getWidth())
            worm.x = WORLD_WIDTH - wormImg.getWidth();
    }

    private void spawnDuck() {
        Rectangle duck = new Rectangle();
        duck.x = MathUtils.random(0, WORLD_WIDTH - duckImg.getWidth());
        duck.y = WORLD_HEIGHT;
        duck.width = duckImg.getWidth();
        duck.height = duckImg.getHeight();
        ducks.add(duck);
        duckSpawnTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
    }

    private void spawnApple() {
        Rectangle apple = new Rectangle();
        apple.x = MathUtils.random(0, WORLD_WIDTH - appleImg.getWidth());
        apple.y = WORLD_HEIGHT;
        apple.width = appleImg.getWidth();
        apple.height = appleImg.getHeight();
        apples.add(apple);
        appleSpawnTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        wormImg.dispose();
        duckImg.dispose();
        bulletImg.dispose();
        bulletSound.dispose();
        duckVoice.dispose();
        appleImg.dispose();
        wormEat.dispose();
        gameOver.dispose();
        bg.dispose();
        renderer.dispose();
    }
}
