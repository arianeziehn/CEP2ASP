# Query Catalog

## Q1 Sequence Pattern

### Pattern 
**PATTERN** SEQ(V v1, Q q1)

**WHERE** v1. value > **velFilter** && q1. value > **quaFilter** 
&& distance(q1,v1) < 10.0 (km)

**WITHIN** Minutes(15) 

### Query
**SELECT** * 

**FROM** velocityStream V **JOIN** quantityStream Q **ON** V.ts < Q.ts

**WHERE** v1. value > **velFilter** && q1. value > **quaFilter**
&& distance(q1,v1) < 10.0 (km)

## Q2 Conjunction Pattern

### Pattern
**PATTERN** AND(V v1, Q q1)

**WHERE** v1. value > **velFilter** && q1. value > **quaFilter**
&& distance(q1,v1) < 10.0 (km)

**WITHIN** Minutes(15)

### Query
**SELECT** *

**FROM** velocityStream V, quantityStream Q 

**WHERE** v1. value > **velFilter** && q1. value > **quaFilter**
&& distance(q1,v1) < 10.0 (km)