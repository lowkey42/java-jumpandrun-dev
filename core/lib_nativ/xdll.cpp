// compile with g++ -o libMy_evilXHack.so -shared -fPIC -Wl,-soname,libMy_evilXHack.so -L/usr/lib/X11 -I/usr/include/X11 xdll.cpp -lX11

#include <Xlib.h>

class a{
public:
  a() { XInitThreads(); }
};

a X;
