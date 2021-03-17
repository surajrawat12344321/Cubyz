package io.cubyz.base.entity_models;

import org.joml.Matrix4f;

import io.cubyz.entity.Entity;
import io.cubyz.entity.EntityModel;

public interface ClientEntityModel extends EntityModel {
	public void render(Matrix4f viewMatrix, Object shaderProgram, Entity ent);
}
