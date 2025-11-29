CREATE TABLE conversaciones (
    id CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    usuario_id CHAR(36) NOT NULL,
    nombre VARCHAR(255),
    es_favorita BOOLEAN NOT NULL DEFAULT FALSE,
    es_chat_ia BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_conversaciones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

CREATE TABLE preguntas_ia (
    id CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    conversacion_id CHAR(36) NOT NULL,
    pregunta TEXT NOT NULL,
    respuesta TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_preguntas_ia_conversacion FOREIGN KEY (conversacion_id) REFERENCES conversaciones (id)
);

CREATE INDEX idx_conversaciones_usuario_id ON conversaciones(usuario_id);
CREATE INDEX idx_preguntas_ia_conversacion_id ON preguntas_ia(conversacion_id);
