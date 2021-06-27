static class SyntaxHighlight {
  static void apltext(String s, float x, float y, float sz, Theme t, PGraphics g) {
    new SyntaxHighlight(s, t, g).draw(x, y, sz);
  }
  final String s;
  final String[] lns;
  final int[] cs;
  final int[] lnstarts;
  final int[] pairs;
  final int[] mark;
  final Theme th;
  PGraphics g;
  SyntaxHighlight(String s, Theme th, PGraphics g) {
    this.g = g;
    this.th = th;
    this.s = s;
    lns = split(s, "\n");
    lnstarts = new int[lns.length];
    int pos = 0;
    for (int i = 0; i < lns.length; i++) {
      lnstarts[i] = pos;
      pos+= lns[i].length() + 1;
    }
    
    cs = new int[s.length()];
    mark = new int[s.length()];
    pairs = new int[s.length()];
    for (int i = 0; i < s.length(); i++) {
      pairs[i] = -1;
      cs[i] = th.def;
    }
    
    try {
      BasicLines l = Tokenizer.tokenize(s, true);
      walk(l, -1);
    } catch(Throwable e) { e.printStackTrace();/* :/ */ } // {[(]j
  }
  
  void draw(float x, float y, float sz) {
    draw(x, y, 0, g.height, sz, -1);
  }
  
  void draw(float x, float y, float sz, int poi) {
    draw(x, y, 0, g.height, sz, poi);
  }
  
  int sel(int poi) {
    if (pairs.length == 0 || poi == -1) return -1;
    else {
      int sel = pairs[min(poi, pairs.length-1)];
      if (sel == -1) sel = pairs[constrain(poi-1, 0, pairs.length-1)];
      return sel;
    }
  }
  
  void draw(float x, float y, float sy, float ey, float sz, int poi) {
    g.textAlign(LEFT, TOP);
    g.textSize(sz);
    int sel = sel(poi);
    float chw = g.textWidth('H');
    for(int ln = max(0, floor((sy-y)/sz)); ln < min(lns.length, floor((ey-y)/sz+1)); ln++) {
      float cx = x;
      String cln = lns[ln];
      for (int i = 0; i < cln.length(); i++) {
        char cc = cln.charAt(i);
        int pos = i + lnstarts[ln];
        g.fill(cs[pos]);
        textS(g, cc, cx, y + ln*sz);
        int markcol = mark[pos];
        if (markcol != 0) {
          g.fill(markcol);
          textS(g, "_", cx, y + ln*sz);
        }
        if (sel == pos) {
          g.stroke(th.pair);
          g.strokeWeight(ceil(sz/20f));
          g.pushMatrix();
          g.translate(cx, y + ln*sz);
          g.beginShape();
          g.noFill();
          g.vertex(  0, sz* .1);
          g.vertex(  0, sz*1.1);
          g.vertex(chw, sz*1.1);
          g.vertex(chw, sz* .1);
          g.endShape(CLOSE);
          g.popMatrix();
        }
        cx+= chw;
      }
    }
  }
  
  void set(Token t, int col) {
    for (int i = t.spos; i < t.epos; i++) cs[i] = col;
    //println(t.spos+"-"+t.epos+":" + new java.awt.Color(col));
  }
  void set(int i, int col) {
    cs[i] = col;
  }
  void err(int i) {
    mark[i] = th.err;
  }
  void walk(Token t, int dlvl) {
    int dfncol = dlvl < 0? th.err : dlvl >= th.dfn.length? th.dfn[0] : th.dfn[dlvl];
    if (t instanceof NumTok) set(t, th.num);
    if (t instanceof BigTok) set(t, th.num);
    if (t instanceof SetTok) set(t, th.set);
    if (t instanceof ErrTok) set(t, th.err);
    if (t instanceof StrTok) set(t, th.str);
    if (t instanceof ChrTok) set(t, th.str);
    
    if (t instanceof BacktickTok) walk(((BacktickTok) t).value(), dlvl);
    if (t instanceof CommentTok) set(t, th.com);
    if (t instanceof ColonTok || t instanceof DColonTok) set(t, dfncol);
    if (t instanceof DiamondTok) set(t, th.dmd);
    
    if (t instanceof  OpTok) {
      switch(((OpTok) t).op) {
        case "⎕": set(t, th.quad); break; // important - otherwise uses stdin
        case "⍞": set(t, th.quad); break; // ^
        case "⍺": set(t, dfncol ); break;
        case "⍵": set(t, dfncol ); break;
        case "⍶": set(t, dfncol ); break;
        case "⍹": set(t, dfncol ); break;
        case "∇": set(t, dfncol ); break;
        case "⍬": set(t, th.zil ); break;
        default: {
          int col;
          try {
            Obj o = new Exec(LineTok.inherit(t), mainSys.gsc).exec();
            col = o instanceof Fun? th.fn
                : o instanceof Mop? th.mop
                : o instanceof Dop? th.dop
                : th.err;
          } catch(Throwable e) {
            col = th.err;
          }
          set(t, col);
        }
      }
    }
    if (t instanceof ParenTok) {
      if (t.raw.charAt(t.epos-1) != ')') err(t.spos);
      else {
        pairs[t.spos  ] = t.epos-1;
        pairs[t.epos-1] = t.spos  ;
      }
    }
    if (t instanceof BracketTok) {
      if (t.raw.charAt(t.epos-1) != ']') err(t.spos);
      else {
        pairs[t.spos  ] = t.epos-1;
        pairs[t.epos-1] = t.spos  ;
      }
    }
    if (t instanceof DfnTok) {
      int ncol = dlvl+1 >= th.dfn.length? th.dfn[0] : th.dfn[dlvl+1];
      if (t.raw.charAt(t.epos-1) != '}') {
        err(t.spos);
      } else {
        set(t.spos  , ncol);
        set(t.epos-1, ncol);
        pairs[t.spos  ] = t.epos-1;
        pairs[t.epos-1] = t.spos  ;
      }
    }
    if (t instanceof TokArr) {
      int ndlvl = t instanceof DfnTok? dlvl+1 : dlvl;
      for (Token c : ((TokArr<?>)t).tokens) {
        walk(c, ndlvl);
      }
    }
  }
}
static class Theme {
  int def = #D2D2D2;
  int err = #FF0000;
  int com = #BBBBBB;
  int num = #AA88BB;
  
  int set = #FFFF00;
  int quad= def    ;
  int str = #DDAAEE;
  int zil = #DD99FF;
  
  int dmd = #FFFF00;
  int fn  = #00FF00;
  int mop = #FF9955;
  int dop = #FFDD66;
  int pair= #777799;
  
  int caret = def;
  int[] dfn = {#AA77BB, #EEBB44, #CC7799, #CCDD00, #B63CDA};
}
static class NoErrTheme extends Theme {
  { err = #AA77BB; }
}
static Theme aplTheme = new Theme();
static Theme noErrTheme = new NoErrTheme();
static Theme errTheme = new AllEqualTheme(#FF0000);
static Theme whiteTheme = new AllEqualTheme(#D2D2D2);
static class AllEqualTheme extends Theme {
  AllEqualTheme(int col) {
    com=num=set=quad=str=zil=dmd=fn=mop=dop=pair=caret=def=err=col;
    dfn = new int[]{col,col,col,col,col};
  }
}
