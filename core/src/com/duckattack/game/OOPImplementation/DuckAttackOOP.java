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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.duckattack.game.OOPImplementation.model.Apple;
import com.duckattack.game.OOPImplementation.model.Assets;
import com.duckattack.game.OOPImplementation.model.Bullet;
import com.duckattack.game.OOPImplementation.model.Duck;
import com.duckattack.game.OOPImplementation.model.Worm;

public class DuckAttackOOP extends ApplicationAdapter {
    public SpriteBatch batch;
    public Worm worm;
    public Bullet bullet;
    public Array<Duck> ducks;
    public Array<Apple> apples;
    public static boolean isBulletFired = false;
    private boolean isGameOver = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ducks = new Array<>();
        apples = new Array<>();
        Assets.load();
        worm = new Worm(Gdx.graphics.getWidth() / 2f - Assets.wormImg.getWidth() / 2f, 20);
        ducks.add(Duck.spawnDuck());
        apples.add(Apple.spawnApple());
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        if (health > 0) update(Gdx.graphics.getDeltaTime());
        batch.begin();
        batch.draw(Assets.bg, 0, -290);
        if (health <= 0) {
            handleGameOver();
        } else {
            renderGameElements();
        }
        batch.end();
    }

    public void update(float delta) {
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

        if (isTimeToSpawnNewDuck()) ducks.add(Duck.spawnDuck());
        if (isTimeToSpawnNewApple()) apples.add(Apple.spawnApple());

        for (Duck duck : ducks) {
            duck.update(Gdx.graphics.getDeltaTime());

            if (worm.isCollisionWithDuck(duck)) {
                if (health <= 0) return;
                ducks.removeValue(duck, true);
            }

            if (isDuckOutOfBounds(duck)) ducks.removeValue(duck, true);

            if (isBulletFired && isDuckHitByBullet(duck, bullet)) {
                Assets.duckVoice.play();
                ducksKilled++;
                ducks.removeValue(duck, true);
                isBulletFired = false;
            }
        }

        for (Apple apple : apples) {
            apple.update(Gdx.graphics.getDeltaTime());
            if (worm.isCollisionWithApple(apple)) apples.removeValue(apple, true);

            if (isAppleOutOfBounds(apple)) apples.removeValue(apple, true);
        }
    }

    @Override
    public void dispose() {
        Assets.dispose();
    }

    private void handleGameOver() {
        if (!isGameOver) {
            isGameOver = true;
            Assets.gameOver.play();
        }
        drawText(batch, Color.RED, "GAME OVER", Gdx.graphics.getWidth() / 2f - 60f, Gdx.graphics.getHeight() / 2f);
    }

    private void renderGameElements() {
        worm.render(batch);
        if (isBulletFired) bullet.render(batch);
        for (Duck duck : ducks) duck.render(batch);
        for (Apple apple : apples) apple.render(batch);

        renderHUD();
    }

    private void renderHUD() {
        drawText(batch, Color.RED, "Health: " + health, 20f, Gdx.graphics.getHeight() - 20f);
        drawText(batch, Color.SLATE, "Score: " + applesCollected, 20f, Gdx.graphics.getHeight() - 50f);
        drawText(batch, Color.DARK_GRAY, "Ducks killed: " + ducksKilled, 20f, Gdx.graphics.getHeight() - 80f);
    }
}