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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class TelekineticGrab extends TargetedSpell {

	{
		image = ItemSpriteSheet.TELE_GRAB;
	}

	@Override
	protected void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.BEACON,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		Char ch = Actor.findChar(bolt.collisionPos);

		//special logic for DK when he is on his throne
		if (ch == null && bolt.path.size() > bolt.dist+1){
			ch = Actor.findChar(bolt.path.get(bolt.dist+1));
			if (!(ch instanceof DwarfKing && Dungeon.level.solid[ch.pos])){
				ch = null;
			}
		}

		if (ch != null && ch.buff(PinCushion.class) != null){

			while (ch.buff(PinCushion.class) != null) {
				Item item = ch.buff(PinCushion.class).grabOne();

				if (item.doPickUp(hero, ch.pos)) {
					hero.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn
					GLog.i( Messages.capitalize(Messages.get(hero, "you_now_have", item.name())) );

				} else {
					GLog.w(Messages.get(this, "cant_grab"));
					Dungeon.level.drop(item, ch.pos).sprite.drop();
					return;
				}

			}

		} else if (Dungeon.level.heaps.get(bolt.collisionPos) != null){

			for (int n : PathFinder.NEIGHBOURS25){
				int cell = bolt.collisionPos + n;

				Heap h = Dungeon.level.heaps.get(cell);
				if (h != null) {

					if (h.type != Heap.Type.HEAP) {
						GLog.w(Messages.get(this, "cant_grab"));
						h.sprite.drop();
						continue;
					}

					while (!h.isEmpty()) {
						Item item = h.peek();
						if (item.doPickUp(hero, h.pos)) {
							h.pickUp();
							hero.spend(-Item.TIME_TO_PICK_UP); //casting the spell already takes a turn
							GLog.i(Messages.capitalize(Messages.get(hero, "you_now_have", item.name())));
						} else {
							GLog.w(Messages.get(this, "cant_grab"));
							h.sprite.drop();
							break;
						}
					}
				}
			}


		} else {
			GLog.w(Messages.get(this, "no_target"));
		}

	}

	@Override
	public long value() {
		//prices of ingredients, divided by output quantity, rounds down
		return (int)((50 + 40) * (quantity/2f));
	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		{
			inputs =  new Class[]{ArcaneCatalyst.class};
			inQuantity = new int[]{3};

			cost = 5;

			output = TelekineticGrab.class;
			outQuantity = 6;
		}

	}

}
