package pojo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CourseDetails {

    private String instructor;
    private String url;
    private String services;
    private String expertise;
    private Map<String, List<Course>> courses;
    private String linkedIn;

    @Data
    public static class Course {
        private String courseTitle;
        private String price;
    }
}


