package com.narxoz.rpg.hero;

public class Mage implements Hero {
    private final String name;
    private final int power;
    private int health;

    public Mage(String name) {
        this.name = name;
        this.power = 25;
        this.health = 80;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public void receiveDamage(int amount) {
        int dmg = Math.max(0, amount);
        health -= dmg;
        if (health < 0) {
            health = 0;
        }
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    public int getHealth() {
        return health;
    }
}
