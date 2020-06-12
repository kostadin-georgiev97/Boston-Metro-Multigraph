package Controller;
import Model.Node;

import java.util.HashSet;
import java.util.Set;

public class Station implements Node {

    private int stationID;
    private String stationName;
    private Set<String> lineNames;

    public Station(int id, String name) {
        stationID = id;
        stationName = name;
        lineNames = new HashSet<>();
    }

    public String getStationName() {
        return stationName;
    }

    public void setNodeID(int id) {
        stationID = id;
    }

    public int getNodeID() {
        return stationID;
    }

    public Set<String> getLineNames(){
        return lineNames;
    }

    public void setLineNames(Set<String> lineNames) {
        this.lineNames = lineNames;
    }
}
