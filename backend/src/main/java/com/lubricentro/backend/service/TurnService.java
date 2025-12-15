package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.*;
import com.lubricentro.backend.entity.*;
import com.lubricentro.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnService {

    private final TurnRepository turnRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final AutomobileRepository automobileRepository;
    private final ProductRepository productRepository;
    private final ServiceRecordRepository serviceRecordRepository;
    private final UserRepository userRepository;

    public TurnService(
            TurnRepository turnRepository,
            BudgetItemRepository budgetItemRepository,
            AutomobileRepository automobileRepository,
            ProductRepository productRepository,
            ServiceRecordRepository serviceRecordRepository,
            UserRepository userRepository
    ) {
        this.turnRepository = turnRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.automobileRepository = automobileRepository;
        this.productRepository = productRepository;
        this.serviceRecordRepository = serviceRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TurnDTO createTurn(CreateTurnRequest request) {
        Automobile automobile = automobileRepository.findByPlate(request.plate())
                .orElseThrow(() -> new RuntimeException("Automobile not found with plate: " + request.plate()));

        Integer turnNumber = getNextTurnNumber();

        Turn turn = Turn.builder()
                .automobile(automobile)
                .status(TurnStatus.WAITING)
                .turnNumber(turnNumber)
                .scheduledDate(request.scheduledDate())
                .scheduledTime(request.scheduledTime())
                .arrivalTime(LocalDateTime.now())
                .notes(request.notes())
                .build();

        turn = turnRepository.save(turn);

        if (request.items() != null && !request.items().isEmpty()) {
            for (BudgetItemRequest itemRequest : request.items()) {
                addBudgetItemInternal(turn, itemRequest);
            }
        }

        return mapToDTO(turn);
    }

    public List<TurnDTO> getTodayTurns() {
        LocalDate today = LocalDate.now();
        List<Turn> turns = turnRepository.findByArrivalDate(today);
        return turns.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<TurnDTO> getWaitingTurns() {
        List<Turn> turns = turnRepository.findByStatusOrderByArrivalTimeAsc(TurnStatus.WAITING);
        return turns.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public TurnDTO getTurnById(Long id) {
        Turn turn = turnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turn not found with id: " + id));
        return mapToDTO(turn);
    }

    @Transactional
    public TurnDTO startTurn(Long turnId, Long employeeId) {
        Turn turn = turnRepository.findById(turnId)
                .orElseThrow(() -> new RuntimeException("Turn not found with id: " + turnId));

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + employeeId));

        turn.setStatus(TurnStatus.IN_PROGRESS);
        turn.setStartTime(LocalDateTime.now());
        turn.setAttendedBy(employee);

        turn = turnRepository.save(turn);
        return mapToDTO(turn);
    }

    @Transactional
    public TurnDTO completeTurn(Long turnId, CompleteTurnRequest request) {
        Turn turn = turnRepository.findById(turnId)
                .orElseThrow(() -> new RuntimeException("Turn not found with id: " + turnId));

        ServiceRecord serviceRecord = ServiceRecord.builder()
                .automobile(turn.getAutomobile())
                .serviceDate(request.serviceDate())
                .currentKm(request.currentKm())
                .oilBrand(request.oilBrand())
                .oilName(request.oilName())
                .oilFilter(request.oilFilter())
                .airFilter(request.airFilter())
                .fuelFilter(request.fuelFilter())
                .gearboxOil(request.gearboxOil())
                .differentialOil(request.differentialOil())
                .nextServiceKm(request.nextServiceKm())
                .build();

        serviceRecord = serviceRecordRepository.save(serviceRecord);

        turn.setServiceRecord(serviceRecord);
        turn.setStatus(TurnStatus.COMPLETED);
        turn.setEndTime(LocalDateTime.now());

        turn = turnRepository.save(turn);
        return mapToDTO(turn);
    }

    @Transactional
    public TurnDTO cancelTurn(Long turnId, String reason) {
        Turn turn = turnRepository.findById(turnId)
                .orElseThrow(() -> new RuntimeException("Turn not found with id: " + turnId));

        turn.setStatus(TurnStatus.CANCELLED);
        turn.setNotes(turn.getNotes() != null
                ? turn.getNotes() + "\nCancelled: " + reason
                : "Cancelled: " + reason);

        turn = turnRepository.save(turn);
        return mapToDTO(turn);
    }

    @Transactional
    public BudgetItemDTO addBudgetItem(Long turnId, BudgetItemRequest request) {
        Turn turn = turnRepository.findById(turnId)
                .orElseThrow(() -> new RuntimeException("Turn not found with id: " + turnId));

        return addBudgetItemInternal(turn, request);
    }

    @Transactional
    public BudgetItemDTO updateBudgetItem(Long itemId, BigDecimal finalPrice, Boolean included) {
        BudgetItem item = budgetItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Budget item not found with id: " + itemId));

        if (finalPrice != null) {
            item.setFinalPrice(finalPrice);
        }
        if (included != null) {
            item.setIncluded(included);
        }

        item = budgetItemRepository.save(item);
        return mapBudgetItemToDTO(item);
    }

    @Transactional
    public void removeBudgetItem(Long itemId) {
        if (!budgetItemRepository.existsById(itemId)) {
            throw new RuntimeException("Budget item not found with id: " + itemId);
        }
        budgetItemRepository.deleteById(itemId);
    }

    public Integer getNextTurnNumber() {
        LocalDate today = LocalDate.now();
        Integer maxNumber = turnRepository.findMaxTurnNumberByDate(today);
        return maxNumber + 1;
    }

    private BudgetItemDTO addBudgetItemInternal(Turn turn, BudgetItemRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.productId()));

        BudgetItem item = BudgetItem.builder()
                .turn(turn)
                .product(product)
                .quantity(request.quantity())
                .unitPrice(product.getUnitPrice())
                .notes(request.notes())
                .included(true)
                .build();

        item = budgetItemRepository.save(item);
        return mapBudgetItemToDTO(item);
    }

    private TurnDTO mapToDTO(Turn turn) {
        List<BudgetItem> items = budgetItemRepository.findByTurnId(turn.getId());
        List<BudgetItemDTO> itemDTOs = items.stream()
                .map(this::mapBudgetItemToDTO)
                .collect(Collectors.toList());

        BigDecimal totalEstimated = items.stream()
                .filter(BudgetItem::getIncluded)
                .map(item -> {
                    BigDecimal price = item.getFinalPrice() != null ? item.getFinalPrice() : item.getUnitPrice();
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AutomobileDTO automobileDTO = new AutomobileDTO(
                turn.getAutomobile().getId(),
                turn.getAutomobile().getPlate(),
                turn.getAutomobile().getBrand(),
                turn.getAutomobile().getModel(),
                turn.getAutomobile().getOwner().getFullName()
        );

        return new TurnDTO(
                turn.getId(),
                automobileDTO,
                turn.getStatus().name(),
                turn.getTurnNumber(),
                turn.getArrivalTime(),
                itemDTOs,
                totalEstimated,
                turn.getNotes()
        );
    }

    private BudgetItemDTO mapBudgetItemToDTO(BudgetItem item) {
        return new BudgetItemDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getFinalPrice(),
                item.getIncluded()
        );
    }
}
