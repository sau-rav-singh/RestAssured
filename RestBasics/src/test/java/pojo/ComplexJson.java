package pojo;

import lombok.Data;

import java.util.List;

public class ComplexJson {
    @Data
    public static class Dashboard {
        private int purchaseAmount;
        private String website;
    }

    @Data
    public static class Sales {
        private int udemy;
        private int website;
    }

    @Data
    public static class Review {
        private String channel;
        private int reviewcount;
    }

    @Data
    public static class Course {
        private String title;
        private int price;
        private int copies;
        private Sales sales;
        private List<Review> reviews;
    }

    @Data
    public static class ComplexJsonData {
        private Dashboard dashboard;
        private List<Course> courses;
    }
}
