package wheelsrotating;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class RollingWheelSimulation extends ApplicationAdapter {
    Wheel wheel;

    @Override
    public void create () {
        wheel = new Wheel(50, 50, 50);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        wheel.update(Gdx.graphics.getDeltaTime());
        wheel.draw();
    }

    @Override
    public void dispose () {
    }

    public static class Wheel {
        private final ShapeRenderer shapeRenderer;
        private float angle;
        private float x;
        private final float y;
        private final float radius;
        private final float circumference;
        private boolean movingRight = true;

        public Wheel(float x, float y, float radius) {
            this.shapeRenderer = new ShapeRenderer();
            this.radius = radius;
            this.circumference = 2 * MathUtils.PI * radius;
            this.x = x;
            this.y = y;
        }

        public void update(float delta) {
            // Speed of the wheel
            float moveSpeed = 150;
            float moveDistance = moveSpeed * delta;
            x += movingRight ? moveDistance : -moveDistance;

            if (x + radius > Gdx.graphics.getWidth() && movingRight) {
                movingRight = false;
            } else if (x - radius < 0 && !movingRight) {
                movingRight = true;
            }

            angle -= (moveDistance / circumference) * 360 * (movingRight ? 1 : -1);
        }

        public void draw() {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.circle(x, y, radius);

            int spokes = 8;
            for (int i = 0; i < spokes; i++) {
                float spokeAngle = angle + (i * 360f / spokes);
                float rad = spokeAngle * MathUtils.degreesToRadians;

                float cos = MathUtils.cos(rad);
                float sin = MathUtils.sin(rad);

                float x0 = x + cos * radius * 0.2f;
                float y0 = y + sin * radius * 0.2f;
                float x1 = x + cos * radius * 0.8f;
                float y1 = y + sin * radius * 0.8f;

                shapeRenderer.setColor(i % 2 == 0 ? 1 : 0.5f, 0, 0, 1);
                shapeRenderer.rectLine(x0, y0, x1, y1, 5);
            }
            shapeRenderer.end();
        }
    }
}
