package si.fri.rso.stationcatalog.models.repositories;

import org.springframework.data.repository.CrudRepository;
import si.fri.rso.stationcatalog.models.entities.Station;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StationRepository extends CrudRepository<Station, Integer> {

}