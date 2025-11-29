# Usar una imagen base de Python oficial y ligera
FROM python:3.10-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo de requerimientos al contenedor
COPY requirements.txt .

# Instalar las dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el código fuente de la aplicación al contenedor
COPY . .

# Exponer el puerto en el que correrá Uvicorn (8015)
EXPOSE 8015

# Comando para ejecutar la aplicación usando Uvicorn
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8015"]
