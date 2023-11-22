package com.duckattack.game.OOPImplementation.model;


import static com.duckattack.game.OOPImplementation.DuckAttackOOP.WORLD_WIDTH;
import static com.duckattack.game.OOPImplementation.DuckAttackOOP.gameplayAtlas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.duckattack.game.OOPImplementation.assets.RegionNames;

public class Worm extends GameObject {
    private boolean isMovingLeft = false;
    private static final float WORM_SPEED = 300;
    public static int health;
    public static int applesCollected;
    public static int ducksKilled;
    private float doublePointsDuration;


    public Worm(float x, float y) {
        super(x, y, gameplayAtlas.findRegion(RegionNames.WORM).getRegionWidth(), gameplayAtlas.findRegion(RegionNames.WORM).getRegionHeight());
        health = 100;
        applesCollected = 0;
        ducksKilled = 0;
        doublePointsDuration = 0;

    }

    @Override
    public void update(float delta) {
        boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isTouched() && Gdx.input.getX() < WORLD_WIDTH / 2);
        boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || (Gdx.input.isTouched() && Gdx.input.getX() >= WORLD_WIDTH / 2);

        // Determine the movement direction
        if (isLeftPressed) {
            isMovingLeft = true;
            moveLeft(Gdx.graphics.getDeltaTime());
        } else if (isRightPressed) {
            isMovingLeft = false;
            moveRight(Gdx.graphics.getDeltaTime());
        }

        if (isMovingLeft && !Assets.wormSprite.isFlipX()) {
            Assets.wormSprite.setFlip(true, false);
        } else if (!isMovingLeft && Assets.wormSprite.isFlipX()) {
            Assets.wormSprite.setFlip(false, false);
        }
        if (doublePointsDuration > 0) {
            doublePointsDuration -= delta;
            if (doublePointsDuration < 0) {
                doublePointsDuration = 0;
            }
        }

    }

    public void activateDoublePoints() {
        doublePointsDuration = 10;
    }

    public boolean isDoublePointsActive() {
        return doublePointsDuration > 0;
    }

    public boolean isCollisionWithDuck(Duck duck) {
        if (this.bounds.overlaps(duck.bounds)) {
            health -= Duck.getDuckDamage();
            if (health > 0) {
                Assets.wormHit.play();
            }
            return true;
        }
        return false;
    }

    public boolean isCollisionWithApple(Apple apple) {
        if (this.bounds.overlaps(apple.bounds)) {
            Assets.collected.play();
            health += Apple.getAppleHealthRegen();
            if (health > 100) health = 100;
            if (isDoublePointsActive()) {
                applesCollected += 2;
            } else {
                applesCollected++;
            }
            return true;
        }
        return false;
    }

    public boolean isCollisionWithGoldenApple(GoldenApple apple) {
        if (this.bounds.overlaps(apple.bounds)) {
            Assets.wormEat.play();
            activateDoublePoints();
            return true;
        }
        return false;
    }


    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.wormSprite, this.bounds.x, this.bounds.y);
    }

    public void moveLeft(float delta) {
        this.bounds.x -= WORM_SPEED * delta;
        if (super.bounds.x < 0) super.bounds.x = 0;
    }

    public void moveRight(float delta) {
        super.bounds.x += WORM_SPEED * delta;
        if (super.bounds.x > WORLD_WIDTH - super.bounds.width)
            super.bounds.x = WORLD_WIDTH - super.bounds.width;
    }
}
