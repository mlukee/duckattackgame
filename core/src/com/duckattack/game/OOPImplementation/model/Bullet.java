package com.duckattack.game.OOPImplementation.model;

import static com.duckattack.game.OOPImplementation.DuckAttackOOP.gameplayAtlas;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.duckattack.game.OOPImplementation.assets.RegionNames;


public class Bullet extends GameObject{
    private static final float BULLET_SPEED = 350;

    public Bullet(float x, float y) {
        super(x, y, gameplayAtlas.findRegion(RegionNames.BULLET).getRegionWidth(), gameplayAtlas.findRegion(RegionNames.BULLET).getRegionHeight());
    }

    @Override
    public void update(float delta) {
        this.bounds.y += BULLET_SPEED * delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(gameplayAtlas.findRegion(RegionNames.BULLET), this.bounds.x, this.bounds.y);
    }


    public static Bullet spawnBullet(float x, float y) {
        return new Bullet(x, y);
    }
}
