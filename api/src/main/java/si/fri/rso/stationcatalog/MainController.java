package si.fri.rso.stationcatalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import si.fri.rso.stationcatalog.models.entities.Station;
import si.fri.rso.stationcatalog.services.StationService;

@RestController // This means that this class is a Controller
//@RequestMapping(path="/") // This means URL's start with /demo (after Application path)
@RefreshScope
public class MainController {

    @Value("${allowCreation:true}")
    private boolean canCreate;

    @Autowired
    private StationService stationService;

    @GetMapping("/")
    public ResponseEntity healthCheck(){
        return ResponseEntity.ok().build();
    }

    @PostMapping(path="/station/add") // Map ONLY POST Requests
    @ResponseBody
    public ResponseEntity addNewUser (@RequestParam String owner, @RequestParam double lat, @RequestParam double lon) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        if(canCreate == false){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Station n = new Station();
        n.setOwner(owner);
        n.setLat(lat);
        n.setLon(lon);
        n.setReserved(false);
        stationService.addStation(n);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path="/station/all")
    public @ResponseBody Iterable<Station> getAllUsers() {
        // This returns a JSON or XML with the users
        return stationService.getAllStations();
    }
}