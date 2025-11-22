from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.operators.empty import EmptyOperator
from datetime import datetime, timedelta

def dummy_etl():
    print("Ejecutando proceso ETL simulado...")

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2024, 1, 1),
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

with DAG(
    'etl_promocion_estudiantil',
    default_args=default_args,
    description='Un DAG simple de ejemplo',
    schedule_interval=timedelta(days=1),
    catchup=False,
) as dag:

    start = EmptyOperator(task_id='start')

    etl_task = PythonOperator(
        task_id='etl_dummy',
        python_callable=dummy_etl,
    )

    end = EmptyOperator(task_id='end')

    start >> etl_task >> end
