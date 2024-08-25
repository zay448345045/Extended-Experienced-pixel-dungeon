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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Overload;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SuperExp;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.RandomItemTicket;
import com.shatteredpixel.shatteredpixeldungeon.items.tieredcards.TieredCard;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Infinity extends Weapon.Enchantment {

	private static Glowing FX = new Glowing( 0xaa7422 );
	
	@Override
	public long proc( Weapon weapon, Char attacker, Char defender, long damage ) {
		int level = (int) Math.max( 0, weapon.buffedLvl() );
		if (Random.Int(4) == 0 && attacker.buff(Overload.class) == null) {
			Buff.affect(attacker, Overload.class, 2f);
		}
		if (Random.Int(100) == 0 && attacker.buff(SuperExp.class) == null) {
			Buff.affect(attacker, SuperExp.class).set(1000f);
		}
		if (Random.Int(10) == 0 && attacker.buff(Barrier.class) == null) {
			Buff.affect(attacker, Barrier.class).setShield(attacker.HT);
		}

		if (attacker.buff(Overload.class) != null || attacker.buff(SuperExp.class) != null) {
			for (int i = 0; i < 5; i++) {
				Dungeon.level.drop(Generator.random(), attacker.pos).sprite.drop();
			}
		}

		damage += damage * 0.05f;
		return damage;
	}
	
	@Override
	public Glowing glowing() {
		return FX;
	}
}
