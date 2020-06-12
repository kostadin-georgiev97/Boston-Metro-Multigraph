package Controller;

import Model.*;
import View.UICodes;
import View.UserInterface;

import java.util.*;

public class MetroMap{

    private MultiGraphADT metro;

    private Map<String, List<Integer>> labelToId;

    private UserInterface ui;

    public MetroMap(){

        metro = new MultiGraph();
        labelToId = new HashMap<>();

    }

    // This function is the main program's loop which interfaces with the user.
    public void run(){
        ui = new UserInterface();
        UICodes optionChosen = ui.presentOptions();

        switch (optionChosen){
            case EXIT:
                ui.closeScanner();
                System.exit(0);
            case GET_DIRECTIONS:
                List<String> directionInput;
                UICodes inputCode;

                do{
                    directionInput = ui.getDirectionsInput();
                    inputCode = checkDirectionInput(directionInput);
                    if(!inputCode.equals(UICodes.GOOD_INPUT))
                        ui.printError(inputCode);
                }while (!inputCode.equals(UICodes.GOOD_INPUT));

                ui.printPath(getDirections(directionInput.get(0), directionInput.get(1)));

                run();
                break;

            case INPUT_ERROR:
                System.exit(-1);
        }
    }

    // This function returns a code depending on the user's input when getting directions between stations.
    private UICodes checkDirectionInput(List<String> stations){

        boolean start = labelToId.containsKey(stations.get(0).toLowerCase());
        boolean end = labelToId.containsKey(stations.get(1).toLowerCase());

        if(start && end){
            return UICodes.GOOD_INPUT;
        }else if(!start && !end){
            return UICodes.BOTH_STATIONS_WRONG;
        }else if(!start){
            return UICodes.START_STATION_ERROR;
        }else {
            return UICodes.END_STATION_ERROR;
        }
    }

    // This function adds a station to the metro field (the graphADT)
    // It returns a boolean depending on if it successfully added the station or not.
    public boolean addStation(int stationID, String stationName){

        int numStationsBefore = metro.nNodes();

        metro.addNode(new Station(stationID, stationName));

        if(metro.nNodes() > numStationsBefore){
            List<Integer> ids = labelToId.get(stationName.toLowerCase());
            if(ids == null) {
                ids = new ArrayList<>();
                ids.add(stationID);
                labelToId.put(stationName.toLowerCase(), ids);

                return true;
            }else {
                ids = labelToId.get(stationName.toLowerCase());
                ids.add(stationID);
                labelToId.replace(stationName.toLowerCase(), ids);

                return true;
            }
        }else {
            return false;
        }

    }

    // This function adds the line names that the station is on to the Station objects
    public void addStationLineName(int stationID, String line){
        if (stationID == 0){
            return;
        }
        Station station = (Station) metro.getNode(stationID);
        Set<String> lines = station.getLineNames();
        if(!lines.contains(line)) {
            lines.add(line);
            station.setLineNames(lines);
        }
    }

    // This function adds a connection (an Edge) between two stations
    // It returns a error code depending on the outcome
    public boolean connectStations(int startID, int endID, int fromStationId){

        Node from = metro.getNode(fromStationId);
        Node start = metro.getNode(startID);
        Node end = metro.getNode(endID);

        boolean returnCode = false;

        if(startID != 0) {
            returnCode = metro.addEdge(new Line((Station) from, (Station) start));

            if (!returnCode) {
                return returnCode;
            }
        }

        if(endID != 0) {
            returnCode = metro.addEdge(new Line((Station) from, (Station) end));
        }

        return returnCode;

    }

    // This function calls the shortest path function from the graphADT
    // It first checks if its arguments are stations that have the same name as others
    // Lastly it turns a list of nodes into a list of stations by casting
    public List<Station> getDirections(String startStation, String endStation){

        List<Integer> startIds = labelToId.get(startStation.toLowerCase());
        List<Integer> endIds = labelToId.get(endStation.toLowerCase());

        Node startNode = getNodeFromDuplicates(startIds, 1);
        Node endNode = getNodeFromDuplicates(endIds, 2);

        List<Station> stationList = new LinkedList<>();

        for(Node node : metro.shortestPath(startNode, endNode)){
            stationList.add((Station) node);
        }

        return stationList;

    }

    // Checks if there are multiple Stations with the same name (label)
    // If there are,
    // Turns a node list into a station list and then through the User Interface class
    // Asks the user which node it means among the ones that have the same name
    // Returns the proper node that the user wants
    private Node getNodeFromDuplicates(List<Integer> idList, int startOrEnd){

        Node result;

        if(idList.size() > 1){ // if there are same name stations
            List<Station> duplicateStations = new ArrayList<>();

            for(int id : idList){
                Node node = metro.getNode(id);
                duplicateStations.add((Station) node);
            }

            result = ui.askWhichStation(duplicateStations, startOrEnd);
        }else{
            result = metro.getNode(idList.get(0)); // If there is just one station with a certain name
        }

        return result;

    }


}