//* PC
void psDraw() { }
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

/*/ // ANDROID


import android.content.ClipboardManager;
import android.content.*;
import android.app.*;

String gottenClip;
Drawable clipRec;

void settings() {
  fullScreen();
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


void copy(String s) {
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
