package cubyz.client.renderUniverse;

import org.joml.Matrix3f;
import org.joml.Vector2d;

import cubyz.Settings;
import cubyz.rendering.Input;

public class Camera {
	private static float upRotation, horizontalRotation;
	private static final Vector2d lastMousePosition = new Vector2d();
	public static Matrix3f getMatrixAndUpdate() {
		float deltaX = (float)(Input.mousePosition.x - lastMousePosition.x)*Settings.MOUSE_SENSITIVITY;
		float deltaY = (float)(Input.mousePosition.y - lastMousePosition.y)*Settings.MOUSE_SENSITIVITY;
		upRotation += deltaY;
		upRotation = Math.min((float)Math.PI, upRotation);
		upRotation = Math.max(-(float)Math.PI, upRotation);
		horizontalRotation += deltaX;
		lastMousePosition.set(Input.mousePosition);
		
		return new Matrix3f().identity().rotateX(upRotation).rotateY(horizontalRotation);
	}
}
