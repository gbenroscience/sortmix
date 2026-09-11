# ModernWittyBucketSort

### **Distribution-Based Sorting for Modern Java**

**ModernWittyBucketSort** is a high-performance distribution-based sorting algorithm for primitive floating-point arrays. It uses hierarchical bucketing and arithmetic-based routing to partition values before ordering them, with the goal of reducing the comparison and branching work associated with conventional comparison-based sorting.

The project also includes a **SIMD variant**, `SimdWittyBucketSort`, which uses Java's Vector API to accelerate parts of the bucket-sorting process.

The current benchmark results show that the approach is particularly competitive on **high-entropy random floating-point data**, while the advantage becomes smaller on highly repetitive distributions such as binary-valued arrays.

---

## 🚀 Current Benchmark Results

The most recent large-scale benchmark uses **67,106,664 floating-point elements**.

### RANDOM_FLOATS

| Algorithm                 |        Time |    Approx. throughput |
| ------------------------- | ----------: | --------------------: |
| `java.util.Arrays.sort()` |     9.598 s |      6.99M elements/s |
| ModernWittyBucketSort     |     7.504 s |      8.94M elements/s |
| **SimdWittyBucketSort**   | **5.013 s** | **13.39M elements/s** |

On this workload:

* ModernWittyBucketSort is approximately **1.28× faster** than `Arrays.sort()`.
* SimdWittyBucketSort is approximately **1.91× faster** than `Arrays.sort()`.
* SimdWittyBucketSort is approximately **1.50× faster** than the scalar ModernWittyBucketSort.
* SIMD WBS reduces measured sorting time by approximately **47.8%** compared with `Arrays.sort()`.

This is currently the strongest benchmark result for the project.

---

## Binary Data

The same 67,106,664-element workload was tested using a binary-valued floating-point distribution.

### BINARY_ARRAY_FLOATS

| Algorithm                 |       Time |     Approx. throughput |
| ------------------------- | ---------: | ---------------------: |
| `java.util.Arrays.sort()` |     622 ms |     107.85M elements/s |
| ModernWittyBucketSort     |     874 ms |      76.80M elements/s |
| **SimdWittyBucketSort**   | **560 ms** | **119.81M elements/s** |

Here, SimdWittyBucketSort is:

* approximately **1.11× faster** than `Arrays.sort()`
* approximately **1.56× faster** than ModernWittyBucketSort

The smaller advantage over `Arrays.sort()` is significant. Binary data is an exceptionally favorable distribution for a conventional sorting implementation, demonstrating that the SIMD WBS advantage is workload-dependent rather than universal.

---

## 📈 10 Million Element Results

The same behavior is visible at 10 million elements.

### RANDOM_FLOATS

| Algorithm               |        Time |
| ----------------------- | ----------: |
| `Arrays.sort()`         |     1.258 s |
| ModernWittyBucketSort   |     0.888 s |
| **SimdWittyBucketSort** | **0.708 s** |

SimdWittyBucketSort is approximately **1.78× faster** than `Arrays.sort()`.

### BINARY_ARRAY_FLOATS

| Algorithm               |        Time |
| ----------------------- | ----------: |
| `Arrays.sort()`         |    102.9 ms |
| ModernWittyBucketSort   |    135.1 ms |
| **SimdWittyBucketSort** | **88.2 ms** |

SimdWittyBucketSort is approximately **1.17× faster** than `Arrays.sort()`.

### POS_AND_NEG_RANDOM_FLOATS

| Algorithm               |       Time |
| ----------------------- | ---------: |
| `Arrays.sort()`         |    1.465 s |
| ModernWittyBucketSort   |     858 ms |
| **SimdWittyBucketSort** | **701 ms** |

SimdWittyBucketSort is approximately **2.09× faster** than `Arrays.sort()`.

An interesting observation is that the SIMD implementation produced almost identical times for the two random distributions:

* RANDOM_FLOATS: **707.9 ms**
* POS_AND_NEG_RANDOM_FLOATS: **700.6 ms**

The corresponding `Arrays.sort()` results were:

* RANDOM_FLOATS: **1.258 s**
* POS_AND_NEG_RANDOM_FLOATS: **1.465 s**

This suggests that the bucket-based approach can be comparatively insensitive to the sign distribution of these random floating-point workloads.

---

# ⚡ SIMD Variant

## SimdWittyBucketSort

`SimdWittyBucketSort` is the vectorized implementation of ModernWittyBucketSort.

It uses the **Java Vector API** to process multiple primitive floating-point values simultaneously rather than handling every value exclusively through scalar operations.

The objective is not to replace the underlying distribution-sort strategy, but to accelerate the computationally intensive portions of that strategy through data-level parallelism.

Conceptually:

```text
ModernWittyBucketSort

input
  │
  ▼
distribution analysis
  │
  ▼
hierarchical bucketing
  │
  ▼
bucket ordering
  │
  ▼
sorted array
```

The SIMD implementation retains this overall strategy while introducing vectorized processing:

```text
SimdWittyBucketSort

input
  │
  ▼
SIMD distribution analysis
  │
  ▼
vectorized bucket routing
  │
  ▼
hierarchical bucket ordering
  │
  ▼
sorted array
```

The benchmark results indicate that this vectorization is significant.

For 67.1M random floats:

```text
ModernWittyBucketSort    7.504 s
SimdWittyBucketSort      5.013 s
```

This represents approximately a **1.50× speedup** from the SIMD implementation.

For 67.1M binary floats:

```text
ModernWittyBucketSort    874 ms
SimdWittyBucketSort      560 ms
```

The SIMD speedup is approximately **1.56×**.

The exact benefit depends on the input distribution and hardware, so these figures should be treated as benchmark results rather than universal performance guarantees.

---

# 🧠 The Approach

ModernWittyBucketSort treats sorting as a **distribution problem** rather than relying exclusively on pairwise comparisons.

Instead of repeatedly asking whether one element is less than another, the algorithm uses the numerical properties of the input to route values into progressively narrower regions.

The broad strategy is:

1. Determine the relevant numerical range.
2. Map values into coarse distribution buckets.
3. Refine the distribution hierarchically.
4. Order values within the resulting regions.
5. Produce the sorted primitive array.

This allows the implementation to replace some comparison-heavy work with arithmetic and data movement.

The exact implementation details are intentionally kept separate from the public API so that the underlying sorting strategy can evolve without changing the calling code.

---

# 💾 Memory Characteristics

ModernWittyBucketSort uses fixed-size bucket metadata rather than allocating one bucket object per input element.

The sorting algorithm's auxiliary bookkeeping is therefore small relative to the input itself.

However, the distinction between **auxiliary algorithmic memory** and **total benchmark allocation** is important.

Large JMH runs of the current implementation report approximately **536.9 MB/op** for 67,106,664-element workloads. This is roughly consistent across `Arrays.sort()`, ModernWittyBucketSort, and SimdWittyBucketSort.

Therefore, the current benchmark results should **not** be interpreted as demonstrating that the complete sorting operation allocates only a few megabytes.

The accurate claim is:

> **ModernWittyBucketSort uses fixed-size bucket metadata rather than an O(n) collection of bucket objects. Its benchmark allocation footprint, however, includes the arrays and other objects involved in the benchmark and should be measured separately from the algorithm's auxiliary bookkeeping.**

This distinction is important when comparing memory behavior.

---

# 🔢 Arithmetic-Based Distribution

A central component of the algorithm is arithmetic mapping of values into distribution regions.

Conceptually, a value can be mapped using a normalized range:

```text
index = (value - min) × scale
```

where the scale is derived from the input range.

Precomputing constants can avoid repeated division in the relevant routing calculations.

The purpose is not to claim that multiplication is universally "faster than division" on every modern processor. Rather, the implementation reduces repeated expensive arithmetic in the critical path where the transformation permits it.

---

# 📐 Complexity

ModernWittyBucketSort is a distribution-based algorithm, so its practical performance depends on the numerical distribution of the input and on how effectively the hierarchy separates values.

The current benchmark evidence supports strong performance on a number of large random floating-point workloads.

However, the algorithm should **not** currently be described as universally `O(n)`.

Distribution-based sorting can approach linear behavior when the distribution and bucket hierarchy are favorable, but the actual execution cost depends on:

* input distribution
* numerical range
* bucket occupancy
* recursion/refinement behavior
* duplicate values
* floating-point characteristics
* hardware and memory hierarchy

For this reason, the README deliberately avoids presenting an oversimplified theoretical complexity claim as though it were a universal bound.

---

# 📊 Benchmark Interpretation

The current results show three important characteristics.

### 1. SIMD provides a substantial improvement

At 67.1M random floats:

**7.504 s → 5.013 s**

from ModernWittyBucketSort to SimdWittyBucketSort.

That is approximately **1.50× faster**.

### 2. The advantage over `Arrays.sort()` depends on the distribution

At 67.1M elements:

**Random floats**

```text
SIMD WBS       5.013 s
Arrays.sort    9.598 s
```

**Binary floats**

```text
SIMD WBS       560 ms
Arrays.sort    622 ms
```

The same algorithm can therefore have a modest advantage on one distribution and a much larger advantage on another.

### 3. Large random workloads are currently the strongest use case

The current benchmark evidence is strongest for **large, high-entropy floating-point datasets**.

This makes the implementation potentially interesting for workloads such as:

* analytical data processing
* numerical datasets
* sensor data
* large in-memory data pipelines
* columnar data processing
* SQL `ORDER BY` operations over numeric columns

These should be considered target workloads rather than guarantees of superiority for every dataset.

---

# 🛠️ Usage

```java
double[] data = BenchMarker.initArrayRandomFloats(24_000_000);

ModernWittyBucketSort.sort(data);
```

For the SIMD implementation:

```java
double[] data = BenchMarker.initArrayRandomFloats(24_000_000);

SimdWittyBucketSort.sort(data);
```

The exact supported primitive types and method signatures depend on the current API of the release.

---

# 🗄️ SQL / Analytical Processing

One of the potential applications of the sorting implementation is analytical query execution.

In particular, a high-throughput primitive sort can serve as the physical sorting mechanism behind operations such as:

```sql
SELECT *
FROM data
ORDER BY value;
```

This is especially relevant to columnar execution engines where the sort key can be represented as a primitive numeric array rather than a collection of boxed Java objects.

The current work is being integrated into a broader Java-based analytical stack involving **ParserNG SQL** and Apache Arrow.

The long-term objective is to investigate how far a pure-Java execution engine can push modern JVM capabilities such as the Vector API for database-style workloads.

---

# ⚠️ Benchmark Notes

These results are **JMH benchmark results for specific workloads and hardware**.

They should not be interpreted as proof that ModernWittyBucketSort or SimdWittyBucketSort is universally faster than `Arrays.sort()`.

In particular:

* different distributions can produce substantially different results
* different CPU architectures can change SIMD performance
* JVM versions and Vector API implementations can affect results
* memory bandwidth and cache behavior can become limiting factors
* the reported JMH error margins should be considered when interpreting exact ratios
* end-to-end SQL performance includes substantially more work than the sorting kernel alone

The most useful comparison is therefore not a single headline number, but the behavior across multiple datasets and scales.

---

# 🚀 Current Direction

The current development focus is on determining how far the SIMD implementation can be pushed on large primitive datasets.

The most promising result so far is:

> **67,106,664 random floats sorted in approximately 5.0 seconds with SimdWittyBucketSort, compared with approximately 9.6 seconds for `java.util.Arrays.sort()` in the same benchmark.**

That is approximately **1.91× the measured throughput**.

The next stage is to evaluate the sorting primitive as part of larger data-processing workloads rather than only as an isolated sorting benchmark.

---

## ✍️ Author

**Jiboye, Oluwagbemiro Olaoluwa**

*Refining the art of the sort since 2016.*
