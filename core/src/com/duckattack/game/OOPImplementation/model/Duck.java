package com.duckattack.game.OOPImplementation.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class Duck extends GameObject implements Pool.Poolable {
    private static final float DUCK_SPEED = 200;
    private static final float DUCK_SPAWN_TIME = 2;
    private static final float DUCK_DAMAGE = 25;
    private static float duckSpawnTime;


    public Duck(float x, float y) {
        super(x, y, Assets.duckImg.getWidth(), Assets.duckImg.getHeight());
    }

    public Duck(){
        super(MathUtils.random(0, Gdx.graphics.getWidth() - Assets.duckImg.getWidth()),Gdx.graphics.getHeight(),Assets.duckImg.getWidth(),Assets.duckImg.getHeight());
    }

    public void init(float posX, float posY) {
        position.set(posX, posY);
        bounds.setPosition(posX, posY);
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


    public static boolean isTimeToSpawnNewDuck(float pauseDuration) {
        float currentTimeInSeconds = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
        float effectiveGameTime = currentTimeInSeconds - pauseDuration;
        return effectiveGameTime - duckSpawnTime > DUCK_SPAWN_TIME;
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

    @Override
    public void reset() {
        this.bounds.x = MathUtils.random(0, Gdx.graphics.getWidth() - Assets.duckImg.getWidth());
        this.bounds.y = Gdx.graphics.getHeight();
    }
}
