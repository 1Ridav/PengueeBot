  # Hello World program in Python
# -*- coding: utf-8 -*-

"""
USAGE EXAMPLE:

#IF THIS FILE UNDER libs DIRECTORY
from libs import readfield
#IF THIS FILE UNDER libs DIRECTORY

#THIS IS USED FOR READING FIELD TEXT VIA SEARCHING FOR A PREDEFINED DICTIONARY, 
readField function will try to find all fragments that are listed in your dictionary and sort them by X coordinates,
then combine in one string and return the resulting string,
dictionary must cuntain a list of your characters that you are trying to read from screen


#DICTIONARY KEY MUST BE EQUAL TO FRAGMENT NAME, VALUE IS THE ACTUAL CHARACTER MEANING
if your fragments are loaded like folder1.subfolder2.fragname then you should specify PREFIX name as folder1.subfolder2


import readfield as rf
a = Action()
a.grab()
rf.init(a)
mydict = {"zero":"0","one":"1","two":"2","three":"3","four":"4","five":"5","six":"6","seven":"7","eight":"8","nine":"9", "slash":"/", "dot":"."}

a.searchRect(Position(1, 1), Position(100, 30)) #LIMIT SEARCH RECTANGLE, here you specify field position on your screen
mystring = rf.readField(mydict, "folder1.subfolder2.")
"""


from time import time
__action = None
dic = {"0":"0","1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9"}

def init(a):
    global __action
    __action = a

def readField(dictionary, prefix = ""):
    global __action
    def selectionSort(alist):#by x coordinates
        for fslot in range(len(alist)-1,0,-1):
            pmax=0
            for loc in range(1,fslot+1):
                if alist[loc].x > alist[pmax].x:
                    pmax = loc
            alist[fslot], alist[pmax] = alist[pmax], alist[fslot]

    templist = []
    for key, value in dictionary.iteritems():
        arr = __action.findAllPos("%s%s" % (prefix, key), value)
        if arr is not None:
            templist.extend(arr)
    #sort by X coord
    selectionSort(templist)
    return ''.join([mp.name for mp in templist])


def waitForReadField(dic, prefix = "", timeout = 1, x1 = None, y1 = None, x2 = None, y2 = None):
    global __action
    t = time() + timeout/1000.0 
    __action.searchRect(x1, y1, x2, y2)
    while time() < t:
        __action.grab(x1, y1, x2, y2)
        buff = readField(dic, prefix)
        if buff != "":
          return buff
    return ""