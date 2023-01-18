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
WITHIN Minutes(windowSize)
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
WITHIN Minutes(windowSize)
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
WITHIN Minutes(windowSize)
UNION
SELECT *
FROM quantityStream Q
WHERE Q. value > quaFilter
WITHIN Minutes(windowSize)
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
WITHIN Minutes(windowSize)
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
WITHIN Minutes(windowSize)
```

## Q6 Iteration Pattern (interevent condition (I1))
#### Pattern
``` 
PATTERN V v1[n]
WHERE v[i].value > v[i-1].value 
WITHIN Minutes(windowSize) 
```
#### Query
``` sql
SELECT *
FROM velocityStream V1 JOIN velocityStream V2 ON V1.ts < V2.ts
                       JOIN ...
                       JOIN velocityStream Vn ON Vn-1.ts < Vn.ts
WHERE V2.value > V1.value && ... && Vn.value > Vn-1.value
WITHIN Minutes(windowSize)
```

## Q7 Iteration Pattern (simple threshold filter (I2))
#### Pattern
``` 
PATTERN V v1[n]
WHERE v[i].value >  velFilter
WITHIN Minutes(windowSize) 
```
#### Query
``` sql
SELECT *
FROM velocityStream V 
WHERE V.value > velFilter && 
    (SELECT COUNT(*)
     FROM velocityStream V ) = n 
WITHIN Minutes(windowSize)
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
WITHIN Minutes(windowSize)
```


## Q9 Sequence Pattern with pattern length 2 [to 6] 

#### Pattern
```
PATTERN SEQ(Q q1, V v1, [PM2 pm2, PM10 pm10, Temp t1, Hum h1])
WHERE q1.value > quaFilter && pm2.value > pm2Filter && pm10.value > pm10Filter && v1.value > velFilter && temp.value > tempFilter && hum.value > humFilter
WITHIN Minutes(windowSize)
```
#### Query
```
SELECT *
FROM quantityStream Q, velocityStream V, partMatter PM10, partMatter PM2, temperaturStream T, humidityStream H
WHERE Q.value > quaFilter && PM2.value > pm2Filter && PM10.value > pm10Filter && V.value > velFilter && T.value > tempFilter && H.value > humFilter
 && Q.ts > V.ts && V.ts > PM10.ts && PM2.ts > PM10.ts && PM10.ts > T.ts && T.ts > H.ts 
WITHIN Minutes(windowSize)
```
