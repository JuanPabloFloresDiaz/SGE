FROM python:3.10-slim

# Set environment variables
ENV AIRFLOW_HOME=/opt/airflow

# Install system dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

# Set work directory
WORKDIR $AIRFLOW_HOME

# Copy requirements
COPY requirements.txt .

# Install python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Esto DEBE hacerse en el Dockerfile si Airflow lo requiere
RUN chown -R 1000:0 /opt/airflow 

# Establecer el usuario de Airflow
USER 1000