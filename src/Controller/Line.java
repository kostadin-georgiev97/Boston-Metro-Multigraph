package Controller;

import Model.Edge;

public class Line implements Edge<Station> {

    private Station outboundStation;
    private Station inboundStation;

    public Line(Station o, Station i) {
        outboundStation = o;
        inboundStation = i;
    }

    public void setStartNode(Station station) {
        outboundStation = station;
    }

    public Station getStartNode() {
        return outboundStation;
    }

    public void setEndNode(Station station) {
        inboundStation = station;
    }

    public Station getEndNode() {
        return inboundStation;
    }

}
