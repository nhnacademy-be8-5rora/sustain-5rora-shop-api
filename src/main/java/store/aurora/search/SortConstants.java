package store.aurora.search;

public class SortConstants {
    public static final String SALE_PRICE = "saleprice";
    public static final String PUBLISH_DATE = "publishdate";
    public static final String TITLE = "title";
    public static final String REVIEW_RATING = "reviewrating";
    public static final String LIKE = "like";
    public static final String VIEW = "view";
    public static final String REVIEWCOUNT = "reviewcount";

    // Private constructor to prevent instantiation
    private SortConstants() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }
}
