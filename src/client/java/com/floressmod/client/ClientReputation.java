package com.floressmod.client;

/** Клиентская копия репутации — обновляется пакетами с сервера. */
public final class ClientReputation {
	private static int value = 0;

	private ClientReputation() {}

	public static int get() {
		return value;
	}

	public static void set(int newValue) {
		value = newValue;
	}
}
