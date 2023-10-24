package bouncingball;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class BouncingBall extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private ArrayList<Ball> balls;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        balls = new ArrayList<>();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float radius = MathUtils.random(20, 80);
                Color color = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
                Ball ball = new Ball(screenX, Gdx.graphics.getHeight() - screenY, radius, color);
                balls.add(ball);
                return true;
            }
        });
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Ball ball : balls) {
            ball.update();
            ball.draw(shapeRenderer);
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

}