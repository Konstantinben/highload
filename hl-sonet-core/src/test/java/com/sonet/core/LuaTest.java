package com.sonet.core;

import org.junit.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua53.Lua53;
import party.iroiro.luajava.value.LuaValue;

import static org.junit.Assert.assertEquals;

public class LuaTest {
    @Test
    public void firstTest() {
        Lua L = new Lua53();
        LuaValue[] returnValues = L.execute("return { a = 1 }, 1024, 'string'");
        assertEquals(3, returnValues.length);
        assertEquals(L.from(1.0),      returnValues[0].get("a"));
        assertEquals(L.from(1024),     returnValues[1]);
        assertEquals(L.from("string"), returnValues[2]);
    }
}
