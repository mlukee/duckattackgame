package com.duckattack.game.OOPImplementation;

import static com.duckattack.game.OOPImplementation.model.Apple.isAppleOutOfBounds;
import static com.duckattack.game.OOPImplementation.model.Apple.isTimeToSpawnNewApple;
import static com.duckattack.game.OOPImplementation.model.Assets.bg;
import static com.duckattack.game.OOPImplementation.model.Assets.drawMemoryInfo;
import static com.duckattack.game.OOPImplementation.model.Assets.drawText;
import static com.duckattack.game.OOPImplementation.model.Duck.isDuckHitByBullet;
import static com.duckattack.game.OOPImplementation.model.Duck.isDuckOutOfBounds;
import static com.duckattack.game.OOPImplementation.model.Duck.isTimeToSpawnNewDuck;
import static com.duckattack.game.OOPImplementation.model.Worm.*;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.duckattack.game.OOPImplementation.assets.AssetDescriptors;
import com.duckattack.game.OOPImplementation.assets.RegionNames;
import com.duckattack.game.OOPImplementation.model.Apple;
import com.duckattack.game.OOPImplementation.model.Assets;
import com.duckattack.game.OOPImplementation.model.Bullet;
import com.duckattack.game.OOPImplementation.model.Duck;
import com.duckattack.game.OOPImplementation.model.GameState;
import com.duckattack.game.OOPImplementation.model.GoldenApple;
import com.duckattack.game.OOPImplementation.model.Worm;
import com.duckattack.game.debug.DebugCameraController;
import com.duckattack.game.debug.MemoryInfo;
import com.duckattack.game.util.ViewPortUtils;

public class DuckAttackOOP extends ApplicationAdapter {
    public static final float WORLD_WIDTH = 750f;
    public static final float WORLD_HEIGHT = 750f;
    private DebugCameraController debugCameraController;
    private MemoryInfo memoryInfo;
    private boolean debug = false;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Viewport hudViewport;

    public static TextureAtlas gameplayAtlas;
    private AssetManager assetManager;
    private ShapeRenderer renderer;

    public SpriteBatch batch;
    public GameState gameState = GameState.PLAYING;
    public Worm worm;
    public Bullet bullet;

    public GoldenApple goldenApple;

    public Array<Duck> ducks;
    private final Pool<Duck> duckPool = Pools.get(Duck.class, 3);

    public Array<Apple> apples;
    private final Pool<Apple> applePool = Pools.get(Apple.class, 3);
    public static boolean isBulletFired = false;

    private float pauseStartTime;
    private float pauseDuration = 0;

    private ParticleEffect peApple;
    private ParticleEffect peBullet;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        hudViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer = new ShapeRenderer();

        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);

        assetManager = new AssetManager();
        assetManager.load(AssetDescriptors.GAMEPLAY);
        assetManager.finishLoading();

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);

        memoryInfo = new MemoryInfo(500);
        batch = new SpriteBatch();
        ducks = new Array<>();
        apples = new Array<>();
        Assets.load();
        worm = new Worm(WORLD_WIDTH / 2f - Assets.wormImg.getWidth() / 2f, 20);
        ducks.add(Duck.spawnDuck());
        duckPool.fill(2);
        apples.add(Apple.spawnApple());
        applePool.fill(2);

        peApple = new ParticleEffect();
        peApple.load(Gdx.files.internal("particles/particle.pe"), Gdx.files.internal("particles"));
        peApple.start();

        peBullet = new ParticleEffect();
        peBullet.load(Gdx.files.internal("particles/bulletPE.pe"), Gdx.files.internal("particles"));
        peBullet.start();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
        ViewPortUtils.debugPixelsPerUnit(viewport);
    }

    @Override
    public void render() {

        switch (gameState) {
            case PLAYING:
                handleInput();
                update(Gdx.graphics.getDeltaTime());
                if (health <= 0) {
                    gameState = GameState.GAME_OVER;
                    Assets.gameOver.play();
                }
                break;
            case PAUSED:
            case GAME_OVER:
                handleInput();
                break;
        }
        viewport.apply();
        renderGameElements();

    }

    private void renderDebug() {
        ViewPortUtils.drawGrid(viewport, renderer, 30);

        viewport.apply();

        Color oldColor = new Color(renderer.getColor());
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();
        renderer.setColor(oldColor);
    }

    private void drawDebug() {
        renderer.setColor(Color.WHITE);
        renderer.rect(worm.bounds.x, worm.bounds.y, worm.bounds.width, worm.bounds.height);
        renderer.setColor(Color.RED);
        for (Duck duck : ducks) {
            renderer.rect(duck.bounds.x, duck.bounds.y, duck.bounds.width, duck.bounds.height);
        }
        renderer.setColor(Color.GREEN);
        for (Apple apple : apples) {
            renderer.rect(apple.bounds.x, apple.bounds.y, apple.bounds.width, apple.bounds.height);
        }
        if (goldenApple != null) {
            renderer.setColor(Color.GOLD);
            renderer.rect(goldenApple.bounds.x, goldenApple.bounds.y, goldenApple.bounds.width, goldenApple.bounds.height);
        }
        if (isBulletFired) {
            renderer.setColor(Color.YELLOW);
            renderer.rect(bullet.bounds.x, bullet.bounds.y, bullet.bounds.width, bullet.bounds.height);
        }
    }

    public void update(float delta) {
        memoryInfo.update();
        if(goldenApple!=null) {
            peApple.setPosition(goldenApple.bounds.x + goldenApple.bounds.width / 2f, goldenApple.bounds.y);
            peApple.update(delta);
        }
        if (GoldenApple.isTimeToSpawnNewApple()) {
            goldenApple = GoldenApple.spawnApple();
        }
        if (goldenApple != null) {
            goldenApple.update(delta);
            if (goldenApple.isAppleOutOfBounds()) {
                peApple.reset();
                goldenApple = null;
            }
            if (goldenApple != null) {
                if (worm.isCollisionWithGoldenApple(goldenApple)) {
                    peApple.reset();
                    goldenApple = null;
                }
            }
        }

        worm.update(delta);
        if (isBulletFired) {
            bullet.update(delta);
            peBullet.setPosition(bullet.bounds.x + bullet.bounds.width / 2f, bullet.bounds.y);
            if (bullet.bounds.y > WORLD_HEIGHT) {
                peBullet.reset();
                isBulletFired = false;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isBulletFired) {
            isBulletFired = true;
            Assets.bulletSound.play();
            bullet = new Bullet(worm.bounds.x + worm.bounds.width / 2f - Assets.bulletImg.getWidth() / 2f, worm.bounds.y + worm.bounds.height);
        }

        if (isTimeToSpawnNewDuck(pauseDuration)) {
            Duck duck = duckPool.obtain();
            duck.init(MathUtils.random(0, WORLD_WIDTH - Assets.duckImg.getWidth()), WORLD_HEIGHT);
            ducks.add(Duck.spawnDuck());
        }
        if (isTimeToSpawnNewApple(pauseDuration)) {
            Apple apple = applePool.obtain();
            apple.init(MathUtils.random(0, WORLD_WIDTH - Assets.appleImg.getWidth()), WORLD_HEIGHT);
            apples.add(Apple.spawnApple());
        }

        for (Duck duck : ducks) {
            duck.update(Gdx.graphics.getDeltaTime());

            if (worm.isCollisionWithDuck(duck)) {
                if (health <= 0) return;
                ducks.removeValue(duck, true);
                duckPool.free(duck);
            }

            if (isDuckOutOfBounds(duck)) {
                ducks.removeValue(duck, true);
                duckPool.free(duck);
            }

            if (isBulletFired && isDuckHitByBullet(duck, bullet)) {
                Assets.duckVoice.play();
                ducksKilled++;
                ducks.removeValue(duck, true);
                duckPool.free(duck);
                isBulletFired = false;
                peBullet.reset();
            }
        }

        for (Apple apple : apples) {
            apple.update(Gdx.graphics.getDeltaTime());
            if (worm.isCollisionWithApple(apple)) {
                applePool.free(apple);
                apples.removeValue(apple, true);
            }

            if (isAppleOutOfBounds(apple)) {
                apples.removeValue(apple, true);
                applePool.free(apple);
            }
        }
    }

    @Override
    public void dispose() {
        Assets.dispose();
        assetManager.dispose();
        peApple.dispose();
        peBullet.dispose();
    }


    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePauseState();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) debug = !debug;
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            handleGameReset();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isBulletFired && gameState == GameState.PLAYING) {
            fireBullet();
        }
    }

    private void togglePauseState() {
        gameState = (gameState == GameState.PLAYING) ? GameState.PAUSED : GameState.PLAYING;

        if (gameState == GameState.PAUSED) {
            pauseStartTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
            pauseDuration = 0;
        } else {
            float pauseEndTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) / 1000f;
            pauseDuration += (pauseEndTime - pauseStartTime);
        }
    }

    private void fireBullet() {
        isBulletFired = true;
        Assets.bulletSound.play();
        bullet = new Bullet(worm.bounds.x + worm.bounds.width / 2f - Assets.bulletImg.getWidth() / 2f,
                worm.bounds.y + worm.bounds.height);
    }


    private void handleGameReset() {
        health = 100;
        applesCollected = 0;
        ducksKilled = 0;
        duckPool.freeAll(ducks);
        applePool.freeAll(apples);
        ducks.clear();
        apples.clear();
        worm.bounds.x = viewport.getWorldWidth() / 2f - Assets.wormImg.getWidth() / 2f;
        worm.bounds.y = 20;
        goldenApple = null;
        ducks.add(Duck.spawnDuck());
        apples.add(Apple.spawnApple());
        gameState = GameState.PLAYING;
    }

    private void renderGameOver() {
        drawText(batch, Color.RED, "GAME OVER", hudViewport.getWorldWidth() / 2f - 60f, hudViewport.getWorldHeight() / 2f);
        drawText(batch, Color.RED, "Press R to restart", hudViewport.getWorldWidth() / 2f - 60f, hudViewport.getWorldHeight() / 2f - 30f);
    }

    private void renderGameElements() {
        if (debug) {
            debugCameraController.handleDebugInput(Gdx.graphics.getDeltaTime());
            debugCameraController.applyTo(camera);
        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        ScreenUtils.clear(0, 0, 0, 1);
        batch.draw(gameplayAtlas.findRegion(RegionNames.BACKGROUND), WORLD_WIDTH / 2f - bg.getWidth() / 2f - 50f, (float) (WORLD_HEIGHT - bg.getHeight() / 2f - bg.getHeight() * 0.44));

        worm.render(batch);
        if(goldenApple!=null) peApple.draw(batch, Gdx.graphics.getDeltaTime());
        if (isBulletFired) {
            bullet.render(batch);
            peBullet.draw(batch, Gdx.graphics.getDeltaTime());
        }
        for (Duck duck : ducks) duck.render(batch);
        for (Apple apple : apples) apple.render(batch);
        if (goldenApple != null) goldenApple.render(batch);
        batch.end();
        hudViewport.apply();
        batch.setProjectionMatrix(hudViewport.getCamera().combined);
        batch.begin();
        renderHUD();
        batch.end();
        if(peApple.isComplete() && goldenApple!=null) peApple.reset();

        if (debug) renderDebug();

    }

    private void renderHUD() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        String screenSize = "Screen/Window size: " + screenWidth + " x " + screenHeight + " px";
        String worldSize = "World size: " + (int) worldWidth + " x " + (int) worldHeight + " world units";
        String oneWorldUnit = "One world unit: " + (screenWidth / worldWidth) + " x " + (screenHeight / worldHeight) + " px";
        String debugCameraMessage = debug ? "Debug Camera: ON" : "Debug Camera: OFF";

        if (gameState == GameState.PAUSED) {
            drawText(batch, Color.RED, "PAUSED", hudViewport.getWorldWidth() / 2f - 60f, hudViewport.getWorldHeight() / 2f);
            return;
        }
        if (gameState == GameState.GAME_OVER) {
            renderGameOver();
            return;
        }
        drawText(batch, Color.RED, "Health: " + health, 20f, hudViewport.getWorldHeight() - 20f);
        drawText(batch, Color.SLATE, "Score: " + applesCollected, 20f, hudViewport.getWorldHeight() - 50f);
        drawText(batch, Color.DARK_GRAY, "Ducks killed: " + ducksKilled, 20f, hudViewport.getWorldHeight() - 80f);
        if (worm.isDoublePointsActive()) {
            drawText(batch, Color.GOLD, "Double points active", 20f, hudViewport.getWorldHeight() - 110f);
        }
        drawText(batch, Color.BLACK, screenSize, 20f, hudViewport.getWorldHeight() - 140f);
        drawText(batch, Color.BLACK, worldSize, 20f, hudViewport.getWorldHeight() - 170f);
        drawText(batch, Color.BLACK, oneWorldUnit, 20f, hudViewport.getWorldHeight() - 200f);
        drawText(batch, Color.BLACK, debugCameraMessage, 20f, hudViewport.getWorldHeight() - 230f);

        if (debug) drawMemoryInfo(memoryInfo, batch);
    }
}