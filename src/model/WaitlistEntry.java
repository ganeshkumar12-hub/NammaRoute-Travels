package model;

public class WaitlistEntry {

    private String routeId;
    private String name;
    private int age;
    private String gender;

    public WaitlistEntry(String routeId, String name,
                        int age, String gender) {

        this.routeId = routeId;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }
}