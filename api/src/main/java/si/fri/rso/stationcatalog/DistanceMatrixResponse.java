package si.fri.rso.stationcatalog;

import java.util.List;

public class DistanceMatrixResponse {
    public List<String> destination_addresses;

    public List<String> origin_addresses;

    public DistanceMatrixRow[] rows;

    public String status;
}
