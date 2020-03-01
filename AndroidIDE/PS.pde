/* PC
import java.awt.Toolkit;
static boolean MOBILE = false;
class FakeTouch { // for interoperability with android mode
  int x, y;
  FakeTouch(int x, int y) { this.x = x; this.y = y; }
}
FakeTouch[] touches = new FakeTouch[0];
void psDraw() {
  if (mousePressed) touches = new FakeTouch[] { new FakeTouch(mouseX, mouseY) };
  else touches = new FakeTouch[0];
}
void settings() {
  //size(540, 830);
  size(1200, 800, JAVA2D);
  //size(960, 540);
}

import java.awt.datatransfer.*;
import java.awt.Toolkit;
void copy(String s) {
  StringSelection stringSelection = new StringSelection(s);
  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  clipboard.setContents(stringSelection, null);
}

void paste(Drawable d) {
  try {
    d.pasted((String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
  } catch (Throwable e) {
    e.printStackTrace();
  }
}
void mouseWheel(MouseEvent e) {
  if (topbar != null && topbar.ctab != null) topbar.ctab.mouseWheel(e.getCount());
}
void openKeyboard() {};

void handleCoded(int keyCode) {
       if (keyCode == java.awt.event.KeyEvent.VK_BACK_SPACE) textInput.ldelete();
  else if (keyCode == java.awt.event.KeyEvent.VK_UP        ) textInput.special("up");
  else if (keyCode == java.awt.event.KeyEvent.VK_LEFT      ) textInput.special("left");
  else if (keyCode == java.awt.event.KeyEvent.VK_DOWN      ) textInput.special("down");
  else if (keyCode == java.awt.event.KeyEvent.VK_RIGHT     ) textInput.special("right");
  else if (keyCode == java.awt.event.KeyEvent.VK_HOME      ) textInput.special("home");
  else if (keyCode == java.awt.event.KeyEvent.VK_END       ) textInput.special("end");
}
KeyEvent fixKE(KeyEvent e) {
  return e;
}

static void beginClip(PGraphics g, float x, float y, float x2, float y2) {
  g.clip(x, y, x2, y2);
}
static void endClip(PGraphics g) {
  g.noClip();
}

/*/ // ANDROID

import android.content.ClipboardManager;
import android.content.*;
import android.app.*;

static boolean MOBILE = true;

String gottenClip;
Drawable clipRec;

void settings() {
  //fullScreen();
  size(displayWidth, displayHeight);
}
void psDraw() {
  if (gottenClip != null) {
    clipRec.pasted(gottenClip);
    gottenClip = null;
  }
}

void prepareClip() {
  if (cba == null) {
    cba = getActivity();
    cbcm = (ClipboardManager) cba.getSystemService(Context.CLIPBOARD_SERVICE);
  }
}

Activity cba;
ClipboardManager cbcm;


void copy(final String s) {
  getActivity().runOnUiThread(new Runnable() {
    public void run() {
      prepareClip();
      ClipData clip = android.content.ClipData.newPlainText("wtf", s);
      cbcm.setPrimaryClip(clip);
    }
  });
}
void paste(Drawable rec) {
  clipRec = rec;
  getActivity().runOnUiThread(new Runnable() {
    public void run() {
      prepareClip();
      if (cbcm.hasPrimaryClip()) {
        ClipData clip = cbcm.getPrimaryClip();
        gottenClip = clip.getItemAt(0).coerceToText(cba).toString();
      }
    }
  });
}
void handleCoded(int keyCode) {
  if (ctrl) {
    if (keyCode == android.view.KeyEvent.KEYCODE_C) textInput.special("copy");
    if (keyCode == android.view.KeyEvent.KEYCODE_V) textInput.special("paste");
    if (keyCode == android.view.KeyEvent.KEYCODE_Z) textInput.special("undo");
    if (keyCode == android.view.KeyEvent.KEYCODE_Y) textInput.special("redo");
    if (keyCode == android.view.KeyEvent.KEYCODE_X) textInput.special("cut");
    if (keyCode == android.view.KeyEvent.KEYCODE_A) textInput.special("sall");
    if (keyCode == android.view.KeyEvent.KEYCODE_ENTER) textInput.special("eval");
    
  }
       if (keyCode == android.view.KeyEvent.KEYCODE_DEL       ) textInput.ldelete();
  else if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_UP   ) textInput.special("up");
  else if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_LEFT ) textInput.special("left");
  else if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_DOWN ) textInput.special("down");
  else if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_RIGHT) textInput.special("right");
  else if (keyCode == android.view.KeyEvent.KEYCODE_MOVE_HOME ) textInput.special("home");
  else if (keyCode == android.view.KeyEvent.KEYCODE_MOVE_END  ) textInput.special("end");
}
KeyEvent fixKE(KeyEvent e) {
  android.view.KeyEvent n = (android.view.KeyEvent) e.getNative();
  return new KeyEvent(e.getNative(), e.getMillis(), e.getAction(), 
    (n.isShiftPressed() ? Event.SHIFT : 0) +
    (n. isCtrlPressed() ? Event.CTRL  : 0) +
    (n. isMetaPressed() ? Event.META  : 0) +
    (n.  isAltPressed() ? Event.ALT   : 0)
  , e.getKey(), e.getKeyCode());
}

import processing.a2d.*;
static void beginClip(PGraphics g, float x, float y, float x2, float y2) {
  if (g instanceof PGraphicsAndroid2D) ((PGraphicsAndroid2D)g).canvas.save();
  g.clip(x, y, x2, y2);
}
static void endClip(PGraphics g) {
  if (g instanceof PGraphicsAndroid2D) ((PGraphicsAndroid2D)g).canvas.restore();
  else g.noClip();
}

//*/
