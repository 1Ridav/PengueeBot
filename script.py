  # Hello World program in Python
# -*- coding: utf-8 -*-
from bot.penguee import MatrixPosition, Action
import time
import urllib2
import array
import random
import math
import collections
import json
import os
import ConfigParser
import java.awt.Toolkit
import java.awt.event
from bisect import bisect_left

a = Action()
#while True:
a.grab()
mp = a.findPos(".mytest1")
if mp:
  print("FOUND!!!!!" )
  a.mouseClick(mp)
  a.mouseClick(mp)
  #print(mp.x, mp.y)
  #a.mouseMove(mp)
else:
  print("FAIL.......")


