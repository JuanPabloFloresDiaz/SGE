# BucketS3Service Integration Guide

This service provides an S3-compatible object storage simulation using MinIO. It is designed to be used as a microservice for storing files and images.

## Prerequisites

- **Docker**: Ensure Docker and Docker Compose are installed and running.
- **Python 3.12+**: Required for running the Python scripts.

## Setup

1.  **Navigate to the service directory:**
    ```bash
    cd BucketS3Service
    ```

2.  **Create and activate the virtual environment:**
    ```bash
    python3 -m venv venv
    source venv/bin/activate
    ```

3.  **Install dependencies:**
    ```bash
    pip install -r requirements.txt
    # Or if using pyproject.toml directly (as currently set up):
    pip install boto3 pandas python-dotenv
    ```

4.  **Start MinIO:**
    ```bash
    docker-compose up -d
    ```
    - Console: [http://localhost:9001](http://localhost:9001)
    - API: [http://localhost:9000](http://localhost:9000)
    - User: `minioadmin`
    - Password: `minioadmin`

## Usage

### Running the Example

The `main.py` script demonstrates how to create a bucket, upload a file, download it, and clean up.

```bash
python main.py
```

### Integration with Python

Use the `MinioClient` class in `minio_client.py` to interact with the service.

```python
from minio_client import MinioClient

client = MinioClient()

# Create a bucket
client.create_bucket("my-bucket")

# Upload a file
client.upload_file("path/to/local/file.txt", "my-bucket", "remote-object-name.txt")

# Download a file
client.download_file("my-bucket", "remote-object-name.txt", "path/to/save/file.txt")

# Delete a file
client.delete_file("my-bucket", "remote-object-name.txt")
```

## Future Integration with Main API

To integrate this service with your main API (e.g., Spring Boot):

1.  **Configuration**: Configure your API to use the MinIO endpoint (`http://localhost:9000`) instead of AWS S3.
2.  **Credentials**: Use the same credentials defined in `docker-compose.yml` (or `.env`).
3.  **SDK**: Use the AWS SDK for your language (e.g., AWS SDK for Java) to interact with MinIO, as it is S3-compatible.
