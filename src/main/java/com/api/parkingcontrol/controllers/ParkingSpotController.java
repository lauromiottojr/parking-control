package com.api.parkingcontrol.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parkingcontrol.dtos.ParkingSpotDTO;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

	final ParkingSpotService parkingSpotService;

	public ParkingSpotController(ParkingSpotService parkingSpotService) {
		this.parkingSpotService = parkingSpotService;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO) {
		if (parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Plata do carro já está em uso!");
		}
		if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Vaga já está em uso!");
		}
		if (parkingSpotService.existsByApartamentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Este apartamento já tem uma vaga!");
		}

		ParkingSpotModel parkingSpotModel = new ParkingSpotModel();
		BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
		parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
		return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping
	public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/{id}")
	public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") Integer id) {
		Optional<ParkingSpotModel> parkingSpotModelOpt = parkingSpotService.findById(id);
		if (!parkingSpotModelOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOpt.get());
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") Integer id) {
		Optional<ParkingSpotModel> parkingSpotModelOpt = parkingSpotService.findById(id);
		if (!parkingSpotModelOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
		}
		parkingSpotService.delete(parkingSpotModelOpt.get());
		return ResponseEntity.status(HttpStatus.OK).body("Vaga foi deletada com sucesso!");
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") Integer id,
			@RequestBody @Valid ParkingSpotDTO objDTO) {
		Optional<ParkingSpotModel> parkingSpotModelOpt = parkingSpotService.findById(id);
		if (!parkingSpotModelOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
		}
		var parkingSpotModel = parkingSpotModelOpt.get();
		parkingSpotModel.setParkingSpotNumber(objDTO.getParkingSpotNumber());
		parkingSpotModel.setLicensePlateCar(objDTO.getLicensePlateCar());
		parkingSpotModel.setModelCar(objDTO.getModelCar());
		parkingSpotModel.setBrandCar(objDTO.getBrandCar());
		parkingSpotModel.setColorCar(objDTO.getColorCar());
		parkingSpotModel.setResponsibleName(objDTO.getResponsibleName());
		parkingSpotModel.setApartment(objDTO.getApartment());
		parkingSpotModel.setBlock(objDTO.getBlock());
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
	}

}
