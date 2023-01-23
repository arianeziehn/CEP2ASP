# Util Folder

In this folder, you can find basic and repetitively used classes for the patterns and queries, e.g., KeySelector or TimeStampAssigners. 

## Event types 

**HumidityEvent, QuantityEvent, TemperatureEvent, Part(icular)Matter2Event, 
Part(icular)Matter10Event**, and **VelocityEvent** are the six used event types for our experiments.
They inherit from KeyedDataPointGeneral (Base: DataPoint) and can be extended further if required.

## SourceFunction 

The **KeyedDataPointSourceFunction** is the general source that handles all in /src/main/resources
provided files. If you change the format of the files make sure you write your own source
function or adjust the provided Source accordingly. 

The **KeyedDataPointParallelSourceFunction** is a special source for the single sensors files, i.e., R200070 and R200073. 
We use this source for our scalability experiments as this data source is executed in parallel. 

## Metrics

We use the **ThroughputLogger** class to monitor the throughput of our application. Be aware that placing
the logger needs to be done carefully as it may cause additional shuffling processes otherwise. 
