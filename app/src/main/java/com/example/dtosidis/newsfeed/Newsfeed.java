package com.example.dtosidis.newsfeed;

/**
 * An {@link Newsfeed} object contains information related to a single Newsfeed.
 */
public class Newsfeed {

    /** section of the Newsfeed */
    private String mSection;

    /** title of the Newsfeed */
    private String mTitle;

    /** author name of the Newsfeed */
    private String mAuthorName;

    /** author surname of the Newsfeed */
    private String mAuthorSurname;

    /** Date and time of the Newsfeed */
    private String mDateTime;

    /** Website URL of the Newsfeed */
    private String mUrl;

    /**
     * Constructs a new {@link Newsfeed} object.
     *
     * @param section is the section (theme) of the Newsfeed
     * @param title is the title of the Newsfeed
     * @param authorName
     * @param authorSurname
     * @param datetime is the date and time in dateTtimeZ format
     * @param url is the website URL to find more details about the Newsfeed
     */
    public Newsfeed(String section, String title, String authorName, String authorSurname, String datetime, String url) {
        mSection = section;
        mTitle = title;
        mAuthorName = authorName;
        mAuthorSurname = authorSurname;
        mDateTime = datetime;
        mUrl = url;
    }

    /**
     * Returns the section of the Newsfeed.
     */
    public String getSection() {
        return mSection;
    }

    /**
     * Returns the title of the Newsfeed.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the author name of the Newsfeed.
     */
    public String getAuthorName() {
        return mAuthorName;
    }
    /**
     * Returns the author surname of the Newsfeed.
     */
    public String getAuthorSurname() {
        return mAuthorSurname;
    }

    /**
     * Returns the datetime of the Newsfeed.
     */
    public String getDateTime() {
        return mDateTime;
    }

    /**
     * Returns the website URL to find more information about the Newsfeed.
     */
    public String getUrl() {
        return mUrl;
    }
}