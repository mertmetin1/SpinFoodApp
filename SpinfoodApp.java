package programms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpinfoodApp {
    public static void main(String[] args) {
        // Read participant data from CSV file
        List<Participant> participants = readParticipantsFromCSV("C:\\Users\\erdem\\OneDrive\\Masaüstü\\participants.txt");

        // Create couples based on the participants' data
        List<Couple> couples = createCouples(participants);

        // Create groups for each course based on the couples' data
        List<Group> groups = createGroups(couples);

        // Read the list of participant cancellations
        
        List<Participant> cancellations = getCancellations(participants);
        // Adjust the couples and groups lists based on the cancellations
        adjustLists(cancellations, couples, groups);
        calculateMetricsForCouples(couples);
        calculateMetricsForGroups(groups);
        visualizePairs(couples);
        visualizeGroups(groups);
        getCancellations(participants);
       
    }
    private static void calculateMetricsForGroups(List<Group> groups) {
        for (Group group : groups) {
            int totalMetrics = 0;
            for (Couple couple : group.getCouples()) {
                totalMetrics += couple.getMetrics();
            }
            group.setMetrics(totalMetrics);
        }
    }
    private static void calculateMetricsForCouples(List<Couple> couples) {
        for (Couple couple : couples) {
            int ageDifference = Math.abs(couple.getParticipant1().getAge() - couple.getParticipant2().getAge());
            int averageAge = (couple.getParticipant1().getAge() + couple.getParticipant2().getAge()) / 2;
            couple.setMetrics(ageDifference + averageAge);
        }
    }

    private static List<Participant> readParticipantsFromCSV(String filename) {
        List<Participant> participants = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                String foodPreference = data[2];
                int age = Integer.parseInt(data[3]);
                String sex = data[4];
                boolean kitchenAvailability = Boolean.parseBoolean(data[5]);

                Participant participant = new Participant(id, name, foodPreference, age, sex, kitchenAvailability);
                participants.add(participant);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return participants;
    }

    
    private static List<Couple> createCouples(List<Participant> participants) {
        List<Couple> couples = new ArrayList<>();

        for (int i = 0; i < participants.size(); i += 2) {
            Participant participant1 = participants.get(i);
            Participant participant2 = participants.get(i + 1);

            Couple couple = new Couple(participant1, participant2);
            couples.add(couple);
        }

        return couples;
    }

    private static List<Group> createGroups(List<Couple> couples) {
        List<Group> groups = new ArrayList<>();

        for (int i = 0; i < couples.size(); i += 3) {
            List<Couple> groupCouples = new ArrayList<>();

            for (int j = i; j < i + 3 && j < couples.size(); j++) {
                Couple couple = couples.get(j);
                groupCouples.add(couple);
            }

            Group group = new Group(groupCouples);
            groups.add(group);
        }

        return groups;
    }

    private static List<Participant> getCancellations(List<Participant> participants) {
        List<Participant> cancellations = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\erdem\\OneDrive\\Masaüstü\\cancellations.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                int cancellationId = Integer.parseInt(line);

                // Find the canceled participant by ID and add it to the cancellations list
                for (Participant participant : participants) {
                    if (participant.getId() == cancellationId) {
                        cancellations.add(participant);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cancellations;
    }









    private static void adjustLists(List<Participant> cancellations, List<Couple> couples, List<Group> groups) {
        // Adjust the couples and groups lists based on the cancellations
        for (Participant cancellation : cancellations) {
            // Remove canceled participant from couples
            for (int i = 0; i < couples.size(); i++) {
                Couple couple = couples.get(i);
                if (couple.getParticipant1().equals(cancellation) || couple.getParticipant2().equals(cancellation)) {
                    couples.remove(i);
                    i--;
                }
            }

            // Remove canceled participant from groups
            for (int i = 0; i < groups.size(); i++) {
                Group group = groups.get(i);
                List<Couple> groupCouples = group.getCouples();

                for (int j = 0; j < groupCouples.size(); j++) {
                    Couple couple = groupCouples.get(j);
                    if (couple.getParticipant1().equals(cancellation) || couple.getParticipant2().equals(cancellation)) {
                        groupCouples.remove(j);
                        j--;
                    }
                }

                // If the group becomes empty, remove it from the list of groups
                if (groupCouples.isEmpty()) {
                    groups.remove(i);
                    i--;
                }
            }
        }
    }

    private static void visualizePairs(List<Couple> couples) {
        System.out.println("Pairs:");
        for (Couple couple : couples) {
            System.out.println("Participant 1: " + couple.getParticipant1().getName());
            System.out.println("Participant 2: " + couple.getParticipant2().getName());
            System.out.println("Metrics: " + couple.getMetrics());
            System.out.println();
        }
    }

    private static void visualizeGroups(List<Group> groups) {
        System.out.println("Groups:");
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            System.out.println("Group " + (i + 1) + ":");
            List<Couple> groupCouples = group.getCouples();
            for (Couple couple : groupCouples) {
                System.out.println("Participant 1: " + couple.getParticipant1().getName());
                System.out.println("Participant 2: " + couple.getParticipant2().getName());
                System.out.println("Metrics: " + couple.getMetrics());
                System.out.println();
            }
            System.out.println();
        }
    }

}

class Participant {
    private int id;
    private String name;
    private String foodPreference;
    private int age;
    private String sex;
    private boolean kitchenAvailability;

    public Participant(int id, String name, String foodPreference, int age, String sex, boolean kitchenAvailability) {
        this.id = id;
        this.name = name;
        this.foodPreference = foodPreference;
        this.age = age;
        this.sex = sex;
        this.kitchenAvailability = kitchenAvailability;
    }

    // Getter and setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isKitchenAvailability() {
        return kitchenAvailability;
    }

    public void setKitchenAvailability(boolean kitchenAvailability) {
        this.kitchenAvailability = kitchenAvailability;
    }
}

class Couple {
    private Participant participant1;
    private Participant participant2;
    private int metrics;

    public Couple(Participant participant1, Participant participant2) {
        this.participant1 = participant1;
        this.participant2 = participant2;
    }

 
    // Getter and setter methods
    public Participant getParticipant1() {
        return participant1;
    }

    public void setParticipant1(Participant participant1) {
        this.participant1 = participant1;
    }

    public Participant getParticipant2() {
        return participant2;
    }

    public void setParticipant2(Participant participant2) {
        this.participant2 = participant2;
    }

    public int getMetrics() {
        return metrics;
    }

    public void setMetrics(int metrics) {
        this.metrics = metrics;
    }
}

class Group {
    private List<Couple> couples;
    private int metrics;

    public Group(List<Couple> couples) {
        this.couples = couples;
    }

    // Getter and setter methods
    public List<Couple> getCouples() {
        return couples;
    }

    public void setCouples(List<Couple> couples) {
        this.couples = couples;
    }

    public int getMetrics() {
        return metrics;
    }

    public void setMetrics(int metrics) {
        this.metrics = metrics;
    }
}

