package com.duckattack.game.OOPImplementation.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.duckattack.game.debug.MemoryInfo;

public class Assets {
    public static Texture wormImg;
    public static Texture duckImg;
    public static Sprite wormSprite;
    public static Texture appleImg;
    public static Texture bulletImg;
    public static Texture goldenAppleImg;
    public static Texture bg;

    public static Sound duckVoice;
    public static Sound wormEat;
    public static Sound gameOver;
    public static Sound bulletSound;
    public static Sound collected;
    public static Sound wormHit;

    public static BitmapFont font;


    public static Texture loadTexture(String file) {
        return new Texture(Gdx.files.internal(file));
    }

    public static void load() {
        bg = loadTexture("images/background.png");
        wormImg = loadTexture("images/worm80.png");
        wormSprite = new Sprite(wormImg);
        duckImg = loadTexture("images/duck96.png");
        appleImg = loadTexture("images/apple72.png");
        bulletImg = loadTexture("images/bullet.png");
        goldenAppleImg = loadTexture("images/goldenapple72.png");


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = generator.generateFont(parameter);
        generator.dispose();

        duckVoice = Gdx.audio.newSound(Gdx.files.internal("sounds/quack1.mp3"));
        wormEat = Gdx.audio.newSound(Gdx.files.internal("sounds/eating.mp3"));
        gameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver.mp3"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        collected = Gdx.audio.newSound(Gdx.files.internal("sounds/collected.mp3"));
        wormHit = Gdx.audio.newSound(Gdx.files.internal("sounds/wormHit.mp3"));

    }

    public static void drawText(SpriteBatch batch, Color color, String text, float x, float y){
        font.setColor(color);
        font.draw(batch, text, x, y);
    }

    public static void drawMemoryInfo(MemoryInfo info, SpriteBatch batch){
        info.render(batch, font);
    }

    public static void dispose(){
        //dispose all
        wormImg.dispose();
        duckImg.dispose();
        appleImg.dispose();
        bulletImg.dispose();
        bg.dispose();
        duckVoice.dispose();
        wormEat.dispose();
        gameOver.dispose();
        bulletSound.dispose();
        goldenAppleImg.dispose();
        collected.dispose();
        wormHit.dispose();
        font.dispose();
    }

}