# Query Catalog

## Q1 Sequence Pattern

#### Pattern 
```
PATTERN SEQ(V v1, Q q1)
WHERE v1. value > velFilter && q1. value > quaFilter && distance(q1,v1) < 10.0 (km)
WITHIN Minutes(windowSize) 
```
#### Query
```
SELECT *
FROM velocityStream V JOIN quantityStream Q ON V.ts < Q.ts
WHERE V. value > velFilter && Q. value > quaFilter && distance(V,Q) < 10.0 (km)
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
```
## Q2 Conjunction Pattern

#### Pattern
```
PATTERN AND(V v1, Q q1)
WHERE v1. value > velFilter && q1. value > quaFilter && distance(q1,v1) < 10.0 (km)
WITHIN Minutes(windowSize) 
```
#### Query
```
SELECT *
FROM velocityStream V, quantityStream Q 
WHERE V. value > velFilter && Q. value > quaFilter && distance(Q,V) < 10.0 (km)
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
```
## Q3 Disjunction Pattern

#### Pattern
```
PATTERN OR(V v1, Q q1)
WHERE v1. value > velFilter && q1. value > quaFilter
WITHIN Minutes(windowSize) 
```
#### Query
```
SELECT *
FROM velocityStream V
WHERE V. value > velFilter
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
UNION
SELECT *
FROM quantityStream Q
WHERE Q. value > quaFilter
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
```
## Q4 Nested Disjunction Pattern

#### Pattern
```
PATTERN SEQ(Q q1, OR(PM10 pm10, PM2 pm2))
WHERE q1.value > quaFilter && pm2.value > pm2Filter && pm10.value > pm10Filter
WITHIN Minutes(windowSize) 
```
#### Query
```
SELECT *
FROM    (SELECT * 
        FROM quantityStream Q, partMatter PM10
        WHERE Q.value > quaFilter && PM10.value > pm10Filter
        UNION
        SELECT *
        FROM quantityStream Q, partMatter PM2
        WHERE Q.value > quaFilter && PM2.value > pm2Filter) 
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
```
## Q5 Nested Negation Pattern (NSEQ)
#### Pattern
``` 
PATTERN SEQ (Q q1 , ¬V v1 , PM2 p2)
WHERE (q1 . value > quaFilter && v1 . value < velFilter) && p2 . value > pm2Filter) 
WITHIN Minutes(windowSize) 
```
#### Query
``` 
SELECT *
FROM quantityStream Q, pm2Stream PM2
WHERE Q.value > quaFilter && PM2.value > pm2Filter && 
    NOT EXISTS (SELECT *
                FROM velocityStream V 
                WHERE  V.value > velFilter && V.ts < PM2.ts && V.ts > Q.ts)
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
```

## Q6 Iteration Pattern (interevent condition (I with O1 and O2))
#### Pattern
``` 
PATTERN V v1[n]
WHERE v[i].value > v[].value 
WITHIN Minutes(windowSize) 
```
#### Query
``` sql
SELECT *
FROM velocityStream V1 JOIN velocityStream V2 ON V1.ts < V2.ts
                       JOIN ...
                       JOIN velocityStream Vn ON Vn-1.ts < Vn.ts
WHERE V2.value > V1.value && ... && Vn.value > Vn-1.value
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
```

## Q7 Iteration Pattern (simple threshold filter (O3))
#### Pattern
``` 
PATTERN V v1[n]
WHERE v[i].value > velFilter
WITHIN Minutes(windowSize) 
```
#### Query
``` sql
SELECT *
FROM velocityStream V 
WHERE V.value > velFilter && 
    (SELECT COUNT(*)
     FROM velocityStream V ) >= n 
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
```
## Q8 Sequence Pattern

#### Pattern
```
PATTERN SEQ(V v1, Q q1)
WHERE v1. value > velFilter && q1. value > quaFilter && v1.id = q1.id
WITHIN Minutes(windowSize) 
```
#### Query
```
SELECT *
FROM velocityStream V JOIN quantityStream Q ON V.ts < Q.ts
WHERE V. value > velFilter && Q. value > quaFilter && Q.id = V.id
WINDOW RANGE[ Minutes(windowSize), Minutes(1) ]
```


## Q9 Sequence Pattern with pattern length 2 [to 6] 

#### Pattern
```
PATTERN SEQ(Q q1, V v1 [,PM2 pm2, PM10 pm10, Temp t1, Hum h1])
WHERE  q1.value > quaFilter && v1.value > velFilter  
    [&& pm2.value > pm2Filter  && pm10.value > pm10Filter  
    && temp.value > tempFilter && hum.value > humFilter]
WITHIN Minutes(windowSize)
```
#### Query
```
SELECT *
FROM quantityStream Q, velocityStream V [, partMatter PM10, partMatter PM2, temperaturStream T, humidityStream H]
WHERE  Q.value > quaFilter && V.value > velFilter
    [&& PM2.value > pm2Filter && PM10.value > pm10Filter 
    && T.value > tempFilter && H.value > humFilter]
    && Q.ts > V.ts 
    [&& V.ts > PM10.ts && PM2.ts > PM10.ts && PM10.ts > T.ts && T.ts > H.ts] 
WINDOW RANGE[ Minutes(windowSize,1), Minutes(1) ]
```
## Q10 Sequence Pattern with pattern length 3

#### Pattern
```
PATTERN SEQ(V v1, Q q1, PM10 pm10)
WHERE  q1.value > quaFilter && v1.value > velFilter && pm10.value > pm10Filter
&& q1.id = v1.id && q1.id = om10.id
WITHIN Minutes(windowSize)
```
#### Query
```
SELECT *
FROM velocityStream V, quantityStream Q, partMatter PM10
WHERE  Q.value > quaFilter && V.value > velFilter && PM10.value > pm10Filter
&& V.ts > Q.ts && Q.ts > PM10.ts  
&& q1.id = v1.id && q1.id = om10.id
WINDOW RANGE[ Minutes(windowSize,1), Minutes(1) ]
```

we can reorder SEQ(V, Q, PM10) to SEQ(SEQ(V,PM10),Q) adding additional constraints:
```
V.ts > PM10.ts &&  pm10.id = v1.id
```

## Q11 Iteration Pattern with pattern length 4

#### Pattern
```
PATTERN V v1[4] // ITER^4(V)  
WHERE  v1.value > velFilter && v1.id = v1[].id 
WITHIN Minutes(windowSize)
```
#### Query
```
SELECT *
FROM velocityStream V1 JOIN velocityStream V2 ON V1.id = V2.id
                       JOIN velocityStream V3 ON V2.id = V3.id
                       JOIN velocityStream V3 ON V3.id = V4.id
WHERE  V1.value > velFilter && V2.value > velFilter 
    && V3.value > velFilter && V4.value > velFilter
    && V1.ts > V2.ts && V2.ts > V3.ts && V3.ts > V4.ts
WINDOW RANGE[ Minutes(windowSize,1), Minutes(1) ]
```
