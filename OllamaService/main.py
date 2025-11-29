# OllamaService/main.py

from fastapi import FastAPI

# 1. Crea una instancia de la aplicación FastAPI
app = FastAPI()

# 2. Define un endpoint básico para verificar que la API funciona
@app.get("/")
def read_root():
    return {"message": "OllamaService (FastAPI) is running!"}

# 3. Aquí irán los endpoints para interactuar con Ollama...