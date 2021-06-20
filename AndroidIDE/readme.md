<img src=../docs/p1.png width="200"></img> <img src=../docs/p2.png width="200"></img> <img src=../docs/l1.png width="356"></img>

[Download APK](https://github.com/dzaima/APL/releases)

By default, this interprets dzaima/APL, but to connect to Dyalog APL (through JSONServer), in Dyalog do:

```apl
)LOAD path/to/Jarvis/Distribution/Jarvis.dws ⍝ https://github.com/Dyalog/Jarvis/blob/master/Distribution/Jarvis.dws
eval←{⎕←' ',⍵ ⋄ 0::{⎕←↑⎕DM⋄⎕DM}⍬ ⋄ ,↓⎕←⍕⍎⍵}
S←⊃Jarvis.Run 1234 # ⍝ replace 1234 with whatever port you want
```

and to connect to it in the app:

```apl
:i dyalog
)ip:1234 ⍝ or whatever port you set
```


---



Compile for android:

1. generate the dzaima/APL interpreter jar file - run `./convert.py` in `app/`.
2. in PS.pde, change start to `/*`
3. either run in Android Mode with a device connected, or File → Export Signed Package to generate an APK.

Alternatively, Processing runs the project in Java mode just fine (keyboard input may be a bit funky though).

---

```
swipe on characters to enter ones around it
M - go to matching bracket
F - switch layout to one with extra functions
N - switch layout to numbers at top-level
A - switch to text layout
# - switch to default layout
⇧ - shift (caps letters, select with «»)
X - close tab (where applicable - grapher & editor)
K - open virtual keyboard
= - evaluate

↶↷ - undo/redo
▲▼ - move trough REPL history
^C/^V - copy/paste


:isz sz     change input box font size
:hsz sz     change REPL history font size
:tsz sz     change top bar size
:g expr     graph the expression (editable in the window)
:clear      clear REPL history
:f  path    edit file at the path
:fx path    edit file at the path, executing on save
:ex path    execute file at the path
:ed fn      edit the function by name in another window (= - save, ⏎ - newline, X - save (!) & close)
```