package com.duckattack.game.OOPImplementation.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet extends GameObject{
    private static final float BULLET_SPEED = 350;

    public Bullet(float x, float y) {
        super(x, y, Assets.bulletImg.getWidth(), Assets.bulletImg.getHeight());
    }

    @Override
    public void update(float delta) {
        this.bounds.y += BULLET_SPEED * delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.bulletImg, this.bounds.x, this.bounds.y);
    }


    public static Bullet spawnBullet(float x, float y) {
        return new Bullet(x, y);
    }
}
