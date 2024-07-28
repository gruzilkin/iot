import os, json, asyncio
import board, busio, adafruit_scd30, adafruit_sgp40
import websockets


i2c = busio.I2C(board.SCL, board.SDA, frequency=50000)
scd = adafruit_scd30.SCD30(i2c)
sgp = adafruit_sgp40.SGP40(i2c)

temperature, humidity = None, None

async def sender(queue):
    host = os.environ['HOST']
    bearer = os.environ['BEARER']

    url = f"ws://{host}/ws/telemetry"
    print(f"Connecting to {url}")

    async with websockets.connect(
            url, extra_headers={"Authorization": f"Bearer {bearer}"}
    ) as websocket:
        while True:
            name, data = await queue.get()
            message = { name: data}
            await websocket.send(json.dumps(message))
            print(f"Sent: {message}")

async def read_sgp40(queue):
    while True:
        if temperature and humidity:
            voc_index = sgp.measure_index(temperature=temperature, relative_humidity=humidity)
            if voc_index != 0:
                await queue.put(("voc", voc_index))
        await asyncio.sleep(1)

async def read_scd30(queue):
    while True:
        if scd.data_available:
            global temperature, humidity
            temperature = scd.temperature
            humidity = scd.relative_humidity
            await queue.put(("temperature", temperature))
            await queue.put(("humidity", humidity))
        await asyncio.sleep(2.1)

def init_sensors():
    temperature_offset = os.environ['TEMPERATURE_OFFSET']
    if temperature_offset:
        scd.temperature_offset = int(temperature_offset)

    altitude = os.environ['ALTITUDE']
    if altitude:
        scd.altitude = int(altitude)

    print("SCD30 Temperature offset: ", scd.temperature_offset)
    print("SCD30 Measurement interval: ", scd.measurement_interval)
    print("SCD30 Self-calibration enabled: ", scd.self_calibration_enabled)
    print("SCD30 Ambient Pressure: ", scd.ambient_pressure)
    print("SCD30 Altitude: ", scd.altitude, " meters above sea level")

async def main():
    init_sensors()

    try:
        queue = asyncio.Queue()
        coros = [read_sgp40(queue), read_scd30(queue), sender(queue)]
        tasks = [asyncio.create_task(coro) for coro in coros]
        await asyncio.gather(*tasks)
    finally:
        for task in tasks:
            task.cancel()
        await asyncio.gather(*tasks, return_exceptions=True)

asyncio.run(main())