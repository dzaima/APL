[docs](https://github.com/dzaima/APL/blob/master/docs/chars.txt) | [differences from Dyalog APL](https://github.com/dzaima/APL/blob/master/docs/differences.txt)

`./build` to build, `./REPL` to start a REPL.

Uses The fonts [APL385](http://apl385.com/fonts/index.htm) and [DejaVu Sans Mono](https://dejavu-fonts.github.io).

To build the APL Android app, 
1. run `convert.py` in the folder `app`
2. Open `app/APLApp` in Processing, change mode to android and change line 109 in `APLApp` to `/*`
3. Export from Processing to change the minimum SDK version to 24; For IntelliJ IDEA:
    - open with "use auto-import" checked
    - file → project structure → Project SDK = 1.8
    - build → build project
    - project structure → facets → Android-Gradle (app) → Flavors → Min Sdk version → 24
    - build → generate signed APK → whatever

Alternatively, you can just run it in Processings Java mode just fine.