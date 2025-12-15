package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.*;
import com.lubricentro.backend.service.TurnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/turns")
public class TurnController {

    private final TurnService turnService;

    public TurnController(TurnService turnService) {
        this.turnService = turnService;
    }

    @Operation(summary = "Create new turn", description = "Creates a new turn with optional budget items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turn created successfully"),
            @ApiResponse(responseCode = "404", description = "Automobile not found")
    })
    @PostMapping
    public ResponseEntity<TurnDTO> createTurn(@RequestBody CreateTurnRequest request) {
        return ResponseEntity.ok(turnService.createTurn(request));
    }

    @Operation(summary = "Get today's turns", description = "Returns all turns for the current day")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turns retrieved successfully")
    })
    @GetMapping("/today")
    public ResponseEntity<List<TurnDTO>> getTodayTurns() {
        return ResponseEntity.ok(turnService.getTodayTurns());
    }

    @Operation(summary = "Get waiting turns", description = "Returns all turns with WAITING status ordered by arrival time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Waiting turns retrieved successfully")
    })
    @GetMapping("/waiting")
    public ResponseEntity<List<TurnDTO>> getWaitingTurns() {
        return ResponseEntity.ok(turnService.getWaitingTurns());
    }

    @Operation(summary = "Get turn by ID", description = "Returns detailed information about a specific turn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turn found"),
            @ApiResponse(responseCode = "404", description = "Turn not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TurnDTO> getTurnById(@PathVariable Long id) {
        return ResponseEntity.ok(turnService.getTurnById(id));
    }

    @Operation(summary = "Start turn", description = "Changes turn status to IN_PROGRESS and assigns an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turn started successfully"),
            @ApiResponse(responseCode = "404", description = "Turn or employee not found")
    })
    @PatchMapping("/{id}/start")
    public ResponseEntity<TurnDTO> startTurn(
            @PathVariable Long id,
            @RequestParam Long employeeId
    ) {
        return ResponseEntity.ok(turnService.startTurn(id, employeeId));
    }

    @Operation(summary = "Complete turn", description = "Creates a service record and marks turn as COMPLETED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turn completed successfully"),
            @ApiResponse(responseCode = "404", description = "Turn not found")
    })
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TurnDTO> completeTurn(
            @PathVariable Long id,
            @RequestBody CompleteTurnRequest request
    ) {
        return ResponseEntity.ok(turnService.completeTurn(id, request));
    }

    @Operation(summary = "Cancel turn", description = "Changes turn status to CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turn cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Turn not found")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TurnDTO> cancelTurn(
            @PathVariable Long id,
            @RequestParam String reason
    ) {
        return ResponseEntity.ok(turnService.cancelTurn(id, reason));
    }

    @Operation(summary = "Add budget item", description = "Adds a new item to the turn budget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget item added successfully"),
            @ApiResponse(responseCode = "404", description = "Turn or product not found")
    })
    @PostMapping("/{id}/items")
    public ResponseEntity<BudgetItemDTO> addBudgetItem(
            @PathVariable Long id,
            @RequestBody BudgetItemRequest request
    ) {
        return ResponseEntity.ok(turnService.addBudgetItem(id, request));
    }

    @Operation(summary = "Update budget item", description = "Updates final price and/or included status of a budget item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Budget item not found")
    })
    @PatchMapping("/{turnId}/items/{itemId}")
    public ResponseEntity<BudgetItemDTO> updateBudgetItem(
            @PathVariable Long turnId,
            @PathVariable Long itemId,
            @RequestParam(required = false) BigDecimal finalPrice,
            @RequestParam(required = false) Boolean included
    ) {
        return ResponseEntity.ok(turnService.updateBudgetItem(itemId, finalPrice, included));
    }

    @Operation(summary = "Remove budget item", description = "Removes an item from the turn budget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Budget item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Budget item not found")
    })
    @DeleteMapping("/{turnId}/items/{itemId}")
    public ResponseEntity<Void> removeBudgetItem(
            @PathVariable Long turnId,
            @PathVariable Long itemId
    ) {
        turnService.removeBudgetItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
