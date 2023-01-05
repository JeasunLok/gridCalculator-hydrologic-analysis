#!C:/Python27/ArcGIS10.8/python
import sys
import numpy as np

data = sys.argv[1]
print(data)
f = open("test.txt", "w")
f.write(data)