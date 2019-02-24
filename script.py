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
#a.sleep(5000)
a.grab()
#a.searchRect(3, 143, 409, 470)
mp = a.findPos("penguin")
if mp:
	print(mp.x, mp.y)
	a.mouseMove(mp)
else:
	print("FAIL")