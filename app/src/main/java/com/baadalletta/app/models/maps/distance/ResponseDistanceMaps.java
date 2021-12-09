package com.baadalletta.app.models.maps.distance;

import java.util.List;

public class ResponseDistanceMaps {

    private List<String> destination_addresses;

    private List<RowsItem> rows;

    private List<String> origin_addresses;

    private String status;

    public List<String> getDestination_addresses() {
        return destination_addresses;
    }

    public List<RowsItem> getRows() {
        return rows;
    }

    public List<String> getOrigin_addresses() {
        return origin_addresses;
    }

    public String getStatus() {
        return status;
    }
}