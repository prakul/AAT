package org.osmdroid.tileprovider.tilesource;

import org.osmdroid.tileprovider.MapTile;

public abstract class OnlineTileSourceBase extends BitmapTileSourceBase {

	private final String mBaseUrls[];

	public OnlineTileSourceBase(final String aName,
			final int aZoomMinLevel, final int aZoomMaxLevel,
			final String aImageFilenameEnding, final String... aBaseUrl) {
		super(aName, aZoomMinLevel, aZoomMaxLevel,
				aImageFilenameEnding);
		mBaseUrls = aBaseUrl;
	}

	public abstract String getTileURLString(MapTile aTile);

	/**
	 * Get the base url, which will be a random one if there are more than one.
	 */
	protected String getBaseUrl() {
		return mBaseUrls[random.nextInt(mBaseUrls.length)];
	}
}
