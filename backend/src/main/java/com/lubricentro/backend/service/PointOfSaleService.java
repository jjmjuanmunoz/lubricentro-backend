package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.pos.CreatePointOfSaleRequest;
import com.lubricentro.backend.dto.pos.PointOfSaleResponse;
import com.lubricentro.backend.entity.AfipPos;
import com.lubricentro.backend.repository.AfipPosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestion de puntos de venta.
 */
@Service
public class PointOfSaleService {

    private static final Logger log = LoggerFactory.getLogger(PointOfSaleService.class);

    private final AfipPosRepository posRepository;

    public PointOfSaleService(AfipPosRepository posRepository) {
        this.posRepository = posRepository;
    }

    /**
     * Lista todos los puntos de venta.
     */
    @Transactional(readOnly = true)
    public List<PointOfSaleResponse> listAll() {
        return posRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lista solo los puntos de venta activos.
     */
    @Transactional(readOnly = true)
    public List<PointOfSaleResponse> listActive() {
        return posRepository.findAll().stream()
                .filter(pos -> pos.getActive() != null && pos.getActive())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un punto de venta por ID.
     */
    @Transactional(readOnly = true)
    public Optional<PointOfSaleResponse> getById(Long id) {
        return posRepository.findById(id).map(this::mapToResponse);
    }

    /**
     * Obtiene un punto de venta por numero.
     */
    @Transactional(readOnly = true)
    public Optional<PointOfSaleResponse> getByNumber(Integer number) {
        return posRepository.findByPosNumber(number).map(this::mapToResponse);
    }

    /**
     * Crea un nuevo punto de venta.
     */
    @Transactional
    public PointOfSaleResponse create(CreatePointOfSaleRequest request) {
        // Verificar que no exista otro con el mismo numero
        if (posRepository.findByPosNumber(request.numero()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un punto de venta con el numero: " + request.numero());
        }

        AfipPos pos = AfipPos.builder()
                .posNumber(request.numero())
                .description(request.descripcion())
                .cbteTypeScope(request.alcanceTipos())
                .homologation(request.homologacion())
                .active(true)
                .build();

        pos = posRepository.save(pos);
        log.info("Punto de venta creado - Numero: {}, ID: {}", pos.getPosNumber(), pos.getId());

        return mapToResponse(pos);
    }

    /**
     * Activa un punto de venta.
     */
    @Transactional
    public PointOfSaleResponse activate(Long id) {
        AfipPos pos = posRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Punto de venta no encontrado: " + id));

        pos.setActive(true);
        pos = posRepository.save(pos);
        log.info("Punto de venta activado - ID: {}", id);

        return mapToResponse(pos);
    }

    /**
     * Desactiva un punto de venta.
     */
    @Transactional
    public PointOfSaleResponse deactivate(Long id) {
        AfipPos pos = posRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Punto de venta no encontrado: " + id));

        pos.setActive(false);
        pos = posRepository.save(pos);
        log.info("Punto de venta desactivado - ID: {}", id);

        return mapToResponse(pos);
    }

    private PointOfSaleResponse mapToResponse(AfipPos pos) {
        return new PointOfSaleResponse(
                pos.getId(),
                pos.getPosNumber(),
                pos.getDescription(),
                pos.getCbteTypeScope(),
                pos.getHomologation(),
                pos.getActive() != null && pos.getActive()
        );
    }
}
