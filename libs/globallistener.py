# -*- coding: utf-8 -*-


"""
#USAGE EXAMPLE
#IF THIS FILE UNDER libs DIRECTORY
import sys
sys.path.append("libs")
#IF THIS FILE UNDER libs DIRECTORY

import globallistener as gl

isMousePressed = False

def mousePressed(e):
  global isMousePressed
  if e.getButton() == 5:
    isMousePressed = True

def mouseReleased(e):
  global isMousePressed
  if e.getButton() == 5:
    isMousePressed = False

eventclass = gl.init()
eventclass.nativeMousePressed = mousePressed
eventclass.nativeMouseReleased = mouseReleased

while True:
  if isMousePressed:
    print("Hello world!")
  else:
    a.sleep(100)

"""



"""
#IF THIS FILE UNDER libs DIRECTORY
import sys
sys.path.append("sys.path.append("libs/jnativehook-1.1.4.jar")")
#IF THIS FILE UNDER libs DIRECTORY
"""
import sys
sys.path.append("libs/jnativehook-1.1.4.jar")

import org.jnativehook.GlobalScreen as GlobalScreen
import org.jnativehook.NativeHookException as NativeHookException
import org.jnativehook.keyboard.NativeKeyEvent as NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener as NativeKeyListener 
import org.jnativehook.mouse.NativeMouseEvent as NativeMouseEvent
import org.jnativehook.mouse.NativeMouseInputListener as NativeMouseInputListener
import org.jnativehook.mouse.NativeMouseWheelListener as NativeMouseWheelListener

class GlobalEventListener(NativeKeyListener, NativeMouseInputListener, NativeMouseWheelListener ): 
  def nativeKeyPressed(self, e):
    #print("Key Pressed: ", e.getKeyCode());
    pass

  def nativeKeyReleased(self, e):
    #print("Key Released: ", e.getKeyCode());
    pass
  

  def nativeKeyTyped(self, e):
    #print("Key Typed: ", e.getKeyCode());
    pass

  def nativeMouseClicked(self, e):
    #print("Mouse Clicked: " , e.getButton())
    #print("Mouse Clicked: " , e.getClickCount())
    pass
    
  
  def nativeMousePressed(self, e):
    #print("Mouse Pressed: ", e.getButton())
    pass
  

  def nativeMouseReleased(self, e):
    #print("Mouse Released: ", e.getButton())
    pass
  

  def nativeMouseMoved(self, e):
    #print("Mouse Moved: " , e.getX(), e.getY())
    pass
  

  def nativeMouseDragged(self, e):
    #print("Mouse Dragged: ", e.getX(), e.getY());
    pass

  def nativeMouseWheelMoved(self, e):
    #System.out.println("Mouse Wheel Moved: ", e.getWheelRotation())
    pass
  
  

__gs = None
def init():
  __gs = GlobalScreen.getInstance()
  __gs.registerNativeHook()
  __eventListener = GlobalEventListener()
  __gs.addNativeKeyListener(__eventListener)
  __gs.addNativeMouseListener(__eventListener)
  __gs.addNativeMouseMotionListener(__eventListener)
  __gs.addNativeMouseWheelListener(__eventListener)
  return __eventListener

def finalize():
  __gs.unregisterNativeHook()

