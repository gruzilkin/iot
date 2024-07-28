FROM python:3.12-slim

WORKDIR /app

RUN apt-get update && apt-get install -y gcc i2c-tools && rm -rf /var/lib/apt/lists/*

COPY requirements.stable.txt requirements.stable.txt
RUN pip3 install -r requirements.stable.txt

COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY ./src/ .

EXPOSE 5678

CMD python3 app.py
