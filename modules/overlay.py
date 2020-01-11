"""
USAGE EXAMPLE:

#IF THIS FILE UNDER libs DIRECTORY
from modules import overlay
#IF THIS FILE UNDER libs DIRECTORY


import java.awt.Color as Color

#                                        x    y   text   font_size   color
myText = overlay.OverlayText(None).init(100, 100, "test", 48.0, Color.RED)

myText.text("Text updated")

#                x    y
myText.move(500, 400) 

#Remove overlay object
myText.dispose() 
"""




# -*- coding: utf-8 -*-
import java.awt.Window
import java.awt.FontMetrics
import java.awt.Color as Color

class OverlayText(java.awt.Window):
  __x = 0
  __y = 0
  __text = None
  __font = float(48.0)
  __color = None
  def init(self, x, y, text, font, color): #custom constructor
    self.__x = x
    self.__y = y
    self.__text = text
    self.__color = color
    self.__font = float(font)
    self.setAlwaysOnTop(True)
    self.setBounds(self.getGraphicsConfiguration().getBounds())
    self.setBackground(java.awt.Color(0, True)) #transparent
    self.setVisible(True)
    return self

  def text(self, t):
    self.__text = t
    #repaint or revalidate does not work, so used this horrible solution 
    self.setVisible(False)
    self.setVisible(True)

  def move(self, x, y):
    self.__x = x
    self.__y = y
    #repaint or revalidate does not work, so used this horrible solution 
    self.setVisible(False)
    self.setVisible(True)

  def paint(self, g):
    font = self.getFont().deriveFont(self.__font)
    g.setFont(font)
    g.setColor(self.__color)
    message = self.__text
    metrics = g.getFontMetrics()
    g.drawString(message,self.__x, self.__y)

  def update(self, g):
    paint(g)
            