# Resources: Data Excerpts  

We use two real-world datasets for our evaluation, QnV-Data and AirQuality-Data. Below, we introduce both data sources. All data samples used in our evaluation are available [here](https://tubcloud.tu-berlin.de/s/myMHrc5Hi6MtSMa).

## QnV-Data 

QnV-Data represents traffic congestion management
data that includes sensor readings from almost 2.5k road segments
in Hessen (Germany) with a frequency of one minute. Each tuple
contains the number of cars, i.e., quantity (𝑄), and their average
speed, i.e., velocity (𝑉), for one minute on a road segment. 

### Tuple: 
```
<id, time, velocity, quantity>

R2000070,1543622400000,67.27777777777777,8.0
```
### Source
The data was publicly available on 𝑚𝐶𝐿𝑂𝑈𝐷, where we extracted various samples as CSV files. The data sets used for our experiments are available on the [TUBCloud](https://tubcloud.tu-berlin.de/s/myMHrc5Hi6MtSMa).
In this repository, we provide two small CSV files with one sensor each and one sample of the QnV-Data stream with multiple sensors:

- (1) R2000070 (QnV_R2000070)
- (2) R2000073 (QnV_R2000073)
- (3) QnV

## AirQuality-Data 

AirQuality-Data represents an air quality dataset that
contains two different sensor types. 𝑆𝐷𝑆011 sensors that measure
air quality, i.e., particulate matter with 𝑃𝑀10 values that indicate
particles of 10 micrometers (𝜇m) or smaller, and 𝑃𝑀2.5 values for
particles that are 2.5 𝜇m or smaller. 𝐷𝐻𝑇22 sensors provide temperature 
and humidity measurements. The sensors do not provide
a fixed frequency and collect data every three to five minutes. 

### Source
The data is publicly available on [𝑆𝑒𝑛𝑠𝑜𝑟.𝑐𝑜𝑚𝑚𝑢𝑛𝑖𝑡𝑦](https://sensor.community/de/), i.e., the data is available under the following link: https://archive.sensor.community. 
In the directory _data_ we provide the phyton code used to collect the data.  
In this repository, we provide two CSV files, one for each sensor type:

- (1) 𝑆𝐷𝑆011 (luftdaten_11245, including headers)
- (2) 𝐷𝐻𝑇22 (luftdaten_11246, including headers)

Note: This folder only contains small samples of the data. Please, check [TUBCloud](https://tubcloud.tu-berlin.de/s/myMHrc5Hi6MtSMa) for the large samples used in our experiments. 
