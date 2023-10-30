package com.duckattack.game.OOPImplementation.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class GoldenApple extends GameObject{
    private static final float APPLE_SPEED = 125;
    private static final float APPLE_SPAWN_TIME = 20;

    private static float appleSpawnTime;


    public GoldenApple(float x, float y) {
        super(x, y, Assets.goldenAppleImg.getWidth(), Assets.goldenAppleImg.getHeight());
    }

    @Override
    public void update(float delta) {
        this.bounds.y -= APPLE_SPEED * delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.goldenAppleImg, this.bounds.x, this.bounds.y);
    }

    public boolean isAppleOutOfBounds() {
        return this.bounds.y + this.bounds.height < 0;
    }

    public static boolean isTimeToSpawnNewApple() {
        float time = (TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f);
        return  time - appleSpawnTime > APPLE_SPAWN_TIME;
    }

    public static GoldenApple spawnApple() {
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - Assets.appleImg.getWidth());
        float y = Gdx.graphics.getHeight();
        appleSpawnTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
        return new GoldenApple(x, y);
    }
}
