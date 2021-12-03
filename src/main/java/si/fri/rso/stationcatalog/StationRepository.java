package si.fri.rso.stationcatalog;

import org.springframework.data.repository.CrudRepository;

import si.fri.rso.stationcatalog.Station;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StationRepository extends CrudRepository<Station, Integer> {

}