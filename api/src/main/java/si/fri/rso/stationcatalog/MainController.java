package si.fri.rso.stationcatalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import si.fri.rso.stationcatalog.models.entities.Station;
import si.fri.rso.stationcatalog.models.entities.StationObject;
import si.fri.rso.stationcatalog.services.StationService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController // This means that this class is a Controller
//@RequestMapping(path="/") // This means URL's start with /demo (after Application path)
@RefreshScope
@CrossOrigin(origins = "http://localhost:4200")
public class MainController {

    @Value("${allowCreation:true}")
    private boolean canCreate;

    @Value("${API_KEY}")
    private String api_key;

    @Autowired
    private StationService stationService;

    @GetMapping("/")
    public ResponseEntity healthCheck(){
        return ResponseEntity.ok().build();
    }

    @PostMapping(path="/station/add") // Map ONLY POST Requests
    @ResponseBody
    public ResponseEntity addNewStation (@RequestBody StationObject stationObject) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        if(canCreate == false){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Station n = new Station();
        n.setOwner(stationObject.owner);
        n.setLat(stationObject.lat);
        n.setLon(stationObject.lon);
        n.setReserved(false);
        stationService.addStation(n);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path="/station/all")
    public @ResponseBody List<Station> getAllStations(@RequestParam double lat, @RequestParam double lon) {
        Iterable<Station> s = stationService.getAllStations().join();
        Iterator<Station> iter = s.iterator();

        List<Station> end = new ArrayList<Station>();

        String destinations = "";

        while(iter.hasNext()){
            Station next = iter.next();
            destinations += next.getLat() + "%2C" + next.getLon() + "%7C";
            end.add(next);
        }

        destinations = destinations.substring(0, destinations.length() - 3);

        String origins = lat+"%2C"+lon;

        String uri = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origins + "&destinations=" + destinations + "&key=" + api_key;

        String result = "";
        try {
            URL url = new URL(uri);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setConnectTimeout(5000);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            result = content.toString();

            con.disconnect();
        }catch (Exception e){

        }

        ObjectMapper objectMapper = new ObjectMapper();

        DistanceMatrixResponse response = null;

        boolean success = false;

        try {
            response = objectMapper.readValue(result, DistanceMatrixResponse.class);
            success = true;
        }catch (Exception e){
            System.out.println(e);
        }

        if(success == true){
            List<Station> endResult = new ArrayList<Station>();


            int num = 10;

            if(end.size() < 10)
                num = end.size();

            for(int k = 0; k < num; k++) {
                int smallest = response.rows[0].elements[0].distance.value;
                int index = 0;
                for (int i = 0; i < response.rows[0].elements.length; i++) {
                    if (response.rows[0].elements[i].distance.value < smallest) {
                        if(!isAlreadyInList(endResult, end.get(i))) {
                            smallest = response.rows[0].elements[i].distance.value;
                            index = i;
                        }
                    }
                }
                endResult.add(end.get(index));
            }
            return endResult;
        }

        return end;
    }

    private boolean isAlreadyInList(List<Station> list, Station s){
        for(int i = 0; i < list.size(); i++){
            if(s == list.get(i)){
                return true;
            }
        }
        return false;
    }
}