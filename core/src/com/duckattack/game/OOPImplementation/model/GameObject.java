package com.duckattack.game.OOPImplementation.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

abstract class GameObject {
    public final Vector2 position;
    public final Rectangle bounds;

    public GameObject (float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x,y,width, height);
    }
    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);
}
