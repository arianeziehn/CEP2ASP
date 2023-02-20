# Resources: Data Excerpts  

We use two real-world datasets for our evaluation:

## QnV-Data 

QnV-Data represents traffic congestion management
data that includes sensor readings from almost 2.5k road segments
in Hessen (Germany) with a frequency of one minute. Each tuple
contains the count of cars, i.e., quantity (𝑄), and their averaged
speed, i.e., velocity (𝑉), for one minute on a road segment. 

### Tuple: 
```
<id, time, velocity, quantity>

R2000070,1543622400000,67.27777777777777,8.0
```
### Source
The data is publicly available on [𝑚𝐶𝐿𝑂𝑈𝐷](https://www.mcloud.de/web/guest/suche/-/results/filter/latest/provider%3AHessen+Mobil+-+Stra%C3%9Fen-+und+Verkehrsmanagement/0/detail/_mcloudde_mdmgeschwindigkeitsdatenhessen) 
We provide two csv files for two sensors of one road segment (one per direction):

- (1) R2000070 (QnV_R2000070)
- (2) R2000073 (QnV_R2000073)

## AirQuality-Data 

AirQuality-Data represents an air quality dataset that
contains two different sensor types. 𝑆𝐷𝑆011 sensors that measure
air quality, i.e., particulate matter with 𝑃𝑀10 values that indicate
particles of 10 micrometers (𝜇m) or smaller, and 𝑃𝑀2.5 values for
particles that are 2.5 𝜇m or smaller. 𝐷𝐻𝑇22 sensors provide temperature 
and humidity measurements. The sensors do not provide
a fix frequency and collect data every three to five minutes. 

### Source
The data is publicly available on [𝑆𝑒𝑛𝑠𝑜𝑟.𝑐𝑜𝑚𝑚𝑢𝑛𝑖𝑡𝑦](https://sensor.community/de/)
We provide two csv files, one for each of the sensors:

- (1) 𝑆𝐷𝑆011 (luftdaten_11245, including headers)
- (2) 𝐷𝐻𝑇22 (luftdaten_11246, including headers)

Note: This folder only contains small samples, we uploaded the large samples of our experiments, i.e., QnV_large.csv (Baseline and Parameters) and QnV_R2000070_i.csv (Scalability Exp.), [to gofile.io](https://gofile.io/d/pjglkV).

**Warning** 
We are currently observing the problem that data links are not consistently updated in the anonymous repository, the link is: https://gofile.io/d/pjglkV.  

