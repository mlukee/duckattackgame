package com.duckattack.game.OOPImplementation.model;

import static com.duckattack.game.OOPImplementation.DuckAttackOOP.WORLD_HEIGHT;
import static com.duckattack.game.OOPImplementation.DuckAttackOOP.WORLD_WIDTH;
import static com.duckattack.game.OOPImplementation.DuckAttackOOP.gameplayAtlas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.duckattack.game.OOPImplementation.assets.RegionNames;

public class Apple extends GameObject implements Pool.Poolable {
    private static final float APPLE_SPEED = 150;
    private static final float APPLE_SPAWN_TIME = 3;
    private static final float APPLE_HEALTH_REGEN = 10;

    private static float appleSpawnTime;


    public Apple(float x, float y) {
        super(x, y, gameplayAtlas.findRegion(RegionNames.APPLE).getRegionWidth(), gameplayAtlas.findRegion(RegionNames.APPLE).getRegionHeight());
    }

    public Apple() {
        super(MathUtils.random(0, WORLD_WIDTH - gameplayAtlas.findRegion(RegionNames.APPLE).getRegionWidth()), WORLD_HEIGHT, gameplayAtlas.findRegion(RegionNames.APPLE).getRegionWidth(), gameplayAtlas.findRegion(RegionNames.APPLE).getRegionHeight());
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
        batch.draw(gameplayAtlas.findRegion(RegionNames.APPLE), this.bounds.x, this.bounds.y);
    }

    public static float getAppleHealthRegen() {
        return APPLE_HEALTH_REGEN;
    }

    public static boolean isAppleOutOfBounds(Apple apple) {
        return apple.bounds.y + apple.bounds.height < 0;
    }

    public static boolean isTimeToSpawnNewApple(float pauseDuration) {
        float currentTimeInSeconds = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
        float effectiveGameTime = currentTimeInSeconds - pauseDuration;
        return effectiveGameTime - appleSpawnTime > APPLE_SPAWN_TIME;
    }

    public static Apple spawnApple() {
        float x = MathUtils.random(0, WORLD_WIDTH - gameplayAtlas.findRegion(RegionNames.APPLE).getRegionWidth());
        float y = WORLD_HEIGHT;
        appleSpawnTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
        return new Apple(x, y);
    }

    @Override
    public void reset() {
        this.bounds.x = MathUtils.random(0, WORLD_WIDTH - gameplayAtlas.findRegion(RegionNames.APPLE).getRegionWidth());
        this.bounds.y = WORLD_HEIGHT;
    }
}
