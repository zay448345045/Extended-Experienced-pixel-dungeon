/*
 *
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2024 Trashbox Bobylev
 *
 * Extended Experienced Pixel Dungeon
 * Copyright (C) 2023-2024 John Nollas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.items.questionnaires;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RefreshCooldown;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.fragments.YellowFragment;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.tieredcards.TieredCard;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Calendar;

public class ThreeOperationQuestionnaire extends Questionnaire {
    {
        image = ItemSpriteSheet.THREE_OP;
        unique = true;
        identify();
        defaultAction = AC_ANSWER;
    }

    private static final String AC_ANSWER = "ANSWER";
    private static final String AC_REFRESH = "REFRESH";

    private int CODE = Random.Int(100);
    private int CODE2 = Random.Int(100);
    private int CODE3 = Random.Int(100);
    private int CODE4 = Random.Int(100);
    private String ANSWER = "start";
    public static int totalAnswers_m = 0;
    public static int streak_m = 0;
    public static int t = totalAnswers_m;
    public static int s = streak_m;
    public static int randomizer = Random.Int(6);
    private String BODY = "Type _start_ to start";
    public static String type = "None";



    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( AC_ANSWER );
        actions.add( AC_REFRESH );
        actions.remove( AC_THROW );
        actions.remove( AC_DROP );
        return actions;
    }

    @Override
    public long quantity() {
        return totalAnswers_m;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public void execute( Hero hero, String action ) {
        super.execute( hero, action );
        if (action.equals( AC_ANSWER ) && hero.buff(CodeCooldown14.class) == null ) {
            askCode();
        } else if (action.equals( AC_ANSWER ) && hero.buff(CodeCooldown14.class) != null) {
            GLog.w(Messages.get(this, "cooldown"));
            SpellSprite.show(hero, SpellSprite.COOLDOWN);
        }
        if (action.equals( AC_REFRESH ) && hero.buff(RefreshCooldown.class) == null){
            Buff.affect(hero, RefreshCooldown.class).set(50);

        }
        else if (action.equals( AC_REFRESH ) && hero.buff(RefreshCooldown.class) != null) {
            GLog.w(Messages.get(RefreshCooldown.class, "cooldown"));
            SpellSprite.show(hero, SpellSprite.COOLDOWN);
        }
    }

    private void askCode() {
        GameScene.show(new WndTextInput( "Input Answer",BODY + "\n\n_Please use the PEMDAS method instead_", "", 10, false, "Done", "Cancel" ) {
            @Override
            public void onSelect( boolean positive, String text ) {
                if (text.equals(ANSWER)) {
                    randomizer = Random.Int(6);
                    switch (randomizer) {
                        case 0:
                            CODE = Random.Int(100);
                            CODE2 = Random.Int(100);
                            CODE3 = Random.Int(100);
                            CODE4 = Random.Int(100);
                            ANSWER = String.valueOf(CODE + CODE2 + CODE3 + CODE4);
                            GLog.h("\n[Type: PLUS x3]");
                            BODY = "Perform the following operations: " + CODE + " + " + CODE2 + " + " + CODE3 + " + " + CODE4;
                            type = "PLUS x3";
                            break;
                        case 1:
                            CODE = Random.Int(100);
                            CODE2 = Random.Int(100);
                            CODE3 = Random.Int(100);
                            CODE4 = Random.Int(100);
                            ANSWER = String.valueOf((CODE + CODE2 + CODE3) - CODE4);
                            GLog.h("\n[Type: PLUS x2 and SUB x1]");
                            BODY = "Perform the following operations: " + CODE + " + " + CODE2 + " + " + CODE3 + " - " + CODE4;
                            type = "PLUS x2 and SUB x1";
                            break;
                        case 2:
                            CODE = Random.Int(100);
                            CODE2 = Random.Int(100);
                            CODE3 = Random.Int(100);
                            CODE4 = Random.Int(100);
                            ANSWER = String.valueOf((CODE + CODE2) - CODE3 - CODE4);
                            GLog.h("\n[Type: PLUS x1 and SUB x2]");
                            BODY = "Perform the following operations: " + CODE + " + " + CODE2 + " - " + CODE3 + " - " + CODE4;
                            type = "PLUS x1 and SUB x2]";
                            break;
                        case 3:
                            CODE = Random.Int(100);
                            CODE2 = Random.Int(100);
                            CODE3 = Random.Int(100);
                            CODE4 = Random.Int(100);
                            ANSWER = String.valueOf(CODE - CODE2 - CODE3 - CODE4);
                            GLog.h("\n[Type: SUB x3]");
                            BODY = "Perform the following operations: " + CODE + " - " + CODE2 + " - " + CODE3 + " - " + CODE4;
                            type = "SUB x3";
                            break;
                        case 4:
                            CODE = Random.Int(100);
                            CODE2 = Random.Int(100);
                            CODE3 = Random.Int(100);
                            CODE4 = Random.Int(100);
                            ANSWER = String.valueOf(CODE - (CODE2 + CODE3) - CODE4);
                            GLog.h("\n[Type: PLUS x1 and SUB x2]");
                            BODY = "Perform the following operations: " + CODE + " - " + CODE2 + " + " + CODE3 + " - " + CODE4;
                            type = "PLUS x1 and SUB x2";
                            break;
                        case 5:
                            CODE = Random.Int(100);
                            CODE2 = Random.Int(100);
                            CODE3 = Random.Int(100);
                            CODE4 = Random.Int(100);
                            ANSWER = String.valueOf((CODE + CODE2) - (CODE3 + CODE4));
                            GLog.h("\n[Type: PLUS x2 and SUB x1]");
                            BODY = "Perform the following operations: " + CODE + " + " + CODE2 + " - " + CODE3 + " + " + CODE4;
                            type = "PLUS x2 and SUB x1";
                            break;
                    }

                    if (text.equals("start")) {
                        GLog.h("You may now start answering the questions.");
                    }

                    if (!text.equals("start")) {
                        Buff.affect(hero, CodeCooldown14.class).set(3);
                        GLog.h("You answered the question correctly!");
                        SpellSprite.show(hero, SpellSprite.CORRECT);
                        if (hero.pointsInTalent(Talent.QUESTIONNAIRE_SUPERVISOR) >= 1){
                            Buff.affect(hero, EnhancedRings.class, 3f);
                        }
                        if (hero.pointsInTalent(Talent.QUESTIONNAIRE_SUPERVISOR) >= 2){
                            streak_m += 1;
                        }
                        if (hero.pointsInTalent(Talent.QUESTIONNAIRE_SUPERVISOR) >= 3){
                            Buff.affect(hero, Barrier.class).setShield(hero.HT/4);
                        }

                        if (hero.pointsInTalent(Talent.QUESTIONNAIRE_SUPERVISOR_II) >= 1){
                            updateQuickslot();
                            Dungeon.level.drop(Generator.random(), curUser.pos).sprite.drop();
                        }
                        if (hero.pointsInTalent(Talent.QUESTIONNAIRE_SUPERVISOR_II) >= 2){
                            Buff.affect(hero, Healing.class).setHeal(hero.HT, 0.1f, 0);
                        }
                        if (hero.pointsInTalent(Talent.QUESTIONNAIRE_SUPERVISOR_II) >= 3 && Random.Int(10) == 0){
                            updateQuickslot();
                            Dungeon.level.drop(new TieredCard().upgrade(Math.round(4 + totalAnswers_m/4)), curUser.pos).sprite.drop();
                        }

                        totalAnswers_m += 1;
                        streak_m += 1 + YellowFragment.questionnairesStreakAdd();
                        if (gregcal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                            streak_m += 2;
                        }
                        if (Random.Float() >= 0.95f) {
                            // 5% of getting an exp
                            updateQuickslot();
                            hero.earnExp(hero.maxExp(), ThreeOperationQuestionnaire.class);
                        } else if (Random.Float() >= 0.05f && Random.Float() < 0.95f) {
                            // 90% of getting scroll of upgrade
                            updateQuickslot();
                            Dungeon.level.drop(new ScrollOfUpgrade().quantity(Math.min(100, 3 + (streak_m/5))), curUser.pos).sprite.drop();
                        } else {
                            // 5% of additional streak point
                            updateQuickslot();
                            streak_m += 1;
                        }
                    }

                } else if (text.equals("")) {
                    GLog.w("You didn't answer the question.");
                } else if (text.equals("31718")) {
                    GLog.h("We can't say that you found this bs...\n");
                    InterlevelScene.curTransition = new LevelTransition(Dungeon.level, -1, LevelTransition.Type.BRANCH_EXIT, -999, 0, LevelTransition.Type.BRANCH_ENTRANCE);
                    InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
                    Game.switchScene( InterlevelScene.class );
                } else {
                    GLog.w("That answer is not equals as the given, try again.");
                    SpellSprite.show(hero, SpellSprite.INCORRECT);
                    streak_m = 0;
                }
            }

            @Override
            public void onBackPressed() {
                GLog.w("You didn't answer the question.");
                this.hide();
            }
        } );
    }


    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing( 0x66785a, 0.3f );
    }

    @Override
    public long level() {
        return streak_m;
    }

    @Override
    public String name() {
        return "Three Operation Questionnaire";
    }

    @Override
    public String desc() {
        return "This item represents your intelligence, it may also give you some rewards.\n\n" + "Body: " + BODY + "\nType: " + type + "\nAnswered Correctly: " + totalAnswers_m + "\nStreak: " + streak_m
                + "\n\n_Streaks resets at zero when wrong answer is entered._"
                + "\n\n_Streak Pass List:_";
    }
    private String STREAKS = "STREAKS";
    private String TOTAL_ANSWERS = "TOTAL_ANSWERS";
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( STREAKS, streak_m );
        bundle.put( TOTAL_ANSWERS, totalAnswers_m );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        streak_m = bundle.getInt(STREAKS);
        totalAnswers_m = bundle.getInt(TOTAL_ANSWERS);
    }

    public static class CodeCooldown14 extends Buff {

        int duration = 0;
        int maxDuration = 0;

        {
            type = buffType.NEUTRAL;
            announced = false;
        }

        public void set(int time) {
            maxDuration = time;
            duration = maxDuration;
        }

        public void hit(int time) {
            duration -= time;
            if (duration <= 0) detach();
        }

        @Override
        public boolean act() {
            duration--;
            if (duration <= 0) {
                detach();
            }
            spend(TICK);
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x66785a);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (maxDuration - duration) / maxDuration);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(duration);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", duration);
        }

        private static final String MAX_DURATION = "maxDuration";
        private static final String DURATION = "duration";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( MAX_DURATION, maxDuration );
            bundle.put( DURATION, duration );
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            maxDuration = bundle.getInt( MAX_DURATION );
            duration = bundle.getInt( DURATION );
        }
    }
}
