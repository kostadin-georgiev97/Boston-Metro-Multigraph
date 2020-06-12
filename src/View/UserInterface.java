package View;

import Controller.Station;

import java.util.*;

public class UserInterface {

    private Scanner reader;

    public UserInterface(){
    }

    public UICodes presentOptions(){

        printMenuOptions();

        int inputInteger = getIntFromUser(0, 1);

        switch (inputInteger){
            case 1:
                return UICodes.GET_DIRECTIONS;
            case 0:
                return UICodes.EXIT;
            default:
                return UICodes.INPUT_ERROR;
        }

    }

    private void printMenuOptions(){
        System.out.println("What would you like to do?");
        System.out.println("1) Get directions between stations.");
        System.out.println("0) Exit the program.");
    }

    private int getIntFromUser(int min, int max){

        reader = new Scanner(System.in);

        int inputInteger = min-1;

        while (inputInteger < min || inputInteger > max){
            String input = reader.next();
            try{
                inputInteger = Integer.parseInt(input);
                if(inputInteger < min || inputInteger > max){
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            }catch (NumberFormatException numEx){
                System.out.println("Please enter a number. Within " + min + " and " + max + ".");
            }
        }

        return inputInteger;
    }

    public List<String> getDirectionsInput(){
        reader = new Scanner(System.in);

        List<String> stations = new ArrayList<>();

        System.out.println("Please enter your starting station. Or type :q to exit the program.");

        String input;

        while (true){
            input = reader.nextLine();
            if(!input.isEmpty())
                break;
            System.out.println("A station has a name. Please enter it.");
        }

        if(input.equals(":q")){
            System.exit(0);
        }

        stations.add(input);

        System.out.println("Please enter your destination station.  Or type :q to exit the program.");

        while (true){
            input = reader.nextLine();
            if(!input.isEmpty())
                break;
            System.out.println("A station has a name. Please enter it.");
        }

        if(input.equals(":q")){
            System.exit(0);
        }

        stations.add(input);

        return stations;

    }

    // To be called for the case where there are stations with the same name
    public Station askWhichStation(List<Station> duplicateStations, int startOrEnd){

        String whichStation = startOrEnd == 1 ? "starting" : "destination";

        System.out.println("There are multiple stations with the name of the " + whichStation + " station. Did you mean:");

        int numOfOptions = printDuplicateStations(duplicateStations);

        return duplicateStations.get(getIntFromUser(0, numOfOptions));

    }

    // Goes through the list of stations with the same name and print's them with an associated number.
    private int printDuplicateStations(List<Station> duplicateStations){
        StringBuilder message = new StringBuilder();

        int i = 0;

        for(; i < duplicateStations.size(); i++){
            Station currentStation = duplicateStations.get(i);
            message.append(i + ") " + currentStation.getStationName() + " on ");
            Set<String> lines = currentStation.getLineNames();
            if(lines.size() > 1){
                message.append("lines");
            }else {
                message.append("line ");
            }
            for(String line : lines){
                message.append(line + " ");
            }
            message.replace(message.length()-1, message.length(), ".\n");
        }
        System.out.print(message.toString());
        return i;
    }

    // Prints the Station path Station by Station e.g. A -> B -> C
    public void printWholePath(List<Station> path){
        StringBuilder routeString = new StringBuilder("Please take the following route:\n");

        for (Station station: path) {
            routeString.append(station.getStationName());
            routeString.append(" -> ");
        }

        routeString.delete(routeString.length()-3, routeString.length());

        System.out.println(routeString.toString());
    }

    // This function tries to print a path in a very verbose manner by figuring out where there are line changes
    // It could do wih modularising and cleaning it up. It isn't perfect either e.g. try KentStreet to Symphony.
    public void printPath(List<Station> path){

        //Preliminary checks, if path is empty or just contains a single station
        if(path.size() == 1 ){
            System.out.println("You are already where you wish to be.");
            return;
        }else if(path.isEmpty()){
            System.out.println("Error. No path was found.");
            return;
        }

        Station startStation = path.get(0);

        StringBuilder routeString = new StringBuilder("Starting at " + startStation.getStationName() + " station ");
        routeString.append(getStartingLine(path));

        Iterator<Station> pathIterator = path.iterator();

        Station currentStation = pathIterator.next();
        Station nextStation = pathIterator.next();

        Set<String> intersection = new HashSet<>(currentStation.getLineNames());

        // This loop continually checks the set intersection between subsequent stations' line names.
        // When the intersection between the previous and next station is empty it means there has been a line change.
        while(pathIterator.hasNext()) {
            intersection.retainAll(nextStation.getLineNames());
            while (!intersection.isEmpty()) {
                currentStation = nextStation;

                if(!pathIterator.hasNext()){
                    break; // We have reached our destination, so break out the first loop.
                }

                nextStation = pathIterator.next();
                intersection.retainAll(nextStation.getLineNames());
            }

            // When we reach this point there has been a line change.
            // To get the line we change to, we get the intersection between the current station and the next station
            Set<String> changeIntersection = new HashSet<>(currentStation.getLineNames());
            changeIntersection.retainAll(nextStation.getLineNames());

            if(!pathIterator.hasNext()){
                // Check for the edge case where the destination is right after a line change.
                if(intersection.isEmpty() && (!changeIntersection.isEmpty())){
                    String lineChange = changeIntersection.iterator().next();
                    routeString.append(" until " + currentStation.getStationName() + " where you change to the " + lineChange + " line");
                }
                break; // Reached our destination, break the second loop.
            }

            if(changeIntersection.isEmpty()){
                // If the change intersection is empty there has been a false-positive line change e.g. Red -> RedA
                // Therefore we reset the intersection to be the one on the next station and go to the top of the loop
                intersection = new HashSet<>(nextStation.getLineNames());
                continue;
            }

            // If we get to this point, there has been a line change and there can only be one line we changed to.
            // We add the line to the StringBuilder and reset the intersection to be the one on the next station
            String lineChange = changeIntersection.iterator().next();
            routeString.append(" until " + currentStation.getStationName() + " where you change to the " + lineChange + " line");
            intersection = new HashSet<>(nextStation.getLineNames());
        }

        routeString.append(" until you reach " + nextStation.getStationName() + ".");

        System.out.println(routeString.toString());

        if(userWantsWholePath()){
            printWholePath(path);
        }

    }

    // Checks which direction we are going (from the starting station) and returns the proper line name
    private String getStartingLine(List<Station> path){

        Set<String> lineNames = path.get(0).getLineNames();

        if(lineNames.size() == 1){
            return "follow the " + lineNames.iterator().next() + " line";
        }else {
            Iterator<Station> pathIterator = path.iterator();
            pathIterator.next();
            Station nextStation = pathIterator.next();
            Set<String> intersection = new HashSet<>(nextStation.getLineNames());
            intersection.retainAll(lineNames);
            while (intersection.size() > 1 && pathIterator.hasNext()){
                nextStation = pathIterator.next();
                intersection.retainAll(nextStation.getLineNames());
            }

            String line;

            if(intersection.iterator().hasNext()){
                line = intersection.iterator().next();
            }else {
                line = lineNames.iterator().next();
            }

            return "follow the " + line + " line";
        }

    }

    private boolean userWantsWholePath(){
        System.out.println("Would you like to see the route station by station? (Yes, No)");

        return getYesOrNo();

    }

    // Returns True when the user input is yes, False when the input is No
    private boolean getYesOrNo(){
        reader = new Scanner(System.in);

        while (true){
            String input = reader.next().toLowerCase();
            if(input.matches("yes|y")){
                return true;
            }else if(input.matches("no|n")){
                return false;
            }else {
                System.out.println("Please type in Yes(Y) or No(N).");
            }
        }
    }

    // Depending on the error code it prints a user-friendly error message
    public void printError(UICodes code){
        switch (code){
            case START_STATION_ERROR:
                System.out.println("The starting station has not been found. Please check your input.");
                break;
            case END_STATION_ERROR:
                System.out.println("The destination station has not been found. Please check your input.");
                break;
            case BOTH_STATIONS_WRONG:
                System.out.println("The starting and destination stations have not been found. Please check your input.");
        }
    }

    // To be called when program shuts down to close the scanner
    // This cannot be done beforehand because calling this function also closes System.in
    // And it cannot be reopened. So you wouldn't be able to get any more input from the user.
    public void closeScanner(){
        reader.close();
    }

}
