package com.duckattack.game.OOPImplementation;

import static com.duckattack.game.OOPImplementation.model.Apple.isAppleOutOfBounds;
import static com.duckattack.game.OOPImplementation.model.Apple.isTimeToSpawnNewApple;
import static com.duckattack.game.OOPImplementation.model.Assets.drawText;
import static com.duckattack.game.OOPImplementation.model.Duck.isDuckHitByBullet;
import static com.duckattack.game.OOPImplementation.model.Duck.isDuckOutOfBounds;
import static com.duckattack.game.OOPImplementation.model.Duck.isTimeToSpawnNewDuck;
import static com.duckattack.game.OOPImplementation.model.Worm.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.duckattack.game.OOPImplementation.model.Apple;
import com.duckattack.game.OOPImplementation.model.Assets;
import com.duckattack.game.OOPImplementation.model.Bullet;
import com.duckattack.game.OOPImplementation.model.Duck;
import com.duckattack.game.OOPImplementation.model.GameState;
import com.duckattack.game.OOPImplementation.model.GoldenApple;
import com.duckattack.game.OOPImplementation.model.Worm;

public class DuckAttackOOP extends ApplicationAdapter {
    public SpriteBatch batch;
    public GameState gameState = GameState.PLAYING;
    public Worm worm;
    public Bullet bullet;

    public GoldenApple goldenApple;

    public Array<Duck> ducks;
    private final Pool<Duck> duckPool = Pools.get(Duck.class, 3);

    public Array<Apple> apples;
    private final Pool<Apple> applePool = Pools.get(Apple.class, 3);
    public static boolean isBulletFired = false;

    private long pauseStartTime;
    private long pauseDuration;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ducks = new Array<>();
        apples = new Array<>();
        Assets.load();
        worm = new Worm(Gdx.graphics.getWidth() / 2f - Assets.wormImg.getWidth() / 2f, 20);
        ducks.add(Duck.spawnDuck());
        duckPool.fill(1);
        apples.add(Apple.spawnApple());
        applePool.fill(2);
    }

    @Override
    public void render() {
        switch (gameState) {
            case PLAYING:
                update(Gdx.graphics.getDeltaTime());
                renderPlaying();
                break;
            case PAUSED:
                renderPaused();
                break;
            case GAME_OVER:
                batch.begin();
                handleInput();
                renderGameOver();
                batch.end();
                break;
        }
    }

    public void update(float delta) {
        if (GoldenApple.isTimeToSpawnNewApple()) {
            goldenApple = GoldenApple.spawnApple();
        }
        if (goldenApple != null) {
            goldenApple.update(delta);
            if (worm.isCollisionWithGoldenApple(goldenApple)) {
                goldenApple = null;
            }
            if (goldenApple.isAppleOutOfBounds()) {
                goldenApple = null;
            }
        }

        worm.update(delta);
        if (isBulletFired) {
            bullet.update(delta);
            if (bullet.bounds.y > Gdx.graphics.getHeight()) isBulletFired = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isBulletFired) {
            isBulletFired = true;
            Assets.bulletSound.play();
            bullet = new Bullet(worm.bounds.x + worm.bounds.width / 2f - Assets.bulletImg.getWidth() / 2f, worm.bounds.y + worm.bounds.height);
        }

        if (isTimeToSpawnNewDuck()) {
            Duck duck = duckPool.obtain();
            duck.init(MathUtils.random(0, Gdx.graphics.getWidth() - Assets.duckImg.getWidth()), Gdx.graphics.getHeight());
            ducks.add(Duck.spawnDuck());
        }
        if (isTimeToSpawnNewApple()) {
            Apple apple = applePool.obtain();
            apple.init(MathUtils.random(0, Gdx.graphics.getWidth() - Assets.appleImg.getWidth()), Gdx.graphics.getHeight());
            apples.add(Apple.spawnApple());
        }

        for (Duck duck : ducks) {
            duck.update(Gdx.graphics.getDeltaTime());

            if (worm.isCollisionWithDuck(duck)) {
                if (health <= 0) return;
                ducks.removeValue(duck, true);
                duckPool.free(duck);
            }

            if (isDuckOutOfBounds(duck)) {
                ducks.removeValue(duck, true);
                duckPool.free(duck);
            }

            if (isBulletFired && isDuckHitByBullet(duck, bullet)) {
                Assets.duckVoice.play();
                ducksKilled++;
                ducks.removeValue(duck, true);
                duckPool.free(duck);
                isBulletFired = false;
            }
        }

        for (Apple apple : apples) {
            apple.update(Gdx.graphics.getDeltaTime());
            if (worm.isCollisionWithApple(apple)) {
                applePool.free(apple);
                apples.removeValue(apple, true);
            }

            if (isAppleOutOfBounds(apple)) {
                apples.removeValue(apple, true);
                applePool.free(apple);
            }
        }
    }

    @Override
    public void dispose() {
        Assets.dispose();
    }

    private void renderPlaying() {
        handleInput();

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        batch.draw(Assets.bg, 0, -290);
        if (health <= 0) {
            gameState = GameState.GAME_OVER;
            Assets.gameOver.play();
        } else {
            renderGameElements();
        }
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePauseState();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            handleGameReset();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isBulletFired && gameState == GameState.PLAYING) {
            fireBullet();
        }
    }

    private void togglePauseState() {
        if (gameState == GameState.PAUSED) {
            long pauseEndTime = System.currentTimeMillis();
            pauseDuration += pauseEndTime - pauseStartTime;
        } else {
            pauseStartTime = System.currentTimeMillis();
        }
        gameState = (gameState == GameState.PAUSED) ? GameState.PLAYING : GameState.PAUSED;
    }

    private void fireBullet() {
        isBulletFired = true;
        Assets.bulletSound.play();
        bullet = new Bullet(worm.bounds.x + worm.bounds.width / 2f - Assets.bulletImg.getWidth() / 2f,
                worm.bounds.y + worm.bounds.height);
    }

    private void renderPaused() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) gameState = GameState.PLAYING;
        batch.begin();
        drawText(batch, Color.RED, "PAUSED", Gdx.graphics.getWidth() / 2f - 60f, Gdx.graphics.getHeight() / 2f);
        batch.end();
    }

    private void handleGameReset() {
        health = 100;
        applesCollected = 0;
        ducksKilled = 0;
        ducks.clear();
        apples.clear();
        worm = new Worm(Gdx.graphics.getWidth() / 2f - Assets.wormImg.getWidth() / 2f, 20);
        goldenApple = null;
        ducks.add(Duck.spawnDuck());
        duckPool.fill(1);
        apples.add(Apple.spawnApple());
        applePool.fill(2);
        gameState = GameState.PLAYING;
    }

    private void renderGameOver() {
        drawText(batch, Color.RED, "GAME OVER", Gdx.graphics.getWidth() / 2f - 60f, Gdx.graphics.getHeight() / 2f);
        drawText(batch, Color.RED, "Press R to restart", Gdx.graphics.getWidth() / 2f - 60f, Gdx.graphics.getHeight() / 2f - 30f);
    }

    private void renderGameElements() {
        worm.render(batch);
        if (isBulletFired) bullet.render(batch);
        for (Duck duck : ducks) duck.render(batch);
        for (Apple apple : apples) apple.render(batch);
        if (goldenApple != null) goldenApple.render(batch);
        renderHUD();
    }

    private void renderHUD() {
        drawText(batch, Color.RED, "Health: " + health, 20f, Gdx.graphics.getHeight() - 20f);
        drawText(batch, Color.SLATE, "Score: " + applesCollected, 20f, Gdx.graphics.getHeight() - 50f);
        drawText(batch, Color.DARK_GRAY, "Ducks killed: " + ducksKilled, 20f, Gdx.graphics.getHeight() - 80f);
        if(worm.isDoublePointsActive()){
            drawText(batch, Color.GOLD, "Double points active", 20f, Gdx.graphics.getHeight() - 110f);
        }
    }
}