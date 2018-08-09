#!/usr/bin/env python

from numpy import array
from math import sqrt

from pyspark import SparkContext
# from pyspark.mllib.clustering import KMeans, KMeansModel
from pyspark.mllib.clustering import KMeans

sc = SparkContext(appName="Kmeans Pyspark")

# Load and parse the data
data = sc.textFile("hdfs://localhost:9000/features/w2v/value_only")


parsedData = data.map(lambda line: array([float(x) for x in line.strip(' ').split(' ')]))

# Build the model (cluster the data)

clusters = KMeans.train(parsedData, 2, maxIterations=10, initializationMode="random")


# Evaluate clustering by computing Within Set Sum of Squared Errors
def error(point):
    center = clusters.centers[clusters.predict(point)]
    return sqrt(sum([x**2 for x in (point - center)]))


WSSSE = parsedData.map(lambda point: error(point)).reduce(lambda x, y: x + y)
print("Within Set Sum of Squared Error = " + str(WSSSE))

# Save and load model
clusters.save(sc, "hdfs://localhost:9000/kmeans/model")
# sameModel = KMeansModel.load(sc, "target/org/apache/spark/PythonKMeansExample/KMeansModel")
