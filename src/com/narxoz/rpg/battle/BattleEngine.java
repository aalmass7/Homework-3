package com.narxoz.rpg.battle;

import java.util.List;
import java.util.Random;

public final class BattleEngine {
    private static final long DEFAULT_SEED = 1L;
    private static final int CRIT_CHANCE_PERCENT = 20;
    private static final int CRIT_MULTIPLIER = 2;

    private static BattleEngine instance;

    private Random random;
    private long seed;

    private BattleEngine() {
        this.seed = DEFAULT_SEED;
        this.random = new Random(DEFAULT_SEED);
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
        return this;
    }

    public void reset() {
       setRandomSeed(DEFAULT_SEED);
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        // TODO: validate inputs and run round-based battle
        // TODO: use random if you add critical hits or target selection
        EncounterResult result = new EncounterResult();
        result.setWinner("TBD");
        result.setRounds(0);
        result.addLog("TODO: implement battle simulation");
        return result;
    }
}
