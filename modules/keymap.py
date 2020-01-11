# -*- coding: utf-8 -*-
#https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
#https://docs.oracle.com/javase/7/docs/api/java/awt/event/InputEvent.html

"""
USAGE EXAMPLE:

#IF THIS FILE UNDER libs DIRECTORY
from modules import keymap
#IF THIS FILE UNDER libs DIRECTORY


import keymap
a = Action()

a.keyClick(keymap.key("+"))
a.keyClick(keymap.key("H"))
a.keyClick(keymap.key("E"))
a.keyClick(keymap.key("L"))
a.keyClick(keymap.key("L"))
a.keyClick(keymap.key("O"))

a.mousePress(keymap.mouse("right"))
a.mouseRelease(keymap.mouse("right"))
"""


from java.awt.event import InputEvent, KeyEvent
__version = 1

__key = {
	"A":KeyEvent.VK_A,
	"B":KeyEvent.VK_B,
	"C":KeyEvent.VK_C,
	"D":KeyEvent.VK_D,
	"E":KeyEvent.VK_E,
	"F":KeyEvent.VK_F,
	"G":KeyEvent.VK_G,
	"H":KeyEvent.VK_H,
	"I":KeyEvent.VK_I,
	"J":KeyEvent.VK_J,
	"K":KeyEvent.VK_K,
	"L":KeyEvent.VK_L,
	"M":KeyEvent.VK_M,
	"N":KeyEvent.VK_N,
	"O":KeyEvent.VK_O,
	"P":KeyEvent.VK_P,
	"Q":KeyEvent.VK_Q,
	"R":KeyEvent.VK_R,
	"S":KeyEvent.VK_S,
	"T":KeyEvent.VK_T,
	"U":KeyEvent.VK_U,
	"V":KeyEvent.VK_V,
	"W":KeyEvent.VK_W,
	"X":KeyEvent.VK_X,
	"Y":KeyEvent.VK_Y,
	"Z":KeyEvent.VK_Z,

	"0":KeyEvent.VK_0,
	"1":KeyEvent.VK_1,
	"2":KeyEvent.VK_2,
	"3":KeyEvent.VK_3,
	"4":KeyEvent.VK_4,
	"5":KeyEvent.VK_5,
	"6":KeyEvent.VK_6,
	"7":KeyEvent.VK_7,
	"8":KeyEvent.VK_8,
	"9":KeyEvent.VK_9,

	"F1":KeyEvent.VK_F1,
	"F2":KeyEvent.VK_F2,
	"F3":KeyEvent.VK_F3,
	"F4":KeyEvent.VK_F4,
	"F5":KeyEvent.VK_F5,
	"F6":KeyEvent.VK_F6,
	"F7":KeyEvent.VK_F7,
	"F8":KeyEvent.VK_F8,
	"F9":KeyEvent.VK_F9,
	"F10":KeyEvent.VK_F10,
	"F11":KeyEvent.VK_F11,
	"F12":KeyEvent.VK_F12,

	"@":KeyEvent.VK_AT,
	"+":KeyEvent.VK_ADD,
	"-":KeyEvent.VK_MINUS,
	" ": KeyEvent.VK_SPACE,
	"'":KeyEvent.VK_QUOTE,
	"\"":KeyEvent.VK_QUOTEDBL,
	"\\": KeyEvent.VK_BACK_SLASH,
	"/":KeyEvent.VK_SLASH,
	")":KeyEvent.VK_RIGHT_PARENTHESIS,
	"(":KeyEvent.VK_LEFT_PARENTHESIS,
	"[":KeyEvent.VK_OPEN_BRACKET,
	"]":KeyEvent.VK_CLOSE_BRACKET,
	"$":KeyEvent.VK_DOLLAR,
	"&":KeyEvent.VK_AMPERSAND,
	"#":KeyEvent.VK_NUMBER_SIGN,
	"!":KeyEvent.VK_EXCLAMATION_MARK,
	":":KeyEvent.VK_COLON,
	",":KeyEvent.VK_COMMA,
	".":KeyEvent.VK_PERIOD,
	"^":KeyEvent.VK_CIRCUMFLEX,
	"*":KeyEvent.VK_ASTERISK,
	"_":KeyEvent.VK_UNDERSCORE,
	"=":KeyEvent.VK_EQUALS,
	">":KeyEvent.VK_GREATER,
	"<":KeyEvent.VK_LESS,

	"CAPSLOCK":KeyEvent.VK_CAPS_LOCK,
	"ALT":KeyEvent.VK_ALT,
	"ALTGR":KeyEvent.VK_ALT_GRAPH,
	"CTRL":KeyEvent.VK_CONTROL,
	"SHIFT":KeyEvent.VK_SHIFT,
	"TAB":KeyEvent.VK_TAB,
	"ESC":KeyEvent.VK_ESCAPE,

	"UP":KeyEvent.VK_UP,
	"DOWN":KeyEvent.VK_DOWN,
	"LEFT":KeyEvent.VK_LEFT,
	"RIGHT":KeyEvent.VK_RIGHT,

	"ENTER":KeyEvent.VK_ENTER,
	"BACKSPACE":KeyEvent.VK_BACK_SPACE

}

__mouse = {
	"left":InputEvent.BUTTON1_MASK,
	"mid":InputEvent.BUTTON2_MASK,
	"right":InputEvent.BUTTON3_MASK
}

def key(k):
	return __key[k]
def mouse(k):
	return __mouse[k]



