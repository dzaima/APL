//* PC
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
  size(540, 830);
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
  if (a == null) {
    a = getActivity();
    b = (ClipboardManager) a.getSystemService(Context.CLIPBOARD_SERVICE);
  }
}

Activity a;
ClipboardManager b;


void copy(final String s) {
  getActivity().runOnUiThread(new Runnable() {
    public void run() {
      prepareClip();
      ClipData clip = android.content.ClipData.newPlainText("wtf", s);
      b.setPrimaryClip(clip);
    }
  });
}
void paste(Drawable rec) {
  clipRec = rec;
  getActivity().runOnUiThread(new Runnable() {
    public void run() {
      prepareClip();
      if (b.hasPrimaryClip()) {
        ClipData clip = b.getPrimaryClip();
        gottenClip = clip.getItemAt(0).coerceToText(a).toString();
      }
    }
  });
}
//*/
