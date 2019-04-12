abstract interface TextReciever {
  void append(String str); // may contain newlines
  void backspace();
  void clear();
  String allText();
  void special(String s);
  void eval();
}
