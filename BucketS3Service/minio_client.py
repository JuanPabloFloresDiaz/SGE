import boto3
import os
from dotenv import load_dotenv
from typing import Optional

load_dotenv()

class MinioClient:
    _s3_client = None

    def __init__(self):
        try:
            self._s3_client = boto3.client(
                's3',
                endpoint_url=os.getenv("MINIO_ENDPOINT", "http://localhost:9000"),
                aws_access_key_id=os.getenv("MINIO_ROOT_USER", "minioadmin"),
                aws_secret_access_key=os.getenv("MINIO_ROOT_PASSWORD", "minioadmin")
            )
        except Exception as e:
            print(f"Error initializing MinioClient: {e}")
            raise

    def create_bucket(self, bucket_name: str) -> None:
        """Creates a new bucket if it doesn't exist."""
        try:
            self._s3_client.head_bucket(Bucket=bucket_name)
            print(f"Bucket '{bucket_name}' already exists.")
        except:
            try:
                self._s3_client.create_bucket(Bucket=bucket_name)
                print(f"Bucket '{bucket_name}' created successfully.")
            except Exception as e:
                print(f"Error creating bucket '{bucket_name}': {e}")

    def upload_file(self, file_name: str, bucket: str, object_name: Optional[str] = None) -> None:
        """Uploads a file to the specified bucket."""
        if object_name is None:
            object_name = os.path.basename(file_name)
        try:
            self._s3_client.upload_file(file_name, bucket, object_name)
            print(f"File '{file_name}' uploaded to '{bucket}/{object_name}'.")
        except Exception as e:
            print(f"Error uploading file '{file_name}': {e}")

    def download_file(self, bucket: str, object_name: str, file_name: str) -> None:
        """Downloads a file from the specified bucket."""
        try:
            self._s3_client.download_file(bucket, object_name, file_name)
            print(f"File '{bucket}/{object_name}' downloaded to '{file_name}'.")
        except Exception as e:
            print(f"Error downloading file '{object_name}': {e}")

    def delete_file(self, bucket: str, object_name: str) -> None:
        """Deletes a file from the specified bucket."""
        try:
            self._s3_client.delete_object(Bucket=bucket, Key=object_name)
            print(f"File '{bucket}/{object_name}' deleted.")
        except Exception as e:
            print(f"Error deleting file '{object_name}': {e}")
