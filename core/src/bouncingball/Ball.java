package bouncingball;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

public class Ball {
    private static final float GRAVITY = 1000;
    private static final float RESTITUTION = 0.8f;

    private final Circle circle;
    private final Color color;
    private float velocityY = 500;

    public Ball(float x, float y, float radius, Color color) {
        circle = new Circle(x, y, radius);
        this.color = color;
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        velocityY += GRAVITY * deltaTime;
        circle.y -= velocityY * deltaTime;

        if (circle.y - circle.radius < 0) {
            velocityY = -velocityY * RESTITUTION;
            circle.y = circle.radius;
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(circle.x, circle.y, circle.radius);
    }
}
