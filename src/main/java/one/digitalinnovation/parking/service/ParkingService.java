package one.digitalinnovation.parking.service;

import one.digitalinnovation.parking.exception.ParkingNotFoundException;
import one.digitalinnovation.parking.model.Parking;
import one.digitalinnovation.parking.repository.ParkingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public ParkingService(ParkingRepository parkingRepository){
        this.parkingRepository = parkingRepository;
    }
    @Transactional(readOnly=true, propagation = Propagation.SUPPORTS)
    public List<Parking> findAll(){
        return parkingRepository.findAll();
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Transactional(readOnly=true, propagation = Propagation.SUPPORTS)
    public Parking findById(String id){
        return parkingRepository.findById(id).orElseThrow( () ->
            new ParkingNotFoundException(id));
    }

    @Transactional
    public Parking create(Parking parkingCreate) {
        String uuid = getUUID();
        parkingCreate.setId(uuid);
        parkingCreate.setEntryDate(LocalDateTime.now());
        parkingRepository.save(parkingCreate);
        return  parkingCreate;
    }

    public void delete(String id) {
        findById(id);
        parkingRepository.deleteById(id);
    }

    public Parking update(String id, Parking parkingCreate) {
        Parking parking = findById(id);
        parking.setColor(parkingCreate.getColor());
        parking.setColor(parkingCreate.getState());
        parking.setColor(parkingCreate.getModel());
        parking.setColor(parkingCreate.getLicence());
        parkingRepository.save(parking);
        return parking;
    }

    public Parking checkOut(String id) {
        // recuperar o estacionado, atualizar a data de saida e calcular o valor
        Parking parking = findById(id);
        parking.setExitDate(LocalDateTime.now());
        parking.setBill(ParkingCheckout.getBill(parking));
        return parkingRepository.save(parking);
    }
}
