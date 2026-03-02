package com.narxoz.rpg.battle;

import java.util.ArrayList;
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
        if (teamA == null || teamB == null) {
            throw new IllegalArgumentException("teamA/teamB mustn't be null");
        }
        List<Combatant> a = copyAndClean(teamA);
        List<Combatant> b = copyAndClean(teamB);

        EncounterResult result = new EncounterResult();
        result.addLog("Seed = " + seed);
        result.addLog("Start: Team A (" + a.size() + ") vs Team B (" + b.size() + ")");

        if (a.isEmpty() && b.isEmpty()) {
            result.setWinner("Draw");
            result.setRounds(0);
            result.addLog("Both teams are empty. Draw");
            return result;
        }
        if (a.isEmpty()) {
            result.setWinner("Team B");
            result.setRounds(0);
            result.addLog("Team A is empty. Team B wins by default");
            return result;
        }
        if(b.isEmpty()) {
            result.setWinner("Team A");
            result.setRounds(0);
            result.addLog("Team B is empty. Team A wins by default");
            return result;
        }

        int rounds = 0;

        final int maxRounds = 10000;

        while (!a.isEmpty() && !b.isEmpty() && rounds < maxRounds){
            rounds ++ ;
            result.addLog("");
            result.addLog("=== Round " + rounds + " ===");
            int damageThisRound = 0;
            damageThisRound += attackPhase("A", a, b, result);

            if (b.isEmpty()) {
                break;
            }
            damageThisRound += attackPhase("B", b, a, result);

            if(damageThisRound == 0){
                result.addLog("No damage dealt this round. Stalemate -> Draw");
                break;
            }
        }

        if (rounds >= maxRounds) {
            result.setWinner("Draw");
        }

        result.setRounds(rounds);
        if(a.isEmpty() && b.isEmpty()){
            result.setWinner("Draw");
        } else if (b.isEmpty()) {
            result.setWinner("Team A");
        }
        else if(a.isEmpty()){
            result.setWinner("Team B");
        }
        else{
            result.setWinner("Draw");
        }

        result.addLog("");
        result.addLog("Battle ended. Winner = " + result.getWinner());
        return result;
    }
        private int attackPhase(String attackersName, List<Combatant> attackers, List<Combatant> defenders, EncounterResult result){
        int totalDamage = 0;

        removeDead(defenders);
        removeDead(attackers);

        for(int i = 0; i<attackers.size() && !defenders.isEmpty(); i++){
            Combatant attacker = attackers.get(i);

            if(attacker == null || !attacker.isAlive()){
                continue;
            }

            Combatant target = getFirstAlive(defenders);
            if(target == null){
                removeDead(defenders);
                break;
            }
            int baseDamage = safeNonNegative(attacker.getAttackPower());

            int roll = random.nextInt(100);
            boolean crit = baseDamage > 0 && roll < CRIT_CHANCE_PERCENT;

            int finalDamage = baseDamage;
            if (crit) {
                finalDamage = baseDamage * CRIT_MULTIPLIER;
            }

            if(finalDamage == 0){
                result.addLog("Team " + attackersName + ": " + attacker.getName()
                        + " attacks " + target.getName() + " but deals 0 damage");
            }
            else{
                result.addLog("Team " + attackersName + ": " + attacker.getName()
                        + " hits " + target.getName() + " for " + finalDamage
                        + " damage" + (crit ? " (CRIT)" : "") + ".");
            }
            target.takeDamage(finalDamage);
            totalDamage += finalDamage;

            if(!target.isAlive()){
                result.addLog("->" + target.getName() + " is defeated and removed from battle");
                removeDead(defenders);
            }
        }
        return totalDamage;

    }

    private List<Combatant> copyAndClean(List<Combatant> team){
        List<Combatant> copy = new ArrayList<>();
        for(Combatant c : team){
            if(c != null && c.isAlive()){
                copy.add(c);
            }
        }
        return copy;
    }

    private Combatant getFirstAlive(List<Combatant> team){
        for(Combatant c : team){
            if(c != null && c.isAlive()){
                return c;
            }
        }
        return null;
    }

    private void removeDead(List<Combatant> team){
        for(int i = team.size() -1; i>=0; i--){
            Combatant c = team.get(i);
            if(c == null || !c.isAlive()){
                team.remove(i);
            }
        }
    }

    private int safeNonNegative(int value){
        return Math.max(0, value);
    }

}
