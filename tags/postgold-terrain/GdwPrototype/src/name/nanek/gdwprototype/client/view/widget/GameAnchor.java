package name.nanek.gdwprototype.client.view.widget;

import name.nanek.gdwprototype.shared.model.Game;

/**
 * Links to a game.
 * 
 * @author Lance Nanek
 *
 */
public class GameAnchor {

	private static final String GAME_LISTING_ANCHOR_PREFIX = "game_";

	private static final char GAME_LISTING_ANCHOR_SEPERATOR = '_';

	/**
	 * Generate the anchor for a game.
	 * 
	 * @param listing
	 *            GameListing detailing game
	 * @return anchor to use
	 */
	public static String generateAnchor(Game listing) {
		String prefixAndId = GAME_LISTING_ANCHOR_PREFIX + listing.getId();
		
		String linkName = listing.getLinkName();
		if ( null == linkName ) {
			return prefixAndId;
		}
		
		return prefixAndId + GAME_LISTING_ANCHOR_SEPERATOR + linkName;
	}

	/**
	 * Extract the game ID from the passed anchor.
	 * 
	 * @param anchor
	 *            String anchor
	 * @return game id or null if none found
	 */
	public static Long getIdFromAnchor(String anchor) {
		if (null == anchor) {
			return null;
		}
		if (!anchor.startsWith(GAME_LISTING_ANCHOR_PREFIX)) {
			return null;
		}

		String textRemoved = anchor.substring(GAME_LISTING_ANCHOR_PREFIX.length());
		int seperatorLocation = textRemoved.indexOf(GAME_LISTING_ANCHOR_SEPERATOR);
		if (-1 != seperatorLocation) {
			textRemoved = textRemoved.substring(0, seperatorLocation);
		}

		Long id;
		try {
			id = Long.parseLong(textRemoved);
		} catch (NumberFormatException e) {
			return null;
		}
		return id;
	}

}
