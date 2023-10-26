package com.duckattack.game.OOPImplementation.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class Apple extends GameObject implements Pool.Poolable {
    private static final float APPLE_SPEED = 150;
    private static final float APPLE_SPAWN_TIME = 3;
    private static final float APPLE_HEALTH_REGEN = 10;

    private static float appleSpawnTime;


    public Apple(float x, float y) {
        super(x, y, Assets.appleImg.getWidth(), Assets.appleImg.getHeight());
    }
    public Apple(){
        super(MathUtils.random(0, Gdx.graphics.getWidth() - Assets.appleImg.getWidth()),Gdx.graphics.getHeight(),Assets.appleImg.getWidth(),Assets.appleImg.getHeight());
    }

    public void init(float posX, float posY) {
        position.set(posX, posY);
        bounds.setPosition(posX, posY);
    }

    @Override
    public void update(float delta) {
        this.bounds.y -= APPLE_SPEED * delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.appleImg, this.bounds.x, this.bounds.y);
    }

    public static float getAppleHealthRegen() {
        return APPLE_HEALTH_REGEN;
    }

    public static boolean isAppleOutOfBounds(Apple apple) {
        return apple.bounds.y + apple.bounds.height < 0;
    }

    public static boolean isTimeToSpawnNewApple() {
        return (TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f) - appleSpawnTime > APPLE_SPAWN_TIME;
    }

    public static Apple spawnApple() {
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - Assets.appleImg.getWidth());
        float y = Gdx.graphics.getHeight();
        appleSpawnTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
        return new Apple(x, y);
    }

    @Override
    public void reset() {
        this.bounds.x = MathUtils.random(0, Gdx.graphics.getWidth() - Assets.appleImg.getWidth());
        this.bounds.y = Gdx.graphics.getHeight();
    }
}