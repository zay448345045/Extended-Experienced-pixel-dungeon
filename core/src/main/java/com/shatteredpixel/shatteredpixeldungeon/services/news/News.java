/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.services.news;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;

import java.util.ArrayList;
import java.util.Date;

public class News {

	public static NewsService service;

	public static boolean supportsNews(){
		return service != null;
	}

	private static Date lastCheck = null;
	private static final long CHECK_DELAY = 1000*60*60; //1 hour

	public static void checkForNews(){
		if (!supportsNews()) return;
		if (lastCheck != null && (new Date().getTime() - lastCheck.getTime()) < CHECK_DELAY) return;

		boolean useHTTPS = true;
		if (Gdx.app.getType() == Application.ApplicationType.Android && Gdx.app.getVersion() < 20){
			useHTTPS = false; //android versions below 5.0 don't support TLSv1.2 by default
		}
		service.checkForArticles(!SPDSettings.WiFi(), useHTTPS, new NewsService.NewsResultCallback() {
			@Override
			public void onArticlesFound(ArrayList<NewsArticle> articles) {
				lastCheck = new Date();
				News.articles = articles;
			}

			@Override
			public void onConnectionFailed() {
				lastCheck = null;
				News.articles = null;
			}
		});

	}

	private static ArrayList<NewsArticle> articles;

	public static boolean articlesAvailable(){
		return articles != null;
	}

	public static ArrayList<NewsArticle> articles(){
		return new ArrayList<>(articles);
	}

	public static int unreadArticles(Date lastRead){
		return 0;
	}

	public static void clearArticles(){
		articles = null;
		lastCheck = null;
	}

	public static Image parseArticleIcon(NewsArticle article){

		try {

			//recognized formats are:
			//"ICON: <name of enum constant in Icons.java>"
			if (article.icon.startsWith("ICON: ")){
				return Icons.get(Icons.valueOf(article.icon.replace("ICON: ", "")));
			//"ITEM: <integer constant corresponding to values in ItemSpriteSheet.java>"
			} else if (article.icon.startsWith("ITEM: ")){
				return new ItemSprite(Integer.parseInt(article.icon.replace("ITEM: ", "")));
			//"<asset filename>, <tx left>, <tx top>, <width>, <height>"
			} else {
				String[] split = article.icon.split(", ");
				return new Image( split[0],
						Integer.parseInt(split[1]),
						Integer.parseInt(split[2]),
						Integer.parseInt(split[3]),
						Integer.parseInt(split[4]));
			}

		//if we run into any formatting errors (or icon is null), default to the news icon
		} catch (Exception e){
			if (article.icon != null) ShatteredPixelDungeon.reportException(e);
			return Icons.get(Icons.NEWS);
		}
	}

}
