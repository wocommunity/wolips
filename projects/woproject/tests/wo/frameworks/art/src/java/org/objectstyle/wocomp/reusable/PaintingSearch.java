package org.objectstyle.wocomp.reusable;


import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import org.objectstyle.art.Gallery;

/** PaintingSearch	*/
public class PaintingSearch extends WOComponent {
    protected String artistNameSearch;
    protected String paintTitleSearch; 
    protected Gallery gallery;
    protected Gallery selectedGallery;  
    

    /**
     * Creates new PaintingSearch component.
     */
    public PaintingSearch(WOContext ctxt) {
        super(ctxt);
    }

    /**
     * Overrides superimplementation to make this component
     * asynchronous.
     */
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
    
    /**
     * Returns "artistNameSearch" search criteria.
     */
    public String getArtistNameSearch() {
        return artistNameSearch;
    }

    /**
     * Sets "artistNameSearch" search criteria.
     */
    public void setArtistNameSearch(String artistNameSearch) {
        this.artistNameSearch = artistNameSearch;
    }
    
    
    /** Returns a list of all galleries from the database. */
    public NSArray allGalleries() {
        return null;
    }
}
