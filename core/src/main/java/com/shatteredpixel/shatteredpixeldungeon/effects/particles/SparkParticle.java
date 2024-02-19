/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2020 Trashbox Bobylev
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
 */

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class SparkParticle extends PixelParticle {

	public static final Emitter.Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((SparkParticle)emitter.recycle( SparkParticle.class )).reset( x, y );
		}
		@Override
		public boolean lightMode() {
			return true;
		}
	};
	
	public static final Emitter.Factory STATIC = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((SparkParticle)emitter.recycle( SparkParticle.class )).resetStatic( x, y );
		}
		@Override
		public boolean lightMode() {
			return true;
		}
	};
	
	public SparkParticle() {
		super();
		
		size( 2 );
		
		acc.set( 0, +50 );
	}
	
	public void reset( float x, float y ) {
		revive();
		
		this.x = x;
		this.y = y;
		size = 5;
		
		left = lifespan = Random.Float( 0.5f, 1.0f );
		
		speed.polar( -Random.Float( 3.1415926f ), Random.Float( 20, 40 ) );
	}
	
	public void resetStatic( float x, float y){
		reset(x, y);
		
		left = lifespan = Random.Float( 0.25f, 0.5f );
		
		acc.set( 0, 0 );
		speed.set( 0, 0 );
	}

	public void setMaxSize( float value ){
		size = value;
	}
	
	@Override
	public void update() {
		super.update();
		size( Random.Float( size * left / lifespan ) );
	}
}