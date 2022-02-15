package com.api.parking.control.controllers;

import com.api.parking.control.dtos.ParkingSpotDto;
import com.api.parking.control.models.ParkingSpotModel;
import com.api.parking.control.services.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@CrossOrigin(origins = "*") // informando que pode ser acessada de qualquer fonte
@RequestMapping("/parking-spot") // URI a nível de classe
public class ParkingSpotController {

    // ponto de injeção via construtor
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    // implementação dos metodos: POST, GET, PUT e DELETE

    // POST
   @PostMapping
   /* ResponseEntity<Object> pq teremos diferentes tipos de retorno dependendo das verificações
    * @Valid é para que as validações dos atributos do DTO sejam válidas*/
   public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){
       // validando a vaga
        if (parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }
       if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
           return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
       }
       if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
           return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block! ");
       }

       var parkingSpotModel = new ParkingSpotModel(); // capturamos um dto
       BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel); // convertendo o dto em model
       parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC"))); // serando a data de registro, pq não está no dto
       return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel)); // salvamos um model
   }

}
