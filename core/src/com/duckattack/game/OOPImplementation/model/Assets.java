package com.duckattack.game.OOPImplementation.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.duckattack.game.debug.MemoryInfo;

public class Assets {
    public static BitmapFont font;


    public static void load() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = generator.generateFont(parameter);
        generator.dispose();

    }

    public static void drawText(SpriteBatch batch, Color color, String text, float x, float y) {
        font.setColor(color);
        font.draw(batch, text, x, y);
    }

    public static void drawMemoryInfo(MemoryInfo info, SpriteBatch batch) {
        info.render(batch, font);
    }

    public static void dispose() {
        //dispose all
        font.dispose();
    }

}