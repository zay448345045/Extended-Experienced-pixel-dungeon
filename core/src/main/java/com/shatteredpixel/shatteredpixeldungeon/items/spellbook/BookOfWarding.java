package com.shatteredpixel.shatteredpixeldungeon.items.spellbook;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.GooWarn;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BookOfWarding extends SpellBook {

    {
        image = ItemSpriteSheet.BOOK_OF_WARDING;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_READ)) {
            if (hero.buff(SpellBookCoolDown.class) != null) {
                GLog.w(Messages.get(this, "cooldown"));
            } else {
                 Buff.affect(hero, SpellBookCoolDown.class).set(35);
                readEffect();
            }
        }
    }

    @Override
    public void readEffect() {
        int spawned = spawnImages(Dungeon.hero.pos, Math.round((2+Dungeon.hero.lvl/2f)*(1+0.5f )), 3);
        if (spawned > 0) {
            GLog.p(Messages.get(this, "appear"));
        }
    }

    @Override
    public String info() {
        String info = super.info();
        if (Dungeon.hero.buff(SpellBookCoolDown.class) == null) {
            info += "\n\n" + Messages.get(this, "time",
                    Math.round((2+Dungeon.hero.lvl/2f)*(1+0.5f )));
        }
        return info;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{WandOfWarding.class};
            inQuantity = new int[]{1};

            cost = 5;

            output = BookOfWarding.class;
            outQuantity = 1;
        }

    }

    public static int spawnImages( int centerPos, int amount, int distance ){
        ArrayList<Integer> respawnPoints = new ArrayList<>();
        PathFinder.buildDistanceMap( centerPos, BArray.or( Dungeon.level.passable, Dungeon.level.avoid, null ), distance );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (i != centerPos
                    && PathFinder.distance[i] < Integer.MAX_VALUE
                    && Dungeon.level.map[i] != Terrain.CHASM
                    && !respawnPoints.contains(i)) {
                respawnPoints.add(i);
            }
        }
        Random.shuffle(respawnPoints);

        int spawned = 0;
        for (int pos : respawnPoints) {
            if (amount <= 0) {
                break;
            }

            MirrorImage mob = new MirrorImage();
            mob.duplicate( Dungeon.hero );
            GameScene.add( mob );
            mob.move( pos, false );

            if (mob.pos == pos) {
                mob.sprite.interruptMotion();
                mob.sprite.place(pos);
            }
            if (Dungeon.level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
            Sample.INSTANCE.play(Assets.Sounds.PUFF);
            amount--;
            spawned++;
        }

        return spawned;
    }
}
