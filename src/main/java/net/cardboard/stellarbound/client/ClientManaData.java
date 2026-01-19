package net.cardboard.stellarbound.client;

public class ClientManaData {
    private static float mana = 100.0f;
    private static float maxMana = 100.0f;

    public static void setMana(float value) {
        mana = value;
    }

    public static void setMaxMana(float value) {
        maxMana = value;
    }

    public static float getMana() {
        return mana;
    }

    public static float getMaxMana() {
        return maxMana;
    }
}