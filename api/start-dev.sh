#!/bin/bash

# Script para iniciar el entorno de desarrollo de SGE API

echo "🚀 Iniciando SGE API - Sistema de Gestión Educativa"
echo "=================================================="
echo ""

# Verificar si Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Error: Docker no está instalado"
    echo "Por favor instala Docker desde: https://docs.docker.com/get-docker/"
    exit 1
fi

# Verificar si Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Error: Docker Compose no está instalado"
    echo "Por favor instala Docker Compose desde: https://docs.docker.com/compose/install/"
    exit 1
fi

echo "✅ Docker y Docker Compose están instalados"
echo ""

# Levantar MySQL
echo "📦 Levantando MySQL en Docker (puerto 3311)..."
docker-compose up -d

# Esperar a que MySQL esté listo
echo "⏳ Esperando a que MySQL esté listo..."
sleep 10

# Verificar el estado
echo ""
echo "📊 Estado de los servicios:"
docker-compose ps

echo ""
echo "✅ MySQL está listo!"
echo ""
echo "Puedes conectarte a MySQL con:"
echo "  Host: localhost"
echo "  Puerto: 3311"
echo "  Usuario: root"
echo "  Password: root"
echo "  Base de datos: SGE"
echo ""
echo "Para iniciar la aplicación Spring Boot, ejecuta:"
echo "  mvn spring-boot:run"
echo ""
echo "O desde tu IDE, ejecuta la clase ApiApplication.java"
echo ""
