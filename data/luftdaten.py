# python -m pip install zipfile pendulum requests pandas
from pendulum import date, period, today
from zipfile import ZipFile
from pathlib import Path
from io import BytesIO
import pandas as pd
import requests, io
import traceback
import sys

url = lambda date: f'https://archive.sensor.community/csv_per_month/{date.format("Y-MM/Y-MM")}_dht22.zip'

for date in period(date(2016, 1, 1), today().date()).range('months'):
	print(date.format("Y/M/Y-MM"))
	try:
		if Path(f'data/{date.format("Y/M/Y-MM")}_dht22.csv').exists(): continue
		_url = url(date)
		response = requests.get(_url, stream=True)
		match response.status_code:
			case 200:
				folder = Path(f'data/{date.format("Y/MM/")}')
				folder.mkdir(exist_ok=True, parents=True)
				z = ZipFile(BytesIO(response.content))
				z.extractall(folder)
			case another:
				print(f'File not found: {_url}')
	except KeyboardInterrupt:
		sys.exit(0)
	except:
		traceback.print_exc(1)

# Please note that some of the monthly records have size of ~4Gb
df = pd.concat([pd.read_csv(f, sep=';', engine='python') for f in Path('data/').glob('**/*.csv')])
df.dropna().to_csv('data/luftdaten.csv', index=False)