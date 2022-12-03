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
WHERE Q. value > quaFilter && PM2.value > pm2Filter && 
    NOT EXISTS (SELECT *
                FROM velocityStream V 
                WHERE  V. value > velFilter && V.ts < PM2.ts && V.ts > Q.ts)
WITHIN Minutes(windowSize)
```