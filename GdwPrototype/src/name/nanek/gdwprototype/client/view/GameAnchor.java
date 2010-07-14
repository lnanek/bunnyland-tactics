package name.nanek.gdwprototype.client.view;

import name.nanek.gdwprototype.client.model.GameListing;

public class GameAnchor {

	private static final String GAME_LISTING_ANCHOR_PREFIX = "game_";
	
	private static final char GAME_LISTING_ANCHOR_SEPERATOR = '_';

	/**
	 * Generate the anchor for a game.
	 * @param listing GameListing detailing game
	 * @return anchor to use
	 */
	public static String generateAnchor(GameListing listing) {
		return GAME_LISTING_ANCHOR_PREFIX + listing.getId() 
			+ GAME_LISTING_ANCHOR_SEPERATOR + listing.getLinkName();
	}

	/**
	 * Extract the game ID from the passed anchor.
	 * @param anchor String anchor
	 * @return game id or null if none found
	 */
	public static Long getIdFromAnchor(String anchor) {
		if ( null == anchor ) {
			return null;
		}		
		if ( !anchor.startsWith(GAME_LISTING_ANCHOR_PREFIX)) {
			return null;
		}
		
		String textRemoved = anchor.substring(GAME_LISTING_ANCHOR_PREFIX.length());
		int seperatorLocation = textRemoved.indexOf(GAME_LISTING_ANCHOR_SEPERATOR);
		if ( -1 != seperatorLocation ) {
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
