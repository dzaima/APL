static class Drawable {
  int x, y;
  int w, h;
  boolean visible;
  Drawable(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    screen.add(this);
  }
  void tick() { } // only update things that changed. For global things called every frame, even if hidden
  void redraw() { } // redraw everything. Should be pure and callable at any point any time (where P5 drawing is allowed); called at least on window/screen resizing/rotating
  void resized() { }
  void mouseWheel(int dir) { }
  void move(int x, int y) {
    this.x = x;
    this.y = y;
    if (visible) redraw();
  }
  void resize(int w, int h) {
    this.w = w;
    this.h = h;
    if (visible) redraw();
    resized();
  }
  void move(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    if (visible) redraw();
    resized();
  }
  void show() {
    if (!visible) redraw();
    visible = true;
  }
  void hide() {
    visible = false;
  }
  void align(int dir) {
    switch(dir) {
      case BOTTOM: move(x, d.height-h);
    }
  }
  final void delete() {
    assert screen.remove(this);
    visible = false;
    x = y = w = h = -1111111;
  }
  boolean mouseInMe() {
    return a.mouseX > x && a.mouseY > y && a.mouseX < x+w && a.mouseY < y+h;
  }
  boolean smouseIn() {
    return smouseX > x && smouseY > y && smouseX < x+w && smouseY < y+h;
  }
  void pasted(String s) {
    throw new Error(this+" didn't ask for a clipboard!");
  }
}
