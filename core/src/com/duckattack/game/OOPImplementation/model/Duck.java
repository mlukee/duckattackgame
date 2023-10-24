package com.duckattack.game.OOPImplementation.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Duck extends GameObject {
    private static final float DUCK_SPEED = 200;
    private static final float DUCK_SPAWN_TIME = 2;
    private static final float DUCK_DAMAGE = 25;
    private static float duckSpawnTime;


    public Duck(float x, float y) {
        super(x, y, Assets.duckImg.getWidth(), Assets.duckImg.getHeight());
    }

    @Override
    public void update(float delta) {
        this.bounds.y -= DUCK_SPEED * delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.duckImg, this.bounds.x, this.bounds.y);
    }

    public static float getDuckDamage() {
        return DUCK_DAMAGE;
    }

    public static boolean isTimeToSpawnNewDuck() {
        return (TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f) - duckSpawnTime > DUCK_SPAWN_TIME;
    }

    public static boolean isDuckOutOfBounds(Duck duck) {
        return duck.bounds.y + duck.bounds.height < 0;
    }

    public static boolean isDuckHitByBullet(Duck duck, Bullet bullet) {
        return duck.bounds.overlaps(bullet.bounds);
    }

    public static Duck spawnDuck() {
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - Assets.duckImg.getWidth());
        float y = Gdx.graphics.getHeight();
        duckSpawnTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
        return new Duck(x, y);
    }

}
