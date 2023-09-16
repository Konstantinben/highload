package com.sonet.core;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua53.Lua53;

public class Hello {
    public static void main(String[] args) {
        try (Lua L = new Lua53()) {
            L.openLibraries();
            L.run("System = java.import('java.lang.System')");
            L.run("System.out:println('Hello World from Lua!')");
        }
    }
}

